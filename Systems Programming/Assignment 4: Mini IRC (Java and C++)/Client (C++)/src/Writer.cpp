#include "../include/Writer.h"

Writer:: Writer(){}

Writer:: ~Writer(){}

void Writer:: sendMsg(ConnectionHandler* connectionHandler){
	while(true){
		string line;
		getline(cin,line);

		Encoder encoder;
		string line_Ascii = encoder.fromUtf8ToAscii(line); // convert message from UTF-8 to Ascii

		if (line.compare("QUIT")==0) {
		    cout << "Conversation ended. Exiting...\n" << endl;
		    break;
		}

		if (!(connectionHandler->sendLine(line_Ascii))) { // try to send Ascii message to the server
		    cout << "Disconnected. Exiting...\n" << endl;
		    break;
		}
	}
	boost::this_thread::yield();
}
