
(define prim-apply
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_apply:" nl
				"PUSH(FP);" nl
				"MOV(FP, SP);" nl
				"MOV(R1, FPARG(0));		/* R1 = env */" nl
				"MOV(R2, FPARG(-1));		/* R2 = return address */" nl
				"MOV(R3, FPARG(-2));		/* R3 = old fp */" nl
				"MOV(R4, FPARG(2));		/* R4 = proc */" nl
				"MOV(R5, FPARG(3));		/* R5 = args */" nl
				"MOV(R6, IMM(0));		/* R6 = args length */" nl
				"CMP(R5, IMM(SOB_NIL));" nl
				"JUMP_EQ(L_Apply_continue1);" nl
				"CMP(IND(R5), IMM(T_PAIR));" nl
				"JUMP_NE(L_Apply_error);" nl
				"/* count number of args */" nl
				"L_Apply_Loop1:" nl
				"	CMP(R5, SOB_NIL);" nl
				"	JUMP_EQ(L_Apply_continue1);" nl
				"	MOV(R5, INDD(R5, 2));		/* R5 = cdr R5 */" nl
				"	ADD(R6, IMM(1));" nl
				"	JUMP(L_Apply_Loop1);" nl
				"L_Apply_continue1:" nl
				"MOV(R5, FPARG(3));		/* R5 = args */" nl
				"/* override stack frame */" nl
				"MOV(R7, R6);" nl
				"DECR(R7);		/* R7 = n-1 */" nl
				"L_Apply_Loop2:" nl
				"	CMP(R7, IMM(0));" nl
				"	JUMP_LT(L_Apply_continue2);" nl
				"	MOV(STACK(SP-6+R7), INDD(R5, 1));" nl
				"	MOV(R5, INDD(R5, 2));" nl
				"	DECR(R7);" nl
				"	JUMP(L_Apply_Loop2);" nl
				"L_Apply_continue2:" nl
				"SUB(SP, IMM(6));" nl
				"ADD(SP, R6);" nl
				"PUSH(R6);			/* push n */" nl
				"CMP(INDD(R4, 1), IMM(5555555));" nl
				"JUMP_NE(L_Apply_proc_env);" nl
				"PUSH(R1);			/* push old env */" nl
				"JUMP(L_Apply_push_ret);" nl
				"L_Apply_proc_env:" nl
				"PUSH(INDD(R4, 1));			/* push proc env */" nl
				"L_Apply_push_ret:" nl
				"PUSH(R2);			/* push ret */" nl
				"MOV(FP, R3);		/* FP = old FP */" nl
				"/* apply proc with its arguments */" nl
				"JUMP(*INDD(R4, 2));" nl
				"L_Apply_error:" nl
				"	printf(\"Error in apply: second argument is not a list\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_apply));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

(define prim-plus
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_plus:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(1));		/* R1 = n */" nl
				"MOV(R0, IMM(0));" nl
				"MOV(R2, IMM(0));" nl
				"L_prim_plus_loop:" nl
				"	CMP(R2, IMM(R1));" nl
				"	JUMP_EQ(L_prim_plus_continue);" nl
				"	ADD(R0, INDD(FPARG(R2+2), 1));" nl
				"	INCR(R2);" nl
				"	JUMP(L_prim_plus_loop);" nl
				"L_prim_plus_continue:" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_plus));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-minus
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_minus:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(1));		/* R1 = n */" nl
				"CMP(R1, IMM(1));" nl
				"JUMP_EQ(L_prim_minus_one_arg);" nl
				"MOV(R0, INDD(FPARG(2), 1));" nl
				"MOV(R2, IMM(1));" nl
				"L_prim_minus_loop:" nl
				"	CMP(R2, IMM(R1));" nl
				"	JUMP_EQ(L_prim_minus_continue);" nl
				"	SUB(R0, INDD(FPARG(R2+2), 1));" nl
				"	INCR(R2);" nl
				"	JUMP(L_prim_minus_loop);" nl
				
				"L_prim_minus_one_arg:" nl
				"MOV(R0, IMM(0));" nl
				"SUB(R0, INDD(FPARG(2), 1));" nl
				
				"L_prim_minus_continue:" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_minus));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-mult
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_mult:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(1));		/* R1 = n */" nl
				"MOV(R0, IMM(1));" nl
				"MOV(R2, IMM(0));" nl
				"L_prim_mult_loop:" nl
				"	CMP(R2, R1);" nl
				"	JUMP_EQ(L_prim_mult_continue);" nl
				"	MUL(R0, INDD(FPARG(R2+2), 1));" nl
				"	INCR(R2);" nl
				"	JUMP(L_prim_mult_loop);" nl
				"L_prim_mult_continue:" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_mult));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-div
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_div:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(1));		/* R1 = n */" nl
				"CMP(R1, IMM(1));" nl
				"JUMP_EQ(L_prim_div_one_arg);" nl
				"MOV(R0, INDD(FPARG(2), 1));" nl
				"MOV(R2, IMM(1));" nl
				"L_prim_div_loop:" nl
				"	CMP(R2, IMM(R1));" nl
				"	JUMP_EQ(L_prim_div_continue);" nl
				"	DIV(R0, INDD(FPARG(R2+2), 1));" nl
				"	INCR(R2);" nl
				"	JUMP(L_prim_div_loop);" nl
				
				"L_prim_div_one_arg:" nl
				"MOV(R0, IMM(1));" nl
				"DIV(R0, INDD(FPARG(2), 1));" nl
				
				"L_prim_div_continue:" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_div));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-bin<?
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_binary_less_than:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));		/* R0 = arg0 */" nl
				"MOV(R1, FPARG(3));		/* R1 = arg1 */" nl
				"MOV(R0, INDD(R0, 1));" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R0, R1);			/* if arg0 >= arg1, return false */" nl
				"JUMP_GE(L_prim_less_than_false);" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_prim_less_than_exit);" nl
				"L_prim_less_than_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_prim_less_than_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_binary_less_than));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

