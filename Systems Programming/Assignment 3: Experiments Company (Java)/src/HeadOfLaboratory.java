import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeadOfLaboratory{

	private Laboratory lab;
	private ExecutorService exe;
	
	/**
	 * empty constructor
	 */
	public HeadOfLaboratory(){
		lab = new Laboratory();
		exe = Executors.newFixedThreadPool(lab.getNumOfScientists());
	}
	
	/**
	 * constructor
	 * @param l, a laboratory object
	 */
	public HeadOfLaboratory(Laboratory l){
		lab = l;
		exe = Executors.newFixedThreadPool(l.getNumOfScientists());
	}
	
	/**
	 * @return the laboratory field
	 */
	public Laboratory getLab(){
		return lab;
	}
	
	/**
	 * this function receives a runnable experiment and executes it 
	 * @param experiment, runnable experiment to execute
	 */
	public void execute(RunnableExperiment experiment) {
		experiment.getExperiment().setStatus("in progress");
		exe.execute(experiment);
	}

	/**
	 * this function shuts down the thread pool of the head of laboratory
	 */
	public void shutDown(){
		exe.shutdown();
	}
}