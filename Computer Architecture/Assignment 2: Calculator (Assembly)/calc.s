%define BUFFERSIZE 81
%define ARRAYSIZE 5
%define MALLOCSIZE 5

section	.rodata
      CALC:		DB	"calc: ",0
      HEX:		DB	"%X",0
      NEW_LINE:		DB	10,0
      OVERFLOW:		DB	"Error: Stack Overflow",10,0
      NOT_ENOUGH_ARGS:	DB	"Error: Not Enough Arguments on Stack",10,0
      RESULT:		DB	"Total number of commands: %d",10,0

section .bss
      BUFFER:		RESB	BUFFERSIZE
      OP_COUNTER:	RESD	1
      STACK:		RESB	20

section .text
      align 16
      global main
      extern printf
      extern gets
      extern malloc
      extern free

main:
	push	ebp
	mov	ebp, esp
	pusha
	
	call	my_calc
	
	push	eax
	push	RESULT
	call	printf
	add	esp, 8
	
	popa
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	
my_calc:
	push	ebp
	mov	ebp, esp
	sub	esp,4		; save space for local variable
	pusha
	
	mov	dword[OP_COUNTER],0	; number of operations
	mov	esi,0			; number of numbers currently in the stack
	
read_input:
	
	; print "calc: "
	push	CALC
	call	printf 
	add	esp,4
	
	; read input from STDIN to BUFFER
	push	BUFFER
	call	gets 
	add	esp,4
	
	; check if the input is a number or command
	push	BUFFER
	call	check_input
	add	esp,4
	
	; if eax = 0, then its a command or unknown
	cmp	eax,0
	jz	choose_command
	
	; otherwise, its a number
	cmp	esi,5		; if there are already 5 numbers in the stack
	je	print_stack_overflow_error
	
	; delete zero from beginning of number
	push	BUFFER
	call	delete_zeors
	add	esp,4
	mov	dword [ebp-4],eax	; put trimmed string in local variable
	
	; check if the number is invalid
	push	dword[ebp-4]
	call	check_number
	add	esp,4
	
	; if eax = 0, then the number is valid
	cmp	eax,0
	jnz	read_input
	
	;get input length
	push	dword [ebp-4]
	call	get_length
	add	esp,4
	
	shr	eax,1		; check if the length is odd or even
	
	mov	edi,dword[ebp-4]	; save pointer to string in edi
	mov	ecx,0
	
	;if the length is even, jump to 
	jnc	convert_two_chars
	
	push	edi
	call	convert_char
	add	esp,4
	
	mov	ebx,eax		; save converted char in ebx
	
	push	ecx		; next
	push	ebx		; data
	call	new_link
	add	esp,8
	
	mov	ecx,eax		; save pointer to the new link
	inc	edi		; next char
	
    convert_two_chars:
	cmp	byte [edi],0
	jz	push_to_stack
	
	push	edi
	call	convert_char
	add	esp,4
	
	mov	ebx,eax
	shl	ebx,4
	inc	edi
	
	push	edi
	call	convert_char
	add	esp,4
	
	add	ebx,eax
	
	push	ecx		; next
	push	ebx		; data
	call	new_link
	add	esp,8
	
	mov	ecx,eax		; save pointer to the new link
	inc	edi
	jmp	convert_two_chars
	
    push_to_stack:
	mov	dword[STACK+4*esi],ecx
	inc	esi		; increase number of numbers currently in the stack
	jmp	read_input
	
    choose_command:
	; check that this is really a command (= just one char)
	cmp	byte [BUFFER+1],0
	jnz	read_input
	
	inc	dword[OP_COUNTER]	; increase the operations counter
	
	cmp	byte [BUFFER],'p'
	jz	call_pop_and_print
	cmp	byte [BUFFER],'+'
	jz	call__add
	cmp	byte [BUFFER],'q'
	jz	my_calc_end
	cmp	byte [BUFFER],'d'
	jz	call_duplicate
	cmp	byte [BUFFER],'l'
	jz	call_log2
	cmp	byte [BUFFER],'n'
	jz	call_num_of_ones
	
	; if non of the above, then this is an invalid command
	dec	dword[OP_COUNTER]
	jmp	read_input
	
    print_stack_overflow_error:
	pusha
	push	OVERFLOW
	call	printf
	add	esp,4
	popa
	jmp	read_input
	
    print_not_enough_args_error:	
	pusha
	push	NOT_ENOUGH_ARGS
	call	printf
	add	esp,4
	popa
	jmp	read_input
	
    call_pop_and_print:
	cmp	esi,0
	jz	print_not_enough_args_error
	
	dec	esi		; decrease number of numbers in the stack
	
	push	dword[STACK+4*esi]
	call	print_linked_list
	add	esp,4
	
	push	dword[STACK+4*esi]
	call	free_linked_list
	add	esp,4
	
	jmp	read_input
	
    call__add:
	cmp	esi,2
	jl	print_not_enough_args_error
	
	push	0
	push	dword [STACK+4*(esi-1)]
	push	dword [STACK+4*(esi-2)]
	call	add_numbers
	add	esp,12
	
	; pop numbers
	push	dword [STACK+4*(esi-2)]
	call	free_linked_list
	add	esp,4
	
	push	dword [STACK+4*(esi-1)]
	call	free_linked_list
	add	esp,4
	
	sub	esi,2
	
	; push result
	mov	[STACK+4*(esi)],eax
	inc	esi
	jmp	read_input
	
    call_duplicate:
	cmp	esi,1
	jl	print_not_enough_args_error
	cmp	esi,5
	jge	print_stack_overflow_error
	
	push	dword[STACK+4*(esi-1)]
	call	duplicate
	add	esp,4
	
	; push to stack
	mov	dword[STACK+4*esi],eax
	inc	esi
	jmp	read_input
	
    call_log2:
	cmp	esi,0
	jz	print_not_enough_args_error
	dec	esi
	
	push	dword[STACK+4*esi]
	call	log2
	add	esp,4
	
	mov	ebx,eax
	
	; pop
	push	dword[STACK+4*esi]
	call	free_linked_list
	add	esp,4
	
	; push
	mov	dword[STACK+4*esi],ebx
	inc	esi
	jmp	read_input
	
    call_num_of_ones:
	cmp	esi,0
	jz	print_not_enough_args_error
	dec	esi
	
	push	dword[STACK+4*esi]
	call	num_of_ones
	add	esp,4
	
	mov	ebx,eax
	
	; pop
	push	dword[STACK+4*esi]
	call	free_linked_list
	add	esp,4
	
	; push
	mov	dword[STACK+4*esi],ebx
	inc	esi
	jmp	read_input
	
    my_calc_end:
	
	push	esi
	push	STACK
	call	free_stack
	add	esp,8
	
	popa
	mov	eax, dword[OP_COUNTER]
	mov	esp, ebp
	pop	ebp
	ret
	
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; returns 1 if the input is number, and 0 if command or invalid input
check_input:
	push	ebp
	mov	ebp, esp
	push	ebx
	
	mov	ebx,[ebp+8]	; get pointer to BUFFER
	
	cmp	byte[ebx],'d'
	je	check_length
	cmp	byte[ebx],'0'
	jl	is_command
	cmp	byte[ebx],'9'
	jle	is_number
	cmp	byte[ebx],'A'
	jl	is_command
	cmp	byte[ebx],'F'
	jle	is_number
	cmp	byte[ebx],'a'
	jl	is_command
	cmp	byte[ebx],'f'
	jle	is_number
	
    is_command:
	mov	eax,0
	jmp	check_input_end
	
    is_number:
	mov	eax,1
	jmp	check_input_end
	
    check_length:
	cmp	byte[ebx+1],0
	jz	is_command
	jmp	is_number
	
    check_input_end:
	pop	ebx
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; returns pointer to the trimmed string
delete_zeors:
	push	ebp
	mov	ebp,esp
	sub	esp,4		; save space for local variable
	pusha
	
	mov	ebx,dword[ebp+8]	;pointer to BUFFER
	
    skip_zero:	
	mov	dword[ebp-4],ebx
	cmp	byte[ebx],'0'
	jnz	end_of_zeros
	inc	ebx
	jmp	skip_zero
	
    end_of_zeros:
	cmp	byte [ebx],0
	jnz	delete_zeors_end
	dec	ebx
	mov	dword[ebp-4],ebx
	
    delete_zeors_end:
	popa
	mov	eax,dword[ebp-4]
	mov	esp,ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; returns 0 if the number is valid, 1 if not