(define prim-bin>?
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_binary_greater_than:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));		/* R0 = arg0 */" nl
				"MOV(R1, FPARG(3));		/* R1 = arg1 */" nl
				"MOV(R0, INDD(R0, 1));" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R0, R1);			/* if arg0 <= arg1, return false */" nl
				"JUMP_LE(L_prim_greater_than_false);" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_prim_greater_than_exit);" nl
				"L_prim_greater_than_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_prim_greater_than_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_binary_greater_than));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-bin=?
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_binary_equal:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));		/* R0 = num1 */" nl
				"MOV(R1, FPARG(3));		/* R1 = num2 */" nl
				"MOV(R0, INDD(R0, 1));" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R0, R1);" nl
				"JUMP_NE(L_prim_binary_equal_false);" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_prim_binary_equal_exit);" nl
				"L_prim_binary_equal_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_prim_binary_equal_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_binary_equal));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
				
(define prim-equal
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_equal:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(1));" nl
				"DECR(R1);		/* R1 = n-1 */" nl
				"MOV(R2, IMM(0));" nl
				"L_prim_equal_loop:" nl
				"	CMP(R2, R1);" nl
				"	JUMP_EQ(L_prim_equal_true);" nl
				"	CMP(INDD(FPARG(R2+2), 1), INDD(FPARG(R2+3), 1));" nl
				"	JUMP_NE(L_prim_equal_false);" nl
				"	INCR(R2);" nl
				"	JUMP(L_prim_equal_loop);" nl
				"L_prim_equal_true:" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_prim_equal_exit);" nl
				"L_prim_equal_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_prim_equal_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_equal));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

