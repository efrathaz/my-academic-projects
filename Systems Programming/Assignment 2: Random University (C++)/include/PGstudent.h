#ifndef PGSTUDENT_H_
#define PGSTUDENT_H_
#include "../include/Student.h"
using namespace std;

class PGstudent : public Student{
public:
	PGstudent();
	PGstudent(vector<Course*> Failed, int ID, string Image, int Current, int CurrentE, int NumOfElective);
	PGstudent(const PGstudent &p);
	virtual void study(Course &c);
	virtual char type();
};
#endif /* PGSTUDENT_H_ */
