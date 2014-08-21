#include "../include/Reader.h"

Reader:: Reader(){}

Reader:: ~Reader(){}

void Reader:: readMsg(ConnectionHandler* connectionHandler){
	Encoder encoder;
	while(true){
		string answer;
		if(!(connectionHandler->getLine(answer))){ // try to read Ascii message from the server
			cout << "Disconnected. Exiting...\n" << endl;
			break;
		}
		else{
			string answer_UTF8 = encoder.fromAsciiToUtf8(answer); // convert message from Ascii to UTF-8
			cout << answer_UTF8;
		}
	}
	boost::this_thread::yield();
}
