DESCRIPTION:

This program implements Conway's Game of Life (http://en.wikipedia.org/wiki/Conway's_Game_of_Life) in Intel 80x86 assembly.

The program gets as arguments:
  1. Initial configuration - a filename of a file which describes the initial state. The file includes just space ' '(dead) , '1'(alive) and a newline character after every width characters. Maximum value of length and width is 100.
  2. Length
  3. Width
  4. Number of generations (t)
  5. Printing frequency (k) - once in how many steps the program print the state of the game.

In order to run the program:
> calculator <filename> <length> <width> <t> <k>

Architecture:

Every cell in the game has its own co-routine, which calculates its next state and updates it.
The program initializes an appropriate mechanism, and control is then passed to a scheduler co-routine which decides the appropriate scheduling for the co-routines. 
A specialized co-routine called the printer prints the organism states for all the cells as a two dimensional array.