#ifndef COURSE_H_
#define COURSE_H_
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
class student;
using namespace std;

class course{
public:
	course();
	course(vector<student*> stuList, int day, string courseID, int space);
	vector<student*> getList() const;
	int getDay() const;
	string getID() const;
	int getSpace() const;
	void addStudent(student* s);
	void print();

private:
	vector<student*> _stuList;
	int _day;
	string _courseID;
	int _space;
};

#endif /* COURSE_H_ */
