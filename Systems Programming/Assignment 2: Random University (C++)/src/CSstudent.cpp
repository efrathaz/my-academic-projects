#include "../include/CSstudent.h"
#include "../include/Course.h"
#include "../include/functions.h"

CSstudent:: CSstudent() : Student(){}

CSstudent:: CSstudent(vector<Course*> Failed, int ID, string Image, int Current, int CurrentE, int NumOfElective): Student(Failed, ID, Image, Current, CurrentE, NumOfElective){}

CSstudent:: CSstudent(const CSstudent &p) : Student(p){}

void CSstudent:: study(Course &c){
	functions func;
	int grade = rand() % 101;
	int chance = rand() % 4; // there is 25 % the student will quit the course
	string line = func.toString(this->getID()) + " took " + c.getName() + " and finished ";
	// chance = 0 means the student quits, chance = {1, 2, 3} means he isn't
	if (chance == 0){
		string line1 = func.toString(this->getID()) + " quits course " + c.getName();
		func.print(line1);
		_failed.push_back(&c);
	}
	else{
		if (grade < c.getMin()){
			_failed.push_back(&c);
			line = line + "unsuccessfully";
		}
		else{
			line = line + "successfully";
		}
		func.print(line);
	}
}

char CSstudent:: type(){
	return 'C';
}
