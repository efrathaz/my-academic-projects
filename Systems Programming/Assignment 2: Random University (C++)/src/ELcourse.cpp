#include "../include/ELcourse.h"

ELcourse:: ELcourse() : Course(){}

ELcourse:: ELcourse(vector<Student*> Students, string Name, int Semester, int MinGrade): Course(Students, Name, Semester, MinGrade){}

ELcourse:: ELcourse(const ELcourse &c) : Course(c){}

void ELcourse:: reg(Student &s){
	_students.push_back(&s);
}

string ELcourse:: type(){
	return "Elective Courses";
}
