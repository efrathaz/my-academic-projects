#include <stdlib.h>
#include <boost/locale.hpp>
#include "../include/ConnectionHandler.h"
#include "../include/utf8.h"
#include "../include/Encoder.h"
#include "../include/Writer.h"
#include "../include/Reader.h"
using namespace std;

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        cerr << "Usage: " << argv[0] << " host port" << endl << endl;
        return -1;
    }
    string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        cerr << "Cannot connect to " << host << ":" << port << endl;
        return 1;
    }

    Writer write;
    Reader read;

    boost::thread th1(&Writer:: sendMsg, write, &connectionHandler);
    boost::thread th2(&Reader:: readMsg, read, &connectionHandler);

    th1.join();
    th2.join();

    connectionHandler.close();

    return 0;
}
