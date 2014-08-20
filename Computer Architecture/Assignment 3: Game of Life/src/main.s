global _start
global World, WorldSize, WorldWidth, WorldLength
global Generations, StepsBetweenPrint
global cellFunction
extern init_co, start_co, resume
extern scheduler, printer

;; /usr/include/asm/unistd_32.h
sys_close:	equ	6
sys_open:	equ	5
sys_write:	equ	4
sys_read:	equ	3
sys_exit:	equ	1
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

%macro check_neighbour 2 ; %1 = row, %2 = column
	mov	edx, 0
	mov	eax, %1
	mul	dword[WorldWidth]
	add	eax, %2			; eax = width*row + column
	cmp	byte[World+eax], '1'
	jl	%%not_alive
	inc	byte[Neighbours+ebx]
	
    %%not_alive:
%endmacro

section	.rodata
    OPEN_ERROR:		db	"Error while openning input file.",10,0
    NOT_ENOUGH_AGRS:	db	"Not enough arguments.",10,0
    NEW_LINE:		db	10,0
    TEN:		dd	10
    
section .bss
    World:		resb 10000	; an array to hold the state of each cell
    Neighbours:		resb 10000 	; an array to hold the number of alive Neighbours of each cell
    CurrentCell:	resb 1
    StepsBetweenPrint:	resd 1
    Generations:	resd 1
    WorldWidth:		resd 1
    WorldLength:	resd 1
    WorldSize:		resd 1
    InitialStateFile:	resd 1
    CurrentRow:		resd 1
    CurrentColumn:	resd 1

section .text
;; ebx = co-routine index to initialize
;; edx = co-routine start (pointer to the function that the co-routine needs to do)

_start:
        enter	0, 0			; initialize stack frame for 'start'
        
        ; get arguments
        mov	ebx, dword[ebp+4]	; argc
        cmp	ebx, 6
        jl	not_enough_args
        
        push	dword[ebp+16]		; length
        call	atoi
        add	esp, 4
        mov	[WorldLength], eax
        
        push	dword[ebp+20]		; width
        call	atoi
        add	esp, 4
        mov	[WorldWidth], eax
        
        push	dword[ebp+24]		; t
        call	atoi
        add	esp, 4
        mov	[Generations], eax
        
        push	dword[ebp+28]		; k
        call	atoi
        add	esp, 4
        mov	[StepsBetweenPrint], eax
        
        mov	eax, [WorldLength]
        imul	dword[WorldWidth]
        mov	[WorldSize], eax	; WorldSize = length*WorldWidth
        
        ; open initial-state file
        pusha
        mov	eax, sys_open
        mov	ebx, dword[ebp+12]	; filename
        mov	ecx, 0			; read-only
        mov	edx, 0x777
        int	80h
        mov	[InitialStateFile], eax
        popa
        
	; save file descriptor
        cmp	dword[InitialStateFile], 0
        jle	open_error
        
        ; initialize World state
        mov	ebx, 0			; current row
        mov	edi, 0			; current column
        mov	ecx, [WorldLength]	; loop 'WorldLength' times
        
read_row:

    read_cell:
	; if edi > WorldWidth, end row
	cmp	edi, [WorldWidth]
	jg	end_row
	
	; read one byte from file to CurrentCell
	pusha
	mov	eax, sys_read
        mov	ebx, dword[InitialStateFile]
        mov	ecx, CurrentCell
        mov	edx, 1
        int	80h
        popa
        
        ; don't save '\n' char
        cmp	dword[CurrentCell], '\n'
        je	end_row
        
        ; update cell value
        mov	eax, [WorldWidth]
        imul	ebx			; eax = row * WorldWidth
        add	eax, edi		; eax = row * WorldWidth + column
        mov	esi, eax
        mov	al, byte[CurrentCell]
        
        mov	byte[World+esi], al	; World[row * WorldWidth + column] = CurrentCell
        
        inc	edi			; next column
        jmp	read_cell
        
    end_row:
	inc	ebx			; next row
	mov	edi, 0			; first column
	loop	read_row
	
	; close initial-state file
	pusha
	mov	eax, sys_close
        mov	ebx, InitialStateFile
        int	80h
        popa
	
        ; initialize scheduler
        xor	ebx, ebx		; scheduler is co-routine 0
        mov	edx, scheduler
        call	init_co

        ; initialize printer
        inc	ebx			; printer is co-routine 1
        mov	edx, printer
        call	init_co

        ; initialize cell co-routines
        mov	ecx,[WorldSize]
    
    initialize_co:
	inc	ebx
	mov	edx, cellFunction
	call	init_co
	loop	initialize_co
        
        ; start scheduler
        xor	ebx, ebx		; co-routine index = 0 (scheduler)
        call	start_co		; start co-routines
        
