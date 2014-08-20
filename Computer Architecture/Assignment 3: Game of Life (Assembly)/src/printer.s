global printer
extern resume
extern World, WorldLength, WorldWidth

;; /usr/include/asm/unistd_32.h
sys_write:	equ	4
STD_OUT:	equ	1

%macro print 2
	pusha
	mov	eax, sys_write
	mov	ebx, STD_OUT
	mov	ecx, %1
	mov	edx, %2
	int	80h
	popa
%endmacro

section	.rodata
    NEW_LINE:		db	10,0
    LINE:		db	"--------------------------",10,0

section .bss
    currentCellValue:	resb 1

section .text

printer:
        mov	eax, 0			; value to print
        mov	ebx, 0			; current row
        mov	ecx, 0			; current column
        
    print_row:
	cmp	ebx, [WorldLength]
	je	end_printer
	
	print_cell:
	cmp	ecx, [WorldWidth]
	je	end_row
	
	; get current cell value
        mov	esi, [WorldWidth]
        imul	esi, ebx		; esi = current_row * width
	mov	al, byte[World+esi+ecx]
	mov	byte[currentCellValue], al
	
	; print cell
	print	currentCellValue, 1
        
        inc	ecx			; next column
        jmp	print_cell
        
    end_row:
	print	NEW_LINE,1
	
	inc	ebx			; next row
	mov	ecx, 0			; first column
	jmp	print_row
        
end_printer:
        print	NEW_LINE, 1
        print	LINE, 27
        print	NEW_LINE, 1

        ; resume scheduler
        xor	ebx, ebx
        call	resume
	
        jmp	printer
