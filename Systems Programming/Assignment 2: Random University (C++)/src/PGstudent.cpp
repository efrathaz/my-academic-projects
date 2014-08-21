#include "../include/PGstudent.h"
#include "../include/Course.h"
#include "../include/functions.h"

PGstudent:: PGstudent() : Student(){}
PGstudent:: PGstudent(vector<Course*> Failed, int ID, string Image, int Current, int CurrentE, int NumOfElective): Student(Failed, ID, Image, Current, CurrentE, NumOfElective){}
PGstudent:: PGstudent(const PGstudent &p) : Student(p){}

void PGstudent:: study(Course &c){
	functions func;
	int grade = rand() % 101;
	int chance = rand() % 5;  // there is 20 % chance the student will leave the course
	string line = func.toString(this->getID()) + " took " + c.getName() + " and finished ";
	// chance = 0 means the student is slacking of, chance = {1, 2, 3, 4} means he isn't
	if (chance == 0){
		string line1 = func.toString(this->getID()) + " is slacking of " + c.getName();
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

char PGstudent:: type(){
	return 'P';
}
