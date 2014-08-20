CALCULATOR DESCRIPTION:

This is a Reverse Polish notation (RPN) calculator, written in Intel 80x86 assembly.
RPN is a mathematical notation in which every operator follows all of its operands, for example 3 + 4 would be presented as "3 4 +".
Input and output operands are in hexadecimal representation.

Any input number is pushed onto an operand stack, and each operation is performed on operands which are taken (and removed) from the stack. The result, if any, is pushed onto the stack.
The operand stack can hold up to 5 numbers.

Every number is represented by a linked list: every byte is held in a link (the most significant byte is in the last link).

The calculator can perform the following operations:
  * Addition (+) - Pops two operands from the stack and pushs their sum onto the stack.
  * Pop-and-print (p) - Pops an operand from the stack and prints it.
  * Duplicate (d) - Pops an operand from the stack and pushes two copies of that operand onto the stack.
  * Log2 (l) - Pops an operand from the stack and pushes its log2 onto the stack.
  * Number of '1' bits (n) - Pops an operand from the stack, calculates the number of '1' bits in its binary representation and pushes it onto the stack.
  * Quit (q) - Prints the number of operations performed by the calculator and exits the program.
  