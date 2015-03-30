/* io/putchar.asm
 * Print a char to stdout
 * 
 * Programmer: Mayer Goldberg, 2010
 */

 PUTCHAR:
  PUSH(FP);
  MOV(FP, SP);
  MOV(R0, FPARG(0));
  OUT(IMM(2), R0);		/* print arg0 to STDOUT */
  POP(FP);
  RETURN;
