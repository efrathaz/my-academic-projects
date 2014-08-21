#ifndef READER_H_
#define READER_H_
#include "../include/ConnectionHandler.h"
#include "../include/utf8.h"
#include "../include/Encoder.h"
#include <boost/thread.hpp>
#include <string>
#include <iostream>
using namespace std;

class Reader{

public:
	Reader();
	virtual ~Reader();
	void readMsg(ConnectionHandler* connectionHandler);
	void readProsesPrint(string line);
};

#endif /* READER_H_ */
