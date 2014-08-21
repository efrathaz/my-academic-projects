#include "../include/functions.h"
#include "../include/image.h"
using namespace std;

int main(int argc, char** args){

	srand(time(NULL));
	functions func;
	ofstream a;
	a.open("output.log");

	// check if the MALAG approves PG studies
	bool MALAG = true;
	if( argc > 1){
		if(args[1][6] == 'n'){
			MALAG = false;
		}
	}

	//////// READING INPUT FILES ////////

	int numOfSemesters = 0;
	int CS_elective = 0; // number of elective courses a CS student has to take
	int PG_elective = 0; // number of elective courses a PG student has to take
	string s1("curriculum.conf");
	func.readCurriculum(s1, numOfSemesters, CS_elective, PG_elective);
	s1.clear();

	vector<Course*> CS_courses;
	vector<Course*> PG_courses;
	vector<Course*> EL_courses;
	string s2("courses.conf");
	func.readCourses(s2, CS_courses, PG_courses, EL_courses);
	s2.clear();

	vector<Student*> CS_students;
	vector<Student*> PG_students;
	string s3("students.conf");
	func.readStudents(s3, CS_students, PG_students, CS_elective, PG_elective);
	s3.clear();

	// sort courses and students
	func.sortCourses(CS_courses, numOfSemesters);
	func.sortCourses(PG_courses, numOfSemesters);
	func.sortCourses(EL_courses, numOfSemesters);
	func.quicksort(CS_students, 0, CS_students.size()-1);
	func.quicksort(PG_students, 0, PG_students.size()-1);

	// check if the MALAG approves politics & government studies
	if (!MALAG){
		for (int i = 0 ; i < (int)PG_students.size() ; i++){
			string s = PG_students[i]->getID() + " is being denied his education";
			func.print(s);
		}
	}

	//////// A DEGREE SIMULATION ////////

	for (int i = 1 ; i <= numOfSemesters ; i++){
		func.printSemester(i);

		///// REGISTRATION //////
		for (int j = 0 ; j < (int)CS_students.size() ; j++){
			CS_students[j]->reg(i, CS_courses, EL_courses);
		}
		if (MALAG){
			for (int j = 0 ; j < (int)PG_students.size() ; j++){
				PG_students[j]->reg(i, PG_courses, EL_courses);
			}
		}

		///// TEACHING /////
		for (int j = 0 ; j < (int)CS_courses.size() ; j++){
			CS_courses[j]->teach();
		}
		if (MALAG){
			for (int j = 0 ; j < (int)PG_courses.size() ; j++){
				PG_courses[j]->teach();
			}
		}
		for (int j = 0 ; j < (int)EL_courses.size() ; j++){
			EL_courses[j]->teach();
		}
	}

	///// GRADUATION /////

	// make a list of all the students who started their studies
	vector<Student*> newList;
	newList = CS_students;
	if (MALAG){
		for (int i = 0 ; i < (int)PG_students.size() ; i++){
			newList.push_back(PG_students[i]);
		}
		func.quicksort(newList, 0, newList.size()-1);
	}

	ImageOperations opr;
	int CS_size = CS_students.size();
	int PG_size = PG_students.size();
	ImageLoader CS_picture = ImageLoader(100, 100*CS_size);
	ImageLoader PG_picture = ImageLoader(100, 100*PG_size);
	int CS_counter = 0;
	int PG_counter = 0;

	for (int i = 0 ; i < (int)newList.size() ; i++){
		// resize image
		ImageLoader srcPic(newList[i]->getImage());
		ImageLoader newPic = ImageLoader(100, 100);
		opr.resize(srcPic.getImage(),newPic.getImage());
		srcPic.getImage().release();

		// graduation for CS students
		if (newList[i]->type() == 'C'){
			string line = func.toString(newList[i]->getID()) + " has ";
			if((newList[i])->isGraduated((int)CS_courses.size(), CS_elective)){
				line = line + "graduated";
				opr.copy_paste_image(newPic.getImage(), CS_picture.getImage(), CS_counter*100);
				CS_counter++;
			}
			else{
				line = line + "not graduated";
				ImageLoader greyscale(100, 100);
				opr.rgb_to_greyscale(newPic.getImage(), greyscale.getImage());
				opr.copy_paste_image(greyscale.getImage(), CS_picture.getImage(), CS_counter*100);
				CS_counter++;
				greyscale.getImage().release();
			}
			func.print(line);
		}
		// graduation for PG students
		else{
			string line = func.toString(newList[i]->getID()) + " has ";
			if((newList[i])->isGraduated((int)PG_courses.size(), PG_elective)){
				line = line + "graduated";
				opr.copy_paste_image(newPic.getImage(), PG_picture.getImage(), PG_counter*100);
				PG_counter++;
			}
			else{
				line = line + "not graduated";
				ImageLoader greyscale = ImageLoader(100, 100);
				opr.rgb_to_greyscale(newPic.getImage(), greyscale.getImage());
				opr.copy_paste_image(greyscale.getImage(), PG_picture.getImage(), PG_counter*100);
				PG_counter++;
				greyscale.getImage().release();
			}
			func.print(line);
		}
		newPic.getImage().release();
	}

	CS_picture.saveImage("CS.jpg");
	PG_picture.saveImage("PG.jpg");
	CS_picture.displayImage();
	PG_picture.displayImage();

	///// DELETE DATA /////

	// delete courses
	func.deleteVector(CS_courses);
	func.deleteVector(PG_courses);
	func.deleteVector(EL_courses);

	// delete students
	func.deleteVector(CS_students);
	func.deleteVector(PG_students);

	// delete images
	CS_picture.getImage().release();
	PG_picture.getImage().release();

	a.close();
}
