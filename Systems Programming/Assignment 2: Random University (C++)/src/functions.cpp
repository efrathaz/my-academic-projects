#include "../include/functions.h"
#include "../include/Course.h"
#include "../include/Student.h"

using namespace std;

functions:: functions(){}


void functions:: readCurriculum(string& fileName, int &numOfSemesters, int &CS_elective, int &PG_elective){
	vector<string> V;
	int k = 0;
	ifstream a;
	a.open(fileName.c_str());
	if (a.is_open()){
		string line = "";
		while (a.good()){
		  getline(a, line);
		  int end = line.find_last_not_of(" \t\f\v\n\r\0");
		  line.erase(end+1);
		  V.push_back(line);
		}
		for(k = 0 ; (V[0])[k] != '=' ; k++){
			// move index k to the number of semesters
		}
		for (k++ ; k < (int)(V[0]).length() && (V[0])[k] != '\0' && (V[0])[k] != (char)13 ; k++){
			numOfSemesters = numOfSemesters*10;
			numOfSemesters = numOfSemesters + (int)((V[0])[k])-48;
		}

		for (k = 3 ; k < (int)(V[1]).length() && (V[1])[k] != '\0' && (V[1])[k] != (char)13  ; k++){
			CS_elective = CS_elective*10;
			CS_elective = CS_elective + (int)((V[1])[k])-48;

		}
		for (k = 3 ; k < (int)(V[2]).length() && (V[2])[k] != '\0' && (V[2])[k] != (char)13 ; k++){
			PG_elective = PG_elective*10;
			PG_elective = PG_elective + (int)((V[2])[k])-48;
		}
	}
	a.close();
	V.clear();
}

void functions:: readCourses(string& fileName, vector<Course*> &CS_courses, vector<Course*> &PG_courses, vector<Course*> &EL_courses){
	ifstream a;
	a.open(fileName.c_str());
	if (a.is_open()){
		string line = "";
		while (a.good()){
		  getline(a, line);
		  int end = line.find_last_not_of(" \t\f\v\n\r\0");
		  line.erase(end+1);
		  splitCourse(line, CS_courses, PG_courses, EL_courses);
		}
	}
	a.close();
}

void functions:: splitCourse(string line, vector<Course*> &CS_courses, vector<Course*> &PG_courses, vector<Course*> &EL_courses){
	string courseName;
	int semester = 0;
	int minGrade = 0;
	int j;
	for(j = 0 ; line[j] != ',' ; j++){
		// move index j to where the name of the course begins
	}
	j++;
	while (line[j] !=','){
		courseName = courseName + line[j];
		j++;
	}
	j++;
	while (line[j] != ','){
		semester = semester*10;
		semester = semester + ((int)line[j])-48;
		j++;
	}
	j++;
	while (line[j] != '\0' && line[j] != (char)13 && line[j] != ' ' && j < (int)line.length()){
		minGrade = minGrade*10;
		minGrade = minGrade + ((int)line[j])-48;
		j++;
	}
	// determine type of course
	vector<Student*> stu;
	if (line[0] == 'C'){
		Course *c = new CScourse(stu, courseName, semester, minGrade);
		CS_courses.push_back(c);
	}
	else if (line[0] == 'P'){
		Course *c = new PGcourse(stu, courseName, semester, minGrade);
		PG_courses.push_back(c);
	}
	else{
		Course *c = new ELcourse(stu, courseName, semester, minGrade);
		EL_courses.push_back(c);
	}
}

void functions:: readStudents(string& fileName, vector<Student*> &CS_students, vector<Student*> &PG_students, int CS_elective, int PG_elective){
	ifstream a;
	a.open(fileName.c_str());
	if (a.is_open()){
		string line;
		while (a.good()){
			getline(a, line);
			int end = line.find_last_not_of(" \t\f\v\n\r\0");
			line.erase(end+1);
			splitStudent(line, CS_students, PG_students, CS_elective, PG_elective);
		}
	}
	a.close();
}

void functions:: splitStudent(string line, vector<Student*> &CS_students, vector<Student*> &PG_students, int CS_elective, int PG_elective){
	string id;
	char dep;
	string image = "";
	vector<Course*> failed;
	int i;
	for (i = 0 ; line[i] != ',' ; i++){
		id = id + line[i];
	}
	int ID = turnToInt(id);
	dep = line[i+1];
	for (i = i+1; line[i] != ',' ; i++){
		// move index i to where the image address begins
	}
	for (i = i+1 ; line[i] != '\0' && line[i] != (char)13 && line[i] != ' ' ; i++){
		image = image + line[i];
	}
	// determine type of student
	if (dep == 'C'){
		Student *s = new CSstudent(failed, ID, image, 0, 0, CS_elective);
		CS_students.push_back(s);
	}
	else{
		Student *s = new PGstudent(failed, ID, image, 0, 0, PG_elective);
		PG_students.push_back(s);
	}
}

void functions:: sortCourses(vector<Course*> &V, int numOfSemesters){
	vector<Course*> temp;
	for (int i = 1 ; i < numOfSemesters ; i++){
		for (int j = 0 ; j < (int)V.size() ; j++){
			if(V[j]->getSemester() == i){
				temp.push_back(V[j]);
			}
		}
	}
	V = temp;
}

int functions:: turnToInt(string s){
	int ans = 0;
	for (int i = 0 ; i < (int)s.length() ; i++){
		ans = ans*10;
		ans = ans + (int)s[i]-48;
	}
	return ans;
}

///// taken from http://www.reviewmylife.co.uk/blog/2008/06/25/quicksort-code-in-c/
int functions:: partition(vector<Student*> &a, int left, int right, int pivotIndex){
    int pivot = a[pivotIndex]->getID();
    do{
        while (a[left]->getID() < pivot) left++;
        while (a[right]->getID() > pivot) right--;
        if (left < right && a[left]->getID() != a[right]->getID()){
            swap(a[left], a[right]);
        }
        else{
            return right;
        }
    }
    while (left < right);
    return right;
}

void functions:: quicksort(vector<Student*> &a, int left, int right){
    if (left < right){
        int pivot = (left + right) / 2; // middle
        int pivotNew = partition(a, left, right, pivot);
        quicksort(a, left, pivotNew - 1);
        quicksort(a, pivotNew + 1, right);
    }
}

void functions:: print(string line){
	ofstream output;
	output.open("random.log", ios::app);
	if (output.is_open()){
		output << line << endl;
	}
	output.close();
}

void functions:: printSemester(int semester){
	string line;
	if (semester % 10 == 1){
		line = toString(semester) + "st semester of the Random U";
	}
	else if (semester % 10 == 2){
		line = toString(semester) + "nd semester of the Random U";
	}
	else if (semester % 10 == 3){
		line = toString(semester) + "rd semester of the Random U";
	}
	else{
		line = toString(semester) + "th semester of the Random U";
	}
	print(line);
}

string functions:: toString(int number){
	string ans;
	while (number > 0){
		ans = (char)((number % 10) + 48) + ans;
		number = number / 10;
	}
	return ans;
}

void functions:: deleteVector(vector<Course*> &V){
	for (int i = 0 ; i < (int)V.size() ; i++){
		delete V[i];
	}
}

void functions:: deleteVector(vector<Student*> &V){
	for (int i = 0 ; i < (int)V.size() ; i++){
		delete V[i];
	}
}
