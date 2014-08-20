#include <stdio.h>
#include<stdlib.h>

#define	BUFFER_SIZE	(128)

extern int isDivisibleBy3(int num);
int check(int evenBits, int oddBits);

int main(int argc, char** argv)
{
  char buf[BUFFER_SIZE];
	
  printf("Enter number: ");
  fflush(stdout);

  fgets(buf, BUFFER_SIZE, stdin);
  int num = atoi(buf);

  isDivisibleBy3(num);

  return 0;
}


int check(int evenBits, int oddBits){
  
  int num = evenBits - oddBits;
  int abs = (num > 0) ? num : (-num);
  
  if (abs == 0){
    return 1;
  }
  else if (abs == 1 || abs == 2){
    return -1;
  }
  else{
    isDivisibleBy3(abs);
  }
  
  return 0;
}