ASSIGNMENT DESCRIPTION:

This program simulates a virtual university. 
The university is defined by two configuration files: courses.conf and students.conf.
The program reads the configuration files and store their data in the designated data structures.
Once all courses and students have been committed to memory, the program computes a valid university schedule.
Then it assigns a student to a course, by adding that student to the courses Vector while keeping track of the available space in each course. A student is assigned to a course only if the matching COURSE-ID appears in that students course list. Once a student is assigned to a course he must take, he cannot be assigned to a second course with the same COURSE-ID.
The program produces two schedule files: 
	* courses.out - includes a list of all participating students for each course.
	* students.out -  includes a list of all the courses each student must attend.

The main source file is matrixU.cpp.
