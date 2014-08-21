#ifndef PGCOURSE_H_
#define PGCOURSE_H_
#include "../include/Course.h"
using namespace std;

class PGcourse : public Course{
public:
	PGcourse();
	PGcourse(vector<Student*> Students, string Name, int Semester, int MinGrade);
	PGcourse(const PGcourse &c);
	virtual void reg(Student &s);
	virtual string type();
};
#endif /* PGCOURSE_H_ */
