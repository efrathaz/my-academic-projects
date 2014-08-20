CC		:=	gcc 
CC_FLAGS	:=	-m32
ASM		:=	nasm
ASM_FLAGS	:=	-f elf 

all: task1

task1:	
	$(ASM) $(ASM_FLAGS)  task1.s -o task1.o
	$(CC) $(CC_FLAGS) main1.c  task1.o -o task1.bin

.PHONY: clean

clean: 
	rm -f *.o task1.bin