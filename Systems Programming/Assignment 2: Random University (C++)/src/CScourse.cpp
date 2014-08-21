#include "../include/CScourse.h"

CScourse:: CScourse() : Course(){}

CScourse:: CScourse(vector<Student*> Students, string Name, int Semester, int MinGrade): Course(Students, Name, Semester, MinGrade){}

CScourse:: CScourse(const CScourse &c) : Course(c){}

void CScourse:: reg(Student &s){
	_students.push_back(&s);
}

string CScourse:: type(){
	return "Computer Science";
}
