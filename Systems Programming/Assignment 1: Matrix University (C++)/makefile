# define some Makefile variables for the compiler and compiler flags
# to use Makefile variables later in the Makefile: $()
CC = g++
CFLAGS  = -g -Wall -Weffc++

# All Targets
all: hw1

# Tool invocations
# Executable "hello" depends on the files hello.o and run.o.
hw1: bin/matrixU.o bin/functions.o bin/course.o bin/student.o
	@echo 'Building target: hw1'
	@echo 'Invoking: C++ Linker'
	$(CC) -o bin/hw1 bin/matrixU.o bin/functions.o bin/course.o bin/student.o
	@echo 'Finished building target: hw1'
	@echo ' '

# Depends on the source and header files
bin/matrixU.o: src/matrixU.cpp
	$(CC) $(CFLAGS) -c -Linclude -o bin/matrixU.o src/matrixU.cpp

# Depends on the source and header files 
bin/functions.o: src/functions.cpp
	$(CC) $(CFLAGS) -c -Linclude -o bin/functions.o src/functions.cpp

# Depends on the source and header files 
bin/course.o: src/course.cpp
	$(CC) $(CFLAGS) -c -Linclude -o bin/course.o src/course.cpp

# Depends on the source and header files 
bin/student.o: src/student.cpp
	$(CC) $(CFLAGS) -c -Linclude -o bin/student.o src/student.cpp

#Clean the build directory
clean: 
	rm -rf bin/*
