#ifndef CSSTUDENT_H_
#define CSSTUDENT_H_
#include "../include/Student.h"
using namespace std;

class CSstudent : public Student{
public:
	CSstudent();
	CSstudent(vector<Course*> Failed, int ID, string Image, int Current, int CurrentE, int NumOfElective);
	CSstudent(const CSstudent &p);
	virtual void study(Course &c);
	virtual char type();
};
#endif /* CSSTUDENT_H_ */
