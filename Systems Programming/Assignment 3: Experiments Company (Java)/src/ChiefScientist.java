import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ChiefScientist implements Observer {

	private ArrayList<HeadOfLaboratory> heads;
	private ArrayList<Experiment> experiments;
	private Statistics statistics;
	private ScienceStoreImpl store;
	private Repository repository;
	private ChiefScientistAssistant assistant;
	
	/**
	 * empty constructor
	 */
	public ChiefScientist(){
		heads = new ArrayList<HeadOfLaboratory>();
		experiments = new ArrayList<Experiment>();
		statistics = new Statistics();
		store = new ScienceStoreImpl();
		repository = new Repository();
		assistant = null;
	}
	
	/**
	 * constructor
	 * @param stor, a science store
	 */
	public ChiefScientist(ScienceStoreImpl stor){
		heads = new ArrayList<HeadOfLaboratory>();
		experiments = new ArrayList<Experiment>();
		statistics = new Statistics();
		store = stor;
		repository = new Repository();
		assistant = new ChiefScientistAssistant();
	}
	
	/**
	 * set the assistant field
	 * @param assist
	 */
	public void setAssistant(ChiefScientistAssistant assist){
		assistant = assist;
	}
	
	/**
	 * @return an ArrayList of the head of laboratories
	 */
	public ArrayList<HeadOfLaboratory> getHeadsList(){
		return heads;
	}
	
	/**
	 * @return an ArrayList of the experiments that need to be executed
	 */
	public ArrayList<Experiment> getExperiments(){
		return experiments;
	}
	
	/**
	 * @return the Statistics
	 */
	public Statistics getStatistics(){
		return statistics;
	}
	
	/**
	 * @return the science store
	 */
	public ScienceStoreImpl getStore(){
		return store;
	}
	
	/**
	 * @return the repository
	 */
	public Repository getRepository(){
		return repository;
	}
	
	/**
	 * adds a new head of laboratory to the list
	 * @param l, lab to add
	 * @return the new head of laboratory
	 */
	public HeadOfLaboratory addHeadOfLaboratory(Laboratory l){
		HeadOfLaboratory head = new HeadOfLaboratory(l);
		heads.add(head);
		return head;
	}
	
	/**
	 * adds an experiment to the list of experiments that need to be executed
	 * @param e, an experiment to add
	 */
	public void addExperiment(Experiment e){
		experiments.add(e);
	}

	/**
	 * adds a new item to the repository
	 * @param e, item to add
	 */
	public void addToRepository(Item e){
		repository.add(e);
	}
	
	/**
	 * update the budget in the statistics
	 * @param budget, a sum to add to the budget
	 */
	public void setBudget(int budget){
		statistics.setBudget(budget);
	}
	
	/**
	 * this function calls the assistant's run() function
	 */
	public void startWorking(){
		assistant.run();
	}
	
	@Override
	/**
	 * this function updates the statistics and notify the assistant that an experiment is complete
	 */
	public void update(Observable o, Object arg) {
		if (arg instanceof Integer){
			int reward = (Integer)arg;
			statistics.updateMoneyGained(reward);
			statistics.setBudget(reward);
		}
		if (o instanceof RunnableExperiment){
			Experiment ex = ((RunnableExperiment)o).getExperiment();
			try{
				statistics.updateExperiments(ex);
			}
			catch (IllegalArgumentException e) {
				System.out.println("Experiment is not complete!");
			}
		}
		synchronized(assistant){
			assistant.setFlag(true); // set the "Ready to Continue" flag to "true"
			assistant.notify(); // update the assistant that the experiment is done
		}
	}

}