(define prim-remainder
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_remainder:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/*  */" nl
				"MOV(R1, FPARG(3));		/* num */" nl
				"CMP(IND(R0), IMM(T_INTEGER));" nl
				"JUMP_NE(L_remainder_error);" nl
				"CMP(IND(R1), IMM(T_INTEGER));" nl
				"JUMP_NE(L_remainder_error);" nl
				"MOV(R0, INDD(R0, 1));" nl
				"MOV(R1, INDD(R1, 1));" nl
				"REM(R0, R1);" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"JUMP(L_remainder_exit);" nl
				"L_remainder_error:" nl
				"	printf(\"Error in remainder: arg is not a number\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_remainder_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_remainder));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-cons
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_cons:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"PUSH(FPARG(3));	/* push cdr */" nl
				"PUSH(FPARG(2));	/* push car */" nl
				"CALL(MAKE_SOB_PAIR);" nl
				"DROP(2);" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_cons));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-car
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_car:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));" nl
				"CMP(IND(R0), IMM(T_PAIR));" nl
				"JUMP_NE(L_car_error);" nl
				"MOV(R0, INDD(R0, 1));" nl
				"JUMP(L_car_exit);" nl
				"L_car_error:" nl
				"	printf(\"Error in car: arg %ld is not a pair\\n\", R0);" nl
				"	JUMP(L_exit);" nl
				"L_car_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_car));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-cdr
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_cdr:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));" nl
				"CMP(IND(R0), IMM(T_PAIR));" nl
				"JUMP_NE(L_cdr_error);" nl
				"MOV(R0, INDD(R0, 2));" nl
				"JUMP(L_cdr_exit);" nl
				"L_cdr_error:" nl
				"	printf(\"Error in cdr: arg is not a pair\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_cdr_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_cdr));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-set-car!
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_set_car:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* pair */" nl
				"MOV(R1, FPARG(3));			/* object */" nl
				"CMP(IND(R0), IMM(T_PAIR));" nl
				"JUMP_NE(L_set_car_error);" nl
				"MOV(INDD(R0, 1), R1);" nl
				"MOV(R0, SOB_VOID);" nl
				"JUMP(L_set_car_exit);" nl
				"L_set_car_error:" nl
				"	printf(\"Error in set-car!: arg is not a pair\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_set_car_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_set_car));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-set-cdr!
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_set_cdr:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* pair */" nl
				"MOV(R1, FPARG(3));			/* object */" nl
				"CMP(IND(R0), IMM(T_PAIR));" nl
				"JUMP_NE(L_set_cdr_error);" nl
				"MOV(INDD(R0, 2), R1);" nl
				"MOV(R0, SOB_VOID);" nl
				"JUMP(L_set_cdr_exit);" nl
				"L_set_cdr_error:" nl
				"	printf(\"Error in set-cdr!: arg is not a pair\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_set_cdr_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_set_cdr));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

; for the procedures: boolean? number? char? pair? procedure? string? symbol? vector? null?
(define prim-question
	(lambda (address type macro)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_" type ":" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));" nl
				"CMP(IND(R0), " macro ");" nl
				"JUMP_NE(L_" type "_false);" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_" type "_exit);" nl
				"L_" type "_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_" type "_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_" type "));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-zero?
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_zero:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));" nl
				"CMP(IND(R0), IMM(T_INTEGER));" nl
				"JUMP_NE(L_zero_false);" nl
				"CMP(INDD(R0, 1), IMM(0));" nl
				"JUMP_NE(L_zero_false);" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_zero_exit);" nl
				"L_zero_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_zero_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_zero));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

