#ifndef WRITER_H_
#define WRITER_H_
#include "../include/utf8.h"
#include "../include/Encoder.h"
#include "../include/ConnectionHandler.h"
#include <boost/thread.hpp>
#include <string>
#include <iostream>
using namespace std;

class Writer{

public:
	Writer();
	virtual ~Writer();
	void sendMsg(ConnectionHandler* connectionHandler);
};


#endif /* WRITER_H_ */