check_number:
	push	ebp
	mov	ebp,esp
	sub	esp,4		; save space for local variable
	pusha
	
	mov	dword [ebp-4],0		; initialize local variable
	mov	ecx,dword[ebp+8]
	
    check_byte:	
	cmp	byte [ecx],0
	jz	check_number_end
	cmp	byte[ecx],'0'
	jl	not_valid
	cmp	byte [ecx],'9'
	jle	is_valid
	cmp	byte [ecx],'A'
	jl	not_valid
	cmp	byte [ecx],'F'
	jle	is_valid
	cmp	byte [ecx],'a'
	jl	not_valid
	cmp	byte [ecx],'f'
	jg	not_valid
	
    is_valid:
	inc	ecx
	jmp	check_byte
	
    not_valid:
	mov	dword [ebp-4],1
	
    check_number_end:
	popa
	mov	eax,dword[ebp-4]
	mov	esp,ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; returns the length of the string including the terminating null char
get_length:
	push	ebp
	mov	ebp, esp
	sub	esp,4		; save space for local variable
	pusha
	
	mov	eax,0
	mov	ebx,dword[ebp+8]	;get pointer to string
	
    next_char:
	cmp	byte[ebx],0
	jz	get_length_end	
	inc	eax
	inc	ebx
	jmp	next_char
	
    get_length_end:
	mov	dword [ebp-4],eax
	popa
	mov	eax,dword[ebp-4]
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

