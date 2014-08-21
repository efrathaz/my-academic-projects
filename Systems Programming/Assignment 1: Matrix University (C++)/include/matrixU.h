#ifndef MATRIXU_H_
#define MATRIXU_H_
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include "../include/course.h"
#include "../include/student.h"

using namespace std;

void turnToString(string fileName , vector<string> &V);
course* readCourse(string s);
void makeCourseVector(vector<string> a , vector<course*> &V);
student* readStudent(string &s);
void makeStudentVector(vector<string> a, vector<course*> &c , vector<student*> &s);
void sortStudntsCouLists(vector<student*> &v);

#endif /* MATRIXU_H_ */
