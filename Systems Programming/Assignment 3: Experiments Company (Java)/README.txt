ASSIGNMENT DESCRIPTION:

This program simulates a company which consists of several scientific laboratories. 
This company receives a list of experiments, and its task is to run these experiments and finish them.
For each mission finished successfully, an award is received.
Each experiment require different tools and different kind of laboratory specialization.
In order to be able to run an experiment, all prerequirment experiments must be finished.

The simulation process starts by purchasing required equipment from a given shop, hiring new scientists and buying a whole new laboratory.
At the end of the process, the program prints out statistics that shows how well the company has done.
The statistics include the budget, money spent, money gained, and how much time it took in order too complete each experiment.

The simulation goes as follows:
The program parses the input data, then calls the Chief Scientist. 
The Chief Scientist calls his assistant, which scans the experiments and finds the ones that do not have prerequirment experiments.  
Once found, he makes sure that for each experiment there is the required equipment, and that a laboratory with the requested specialization is availible (if not, he buys it).
Then the assistant sends the experiment to the appropriate Head of Laboratory for execution.
Once an experiment is finished, it notifies the assistant, which starts the process all over again.
Once all the experiments are marked as "complete", the assistant prints the statistics and the program ends.