convert_char:
	push	ebp
	mov	ebp, esp
	push	ebx
	
	mov	ebx,dword[ebp+8] 	; get pointer to BUFFER
	
	cmp	byte [ebx],'9'
	jle	convert_digit
	cmp	byte [ebx],'F'
	jle	convert_letter
	sub	byte [ebx],87
	jmp	convert_char_end
	
    convert_digit:
	sub	byte[ebx],48
	jmp	convert_char_end
	
    convert_letter:
	sub	byte[ebx],55
	
    convert_char_end:
	mov	eax,0
	mov	al,byte [ebx]
	
	pop	ebx
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; returns pointer to the new link
new_link:
	push	ebp
	mov	ebp, esp
	sub	esp,4			; save space for local variable
	pusha
	
	mov	ebx,dword[ebp+8]	;get link data
	mov	ecx,dword[ebp+12]	;get link next
	
	pusha
	push	5
	call	malloc
	add	esp,4
	mov	dword[ebp-4],eax
	popa
	
	mov	eax,dword[ebp-4]	; put pointer to the new link in eax
	
	mov	byte[eax],bl
	mov	dword[eax+1],ecx
	
    new_link_end:
	popa
	mov	eax,dword[ebp-4]
	add	esp,4
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

append_to_end:
	push	ebp
	mov	ebp, esp
	pusha
	
	mov	ebx,dword[ebp+8] 	; get start of linked-list
	mov	ecx,dword[ebp+12]	; get link to appened
	
	cmp	ebx,0
	jz	new_linked_list
	
    run_to_end:
	cmp	dword[ebx+1],0
	jz	append_link
	mov	ebx,dword[ebx+1]
	jmp	run_to_end
	
    append_link:
	mov	dword[ebx+1],ecx
	jmp	end
	
    new_linked_list:
	mov	eax,dword[ebp+12]
	mov	dword[ebp+8],eax
	
    end:
	popa
	mov	eax,dword[ebp+8]
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

print_linked_list:	
	push	ebp
	mov	ebp, esp
	pusha
	
	mov	ecx,dword[ebp+8]	;pointer to linked-list
	
	push	ecx
	call	print_recursive
	add	esp,4
	
	pusha
	push	NEW_LINE
	call	printf
	add	esp,4
	popa
	
	popa
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