(define prim-make-string
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_make_string:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(2));" nl
				"MOV(R1, INDD(R1,1));		/* num */" nl
				"MOV(R3, R1);" nl
				"CMP(FPARG(1), IMM(1));" nl
				"JUMP_EQ(L_make_string_no_char);"
				"MOV(R2, FPARG(3));			/* char */" nl
				"CMP(IND(R2), IMM(T_CHAR));" nl
				"JUMP_NE(L_make_string_error);" nl
				"MOV(R2, INDD(R2, 1));" nl
				"JUMP(L_make_string_loop);" nl
				"L_make_string_no_char:"
				"MOV(R2, IMM(0));" nl
				"L_make_string_loop:" nl
				"	CMP(R3, IMM(0));" nl
				"	JUMP_EQ(L_make_string_continue);" nl
				"	PUSH(R2);" nl
				"	DECR(R3);" nl
				"	JUMP(L_make_string_loop);" nl
				"L_make_string_continue:" nl
				"PUSH(R1);" nl
				"CALL(MAKE_SOB_STRING);" nl
				"INCR(R1);" nl
				"DROP(R1);" nl
				"JUMP(L_make_string_exit);" nl
				"L_make_string_error:" nl
				"	printf(\"Error in make-string: arg is not a char\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_make_string_exit:"
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_make_string));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-make-vector
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_make_vector:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(2));" nl
				"MOV(R1, INDD(R1,1));	/* num */" nl
				"MOV(R3, R1);" nl
				"CMP(FPARG(1), IMM(2));" nl
				"JUMP_NE(L_make_vector_zeros);" nl
				"MOV(R2, FPARG(3));" nl
				"JUMP(L_make_vector_loop);" nl
				"L_make_vector_zeros:" nl
				"PUSH(IMM(0));" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"MOV(R2, R0);" nl
				"L_make_vector_loop:" nl
				"	CMP(R3, IMM(0));" nl
				"	JUMP_EQ(L_make_vector_continue);" nl
				"	PUSH(R2);" nl
				"	DECR(R3);" nl
				"	JUMP(L_make_vector_loop);" nl
				"L_make_vector_continue:" nl
				"PUSH(R1);" nl
				"CALL(MAKE_SOB_VECTOR);" nl
				"INCR(R1);" nl
				"DROP(R1);" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_make_vector));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-char->integer
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_char_integer:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(2));" nl
				"CMP(IND(R1), IMM(T_CHAR));" nl
				"JUMP_NE(L_char_integer_error);" nl
				"MOV(R1, INDD(R1,1));		/* char value */" nl
				"PUSH(R1);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"JUMP(L_char_integer_exit);" nl
				"L_char_integer_error:" nl
				"	printf(\"Error in char->integer: arg is not a char\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_char_integer_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_char_integer));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-integer->char
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_integer_char:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R1, FPARG(2));" nl
				"CMP(IND(R1), IMM(T_INTEGER));" nl
				"JUMP_NE(L_integer_char_error);" nl
				"MOV(R1, INDD(R1,1));		/* integer value */" nl
				"PUSH(R1);" nl
				"CALL(MAKE_SOB_CHAR);" nl
				"DROP(1);" nl
				"JUMP(L_integer_char_exit);" nl
				"L_integer_char_error:" nl
				"	printf(\"Error in char->integer: arg is not an integer\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_integer_char_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_integer_char));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-string->symbol
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_string_symbol:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R4, FPARG(2));		/* R4 = pointer to string object */" nl
				"CMP(IND(R4), IMM(T_STRING));" nl
				"JUMP_NE(L_string_symbol_error);" nl
				"MOV(R1, SYMBOLS_START);		/* R1 = pointer to bucket (link in the linked-list) */" nl
				"MOV(R5, IMM("(number->string (find-address 'binary-string=? *fvar-table*))"));" nl
				"MOV(R5, IND(R5));						/* R5 = binary-string=? */" nl
				"L_string_symbol_loop:" nl
				"	CMP(R1, SYMBOLS_LAST);				/* check if we are at the end of the linked-list */" nl
				"	JUMP_EQ(L_string_symbol_not_found);" nl
				"	MOV(R2, IND(R1))				/* R2 = pointer to symbol */;" nl
				"	/* compare strings */" nl
				"	PUSH(R5);" nl
				"	PUSH(R4);" nl
				"	PUSH(R2);" nl
				"	PUSH(R1);" nl
				"	PUSH(SOB_NIL);" nl
				"	PUSH(R4);" nl
				"	PUSH(INDD(R2, 1));" nl
				"	PUSH(IMM(2));" nl
				"	PUSH(INDD(R5, 1))" nl
				"	CALL(*INDD(R5, 2));" nl
				"	DROP(STARG(0)+3);" nl
				"	POP(R1);" nl
				"	POP(R2);" nl
				"	POP(R4);" nl
				"	POP(R5);" nl
				"	CMP(INDD(R0, 1), IMM(1));" nl
				"	JUMP_EQ(L_string_symbol_found);" nl
				"	MOV(R1, INDD(R1, 1));			/* R1 = next bucket */" nl
				"	JUMP(L_string_symbol_loop);" nl
				"L_string_symbol_found:" nl
				"	MOV(R0, R2);" nl
				"	JUMP(L_string_symbol_exit);" nl
				"L_string_symbol_not_found:" nl
				"	PUSH(R4);					/* R3 = pointer to string */" nl
				"	CALL(MAKE_SOB_SYMBOL);" nl
				"	DROP(1);" nl
				"	MOV(R2, R0);				/* R2 = new symbol */" nl
				"	/* create new bucket and set it to be SYMBOLS_LAST */" nl
				"	PUSH(IMM(2));" nl
				"	CALL(MALLOC);" nl
				"	DROP(1);" nl
				"	MOV(IND(SYMBOLS_LAST), R2);" nl
				"	MOV(INDD(SYMBOLS_LAST, 1), R0);" nl
				"	MOV(SYMBOLS_LAST, R0);" nl
				"	MOV(IND(SYMBOLS_LAST), SOB_VOID);" nl
				"	MOV(INDD(SYMBOLS_LAST, 1), SOB_NIL);" nl
				"	MOV(R0, R2);" nl
				"	JUMP(L_string_symbol_exit);" nl
				"L_string_symbol_error:" nl
				"	printf(\"Error in string->symbol: arg is not a string\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_string_symbol_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_string_symbol));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-symbol->string
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_symbol_string:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));		/* R1 = pointer to string object */" nl
				"CMP(IND(R0), IMM(T_SYMBOL));" nl
				"JUMP_NE(L_symbol_string_error);" nl
				"MOV(R0, INDD(R0, 1));" nl
				"JUMP(L_symbol_string_exit);" nl
				"L_symbol_string_error:" nl
				"	printf(\"Error in symbol->string: arg is not a symbol\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_symbol_string_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_symbol_string));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-string-length
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_string_length:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));		/* R0 = pointer to string object */" nl
				"CMP(IND(R0), IMM(T_STRING));" nl
				"JUMP_NE(L_string_length_error);" nl
				"MOV(R0, INDD(R0, 1));	/* R0 = n */" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"JUMP(L_string_length_exit);" nl
				"L_string_length_error:" nl
				"	printf(\"Error in string-length: arg is not a string\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_string_length_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_string_length));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

