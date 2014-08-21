ASSIGNMENT DESCRIPTION:

This program simulates a virtual university.
The university is defined by three configuration files: courses.conf curriculum.conf and students.conf.
There are many semesters, and courses are given in a specific semester (autumn, spring). Students can fail in a course they took (and then repeat it), and there are pre-conditions to a course.
The program creates student and course instances based on the configuration files, then simulate a 3-5 years BA studies at Random university (from assignment 1). 
The Politics and Government (PG) departments courses open only with the approval of the MALAG. If the MALAG flag is ”yes”, they study as usual, and if its ”no” they cannot register any course.
The MALAG flag is passed to the program as a parameter, as in:
	./randomU MALAG=no

After reading configuration files, the program simulates a degree at Random University.
The simulation progresses semester by semester. Each student has to take all courses available to him according to the corriculum. A student that fails in a course cannot take any course from the next semester untill he repeats the failed course(s), and succeeds in it.
Courses are given either at autumn or at the spring semseter, according to the courses file.

Once no more students can be registered, teaching will commence: the teach function for each course. This function calls the study function of all its students.
Once study has been called, each student generates a random number, that is compared to the minimum grade, and the student passes or fails according to that.
In addition, the CS student have a 25% chance of not handling the workload, and fail the course.
PG Students may be too lazy and have a 20% chance to slack off a course.

Once the defined number of years has passed, all students that have finished all their duties graduate, while the rest never get a degree.
The program produces two images and shows it on screen, the first picture for CS students and the second picture for PG students. 
In each picture the images of all students of the relevant department appear in a line sorted according to the students name. The pictures of the graduated students will be in colors, while the pictures of the students who did not graduate will be in greyscales.
This is done using OpenCV.

The simulation is terminated once the number of years have passed, and all students pictures have been put in a list on the screen.
