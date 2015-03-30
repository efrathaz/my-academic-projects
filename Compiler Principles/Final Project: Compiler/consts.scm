(define T_UNDEF 937611)
(define T_VOID 937610)
(define T_NIL 722689)
(define T_BOOL 741553)
(define T_CHAR 181048)
(define T_INTEGER 945311)
(define T_STRING 799345)
(define T_SYMBOL 368031)
(define T_PAIR 885397)
(define T_VECTOR 335728)

(define nl (list->string (list #\newline)))

(define *constant-table* '())
						   
(define initialize-const-table
	(lambda()
		(set! *constant-table* `((1 undef ,T_UNDEF)
								 (2 ,(make-void) ,T_VOID)
								 (3 ,'() ,T_NIL) 
								 (4 ,#f ,T_BOOL 0) 
								 (6 ,#t ,T_BOOL 1)))))

; return the next available address in the table
(define find-next-addr
	(lambda (table)
		(if (null? table)
			(find-next-addr *constant-table*)
			(let ((last (car (reverse table))))
				(+ (car last) (length (cddr last)))))))

(define build-const-table
	(lambda (sexprs)
		(map find-symbols sexprs)))
		
		
(define find-symbols
	(lambda (e)
		(if (not (or (null? e) (not (list? e))))
			(if (equal? (car e) 'const)
				(consts-foo (cadr e))
				(map find-symbols e)))))

(define consts-foo
	(lambda (e)
		(let ((found (find-address e *constant-table*))
			  (next-available-address (find-next-addr *constant-table*)))
			(cond (found found)
				  ((number? e) (add-const (list next-available-address e T_INTEGER e)))
				  ((char? e) (add-const (list next-available-address e T_CHAR (char->integer e))))
				  ((string? e) (create-string-const e next-available-address))
				  ((symbol? e) (create-sym-const e))
				  ((pair? e) (create-pair-const e))
				  ((vector? e) (create-vector-const e))
				  (else (error 'consts-foo "error in consts-foo"))))))

(define create-string-const
	(lambda (e next-available-address)
		(add-const (append (list next-available-address e T_STRING (string-length e)) (map char->integer (string->list e))))))

(define create-sym-const
	(lambda (e)
		(let ((name (consts-foo (symbol->string e))))
			(add-const (list (find-next-addr *constant-table*) e T_SYMBOL name)))))
				
(define create-pair-const
	(lambda (e)
		(let ((a (consts-foo (car e)))
			  (b (consts-foo (cdr e))))
			(add-const (list (find-next-addr *constant-table*) e T_PAIR a b)))))

(define create-vector-const
	(lambda (e)
		(let ((addresses (map consts-foo (vector->list e))))
			(add-const (append (list (find-next-addr *constant-table*) e T_VECTOR (length addresses)) addresses)))))
		
			
; if the const is in the table, return its address, otherwise retun false
(define find-address
	(lambda (const table)
		(if (null? table)
			#f
			(if (equal? const (cadar table))
				(caar table)
				(find-address const (cdr table))))))

; add const to the constant table and return its address				
(define add-const
	(lambda (const)
		(set! *constant-table* (append *constant-table* (list const)))
		(car const)))

(define create-consts-array
	(lambda ()
		(let ((array (apply append (map cddr *constant-table*))))
			(string-append
				"	/***************** create constants *****************/" nl
				"	long consts[" (number->string (length array)) "] = {"
				(apply string-append (cdr (apply append 
					(map (lambda (x) `("," ,(number->string x))) array))))
				"};" nl
				"	PUSH(IMM(" (number->string (length array)) "));" nl
				"	CALL(MALLOC);" nl
				"	DROP(1);" nl
				"	MOV(R2, IMM(0));" nl
				"	L_constants_loop:" nl
				"		CMP(R2, IMM(" (number->string (length array)) "));" nl
				"		JUMP_EQ(L_constants_end_loop);" nl
				"		MOV(INDD(R0, R2), IMM(consts[R2]));" nl
				"		INCR(R2);" nl
				"		JUMP(L_constants_loop);" nl
				"	L_constants_end_loop:" nl nl))))
