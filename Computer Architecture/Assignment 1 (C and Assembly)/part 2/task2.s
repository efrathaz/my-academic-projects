section	.rodata
      YES:	DB	"The number is divisible by 3", 10, 0	; Format string
      NOT:	DB	"The number is NOT divisible by 3", 10, 0	; Format string
      
section .data
	counter: DD 16

section .text
	align 16
	global isDivisibleBy3
	extern check
	extern printf

isDivisibleBy3:
	push	ebp
	mov	ebp, esp	; Entry code - set up ebp and esp
	pusha			; Save registers
	
	sub	esp, 8		; allocate space for local vars
	mov 	edx, dword[ebp+8]	; Get argument (int num)
	
	; initialize local vars
	mov	dword [ebp-36],0	; even
	mov	dword [ebp-40],0	; odd
	
loop:
	cmp	byte[counter],0
	je	call_check
	shr	edx,1
	adc	dword [ebp-40],0
	shr	edx,1
	adc	dword [ebp-36],0
	sub	byte[counter],1
	jmp	loop
	
call_check:
	push	dword [ebp-36]	; even
	push	dword [ebp-40]	; odd
	call	check
	add 	esp, 8		; restore esp
	
	cmp	eax,1
	jne	not_divisible
	push	YES
	call	printf
	add	esp, 4
	jmp	end
	
not_divisible:
	push	NOT
	call	printf
	add	esp, 4
end:
	add	esp, 8		; free local vars space
	popa			; Restore registers
	mov	esp, ebp	; Function exit code
	pop	ebp
	ret
	