exit:
        mov	eax, sys_exit
        xor	ebx, ebx
        int	80h
        
open_error:
        print	OPEN_ERROR, 34
        jmp	exit
        
not_enough_args:
        print	NOT_ENOUGH_AGRS, 22
        jmp	exit
        
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        
;; function to be executed by every cell co-routine
;; ebx = cell index
;; ecx = above / below
;; esi = right
;; edi = left
cellFunction:
	
	push	ebp
	mov	ebp, esp
	
	sub	ebx, 2				; ebx = cell index in 'World'

do_again:
	mov	edx, [ebp+4]
	mov	dword[CurrentRow], edx
	mov	edx, [ebp+8]
	mov	dword[CurrentColumn], edx
	

	;;;;;; calculate cell state ;;;;;;
	
	mov	byte[Neighbours+ebx], 0
	
  get_above:
	cmp	dword[CurrentRow], 0
	je	first_row
	mov	ecx, dword[CurrentRow]
	dec	ecx				; above = row-1
	jmp	get_left
	
    first_row:
	mov	ecx, [WorldLength]		; above = WorldLength-1
	dec	ecx
	
  get_left:
	cmp	dword[CurrentColumn], 0
	je	first_column
	mov	edi, dword[CurrentColumn]
	dec	edi				; left = column-1
	jmp	get_right
	
    first_column:
	mov	edi, [WorldWidth]
	dec	edi				; left = WorldWidth-1
	
  get_right:
	mov	esi, [CurrentColumn]
	inc	esi				; right = column+1
	cmp	esi, [WorldWidth]
	jne	calc_neighbours
	mov	esi, 0				; right = 0
	
  calc_neighbours:
	check_neighbour ecx, edi			; above-left
	check_neighbour ecx, dword[CurrentColumn]	; above
	check_neighbour ecx, esi			; above-right
	
	check_neighbour dword[CurrentRow], edi		; left
	check_neighbour dword[CurrentRow], esi		; right
	
  get_below:
	mov	ecx, [CurrentRow]
	inc	ecx				; below = row+1
	cmp	ecx, [WorldLength]
	jne	update_below
	mov	ecx, 0				; below = 0
	
    update_below:
	check_neighbour ecx, edi			; below-left
	check_neighbour ecx, dword[CurrentColumn]	; below
	check_neighbour ecx, esi			; below-right
	
	mov	edx, 0
	mov	dl, byte[Neighbours+ebx]
	;;;;;;;; resume scheduler ;;;;;;;;
	pusha
	mov	ebx, 0
	call	resume
	popa
	
	;;;;;;; update cell state ;;;;;;;;
	
	cmp	byte[World+ebx],'1'
	jl	currently_dead
	cmp	byte[World+ebx], '9'
	je	currently_alive
	inc	byte[World+ebx]
	
    currently_alive:
	cmp	edx, 2
	je	end_update
	cmp	edx, 3
	je	end_update
	
	mov	byte[World+ebx], ' '
	jmp	end_update
	
    currently_dead:
	cmp	edx, 3
	jne	end_update
	mov	byte[World+ebx], '1'
	
    end_update:
	
	;;;;;;;; resume scheduler ;;;;;;;;
	pusha
	mov	ebx, 0
	call	resume
	popa
	
	jmp	do_again
	

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; atoi ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

atoi:
        push	ebp
        mov	ebp, esp
        push	ecx
        push	edx
        push	ebx
        mov	ecx, dword [ebp+8]	; Get argument (pointer to string)
        xor	eax,eax
        xor	ebx,ebx

atoi_loop:
        xor	edx,edx
        cmp	byte[ecx], 0
        jz	atoi_end
        imul	dword[TEN]
        mov	bl, byte[ecx]
        sub	bl,'0'
        add	eax,ebx
        inc	ecx
        jmp	atoi_loop

atoi_end:
        pop	ebx			; Restore registers
        pop	edx
        pop	ecx
        mov	esp, ebp		; Function exit code
        pop	ebp
        ret
        
        
        