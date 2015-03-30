
;;;;;;;;;;;; Lexical Addresses ;;;;;;;;;;;;

(define pe->lex-pe
	(lambda (pe)
		(update-lexical-addrs pe '() '('()))))

(define update-lexical-addrs
	(lambda (pe params env)
		(cond ((not (list? pe)) pe)
			  ((null? pe) '())
			  ((equal? 'var (car pe)) (get-lexical-addr (cadr pe) params env))
			  ((equal? 'lambda-simple (car pe))
					(let ((new-params (cadr pe))
						  (body (caddr pe)))
						`(lambda-simple ,new-params ,(update-lexical-addrs body new-params (extend-env new-params env)))))
			  ((equal? 'lambda-opt (car pe))
					(let ((new-params (append (cadr pe) (list (caddr pe))))
						  (body (cadddr pe)))
						`(lambda-opt ,(cadr pe) ,(caddr pe) ,(update-lexical-addrs body new-params (extend-env new-params env)))))
			  ((equal? 'lambda-variadic (car pe))
					(let ((new-params (list (cadr pe)))
						  (body (caddr pe)))
						`(lambda-variadic ,(cadr pe) ,(update-lexical-addrs body new-params (extend-env new-params env)))))
			  (else (map update-lexical-addrs pe (make-list (length pe) params) (make-list (length pe) env))))))

(define extend-env
	(lambda (x env)
		(append (list x) env)))
		
(define get-lexical-addr
	(lambda (var params env)
		(if (contains params var) 
			`(pvar ,var ,(find-location params var 0))
			(lookup var (cdr env) 0))))
			  
(define find-location
	(lambda (lst item depth)
		(cond ((null? lst) -1)
			  ((equal? (car lst) item) depth)
			  (else (find-location (cdr lst) item (+ depth 1))))))
			  
(define lookup
	(lambda (var env depth)
		(cond ((null? env) `(fvar ,var))
			  ((contains (car env) var) `(bvar ,var ,depth ,(find-location (car env) var 0)))
			  (else (lookup var (cdr env) (+ depth 1))))))

;;;;;;;;;;;; Annotated Tale Position ;;;;;;;;;;;;

(define annotate-tc
	(lambda (pe)
		(atp pe #f)))
		
(define atp
	(lambda (pe tp?)
		(let ((tag (car pe)))
			(cond ((equal? 'const tag) pe)
				  ((or (equal? 'var tag) (equal? 'fvar tag) (equal? 'bvar tag) (equal? 'pvar tag)) pe)
				  ((equal? 'if3 tag) 
						(with (cdr pe)
							(lambda (test dit dif)
								`(if3 ,(atp test #f)
									  ,(atp dit tp?)
									  ,(atp dif tp?)))))
				  ((equal? 'or tag)
						(with (cdr pe)
							(lambda (items)
								(let ((last-item (car (reverse items)))
									  (first-items (reverse (cdr (reverse items)))))
									`(or ,(append (map atp first-items (make-list (length first-items) #f)) (list (atp last-item tp?))))))))
				  ((equal? 'seq tag)
						(with (cdr pe)
							(lambda (items)
								(let ((last-item (car (reverse items)))
									  (first-items (reverse (cdr (reverse items)))))
									`(seq ,(append (map atp first-items (make-list (length first-items) #f)) (list (atp last-item tp?))))))))
				  ((equal? 'define tag)
						(with (cdr pe)
							(lambda (var value)
								`(define ,var ,(atp value #f)))))
				  ((equal? 'lambda-simple tag)
						(with (cdr pe)
							(lambda (params body)
								`(lambda-simple ,params ,(atp body #t)))))
				  ((equal? 'lambda-opt tag)
						(with (cdr pe)
							(lambda (params rest body)
								`(lambda-opt ,params ,rest ,(atp body #t)))))
				  ((equal? 'lambda-variadic tag)
						(with (cdr pe)
							(lambda (params body)
								`(lambda-variadic ,params ,(atp body #t)))))								
				  ((equal? 'applic tag)
						(with (cdr pe)
							(lambda (proc args)
								(if tp?
									`(tc-applic ,(atp proc #f) ,(map atp args (make-list (length args) #f)))
									`(applic ,(atp proc #f) ,(map atp args (make-list (length args) #f)))))))
				  (else pe)))))
							
(define test
	(lambda (e)
		(annotate-tc (pe->lex-pe (parse e)))))