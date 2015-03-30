(define fvars-next-address 0)
(define fvar-counter 0)

(define *fvar-table* '())

(define initialize-fvar-table
	(lambda ()
		(set! *fvar-table* '())))

(define create-fvars
	(lambda (sexprs)
		(map find-fvars sexprs)))
		
		
(define find-fvars
	(lambda (e)
		(if (not (or (null? e) (not (list? e))))
			(if (equal? (car e) 'fvar)
				(add-fvar (cadr e))
				(map find-fvars e)))))

(define add-fvar
	(lambda (e)
		(if (not (find-address e *fvar-table*))
			(begin (set! *fvar-table* (append *fvar-table* (list (list fvars-next-address e))))
				   (set! fvars-next-address (add1 fvars-next-address))))))

(define add-define-fvar
	(lambda (address)
		(string-append
			"MOV(IND(" (number->string address) "), SOB_UNDEF);" nl)))
			
(define create-fvar-codes
	(lambda ()
		(string-append
			"/******************* create fvars *******************/" nl
			"/* allocate memory for fvar table */" nl
			"PUSH(IMM(" (number->string (length *fvar-table*)) "));" nl
			"CALL(MALLOC);" nl
			"DROP(1);" nl nl
			(print-fvars *fvar-table*)
			"/******************* end of fvars *******************/" nl nl)))
				  
(define print-fvars
	(lambda (table)
		(if (null? table)
			""
			(let* ((first (car table))
				   (fvar (cadr first))
				   (address (car first)))
				(set! fvar-counter (add1 fvar-counter))
				(string-append
					(cond ((eq? fvar 'apply) (prim-apply address))
						  ((eq? fvar '+) (prim-plus address))
						  ((eq? fvar '-) (prim-minus address))
						  ((eq? fvar '*) (prim-mult address))
						  ((eq? fvar '/) (prim-div address))
						  ((eq? fvar 'bin<?) (prim-bin<? address))
						  ((eq? fvar 'bin>?) (prim-bin>? address))
						  ((eq? fvar 'bin=?) (prim-bin=? address))
						  ((eq? fvar '=) (prim-equal address))
						  ((eq? fvar 'remainder) (prim-remainder address))
						  ((eq? fvar 'cons) (prim-cons address))
						  ((eq? fvar 'car) (prim-car address))
						  ((eq? fvar 'cdr) (prim-cdr address))
						  ((eq? fvar 'set-car!) (prim-set-car! address))
						  ((eq? fvar 'set-cdr!) (prim-set-cdr! address))
						  ((eq? fvar 'boolean?) (prim-question address "boolean" "T_BOOL"))
						  ((eq? fvar 'number?) (prim-question address "number" "T_INTEGER"))
						  ((eq? fvar 'integer?) (prim-question address "integer" "T_INTEGER"))
						  ((eq? fvar 'char?) (prim-question address "char" "T_CHAR"))
						  ((eq? fvar 'pair?) (prim-question address "pair" "T_PAIR"))
						  ((eq? fvar 'procedure?) (prim-question address "procedure" "T_CLOSURE"))
						  ((eq? fvar 'string?) (prim-question address "string" "T_STRING"))
						  ((eq? fvar 'symbol?) (prim-question address "symbol" "T_SYMBOL"))
						  ((eq? fvar 'vector?) (prim-question address "vector" "T_VECTOR"))
						  ((eq? fvar 'null?) (prim-question address "null" "T_NIL"))
						  ((eq? fvar 'zero?) (prim-zero? address))
						  ((eq? fvar 'make-string) (prim-make-string address))
						  ((eq? fvar 'make-vector) (prim-make-vector address))
						  ((eq? fvar 'char->integer) (prim-char->integer address))
						  ((eq? fvar 'integer->char) (prim-integer->char address))
						  ((eq? fvar 'string->symbol) (prim-string->symbol address))
						  ((eq? fvar 'symbol->string) (prim-symbol->string address))
						  ((eq? fvar 'string-length) (prim-string-length address))
						  ((eq? fvar 'vector-length) (prim-vector-length address))
						  ((eq? fvar 'string-ref) (prim-string-ref address))
						  ((eq? fvar 'vector-ref) (prim-vector-ref address))
						  ((eq? fvar 'string-set!) (prim-string-set! address))
						  ((eq? fvar 'vector-set!) (prim-vector-set! address))
						  ((eq? fvar 'eq?) (prim-eq? address))
						  ((eq? fvar 'not) (prim-not address))
						  (else (add-define-fvar address)))
					(print-fvars (cdr table)))))))