(define prim-vector-length
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_vector_length:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));		/* R0 = pointer to vector object */" nl
				"CMP(IND(R0), IMM(T_VECTOR));" nl
				"JUMP_NE(L_vector_length_error);" nl
				"MOV(R0, INDD(R0, 1));	/* R0 = n */" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_INTEGER);" nl
				"DROP(1);" nl
				"JUMP(L_vector_length_exit);" nl
				"L_vector_length_error:" nl
				"	printf(\"Error in vector-length: arg is not a vector\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_vector_length_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_vector_length));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-string-ref
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_string_ref:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* R0 = pointer to string object */" nl
				"MOV(R1, FPARG(3));			/* R1 = index */" nl
				"CMP(IND(R0), IMM(T_STRING));" nl
				"JUMP_NE(L_string_ref_error);" nl
				"CMP(IND(R1), IMM(T_INTEGER));" nl
				"JUMP_NE(L_string_ref_error);" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R1, INDD(R0, 1));		/* check if index < n */" nl
				"JUMP_GE(L_string_ref_error);" nl
				"ADD(R1, IMM(2));"
				"MOV(R0, INDD(R0, R1));" nl
				"PUSH(R0);" nl
				"CALL(MAKE_SOB_CHAR);" nl
				"DROP(1);" nl
				"JUMP(L_string_ref_exit);" nl
				"L_string_ref_error:" nl
				"	printf(\"Error in string-ref\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_string_ref_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_string_ref));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-vector-ref
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_vector_ref:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* R0 = pointer to vector object */" nl
				"MOV(R1, FPARG(3));			/* R1 = index */" nl
				"CMP(IND(R0), IMM(T_VECTOR));" nl
				"JUMP_NE(L_vector_ref_error);" nl
				"CMP(IND(R1), IMM(T_INTEGER));" nl
				"JUMP_NE(L_vector_ref_error);" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R1, INDD(R0, 1));		/* check if index < n */" nl
				"JUMP_GE(L_vector_ref_error);" nl
				"ADD(R1, IMM(2));"
				"MOV(R0, INDD(R0, R1));" nl
				"JUMP(L_vector_ref_exit);" nl
				"L_vector_ref_error:" nl
				"	printf(\"Error in vector-ref\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_vector_ref_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_vector_ref));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-string-set!
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_string_set:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* R0 = pointer to string object */" nl
				"MOV(R1, FPARG(3));			/* R1 = index */" nl
				"MOV(R2, FPARG(4));			/* R2 = char */" nl
				"CMP(IND(R0), IMM(T_STRING));" nl
				"JUMP_NE(L_string_set_error);" nl
				"CMP(IND(R1), IMM(T_INTEGER));" nl
				"JUMP_NE(L_string_set_error);" nl
				"CMP(IND(R2), IMM(T_CHAR));" nl
				"JUMP_NE(L_string_set_error);" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R1, INDD(R0, 1));		/* check if index < n */" nl
				"JUMP_GE(L_string_set_error);" nl
				"ADD(R1, IMM(2));" nl
				"MOV(INDD(R0, R1), INDD(R2, 1));" nl
				"MOV(R0, SOB_VOID);" nl
				"JUMP(L_string_set_exit);" nl
				"L_string_set_error:" nl
				"	printf(\"Error in string-set\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_string_set_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_string_set));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))

