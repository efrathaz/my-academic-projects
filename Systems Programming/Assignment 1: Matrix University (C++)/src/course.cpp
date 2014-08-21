#include "../include/course.h"
#include "../include/student.h"

course:: course(): _stuList(), _day(0), _courseID(""), _space(0){}
course:: course(vector<student*> stuList, int day, string courseID, int space):_stuList(stuList), _day(day), _courseID(courseID), _space(space){}

vector<student*> course:: getList() const{
	return _stuList;
}

int course:: getDay() const{
	return _day;
}

string course:: getID() const{
	return _courseID;
}

int course:: getSpace() const{
	return _space;
}

void course:: addStudent(student* s){
	_stuList.push_back(s);
	_space--;
}

void course:: print(){
	cout << _day << " " << _courseID << endl;
	for (int i = 0 ; i < (int)_stuList.size() ; i++){
		cout << _stuList[i]->getName() << endl;
	}

}
