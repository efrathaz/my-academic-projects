#ifndef STUDENT_H_
#define STUDENT_H_
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <stdlib.h>
#include <time.h>
class Course;
class functions;
using namespace std;

class Student{
	public:
		Student();
		Student(vector<Course*> Failed, int ID, string Image, int Current, int CurrentE, int NumOfElective);
		Student(const Student &s);
		virtual void study(Course &c)=0;
		int getID() const;
		string getImage() const;
		vector<Course*> getFailed() const;
		int getCurrent() const;
		int getCurrentE() const;
		int getNumOfElective() const;
		virtual ~Student();
		void reg(int semester, vector<Course*> & Courses, vector<Course*> & ELcourses);
		Student& operator=(const Student &s);
		bool isGraduated(int courses, int ELcourses);
		virtual char type()=0;

		vector<Course*> _failed; // courses that need to be repeated

	private:
		int _ID;
		string _image;
		int _current; // shows the progress of the course taking
		int _currentE; //shows the progress of the elective course taking
		int _numOfElective; // the number of elective courses the student has to take
};
#endif /* STUDENT_H_ */
