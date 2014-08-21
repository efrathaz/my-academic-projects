#include "../include/Course.h"
#include "../include/Student.h"

Course:: Course(): _students(), _name(""), _semester(1), _minGrade(0){}

Course:: Course(vector<Student*> Students, string Name, int Semester, int MinGrade): _students(Students), _name(Name), _semester(Semester), _minGrade(MinGrade){}

Course:: Course(const Course &c): _students(c.getStudents()), _name(c.getName()), _semester(c.getSemester()), _minGrade(c.getMin()){}

void Course:: teach(){
	while(_students.size() > 0){
		_students.back()->study(*this);
		_students.pop_back();
	}
}

string Course:: getName() const{
	return _name;
}

int Course:: getSemester() const{
	return _semester;
}

int Course:: getMin() const{
	return _minGrade;
}

vector<Student*> Course:: getStudents() const{
	return _students;
}

Course:: ~Course(){
	_name.clear();
	//the students list is a vector with pointers to Student objects which were created in the main,
	//therefore they should be deleted from the main, and not from the Course class
}

Course& Course:: operator=(const Course &c){
	if (this != &c){
		_students = c.getStudents();
		_name = c.getName();
		_semester = c.getSemester();
		_minGrade = c.getMin();
	}
	return *this;
}
