#include "../include/student.h"
#include "../include/course.h"

student:: student(): _name(""), _couList(){}
student:: student(string Name, vector<course*> couList): _name(Name), _couList(couList){}

string student:: getName() const{
	return _name;
}

vector<course*> student:: getList() const{
	return _couList;
}

void student:: print(){
	cout << _name << endl;
	for (int i = 0 ; i < (int)_couList.size() ; i++){
		cout << _couList[i]->getDay() << " " << _couList[i]->getID() << endl;
	}
}
void student:: sortCouList(){
	vector<course*> newCouList;
	for( int i=1 ; i<=7 ; i++ ){
		for( int j=0 ; j < (int)_couList.size() ; j++ ){
			if(_couList[j]->getDay() == i){
				newCouList.push_back(_couList[j]);
			}
		}
	}
	_couList = newCouList;
}