(define prim-vector-set!
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_vector_set:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* R0 = pointer to string object */" nl
				"MOV(R1, FPARG(3));			/* R1 = index */" nl
				"MOV(R2, FPARG(4));			/* R2 = object */" nl
				"CMP(IND(R0), IMM(T_VECTOR));" nl
				"JUMP_NE(L_vector_set_error);" nl
				"CMP(IND(R1), IMM(T_INTEGER));" nl
				"JUMP_NE(L_vector_set_error);" nl
				"MOV(R1, INDD(R1, 1));" nl
				"CMP(R1, INDD(R0, 1));		/* check if index < n */" nl
				"JUMP_GE(L_vector_set_error);" nl
				"ADD(R1, IMM(2));" nl
				"MOV(INDD(R0, R1), R2);" nl
				"MOV(R0, SOB_VOID);" nl
				"JUMP(L_vector_set_exit);" nl
				"L_vector_set_error:" nl
				"	printf(\"Error in vector-set\\n\");" nl
				"	JUMP(L_exit);" nl
				"L_vector_set_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_vector_set));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-eq?
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_eq:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* R0 = object1 */" nl
				"MOV(R1, FPARG(3));			/* R1 = object2 */" nl
				"/* check types */" nl
				"CMP(IND(R0), IND(R1));" nl
				"JUMP_NE(L_eq_false);" nl
				"/* if type is string/vector/pair, compare addresses */" nl
				"CMP(IND(R0), IMM(T_STRING));" nl
				"JUMP_EQ(L_eq_compare_addresses);" nl
				"CMP(IND(R0), IMM(T_VECTOR));" nl
				"JUMP_EQ(L_eq_compare_addresses);" nl
				"CMP(IND(R0), IMM(T_PAIR));" nl
				"JUMP_EQ(L_eq_compare_addresses);" nl
				"/* for void, nil, undef - return true */" nl
				"CMP(IND(R0), IMM(T_UNDEF));" nl
				"JUMP_EQ(L_eq_true);" nl
				"CMP(IND(R0), IMM(T_VOID));" nl
				"JUMP_EQ(L_eq_true);" nl
				"CMP(IND(R0), IMM(T_NIL));" nl
				"JUMP_EQ(L_eq_true);" nl
				"CMP(IND(R0), IMM(T_CLOSURE));" nl
				"JUMP_EQ(L_eq_compare_closures);" nl
				
				"/* for symbol/boolean/char/integer, compare value. for closure, compare env */" nl
				"CMP(INDD(R0, 1), INDD(R1, 1));" nl
				"JUMP_NE(L_eq_false);" nl
				"JUMP(L_eq_true);" nl
				
				"/* for closure, compare label value */" nl
				"L_eq_compare_closures:" nl
				"CMP(INDD(R0, 2), INDD(R1, 2));" nl
				"JUMP_NE(L_eq_false);" nl
				"JUMP(L_eq_true);" nl
				
				"L_eq_compare_addresses:" nl
				"CMP(R0, R1);" nl
				"JUMP_EQ(L_eq_true);" nl
				"JUMP(L_eq_false);" nl
				
				"L_eq_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"JUMP(L_eq_exit);" nl
				"L_eq_true:" nl
				"MOV(R0, SOB_TRUE);" nl
				"L_eq_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_eq));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))
				
(define prim-not
	(lambda (address)
		(let ((num (number->string fvar-counter)))
			(string-append
				"JUMP(L_continue" num ");" nl nl
				"L_prim_not:" nl
				"PUSH(FP);" nl
				"MOV(FP,SP);" nl
				"MOV(R0, FPARG(2));			/* R0 = arg */" nl
				"CMP(IND(R0), T_BOOL);" nl
				"JUMP_NE(L_not_false);" nl
				"CMP(INDD(R0, 1), IMM(1));" nl
				"JUMP_EQ(L_not_false);" nl
				"MOV(R0, SOB_TRUE);" nl
				"JUMP(L_not_exit);" nl
				"L_not_false:" nl
				"MOV(R0, SOB_FALSE);" nl
				"L_not_exit:" nl
				"POP(FP);" nl
				"RETURN;" nl
				"L_continue" num ":" nl
				"PUSH(LABEL(L_prim_not));" nl
				"PUSH(IMM(5555555));" nl
				"CALL(MAKE_SOB_CLOSURE);" nl
				"DROP(2);" nl
				"MOV(IND(" (number->string address) "), R0);" nl))))