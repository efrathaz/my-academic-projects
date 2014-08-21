#ifndef STUDENT_H_
#define STUDENT_H_
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
class course;
using namespace std;

class student{

public:
	student();
	student(string Name, vector<course*> couList);
	string getName() const;
	vector<course*> getList() const;
	void print();
	void sortCouList();
private:
	string _name;
	vector<course*> _couList;
};
#endif /* STUDENT_H_ */
