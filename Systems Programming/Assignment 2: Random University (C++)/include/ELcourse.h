#ifndef ELCOURSE_H_
#define ELCOURSE_H_
#include "../include/Course.h"
#include "../include/Student.h"
using namespace std;

class ELcourse : public Course{
public:
	ELcourse();
	ELcourse(vector<Student*> Students, string Name, int Semester, int MinGrade);
	ELcourse(const ELcourse &c);
	virtual void reg(Student &s);
	virtual string type();
};
#endif /* ELCOURSE_H_ */