print_recursive:
	push	ebp
	mov	ebp, esp
	pusha
	
	mov	ecx,dword[ebp+8]	;pointer to list
	mov	eax,0
	
	cmp	ecx,0
	jz	print_recursive_end
	
	; print all the previous bytes
	push	dword [ecx+1]
	call	print_recursive
	add	esp,4
	
	; print this byte
	mov	al,byte [ecx]
	pusha
	push	eax
	push	HEX
	call	printf
	add	esp,8
	popa
	
    print_recursive_end:
	popa
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

free_linked_list:
	push	ebp
	mov	ebp, esp
	pusha
	
	mov	ebx,dword[ebp+8]	; get pointer to the linked-list
	
    free_next_link:
	cmp	ebx,0
	jz	free_linked_list_end 
	mov	ecx,dword [ebx+1]	; save the next link in ecx
	
	pusha
	push	ebx
	call	free			; free this link
	add	esp,4
	popa
	
	mov	ebx,ecx			; put pointer to the next link in ebx
	jmp	free_next_link
	
    free_linked_list_end:
	popa
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

free_stack:
	push	ebp
	mov	ebp, esp
	pusha
	
	mov	ebx,dword[ebp+8]	; get pointer to stack
	mov	ecx,dword[ebp+12]	; get number of items currently in the stack
	
  free_next:
	cmp	ecx,0
	jz	free_stack_end
	dec	ecx
	
	push	dword[ebx+4*ecx]
	call	free_linked_list
	add	esp,4
	
	jmp	free_next
	
  free_stack_end:
	popa
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

add_numbers:
	push	ebp
	mov	ebp, esp
	sub	esp,4		; save space for local variable
	pusha
	
	mov	ebx, dword [ebp+8]	; get first number
	mov	ecx, dword [ebp+12]	; get secont number
	mov	esi, dword [ebp+16]	; get carry from previous call (recursive)
	mov	edi,0			; carry
	
	; check if the numbers have the same number of bytes
	cmp	ebx,0
	jz	check_second_number
	cmp	ecx,0
	jz	add_first_number
	
	; add the least significant bytes of the two numbers
	mov	al,byte[ebx]
	mov	ah,byte[ecx]
	shr	esi,1
	adc	al,ah
	jnc	continue
	mov	edi,1		; set carry to 1
	
    continue:
	mov	edx,0
	mov	dl,al		; save the sum of the two bytes in edx
	
	push	edi
	push	dword[ecx+1]
	push	dword[ebx+1]
	call	add_numbers
	add	esp,12
	
	push	eax		; next
	push	edx		; data
	call	new_link
	add	esp,8
	
	mov	dword[ebp-4],eax	; save pointer to the new link in local variable
	jmp	add_numbers_end
	
    ; if the first number is shorter
    check_second_number:
	cmp	ecx,0
	jnz	add_second_number
	cmp	esi,0			; check carry from the previous call
	jnz	add_link_for_carry
	mov	dword[ebp-4],0		; this will be the last link, therefore the next link is 0
	jmp	add_numbers_end
	
    add_link_for_carry:
	push	0		; next
	push	1		; data
	call	new_link
	add	esp,8
	
	mov	dword[ebp-4],eax	; save pointer to the new link in local variable
	jmp	add_numbers_end
	
    add_first_number:
	mov	al,byte[ebx]
	shr	esi,1
	adc	al,0
	mov	edi,0
	jnc	continue_1
	mov	edi,1
	
      continue_1:
	mov	edx,0
	mov	dl,al
	
	push	edi
	push	dword 0
	push	dword[ebx+1]
	call	add_numbers
	add	esp,12
	
	push	eax
	push	edx
	call	new_link
	add	esp,8
	
	mov	dword[ebp-4],eax
	jmp	add_numbers_end
	
    add_second_number:
	mov	al,byte[ecx]
	shr	esi,1
	adc	al,0
	mov	edi,0
	jnc	continue_2
	mov	edi,1
	
      continue_2:
	mov	edx,0
	mov	dl,al
	
	push	edi
	push	dword[ecx+1]
	push	dword 0
	call	add_numbers
	add	esp,12
	
	push	eax
	push	edx
	call	new_link
	add	esp,8
	
	mov	dword[ebp-4],eax
	jmp	add_numbers_end
	
    add_numbers_end:
	popa
	mov	eax,dword[ebp-4]
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

