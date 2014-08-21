#ifndef FUNCTIONS_H_
#define FUNCTIONS_H_
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include "../include/CScourse.h"
#include "../include/PGcourse.h"
#include "../include/ELcourse.h"
#include "../include/CSstudent.h"
#include "../include/PGstudent.h"
using namespace std;

class functions{

public:
	functions();
	void readCurriculum(string& fileName, int &numOfSemesters, int &CS_elective, int &PG_elective);
	void readCourses(string& fileName, vector<Course*> &CS_courses, vector<Course*> &PG_courses, vector<Course*> &EL_courses);
	void splitCourse(string line, vector<Course*> &CS_courses, vector<Course*> &PG_courses, vector<Course*> &EL_courses);
	void readStudents(string& fileName, vector<Student*> &CS_students, vector<Student*> &PG_students, int CS_elective, int PG_elective);
	void splitStudent(string line, vector<Student*> &CS_students, vector<Student*> &PG_students, int CS_elective, int PG_elective);
	void sortCourses(vector<Course*> &V, int numOfSemesters);
	int turnToInt(string s);
	int partition(vector<Student*> &a, int left, int right, int pivotIndex);
	void quicksort(vector<Student*> &a, int left, int right);
	void print(string line);
	void printSemester(int semester);
	string toString(int number);
	void deleteVector(vector<Course*> &V);
	void deleteVector(vector<Student*> &V);
};
#endif /* FUNCTIONS_H_ */
