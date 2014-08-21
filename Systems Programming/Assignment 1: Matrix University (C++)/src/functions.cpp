#include "../include/matrixU.h"
#include <cmath>

void turnToString(string fileName , vector<string> &V){
	ifstream a;
	a.open(fileName.c_str());
	if (a.is_open()){
		string line;
		while (a.good()){
		  getline(a, line);
		  V.push_back(line);
		}
		a.close();
	}
}

course* readCourse(string s){
	int day = s[0]-48;
	int space = 0;
	string courseID = "";
	int i;
	for (i = 2 ; s[i] != ',' ; i++){
			courseID = courseID + s[i];
		}
	for (i = i+1 ; s[i] != '\0' && s[i] != char(13) ; i++){
		space = space*10;
		space = space + (s[i]-48);
	}
	vector<student*> v;
	course* ans = new course(v, day, courseID, space);
	return ans;
}

void makeCourseVector(vector<string> a , vector<course*> &V){
	int size = a.size();
	for (int i = 0 ; i < size && a[i] != "" ; i++){
		course* c = readCourse(a[i]);
		V.push_back(c);
	}
}

student* readStudent(string s, vector<course*> &v){
	string name = "";
	vector<course*> c;
	int i;
	for (i = 0 ; s[i] != ',' ; i++){
		name = name + s[i];
	}
	i++;
	while (i < int(s.length()) && s[i] != '\0' && s[i] != char(13)){
		string courseName = "";
		for (i = i ; i < int(s.length()) && s[i] != ',' && s[i] != '\0' && s[i] != char(13) && s[i] != ' '; i++){
			courseName = courseName + s[i];
		}
		int j;
		for (j = 0 ; courseName != v[j]->getID() || v[j]->getSpace() == 0 ; j++){}
		c.push_back(v[j]);
		i++;
	}
	student* ans = new student(name, c);
	int size = c.size();
	for (int j = 0 ; j < size ; j++){
		c[j]->addStudent(ans);
	}
	return ans;
}

void makeStudentVector(vector<string> a, vector<course*> &c , vector<student*> &s){
	int size = a.size();
	for (int i = 0 ; i < size && a[i] != "" ; i++){
		s.push_back(readStudent(a[i], c));
	}

}

void sortStudntsCouLists(vector<student*> &v){
	for(int i=0 ; i < (int)v.size(); i++){
		v[i]->sortCouList();
	}
}