duplicate:
	push	ebp
	mov	ebp, esp
	sub	esp,4			; save space for local variable
	pusha
	
	mov	ecx,dword[ebp+8]	; put pointer to linked-list in ecx
	mov	ebx,0
	mov	bl,byte[ecx]		; put the first byte in ebx
	
	push	0		; next
	push	ebx		; data
	call	new_link
	add	esp,8
	
	mov	esi,eax			; save pointer to the first link in the new list
	mov	ecx,dword[ecx+1]	; get next link
	
    duplicate_byte:
	cmp	ecx,0
	jz	duplicate_end
	
	push	0
	mov	bl,byte[ecx]
	push	ebx
	call	new_link
	add	esp,8
	
	mov	edx,eax
	
	push	edx		; pointer to the new link
	push	esi		; pointer to the start of the linked-list
	call	append_to_end
	add	esp,8
	
	mov	ecx,dword[ecx+1]	; get next link
	jmp	duplicate_byte
	
    duplicate_end:
	mov	dword[ebp-4],esi
	popa
	mov	eax,dword[ebp-4]
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

log2:
	push	ebp
	mov	ebp, esp
	sub	esp,4			; save space for local variable
	pusha
	
	mov	ebx,dword[ebp+8]	; get pointer to linked-list
	mov	eax,0			; initialize answer
	
    run_to_last_link:
	cmp	dword[ebx+1],0
	jz	last_link
	
	add	eax,8
	mov	ebx,dword[ebx+1]
	jmp	run_to_last_link
	
    last_link:
	mov	ecx,0
	mov	cl,byte[ebx]
      shift:
	shr	ecx,1
	cmp	ecx,0		; check if the rest of the number after the shift is 0
	je	log2_end
	
	add	eax,1
	jmp	shift
	
    log2_end:
	push	eax
	call	convert_num_to_linked_list
	add	esp,4
	
	mov	dword[ebp-4],eax
	popa
	mov	eax,dword[ebp-4]
	mov	esp, ebp
	pop	ebp
	ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

num_of_ones:
	push	ebp
	mov	ebp, esp
	sub	esp,4			; save space for local variable
	pusha
	
	mov	ebx,dword[ebp+8]	; get pointer to linked-list
	mov	eax,0			; initialize answer
	
    next_link:
	cmp	ebx,0
	jz	num_of_ones_end
	
	mov	edx,0
	mov	dl,byte[ebx]
	mov	ecx,8
	
      next_bit:
	shr	edx,1
	jnc	jmp_loop
	inc	eax
	
	jmp_loop:
	loop	next_bit,cx
	
	mov	ebx,dword[ebx+1]
	jmp	next_link
	
    num_of_ones_end:
	push	eax
	call	convert_num_to_linked_list
	add	esp,4
	
	mov	dword[ebp-4],eax
	popa
	mov	eax,dword[ebp-4]
	mov	esp, ebp
	pop	ebp
	ret
	
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

convert_num_to_linked_list:
	push	ebp
	mov	ebp, esp
	sub	esp,4			; save space for local variable
	pusha
	
	mov	eax,dword[ebp+8]	; get pointer to nubmer
	mov	ebx,0
	mov	ecx,0
	mov	edx,0
	
	; put each byte of the number in a different register
	mov	dl,al
	shr	eax,8
	mov	cl,al
	shr	eax,8
	mov	bl,al
	shr	eax,8
	; eax hold the most significant byte
	; edx hold the least significant byte
	
    add_eax:
	cmp	eax,0
	je	add_ebx
	
	push	0		; next
	push	eax		; data
	call	new_link
	add	esp,8
	
    add_ebx:
	cmp	ebx,0
	je	add_ecx
	
	push	eax		; next
	push	ebx		; data
	call	new_link
	add	esp,8
	
    add_ecx:
	cmp	ecx,0
	je	add_edx
	
	push	eax		; next
	push	ecx		; data
	call	new_link
	add	esp,8
	
    add_edx:
	push	eax		; next
	push	edx		; data
	call	new_link
	add	esp,8
	
	mov	dword[ebp-4],eax
	popa
	mov	eax,dword[ebp-4]
	mov	esp, ebp
	pop	ebp
	ret