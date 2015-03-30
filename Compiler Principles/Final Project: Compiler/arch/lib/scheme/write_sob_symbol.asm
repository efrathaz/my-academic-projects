

WRITE_SOB_SYMBOL:
	PUSH(FP);
	MOV(FP, SP);
	PUSH(R1);
	PUSH(R2);
	PUSH(R3);
	
	MOV(R0, FPARG(0)); /* R0 = pointer to symbol */
	MOV(R1, INDD(R0, 1)); /* R1 = Mem[R0+1] = address of the string representing this symbol = T_STRING */
	MOV(R2, INDD(R1, 1)); /* R2 = number of chars in the string = n*/
	MOV(R3, R1)
	ADD(R3, IMM(2)); /* R3 = first char in the string */
	
L_WSSYM_LOOP:
	CMP(R2, IMM(0)); /* if n == 0, exit */
	JUMP_EQ(L_WSSYM_EXIT);
	CMP(IND(R3), '\n');
	JUMP_EQ(L_WSSYM_NEWLINE);
	CMP(IND(R3), '\t');
	JUMP_EQ(L_WSSYM_TAB);
	CMP(IND(R3), '\f');
	JUMP_EQ(L_WSSYM_PAGE);
	CMP(IND(R3), '\r');
	JUMP_EQ(L_WSSYM_RETURN);
	CMP(IND(R3), '\\');
	JUMP_EQ(L_WSSYM_BACKSLASH);
	CMP(IND(R3), '\"');
	JUMP_EQ(L_WSSYM_DQUOTE);
	CMP(IND(R3), ' ');
	JUMP_LT(L_WSSYM_OCT_CHAR);
	PUSH(IND(R3));
	CALL(PUTCHAR);
	DROP(1);
	JUMP(L_WSSYM_LOOP_CONT);
L_WSSYM_DQUOTE:
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	PUSH(IMM('\"'));
	CALL(PUTCHAR);
	DROP(2);
	JUMP(L_WSSYM_LOOP_CONT);
L_WSSYM_BACKSLASH:
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	DROP(2);
	JUMP(L_WSSYM_LOOP_CONT);
L_WSSYM_RETURN:
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	PUSH(IMM('r'));
	CALL(PUTCHAR);
	DROP(2);
	JUMP(L_WSSYM_LOOP_CONT);
L_WSSYM_PAGE:
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	PUSH(IMM('f'));
	CALL(PUTCHAR);
	DROP(2);
	JUMP(L_WSSYM_LOOP_CONT);
L_WSSYM_TAB:
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	PUSH(IMM('t'));
	CALL(PUTCHAR);
	DROP(2);
	JUMP(L_WSSYM_LOOP_CONT);  
L_WSSYM_NEWLINE:
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	PUSH(IMM('n'));
	CALL(PUTCHAR);
	DROP(2);
	JUMP(L_WSSYM_LOOP_CONT);
L_WSSYM_OCT_CHAR:
	MOV(R0, IND(R3));
	MOV(R3, R0);
	REM(R3, IMM(8));
	PUSH(R3);
	DIV(R0, IMM(8));
	MOV(R3, R0);
	REM(R3, IMM(8));
	PUSH(R3);
	DIV(R0, IMM(8));
	REM(R0, IMM(8));
	PUSH(R0);
	PUSH(IMM('\\'));
	CALL(PUTCHAR);
	DROP(1);
	CALL(WRITE_INTEGER);
	DROP(1);
	CALL(WRITE_INTEGER);
	DROP(1);
	CALL(WRITE_INTEGER);
	DROP(1);
L_WSSYM_LOOP_CONT:
	INCR(R3);
	DECR(R2);
	JUMP(L_WSSYM_LOOP);
L_WSSYM_EXIT:

	POP(R3);
	POP(R2);
	POP(R1);
	POP(FP);
	RETURN;
	
	