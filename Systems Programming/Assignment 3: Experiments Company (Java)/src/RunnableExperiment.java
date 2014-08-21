import java.util.Date;
import java.util.Observable;
import java.util.Observer;


public class RunnableExperiment extends Observable implements Runnable {
	
	private Experiment exp;
	private Repository rep;
	
	/**
	 * constructor
	 * @param e, experiment
	 * @param r, repository
	 * @param o, observer
	 */
	public RunnableExperiment(Experiment e, Repository r, Observer o){
		exp = e;
		rep = r;
		addObserver(o);
	}
	
	/**
	 * @return the experiment object
	 */
	public Experiment getExperiment(){
		return exp;
	}
	
	/**
	 * go over the list of required equipment of the experiment and borrow the required items from the repository
	 */
	private void borrowEquipment(){
		for (int i = 0 ; i < exp.getRequiredEquipment().size() ; i++){
			Item temp = exp.getRequiredEquipment().get(i);
			rep.borrowItem(temp);
		}
	}
	
	/**
	 * go over the list of required equipment of the experiment and return the items to the repository
	 */
	private void returnEquipment(){
		for (int i = 0 ; i < exp.getRequiredEquipment().size() ; i++){
			Item temp = exp.getRequiredEquipment().get(i);
			rep.returnItem(temp);
		}
	}
	
	/**
	 * this function makes the runnable experiment sleep for a certain amount of time
	 * @param time, number of "hours" to sleep
	 */
	private void sleep(int time){
		try{
			Thread.sleep(time*100);
		}
		catch (InterruptedException e){
			System.out.println("Sleep Interrupted");
		}
	}
	
	/**
	 * this function calculates the reward of the experiment considering the actual runtime
	 * @param actual, actual runtime of the experiment
	 * @return the reward of the experiment
	 */
	private int calculateReward(int actual){
		int reward = exp.getReward();
		if (actual > exp.getRuntime()*1.15){
			reward = exp.getReward()/10;
		}
		return reward;
	}
	
	/**
	 * this function runs the experiment: first borrows the equipment from the repository,
	 * then makes it "work" for 8 "hours" and "sleep" for 16 "hours" until the runtime is up,
	 * then returns the equipment to the repository and changes the experiment's status to "complete"
	 */
	@Override
	public void run() {
		Date start = new Date();
		int restTime = 0;
		int time = 0;
		for(time = exp.getRuntime() ; time > 8 ;time = time - 8){
			borrowEquipment();
			sleep(8);
			returnEquipment();
			sleep(16);
			restTime = restTime + 16;
		}
		borrowEquipment();
		sleep(time);
		returnEquipment();
		Date end = new Date();
		int actualRuntime = (int)(end.getTime()/100 - start.getTime()/100) - restTime;
		exp.setStatus("complete");
		setChanged();
		notifyObservers(calculateReward(actualRuntime));
	}

}