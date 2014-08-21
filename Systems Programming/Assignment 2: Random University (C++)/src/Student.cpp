#include "../include/Student.h"
#include "../include/Course.h"
#include "../include/functions.h"

Student:: Student():  _failed(), _ID(0), _image(""), _current(0), _currentE(0), _numOfElective(0){}

Student:: Student(vector<Course*> Failed, int ID, string Image, int Current, int CurrentE, int NumOfElective):_failed(Failed), _ID(ID), _image(Image), _current(Current), _currentE(CurrentE), _numOfElective(NumOfElective){}

Student:: Student(const Student &s): _failed(s.getFailed()), _ID(s.getID()), _image(s.getImage()), _current(s.getCurrent()), _currentE(s.getCurrentE()), _numOfElective(s.getNumOfElective()){}

void Student:: reg(int semester, vector<Course*> & Courses, vector<Course*> & ELcourses){
	functions func;
	// check if there are any failed courses to repeat
	if (_failed.size() != 0){
		int f_semester = _failed.back()->getSemester();
		// register to the courses that fit this semester
		while(_failed.size() != 0 && (f_semester % 2 == semester % 2)){
			Course *c(_failed.back());
			c->reg(*this);
			_failed.pop_back();
			string line = func.toString(_ID) + " is taking " + c->getName() + " from " + c->type();
			func.print(line);
		}
	}
	// if there are not any courses to repeat
	else{
		int i;
		// register to the department's courses of this semester
		for (i = _current ; i < (int)Courses.size() && Courses[_current]->getSemester() == Courses[i]->getSemester() ; i++){
			Courses[i]->reg(*this);
			string line = func.toString(_ID) + " is taking " + Courses[i]->getName() + " from " + Courses[i]->type();
			func.print(line);
		}
		_current = i;
		int j;
		 // register to the elective courses of this semester, if needed
		for (j = _currentE ; j <_numOfElective && ELcourses[_currentE]->getSemester()%2 == ELcourses[j]->getSemester()%2 ; j++){
			ELcourses[j]->reg(*this);
			string line = func.toString(_ID) + " is taking " + ELcourses[j]->getName() + " from Elective Courses";
			func.print(line);
		}
		_currentE = j;
	}
}

int Student:: getID() const{
	return _ID;
}

string Student:: getImage() const{
	return _image;
}

vector<Course*> Student:: getFailed() const{
	return _failed;
}

int Student:: getCurrent() const{
	return _current;
}

int Student:: getCurrentE() const{
	return _currentE;
}

int Student:: getNumOfElective() const{
	return _numOfElective;
}

Student:: ~Student(){
	_image.clear();
}

Student& Student:: operator=(const Student &s){
	if (this != &s){
		_ID = s.getID();
		_image = s.getImage();
		_current = s.getCurrent();
		_currentE = s.getCurrentE();
		_numOfElective = s.getNumOfElective();
	}
	return *this;
}

bool Student:: isGraduated(int courses, int ELcourses){
	bool flag = false;
	if (_current == courses && _currentE == ELcourses && (int)_failed.size() == 0){
		flag = true;
	}
	return flag;
}
