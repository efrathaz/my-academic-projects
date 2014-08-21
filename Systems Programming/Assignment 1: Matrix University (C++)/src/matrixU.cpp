#include "../include/matrixU.h"

int main(){

	vector<string> C;
	turnToString("courses.conf", C );

	vector<course*> courses;
	makeCourseVector(C , courses);

	vector<string> S;
	turnToString("students.conf", S );

	vector<student*> students;
	makeStudentVector(S, courses , students);

	sortStudntsCouLists(students);

	ofstream outputCourses("courses.out");
	if (outputCourses.is_open()){
		for (int i = 0 ; i < (int)courses.size() ; i++){
			outputCourses << courses[i]->getDay() << " " << courses[i]->getID() << endl;
			vector<student*> list = courses[i]->getList();
			for (int i = 0 ; i < (int)list.size() ; i++){
				outputCourses << list[i]->getName() << endl;
			}
			outputCourses << "" << endl;
		}
	}
	outputCourses.close();


	ofstream outputStudents("students.out");
	if (outputStudents.is_open()){
		for (int i = 0 ; i < (int)students.size() ; i++){
			outputStudents << students[i]->getName() << endl;
			vector<course*> list = students[i]->getList();
			for (int i = 0 ; i < (int)list.size() ; i++){
				outputStudents << "  " << list[i]->getDay() << " " << list[i]->getID() << endl;
			}
			outputStudents << "" << endl;
		}
	}
	outputStudents.close();


	for ( int i=0 ; i < int(courses.size()) ; i++ ){
		delete courses[i];
	}
	for ( int i=0 ; i < int(students.size()) ; i++ ){
		delete students[i];
	}

}

