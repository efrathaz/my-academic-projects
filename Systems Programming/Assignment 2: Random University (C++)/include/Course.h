#ifndef COURSE_H_
#define COURSE_H_
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cstdlib>
class Student;
class functions;
using namespace std;

class Course{
	public:
		Course();
		Course(vector<Student*> Students, string Name, int Semester, int MinGrade);
		Course(const Course &c);
		virtual void teach();
		virtual void reg(Student &s)=0;
		string getName() const;
		int getSemester() const;
		int getMin() const;
		vector<Student*> getStudents() const;
		virtual ~Course();
		Course& operator=(const Course &c);
		virtual string type()=0;

		vector<Student*> _students;

	private:
		string _name;
		int _semester;
		int _minGrade;
};
#endif /* COURSE_H_ */
