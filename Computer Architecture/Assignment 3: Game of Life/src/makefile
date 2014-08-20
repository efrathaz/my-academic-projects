all: calculator

calculator: main.o coroutines.o scheduler.o printer.o
	ld -melf_i386 main.o coroutines.o scheduler.o printer.o -o calculator

main.o: main.s
	nasm -f elf main.s -l listing.lst -o main.o

coroutines.o: coroutines.s
	nasm -f elf coroutines.s -o coroutines.o

scheduler.o: scheduler.s
	nasm -f elf scheduler.s -o scheduler.o

printer.o: printer.s
	nasm -f elf printer.s -o printer.o

	
.PHONY: clean

clean:
	rm -f *.o calculator