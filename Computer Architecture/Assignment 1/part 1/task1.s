section	.rodata
      THREE:	DD	3
      LC0:	DB	"The result is:  %s", 10, 0	; Format string
      
section .data
	counter: DB 0
      
section .bss
      LC1:	RESB	32

section .text
	align 16
	global my_func
	extern printf

my_func:
	push	ebp
	mov	ebp, esp	; Entry code - set up ebp and esp
	pusha			; Save registers
	mov	eax, 0
	mov 	ecx, dword[ebp+8]	; Get argument (pointer to string)
	
start:
	cmp	byte[ecx], 0
	jne	count_chars
	sub	byte [counter], 8 ; don't count '/n' and null
	mov 	ecx, dword[ebp+8] ; return pointer to the start of the string
	jmp	read_byte
	
; add 4 bits for every char
count_chars:
	inc	ecx
	add	byte[counter],4
	jmp	start
	
read_byte:
	mov	ebx, 0
	mov	bl, byte [ecx]

	cmp	bl, 97 ; check if the byte is greater than 'a'
	jge	check_lower
	cmp	bl, 65 ; check if the byte is greater than 'A'
	jge	check_upper
	cmp	bl, 48 ; check if the byte is greater than '0'
	jge	check_num
	jmp	increment

check_num:
	cmp	bl, 57 ; check if the byte is less than '9'
	jle	convert_num
	jmp	increment
check_lower:
	cmp	bl, 102 ; check if the byte is less than 'f'
	jle	convert_lower
	jmp	increment
check_upper:
	cmp	bl, 70 ; check if the byte is less than 'F'
	jle	convert_upper
	jmp	increment

; convert from char to int
convert_num:
	sub	bl, 48
	jmp	loop
convert_lower:
	sub	bl, 87
	jmp	loop
convert_upper:
	sub	bl, 55
	jmp	loop

loop:
	push	ecx			; save ecx value
	mov	cl, byte[counter]      ;how much shifting to do   
	sub	byte[counter], 4	;dec counter
	shl	ebx, cl
	pop	ecx			; restore ecx value
	add	eax, ebx		; update the number
increment:
	inc	ecx
	cmp	byte [ecx], 0
	jne	read_byte
	mov 	dword[counter], 0	; reset num of chars

; convert the number to a string representing the number in base 3
to_ternary:
	mov	edx,0		; initialize remainder
	div	dword[THREE]	; divide by 3
	add	dl, 48		; convert the remainder to a char
	push	edx		; store the next char in stack
	inc	dword[counter]	; add 1 to the count (to know how many chars to pop later)
	cmp	eax,0
	jne	to_ternary
	mov	ecx, LC1	; ecx points to the answer

pop_chars:
	pop	edx
	mov	byte[ecx],dl	; concatenate char to answer
	inc	ecx
	dec	dword[counter]	
	cmp	dword[counter], 0	; when all chars are written
	jne	pop_chars

end:
	push	LC1		; Call printf with 2 arguments: pointer to str
	push	LC0		; and pointer to format string.
	call	printf
	add 	esp, 8		; Clean up stack after call

	popa			; Restore registers
	mov	esp, ebp	; Function exit code
	pop	ebp
	ret

