all: out

out: out.o
	gcc -g -m32 -o out out.o

out.o: out.c
	gcc -m32 -g -c -o out.o out.c

.PHONY: clean

clean: 
	rm -f *.o out