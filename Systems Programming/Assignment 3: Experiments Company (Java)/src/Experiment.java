import java.util.ArrayList;

public class Experiment {

	private int ID;
	private String specialization;
	private int runtime;
	private ArrayList<Integer> prerequirments;
	private ArrayList<Item> requiredEquipment;
	private int reward;
	private String status;
	private String OriginalPrerequirments;
	
	/**
	 * empty constructor
	 */
	public Experiment(){
		ID = 0;
		specialization = "";
		runtime = 0;
		prerequirments = new ArrayList<Integer>();
		requiredEquipment = new ArrayList<Item>();
		reward = 0;
		status = "incomplete";
		OriginalPrerequirments = "";
	}
	
	/**
	 * constructor
	 * @param id
	 * @param spec
	 * @param run
	 * @param pre
	 * @param items
	 * @param rew
	 * @param sta
	 */
	public Experiment(int id, String spec, int run, ArrayList<Integer> pre, ArrayList<Item> items, int rew, String sta){
		ID = id;
		specialization = spec;
		runtime = run;
		prerequirments = new ArrayList<Integer>();
		for (int i = 0 ; i < pre.size() ; i++){
			prerequirments.add(pre.get(i));
		}
		requiredEquipment = new ArrayList<Item>();
		for (int i = 0 ; i < items.size() ; i++){
			Item temp = new Item(items.get(i));
			requiredEquipment.add(temp);
		}
		reward = rew;
		status = sta;
		StringBuilder str = new StringBuilder("");
		for (int i = 0 ; i < pre.size() ; i++){
			str.append(pre.get(i));
			str.append(", ");
		}
		OriginalPrerequirments = str.toString();
	}
	
	/**
	 * @return the id of the experiment
	 */
	public int getID(){
		return ID;
	}
	
	/**
	 * @return the specialization of the experiment
	 */
	public String getSpecialization(){
		return specialization;
	}
	
	/**
	 * @return the runtime of the experiment
	 */
	public int getRuntime(){
		return runtime;
	}
	
	/**
	 * @return the ArrayList of prerequirments for the experiment
	 */
	public ArrayList<Item> getRequiredEquipment(){
		return requiredEquipment;
	}
	
	/**
	 * @return the reward for this experiment
	 */
	public int getReward(){
		return reward;
	}
	
	/**
	 * @return the status of this experiment
	 */
	public String getStatus(){
		return status;
	}
	
	/**
	 * this function updates the status of the experiment
	 * @param s, a status to be updated
	 */
	public void setStatus(String s){
		status = s;
	}
	
	/**
	 * this function goes over the list of completed experiments and checks if the experiment's 
	 * prerequirments are completed
	 * @param s, statistics whose list of complete experiments is scanned
	 * @return true if all the prerequirments are completed, false otherwise
	 */
	public boolean noPrerequirments(Statistics s){
		for (int i = 0 ; i < prerequirments.size() ; i++){
			// if the experiment from the pre-requirment list is in the list of completed experiments
			if (s.getCompletedExperiments().containsKey(prerequirments.get(i))){
				prerequirments.remove(i);
			}
			else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return a string representing the experiment's fields
	 */
	public String toString(){
		StringBuilder str = new StringBuilder("");
		str.append(ID);
		str.append(", ");
		str.append(OriginalPrerequirments);
		str.append(specialization);
		str.append(", ");
		for (int i = 0 ; i < prerequirments.size() ; i++){
			str.append(prerequirments.get(i));
			str.append(", ");
		}
		str.append("Runtime = ");
		str.append(runtime);
		str.append(", ");
		str.append("Reward = ");
		str.append(reward);
		return str.toString();
	}
}