/* io/write.asm
 * Takes a pointer to a null-terminated string, 
 * and prints it to STDOUT.
 * 
 * Programmer: Mayer Goldberg, 2010
 */

 WRITE:
  PUSH(FP);
  MOV(FP, SP);
  PUSH(R1);
  PUSH(R2);
  MOV(R1, FPARG(0));	 /* R1 = pointer to string */
 L_WRITE0:
  MOV(R2, IND(R1)); 	/* R2 = first char */
  CMP(R2, IMM('\0')); 	/* while (R2 =! '\0') */
  JUMP_EQ(L_WRITE_END);
  PUSH(R2);
  CALL(PUTCHAR);		/* print R2 */
  POP(R2);
  INCR(R1);				/* R1++ */
  JUMP(L_WRITE0);
 L_WRITE_END:
  POP(R2);
  POP(R1);
  POP(FP);
  RETURN;

