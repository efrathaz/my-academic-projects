(load "parser.scm")
(load "lex-tp.scm")
(load "consts.scm")
(load "primitives.scm")
(load "fvars.scm")
(load "symbols.scm")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; FINAL PROJECT ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define sexpr-counter 0)

(define ^^label
	(lambda (name)
		(let ((n 0))
			(lambda ()
				(set! n (+ n 1))
				(string-append name (number->string n))))))

(define prolog
	(string-append 
		"#include <stdio.h>" nl
		"#include <stdlib.h>" nl
		"#include \"arch/cisc.h\"" nl
		"#define DO_SHOW 1" nl
		"#define SOB_UNDEF 1" nl
		"#define SOB_VOID 2" nl
		"#define SOB_NIL 3" nl
		"#define SOB_FALSE 4" nl
		"#define SOB_TRUE 6" nl nl
		"int main(){" nl
		"	int SYMBOLS_START, SYMBOLS_LAST;" nl
		"	START_MACHINE;" nl
		"	JUMP(CONTINUE);" nl nl
		"#include \"arch/char.lib\"" nl
		"#include \"arch/io.lib\"" nl
		"#include \"arch/math.lib\"" nl
		"#include \"arch/scheme.lib\"" nl
		"#include \"arch/string.lib\"" nl
		"#include \"arch/system.lib\"" nl nl
		"	CONTINUE:" nl
		"	NOP;" nl nl))
	 
(define epilog
	(string-append
		"	JUMP(L_exit);" nl nl
		"L_fvar_undefined:" nl
		"	printf(\"fvar undefined!\\n\");" nl nl
		"L_exit:" nl
		"	STOP_MACHINE;" nl
		"	return 0;" nl
		"}" nl))

