#ifndef CSCOURSE_H_
#define CSCOURSE_H_
#include "../include/Course.h"
#include "../include/Student.h"
using namespace std;

class CScourse : public Course{
public:
	CScourse();
	CScourse(vector<Student*> Students, string Name, int Semester, int MinGrade);
	CScourse(const CScourse &c);
	virtual void reg(Student &s);
	virtual string type();

};
#endif /* CSCOURSE_H_ */
