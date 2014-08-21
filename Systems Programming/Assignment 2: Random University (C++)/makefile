all: RandomU

RandomU: bin/Course.o bin/Student.o bin/image.o bin/CScourse.o bin/CSstudent.o bin/PGcourse.o bin/PGstudent.o bin/ELcourse.o bin/functions.o bin/RandomU.o
	g++ -o bin/RandomU bin/Course.o bin/Student.o bin/image.o bin/CScourse.o bin/CSstudent.o bin/PGcourse.o bin/PGstudent.o bin/ELcourse.o bin/functions.o bin/RandomU.o -L/usr/lib -lopencv_core -lopencv_highgui -lopencv_imgproc

bin/Course.o: src/Course.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/Course.o src/Course.cpp

bin/Student.o: src/Student.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/Student.o src/Student.cpp

bin/image.o: src/image.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/image.o src/image.cpp

bin/CScourse.o: src/CScourse.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/CScourse.o src/CScourse.cpp

bin/CSstudent.o: src/CSstudent.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/CSstudent.o src/CSstudent.cpp

bin/PGcourse.o: src/PGcourse.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/PGcourse.o src/PGcourse.cpp

bin/PGstudent.o: src/PGstudent.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/PGstudent.o src/PGstudent.cpp

bin/ELcourse.o: src/ELcourse.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/ELcourse.o src/ELcourse.cpp

bin/functions.o: src/functions.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/functions.o src/functions.cpp

bin/RandomU.o: src/RandomU.cpp
	g++ -g -Wall -Weffc++ -c -I/usr/include/opencv -L/usr/lib -Linclude -o bin/RandomU.o src/RandomU.cpp


.PHONY: clean
clean:
	rm -fR bin/*