(define compile-scheme-file
	(lambda (input output)
		(let ((input-port1 (open-input-file "support-code.scm"))
			  (input-port2 (open-input-file input))
			  (output-port (open-output-file output 'replace)))
			(let ((sexprs (reverse (append (read-input input-port1) (read-input input-port2)))))
				(initialize-const-table)
				(initialize-fvar-table)
				(build-const-table sexprs)
				(set! fvars-next-address (find-next-addr *constant-table*))
				(create-fvars sexprs)
				(display prolog output-port)
				(display (create-consts-array) output-port)
				(display (create-fvar-codes) output-port)
				(display (create-top-level) output-port)
				(display (apply string-append (reverse (map code-gen-start sexprs))) output-port)
				(display epilog output-port)
				(close-input-port input-port1)
				(close-input-port input-port2)
				(close-output-port output-port)))))
				
(define read-input
	(lambda (port)
		(let ((sexpr (read port)))
			(if (eof-object? sexpr)
				'()
				(cons (test sexpr) (read-input port))))))

(define code-gen-start
	(lambda (expr)
		(set! sexpr-counter (add1 sexpr-counter))
		(string-append
			(cond ((equal? 'const (car expr)) (cg-const expr))
				  ((equal? 'fvar (car expr)) (cg-fvar expr))
				  ((equal? 'pvar (car expr)) (cg-pvar expr))
				  ((equal? 'bvar (car expr)) (cg-bvar expr))
				  ((equal? 'set! (car expr)) (cg-set expr 0 0))
				  ((equal? 'if3 (car expr)) (cg-if3 expr 0 0))
				  ((equal? 'or (car expr)) (cg-or expr 0 0))
				  ((equal? 'seq (car expr)) (cg-seq expr 0 0))
				  ((equal? 'lambda-simple (car expr)) (cg-lambda-simple expr 0 0))
				  ((equal? 'lambda-opt (car expr)) (cg-lambda-opt expr 0 0))
				  ((equal? 'lambda-variadic (car expr)) (cg-lambda-variadic expr 0 0))
				  ((equal? 'applic (car expr)) (cg-applic expr 0 0))
				  ((equal? 'tc-applic (car expr)) (cg-applic-tc expr 0 0))
				  ((equal? 'define (car expr)) (cg-define expr 0 0))
				  (else (error 'code-gen "not implemented yet")))
			"/* print R0 */" nl
			"CMP(R0, SOB_VOID);" nl
			"JUMP_EQ(L_after_print" (number->string sexpr-counter) ");" nl
			"PUSH(R0);" nl
			"CALL(WRITE_SOB);" nl
			"DROP(1);" nl
			"printf(\"\\n\");" nl
			"L_after_print" (number->string sexpr-counter) ":" nl)))
				

(define code-gen
	(lambda (expr env-size param-size)
		(cond ((equal? 'const (car expr)) (cg-const expr))
			  ((equal? 'fvar (car expr)) (cg-fvar expr))
			  ((equal? 'pvar (car expr)) (cg-pvar expr))
			  ((equal? 'bvar (car expr)) (cg-bvar expr))
			  ((equal? 'set! (car expr)) (cg-set expr env-size param-size))
			  ((equal? 'if3 (car expr)) (cg-if3 expr env-size param-size))
			  ((equal? 'or (car expr)) (cg-or expr env-size param-size))
			  ((equal? 'seq (car expr)) (cg-seq expr env-size param-size))
			  ((equal? 'lambda-simple (car expr)) (cg-lambda-simple expr env-size param-size))
			  ((equal? 'lambda-opt (car expr)) (cg-lambda-opt expr env-size param-size))
			  ((equal? 'lambda-variadic (car expr)) (cg-lambda-variadic expr env-size param-size))
			  ((equal? 'applic (car expr)) (cg-applic expr env-size param-size))
			  ((equal? 'tc-applic (car expr)) (cg-applic-tc expr env-size param-size))
			  ((equal? 'define (car expr)) (cg-define expr env-size param-size))
			  (else (error 'code-gen "not implemented yet")))))
				

;;;;;;;;;;;;;;;; const ;;;;;;;;;;;;;;
				
(define cg-const
	(lambda (e)
		(with e
			(lambda (const c)
				(let ((addr (find-address c *constant-table*)))
					(string-append "MOV(R0, IMM(" (number->string addr) "));	/* const */ " nl))))))


;;;;;;;;;;;;;;;; vars ;;;;;;;;;;;;;;;
								
(define cg-fvar
	(lambda (e)
		(with e
			(lambda (fvar v)
				(string-append
					"/* fvar */" nl
					"MOV(R0, IND(" (number->string (find-address v *fvar-table*)) "));" nl
			;		"printf(\"fvar address = %ld\\n\", " (number->string (find-address v *fvar-table*)) ");" nl
					"CMP(IND(R0), IMM(T_UNDEF));" nl
					"JUMP_EQ(L_fvar_undefined);" nl
					)))))
				
(define cg-pvar
	(lambda (e)
		(with e
			(lambda (pvar v i)
				(string-append
					"MOV(R0, FPARG(" (number->string (+ 2 i)) "));	/* pvar */" nl)))))

(define cg-bvar
	(lambda (e)
		(with e
			(lambda (bvar v i j)
				(string-append
					"/* bvar */" nl
					"MOV(R0, FPARG(0)); /* env */" nl
					"MOV(R0, INDD(R0," (number->string i) ")); /* major */" nl
					"MOV(R0, INDD(R0," (number->string j) ")); /* value */" nl
					"/* End of bvar */" nl)))))


;;;;;;;;;;;;;;;;; set ;;;;;;;;;;;;;;;
				
(define cg-set
	(lambda (e env-size param-size)
		(with e
			(lambda (sett var val)
				(cond ((equal? (car var) 'fvar) (cg-set-fvar e env-size param-size))
				      ((equal? (car var) 'pvar) (cg-set-pvar e env-size param-size))
				      ((equal? (car var) 'bvar) (cg-set-bvar e env-size param-size))
				      (else (error 'cg-set "not a variable")))))))

(define cg-set-fvar
	(lambda (e env-size param-size)
		(with e
			(lambda (sett fvar expr)
				(let ((x (cadr fvar)))
					(string-append
						"/* set fvar */" nl
						(code-gen expr env-size param-size) nl
						"MOV(IND("  (number->string (find-address x *fvar-table*)) "), R0);" nl
						"MOV(R0, IMM(SOB_VOID));" nl
						"/* End of set fvar */" nl))))))

(define cg-set-pvar
	(lambda (e env-size param-size)
		(with e
			(lambda (sett pvar expr)
				(let ((i (caddr pvar)))
					(string-append
						"/* set pvar */" nl
						(code-gen expr env-size param-size) nl
						"MOV(FPARG(" (number->string (+ 2 i)) "), R0);" nl
						"MOV(R0, IMM(SOB_VOID));" nl
						"/* End of set pvar */" nl))))))

(define cg-set-bvar
	(lambda (e env-size param-size)
		(with e
			(lambda (sett bvar expr)
				(let ((i (caddr bvar))
					  (j (cadddr bvar)))
					(string-append
						"/* set bvar */" nl
						(code-gen expr env-size param-size) nl
						"MOV(R1, FPARG(0)); /* env */" nl
						"MOV(R1, INDD(R1," (number->string i) ")); /* major */" nl
						"MOV(INDD(R1," (number->string j) "), R0); /* value */" nl
						"MOV(R0, IMM(SOB_VOID));" nl
						"/* End of set bvar */" nl))))))


;;;;;;;;;;;;;;;;; if ;;;;;;;;;;;;;;;;

(define ^label-if3else (^^label "L_If3_else"))
(define ^label-if3exit (^^label "L_If3_exit"))

(define cg-if3
	(lambda (e env-size param-size)
		(with e
			(lambda (if3 test dit dif)
				(let ((code-test (code-gen test env-size param-size))
					  (code-dit (code-gen dit env-size param-size))
					  (code-dif (code-gen dif env-size param-size))
					  (label-else (^label-if3else))
					  (label-exit (^label-if3exit)))
					(string-append
						"/******** if ********/" nl
						code-test nl ; when run, the result of the test will be in R0
						"CMP(R0, SOB_FALSE);" nl
						"JUMP_EQ(" label-else ");" nl
						code-dit nl
						"JUMP(" label-exit ");" nl
						label-else ":" nl
						code-dif nl
						label-exit ":" nl
						"/**** End of if ****/" nl nl))))))


;;;;;;;;;;;;;;;;; or ;;;;;;;;;;;;;;;;

(define ^label-orExit (^^label "L_or_exit"))

(define cg-or
	(lambda (e env-size param-size)
		(with e
			(lambda (or1 lst)
				(let ((label-exit (^label-orExit)))
					(string-append
						"/******** or ********/" nl
						(rec-cg-or lst label-exit env-size param-size) nl
						"/**** End of or ****/" nl nl))))))
								
(define rec-cg-or
	(lambda (lst label-exit env-size param-size)
		(let ((first (car lst))
			  (rest (cdr lst)))
			(if (null? rest)
				(string-append
					(code-gen first env-size param-size) nl
					label-exit ":")
				(string-append
					(code-gen first env-size param-size) nl
					"CMP(R0, SOB_FALSE);" nl
					"JUMP_NE(" label-exit "); /* if not a boolean, then exit or */" nl
					(rec-cg-or rest label-exit env-size param-size))))))


;;;;;;;;;;;;;; sequence ;;;;;;;;;;;;;

;; (define cg-seq
;; 	(lambda (e env-size param-size)
;; 		(with e
;; 			(lambda (seq lst)
;; 				(let* ((all-but-last (reverse (cdr (reverse lst))))
;; 					   (last (car (reverse lst)))
;; 					   (side-effects-exprs (filter has-side-effect all-but-last))
;; 					   (len (length side-effects-exprs)))
;; 					(string-append
;; 						"/******* seq *******/" nl
;; 						(apply string-append (map code-gen side-effects-exprs (make-list len env-size) (make-list len param-size))) nl
;; 						(code-gen last env-size param-size) nl
;; 						"/*** End of seq ***/" nl nl))))))

(define cg-seq
	(lambda (e env-size param-size)
		(with e
			(lambda (seq lst)
				(let ((len (length lst)))
					(string-append
						"/******* seq *******/" nl
						(apply string-append (map code-gen lst (make-list len env-size) (make-list len param-size))) nl
						"/*** End of seq ***/" nl nl))))))
						
;; (define has-side-effect
;; 	(lambda (expr)
;; 		(or (recursive-contains expr 'set!)
;; 			(recursive-contains expr 'set-car!)
;; 			(recursive-contains expr 'set-cdr!)
;; 			(recursive-contains expr 'vector-set!)
;; 			(recursive-contains expr 'string-set!)
;; 			(recursive-contains expr 'display)
;; 			(recursive-contains expr 'write))))
;; 
;; (define recursive-contains
;; 	(lambda (lst e)
;; 		(if (contains lst e)
;; 			#t
;; 			(let ((sub-lsts (filter list? lst)))
;; 				(contains (map contains sub-lsts (make-list (length sub-lsts) e)) #t)))))


;;;;;;;;;;;; lambda-simple ;;;;;;;;;;

(define ^label-lambda-simple-body (^^label "L_Lambda_Simple_Body"))
(define ^label-lambda-simple-exit (^^label "L_Lambda_Simple_Exit"))
(define ^label-lambda-simple-loop1 (^^label "L_Lambda_Simple_loop1"))
(define ^label-lambda-simple-end-loop1 (^^label "L_Lambda_Simple_end_loop1"))
(define ^label-lambda-simple-loop2 (^^label "L_Lambda_Simple_loop2"))
(define ^label-lambda-simple-end-loop2 (^^label "L_Lambda_Simple_end_loop2"))

(define cg-lambda-simple
	(lambda (e env-size param-size)
		(with e
			(lambda (lambd params body)
				(let ((label-body (^label-lambda-simple-body))
					  (label_exit (^label-lambda-simple-exit))
					  (label-loop1 (^label-lambda-simple-loop1))
					  (label-end-loop1 (^label-lambda-simple-end-loop1))
					  (label-loop2 (^label-lambda-simple-loop2))
					  (label-end-loop2 (^label-lambda-simple-end-loop2))
					  (code-body (code-gen body (add1 env-size) (length params))))
				(string-append
					"/********** lambda-simple *********/" nl
					"MOV(R1, FPARG(0));				/* R1 = env */" nl
					"PUSH(IMM(" (number->string (add1 env-size)) "));" nl
					"CALL(MALLOC);" nl
					"DROP(1);" nl
					"MOV(R2, R0);					/* R2 = new env */" nl
					"/* copy last env */" nl
					"MOV(R3, IMM(0));" nl
					"MOV(R4, IMM(1));" nl
					label-loop1 ":" nl
					"	CMP(R3, IMM(" (number->string env-size) "));" nl
					"	JUMP_EQ(" label-end-loop1 ");" nl
					"	MOV(INDD(R2, R4), INDD(R1, R3));" nl
					"	INCR(R3);" nl
					"	INCR(R4);" nl
					"	JUMP(" label-loop1 ");" nl
					label-end-loop1 ":" nl
					"/* build env extension */" nl
					"MOV(R5, IMM(" (number->string (add1 param-size)) ")) 		/* R5 = param-size+1 */;" nl
					"PUSH(IMM(R5));" nl
					"CALL(MALLOC);" nl
					"DROP(1);						/* R0 = env extension */" nl
					"MOV(R4, IMM(0));" nl
					label-loop2 ":" nl
					"	CMP(R4, R5);" nl
					"	JUMP_EQ(" label-end-loop2 ");" nl
					"	MOV(INDD(R0, R4), FPARG(R4+2));" nl
					"	INCR(R4);" nl
					"	JUMP(" label-loop2 ");" nl
					label-end-loop2 ":" nl
					"MOV(IND(R2), R0);				/* new env[0] = env extension */" nl
					"PUSH(LABEL(" label-body "));	/* push code */" nl
					"PUSH(R2);						/* push new env */" nl
					"CALL(MAKE_SOB_CLOSURE);" nl
					"DROP(2);" nl
					"JUMP(" label_exit ");" nl
					label-body ":" nl
					"PUSH(FP);" nl
					"MOV(FP,SP);" nl
					code-body
					"POP(FP);" nl
					"RETURN;" nl
					label_exit ":" nl
					"/****** End of lambda-simple ******/" nl nl))))))


;;;;;;;;;;;;;; lambda-opt ;;;;;;;;;;;

(define ^label-lambda-opt-body (^^label "L_Lambda_opt_Body"))
(define ^label-lambda-opt-exit (^^label "L_Lambda_opt_Exit"))
(define ^label-lambda-opt-dont-fix (^^label "L_Lambda_opt_dont_fix"))
(define ^label-lambda-opt-loop1 (^^label "L_Lambda_opt_loop1"))
(define ^label-lambda-opt-end-loop1 (^^label "L_Lambda_opt_end_loop1"))
(define ^label-lambda-opt-loop2 (^^label "L_Lambda_opt_loop2"))
(define ^label-lambda-opt-end-loop2 (^^label "L_Lambda_opt_end_loop2"))
(define ^label-lambda-opt-loop3 (^^label "L_Lambda_opt_loop3"))
(define ^label-lambda-opt-end-loop3 (^^label "L_Lambda_opt_end_loop3"))
(define ^label-lambda-opt-loop4 (^^label "L_Lambda_opt_loop4"))
(define ^label-lambda-opt-end-loop4 (^^label "L_Lambda_opt_end_loop4"))

(define cg-lambda-opt
	(lambda (e env-size param-size)
		(with e
			(lambda (lambd params rest body)
				(let ((label-body (^label-lambda-opt-body))
					  (label_exit (^label-lambda-opt-exit))
					  (label-loop1 (^label-lambda-opt-loop1))
					  (label-end-loop1 (^label-lambda-opt-end-loop1))
					  (label-loop2 (^label-lambda-opt-loop2))
					  (label-end-loop2 (^label-lambda-opt-end-loop2))
					  (label-loop3 (^label-lambda-opt-loop3))
					  (label-end-loop3 (^label-lambda-opt-end-loop3))
					  (label-loop4 (^label-lambda-opt-loop4))
					  (label-end-loop4 (^label-lambda-opt-end-loop4))
					  (label-dont-fix (^label-lambda-opt-dont-fix))
					  (code-body (code-gen body (add1 env-size) (length params))))
				(string-append
					"/*********** lambda-opt ***********/" nl
					"MOV(R1, FPARG(0));				/* R1 = env */" nl
					"PUSH(IMM(" (number->string (add1 env-size)) "));" nl
					"CALL(MALLOC);" nl
					"DROP(1);" nl
					"MOV(R2, R0);					/* R2 = new env */" nl
					"/* copy last env */" nl
					"MOV(R3, IMM(0));" nl
					"MOV(R4, IMM(1));" nl
					label-loop1 ":" nl
					"	CMP(R3, IMM(" (number->string env-size) "));" nl
					"	JUMP_EQ(" label-end-loop1 ");" nl
					"	MOV(INDD(R2, R4), INDD(R1, R3));" nl
					"	INCR(R3);" nl
					"	INCR(R4);" nl
					"	JUMP(" label-loop1 ");" nl
					label-end-loop1 ":" nl
					"/* build env extension */" nl
					"MOV(R5, IMM(" (number->string (add1 param-size)) ")) 		/* R5 = param-size+1 */;" nl
					"PUSH(IMM(R5));" nl
					"CALL(MALLOC);" nl
					"DROP(1);						/* R0 = env extension */" nl
					"MOV(R4, IMM(0));" nl
					label-loop2 ":" nl
					"	CMP(R4, R5);" nl
					"	JUMP_EQ(" label-end-loop2 ");" nl
					"	MOV(INDD(R0, R4), FPARG(R4+2));" nl
					"	INCR(R4);" nl
					"	JUMP(" label-loop2 ");" nl
					label-end-loop2 ":" nl
					"MOV(IND(R2), R0);				/* new env[0] = env extension */" nl
					"PUSH(LABEL(" label-body "));	/* push code */" nl
					"PUSH(R2);						/* push new env */" nl
					"CALL(MAKE_SOB_CLOSURE);" nl
					"DROP(2);" nl
					"JUMP(" label_exit ");" nl
					label-body ":" nl
					"PUSH(FP);" nl
					"MOV(FP,SP);" nl
					"/* fixing the stack so that optional args are packed in a list */" nl
					"MOV(R1, FPARG(0));		/* R1 = env */" nl
					"MOV(R2, FPARG(-1));	/* R2 = ret */" nl
					"MOV(R3, FPARG(-2));	/* R3 = old FP */" nl
					"MOV(R4, FPARG(1));		/* R4 = n */" nl
					"MOV(R5, IMM(" (number->string (length params)) "));		/* R5 = |params| */" nl
					"/* if there are no optional args, don't fix stack */" nl
					"CMP(R4, R5);" nl
					"JUMP_EQ(" label-dont-fix ");" nl
					"/* pack optional args in a list */" nl
					"PUSH(IMM(SOB_NIL));" nl
					"PUSH(FPARG(R4+1));" nl
					"CALL(MAKE_SOB_PAIR);" nl
					"DROP(2);" nl
					"INCR(R5);				/* R5 = |params|+1 */" nl
					label-loop3 ":" nl
					"	CMP(R4, R5);" nl
					"	JUMP_EQ(" label-end-loop3 ");" nl
					"	PUSH(R0);" nl
					"	PUSH(FPARG(R4));" nl
					"	CALL(MAKE_SOB_PAIR);" nl
					"	DROP(2);" nl
					"	DECR(R4);" nl
					"	JUMP(" label-loop3 ");" nl
					label-end-loop3 ":" nl
					"MOV(R4, FPARG(1));		/* R4 = n */" nl
					"MOV(FPARG(R4+2), R0);" nl
					"/* fix the stack */" nl
					"INCR(R4);" nl
					label-loop4 ":" nl
					"	CMP(R5, IMM(1));" nl
					"	JUMP_EQ(" label-end-loop4 ");" nl
					"	MOV(FPARG(R4), FPARG(R5));" nl
					"	DECR(R4);" nl
					"	DECR(R5);" nl
					"	JUMP(" label-loop4 ");" nl
					label-end-loop4 ":" nl
					"MOV(R4, FPARG(1));" nl
					"SUB(R4, IMM(" (number->string (length params)) "));			/* R4 = n-|params| */" nl
					"ADD(R4, IMM(4));" nl
					"DROP(R4);" nl
					"PUSH(IMM(" (number->string (length params)) ")); 	/* push |params| */" nl
					"PUSH(R1);		/* push env */" nl
					"PUSH(R2);		/* push ret */" nl
					"PUSH(R3);		/* push old FP */" nl
					"MOV(FP, SP);" nl
					label-dont-fix ":" nl
					"/* lambda body */" nl
					code-body nl
					"POP(FP);" nl
					"RETURN;" nl
					label_exit ":" nl
					"/******** End of lambda-opt *******/" nl nl))))))


;;;;;;;;;;; lambda-variadic ;;;;;;;;;

(define ^label-lambda-variadic-body (^^label "L_Lambda_variadic_Body"))
(define ^label-lambda-variadic-exit (^^label "L_Lambda_variadic_Exit"))
(define ^label-lambda-variadic-dont-fix (^^label "L_Lambda_variadic_dont_fix"))
(define ^label-lambda-variadic-loop1 (^^label "L_Lambda_variadic_loop1"))
(define ^label-lambda-variadic-end-loop1 (^^label "L_Lambda_variadic_end_loop1"))
(define ^label-lambda-variadic-loop2 (^^label "L_Lambda_variadic_loop2"))
(define ^label-lambda-variadic-end-loop2 (^^label "L_Lambda_variadic_end_loop2"))
(define ^label-lambda-variadic-loop3 (^^label "L_Lambda_variadic_loop3"))
(define ^label-lambda-variadic-end-loop3 (^^label "L_Lambda_variadic_end_loop3"))

(define cg-lambda-variadic
	(lambda (e env-size param-size)
		(with e
			(lambda (lambd args body)
				(let ((label-body (^label-lambda-variadic-body))
					  (label_exit (^label-lambda-variadic-exit))
					  (label-loop1 (^label-lambda-variadic-loop1))
					  (label-end-loop1 (^label-lambda-variadic-end-loop1))
					  (label-loop2 (^label-lambda-variadic-loop2))
					  (label-end-loop2 (^label-lambda-variadic-end-loop2))
					  (label-loop3 (^label-lambda-variadic-loop3))
					  (label-end-loop3 (^label-lambda-variadic-end-loop3))
					  (label-dont-fix (^label-lambda-variadic-dont-fix))
					  (code-body (code-gen body (add1 env-size) 0)))
				(string-append
					"/********* lambda-variadic ********/" nl
					"MOV(R1, FPARG(0));				/* R1 = env */" nl
					"PUSH(IMM(" (number->string (add1 env-size)) "));" nl
					"CALL(MALLOC);" nl
					"DROP(1);" nl
					"MOV(R2, R0);					/* R2 = new env */" nl
					"/* copy last env */" nl
					"MOV(R3, IMM(0));" nl
					"MOV(R4, IMM(1));" nl
					label-loop1 ":" nl
					"	CMP(R3, IMM(" (number->string env-size) "));" nl
					"	JUMP_EQ(" label-end-loop1 ");" nl
					"	MOV(INDD(R2, R4), INDD(R1, R3));" nl
					"	INCR(R3);" nl
					"	INCR(R4);" nl
					"	JUMP(" label-loop1 ");" nl
					label-end-loop1 ":" nl
					"/* build env extension */" nl
					"MOV(R5, IMM(" (number->string (add1 param-size)) ")) 		/* R5 = param-size+1 */;" nl
					"PUSH(IMM(R5));" nl
					"CALL(MALLOC);" nl
					"DROP(1);						/* R0 = env extension */" nl
					"MOV(R4, IMM(0));" nl
					label-loop2 ":" nl
					"	CMP(R4, R5);" nl
					"	JUMP_EQ(" label-end-loop2 ");" nl
					"	MOV(INDD(R0, R4), FPARG(R4+2));" nl
					"	INCR(R4);" nl
					"	JUMP(" label-loop2 ");" nl
					label-end-loop2 ":" nl
					"MOV(IND(R2), R0);				/* new env[0] = env extension */" nl
					"PUSH(LABEL(" label-body "));	/* push code */" nl
					"PUSH(R2);						/* push new env */" nl
					"CALL(MAKE_SOB_CLOSURE);" nl
					"DROP(2);" nl
					"JUMP(" label_exit ");" nl
					label-body ":" nl
					"PUSH(FP);" nl
					"MOV(FP,SP);" nl
					"/* fixing the stack so that all args are packed in a list */" nl
					"MOV(R1, FPARG(1));		/* R1 = n */" nl
					"MOV(R2, FPARG(0));		/* R2 = env */" nl
					"MOV(R3, FPARG(-1));		/* R3 = ret */" nl
					"MOV(R4, FPARG(-2));		/* R5 = old FP */" nl
					"/* if there are no args, don't fix stack */" nl
					"CMP(R1, IMM(0));" nl
					"JUMP_EQ(" label-dont-fix ");" nl
					"/* else, pack args in a list */" nl
					"PUSH(IMM(SOB_NIL));" nl
					"PUSH(FPARG(R1+1));" nl
					"CALL(MAKE_SOB_PAIR);" nl
					"DROP(2);" nl
					"MOV(R5, R1);" nl
					label-loop3 ":" nl
					"	CMP(R5, IMM(1));" nl
					"	JUMP_EQ(" label-end-loop3 ");" nl
					"	PUSH(R0);			/* cdr */" nl
					"	PUSH(FPARG(R5));	/* car */" nl
					"	CALL(MAKE_SOB_PAIR);" nl
					"	DROP(2);" nl
					"	DECR(R5);" nl
					"	JUMP(" label-loop3 ");" nl
					label-end-loop3 ":" nl
					"MOV(FPARG(R1+2), R0);" nl
					"DROP(R1+4);" nl
					"PUSH(IMM(0));" nl
					"PUSH(R2);" nl
					"PUSH(R3);" nl
					"PUSH(R4);" nl
					"MOV(FP, SP);" nl
					label-dont-fix ":" nl
					"/* lambda body */" nl
					code-body nl
					"POP(FP);" nl
					"RETURN;" nl
					label_exit ":" nl
					"/***** End of lambda-variadic *****/" nl nl))))))


;;;;;;;;;;;;;;; applic ;;;;;;;;;;;;;;

(define ^label-applic-not-closure (^^label "L_Applic_Not_a_Closure"))
(define ^label-applic-exit (^^label "L_Applic_Exit"))
(define ^label-applic-push-proc-env (^^label "L_Applic_push_env"))
(define ^label-applic-push-continue (^^label "L_Applic_continue"))

(define cg-applic
	(lambda (e env-size param-size)
		(with e
			(lambda (applic proc args)
				(let ((label-not-closure (^label-applic-not-closure))
					  (label-exit (^label-applic-exit))
					  (label-push-proc-env (^label-applic-push-proc-env))
					  (label-push-continue (^label-applic-push-continue))
					  (len (length args)))
					(string-append
						"/************* applic *************/" nl
						"PUSH(IMM(SOB_NIL));		/* push magic arg */" nl
						(push-args (reverse args) env-size param-size)
						"PUSH(IMM(" (number->string len) "));	/* push n */" nl
						(code-gen proc env-size param-size) nl
						"CMP(IND(R0),IMM(T_CLOSURE));" nl
						"JUMP_NE(" label-not-closure ");" nl
						"CMP(INDD(R0, 1), IMM(5555555));" nl
						"JUMP_NE(" label-push-proc-env ");" nl
						"PUSH(FPARG(0));					/* push old env */" nl
						"JUMP(" label-push-continue ");" nl
						label-push-proc-env ":" nl
						"PUSH(INDD(R0, 1));			/* push proc env */" nl
						label-push-continue ":" nl
						"CALL(*INDD(R0,2));" nl
						"DROP(STARG(0)+3);" nl
						"JUMP(" label-exit ");" nl
						label-not-closure ":" nl
						"	printf(\"In applic: proc %ld is not a closure!\\n\", R0);" nl
						"	JUMP(L_exit);" nl
						label-exit ":" nl
						"/********* End of applic *********/" nl nl))))))

(define push-args
	(lambda (args env-size param-size)
		(if (null? args)
			""
			(string-append
				(code-gen (car args) env-size param-size)
				"PUSH(R0);" nl
				(push-args (cdr args) env-size param-size)))))

				
;;;;;;;;;;;;; applic-tc ;;;;;;;;;;;;;

(define ^label-applic-tc-not-closure (^^label "L_Applic_tc_Not_a_Closure"))
(define ^label-applic-tc-loop (^^label "L_Applic_tc_Loop"))
(define ^label-applic-tc-continue (^^label "L_Applic_tc_Continue"))
(define ^label-applic-tc-proc-env (^^label "L_Applic_tc_push_env"))
(define ^label-applic-tc-ret-addr (^^label "L_Applic_tc_push_ret"))

(define cg-applic-tc
	(lambda (e env-size param-size)
		(with e
			(lambda (applic proc args)
				(let ((label-not-closure (^label-applic-tc-not-closure))
					  (label-loop (^label-applic-tc-loop))
					  (label-continue (^label-applic-tc-continue))
					  (label-push-proc-env (^label-applic-tc-proc-env))
					  (label-push-ret-addr (^label-applic-tc-ret-addr))
					  (len (length args)))
					(string-append
						"/************ applic-tc ************/" nl
						"PUSH(IMM(SOB_NIL));		/* push magic arg */" nl
						(push-args (reverse args) env-size param-size)
						"PUSH(IMM(" (number->string len) "));	/* push n */" nl
						(code-gen proc env-size param-size) nl
						"CMP(IND(R0),IMM(T_CLOSURE));" nl
						"JUMP_NE(" label-not-closure ");" nl
						"/* override old frame */" nl
						"MOV(R1, FPARG(-2));		/* R1 = old FP */" nl
						"MOV(R2, FPARG(-1));		/* R2 = return address */" nl
						"MOV(R3, FPARG(0));		/* R3 = env */" nl
						"MOV(R4, FPARG(1));		/* R4 = n */" nl
						"MOV(R5, IMM(0));" nl
						label-loop ":" nl
						"	CMP(R5, IMM(" (number->string (+ 2 len)) "));" nl
						"	JUMP_EQ(" label-continue ");" nl
						"	MOV(STACK(FP-R4-5+R5), LOCAL(R5));" nl
						"	INCR(R5);" nl
						"	JUMP(" label-loop ");" nl
						label-continue ":" nl
						"ADD(R4, IMM(5));" nl
						"DROP(R4);					/* drop new args */" nl
						"CMP(INDD(R0, 1), IMM(5555555));" nl
						"JUMP_NE(" label-push-proc-env ");" nl
						"PUSH(R3);					/* push old env */" nl
						"JUMP(" label-push-ret-addr ");" nl
						label-push-proc-env ":" nl
						"PUSH(INDD(R0, 1));			/* push proc env */" nl
						label-push-ret-addr ":" nl
						"PUSH(R2);			/* push return address */" nl
						"MOV(FP, R1);		/* FP = old FP */" nl
						"JUMP(*INDD(R0,2));" nl
						label-not-closure ":" nl
						"	printf(\"In applic-tc: proc %ld is not a closure!\\n\", R0);" nl
						"	JUMP(L_exit);" nl
						"/******** End of applic-tc ********/" nl nl))))))


;;;;;;;;;;;;;;; define ;;;;;;;;;;;;;;

(define cg-define
	(lambda (e env-size param-size)
		(with e
			(lambda (def var val)
				(string-append
					"/******* define *******/" nl
					(code-gen val env-size param-size) nl
					"MOV(IND(" (number->string (find-address (cadr var) *fvar-table*)) "), R0);" nl
					"MOV(R0, SOB_VOID);" nl
					"/*** End of define ***/" nl)))))
				