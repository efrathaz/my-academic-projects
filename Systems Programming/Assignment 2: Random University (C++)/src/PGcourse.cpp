#include "../include/PGcourse.h"
#include "../include/Student.h"

PGcourse:: PGcourse() : Course(){}

PGcourse:: PGcourse(vector<Student*> Students, string Name, int Semester, int MinGrade): Course(Students, Name, Semester, MinGrade){}

PGcourse:: PGcourse(const PGcourse &c) : Course(c){}

void PGcourse:: reg(Student &s){
	_students.push_back(&s);
}

string PGcourse:: type(){
	return "Politics and Government";
}
