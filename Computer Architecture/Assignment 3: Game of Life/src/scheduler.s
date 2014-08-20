global scheduler
extern resume, end_co
extern WorldSize, WorldLength, WorldWidth
extern Generations, StepsBetweenPrint

%macro print 2
	pusha
	mov	eax, 4
	mov	ebx, 1
	mov	ecx, %1
	mov	edx, %2
	int	80h
	popa
%endmacro

section .bss
    current_co:		resd 1

section .text

scheduler:
        mov	ebx, 2			; co-routine index
        add	dword[WorldSize], 2	; number of co-routines = WorldSize + 2
        mov	esi, 0			; number of steps since the last print
	mov	ecx, [Generations]
	shl	ecx, 1			; ecx = Generations * 2

half_generation:
	
  step:
	; while co-routine index <= WorldSize
	cmp	ebx, [WorldSize]
	jge	end_half_generation
	
	; if it is time to print
	cmp	esi, [StepsBetweenPrint]
	je	resume_printer
	
	; else
    resume_cell:
	call	resume
	inc	ebx
	inc	esi
	jmp	step
	
  resume_printer:
	mov	[current_co],ebx	; save current index
	mov	ebx,1
	call	resume			; resume printer
        mov	ebx,[current_co]	; restore current index
        mov	esi,0			; number of steps since the last print = 0
        jmp	step
        
  end_half_generation:
        mov	ebx,2
        loop	half_generation

        mov	ebx,1
        call	resume			; resume printer
        
        call	end_co