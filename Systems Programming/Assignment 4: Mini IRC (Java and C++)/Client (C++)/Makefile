 
CFLAGS:=-c -Wall -Weffc++ -g
LDFLAGS:=-Lusr/lib -lboost_system -lboost_locale -lboost_thread -lboost_filesystem -lpthread

all: bin/Writer.o bin/Reader.o bin/ConnectionHandler.o bin/Client.o bin/Encoder.o
	g++ -o bin/IRC_Client bin/ConnectionHandler.o bin/Client.o bin/Writer.o bin/Reader.o bin/Encoder.o $(LDFLAGS) 

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

bin/Writer.o: src/Writer.cpp
	g++ $(CFLAGS) -o bin/Writer.o src/Writer.cpp
	
bin/Reader.o: src/Reader.cpp
	g++ $(CFLAGS) -o bin/Reader.o src/Reader.cpp

bin/Encoder.o: src/Encoder.cpp
	g++ $(CFLAGS) -o bin/Encoder.o src/Encoder.cpp

.PHONY: clean
clean:
	rm -f bin/*
