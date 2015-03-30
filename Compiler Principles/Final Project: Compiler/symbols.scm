(define create-top-level
	(lambda ()
		(string-append
			"	/*************** initialize top level ***************/" nl
			"	PUSH(IMM(2));" nl
			"	CALL(MALLOC);" nl
			"	DROP(1);" nl
			"	MOV(IND(R0), SOB_VOID);" nl
			"	MOV(INDD(R0, 1), SOB_NIL);" nl
			"	MOV(SYMBOLS_START, R0);" nl
			"	MOV(SYMBOLS_LAST, SYMBOLS_START);" nl
			(add-symbols *constant-table*) nl

)))

(define add-symbols
	(lambda (table)
		(if (null? table)
		""
		(let ((first (car table)))
			(string-append
				(if (equal? (caddr first) T_SYMBOL)
				    (string-append
					"	MOV(IND(SYMBOLS_LAST), IMM(" (number->string (car first)) "));" nl
					"	PUSH(IMM(2));" nl
					"	CALL(MALLOC);" nl
					"	DROP(1);" nl
					"	MOV(INDD(SYMBOLS_LAST, 1), R0);" nl
					"	MOV(SYMBOLS_LAST, R0);" nl
					"	MOV(IND(SYMBOLS_LAST), SOB_VOID);" nl
					"	MOV(INDD(SYMBOLS_LAST, 1), SOB_NIL);" nl)
				    "")
				(add-symbols (cdr table)))))))