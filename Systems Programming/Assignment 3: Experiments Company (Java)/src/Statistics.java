import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Statistics {

	private int budget;
	private int moneyGained;
	private int moneySpent;
	private ArrayList<Scientist> scientistsPurchased;
	private ArrayList<EquipmentPackage> equipmentPacksPurchased;
	private ArrayList<Laboratory> laboratoriesPurchased;
	private HashMap<Integer, Experiment> completedExperiments;
	
	/**
	 * empty constructor
	 */
	public Statistics(){
		budget = 0;
		moneyGained = 0;
		moneySpent = 0;
		scientistsPurchased = new ArrayList<Scientist>();
		equipmentPacksPurchased = new ArrayList<EquipmentPackage>();
		laboratoriesPurchased = new ArrayList<Laboratory>();
		completedExperiments = new HashMap<Integer, Experiment>();
	}
	
	/**
	 * @return a HashMap of the completed experiments
	 */
	public HashMap<Integer, Experiment> getCompletedExperiments(){
		return completedExperiments;
	}
	
	/**
	 * update the budget
	 * @param i, a sum to add to the budget (might be negative)
	 */
	public void setBudget(int i){
		budget = budget + i;
	}
	
	/**
	 * update the money gained from finishing experiments
	 * @param i, a sum to add
	 */
	public void updateMoneyGained(int i){
		moneyGained = moneyGained + i;
	}
	
	/**
	 * update the list of scientists that were purchased, and the moneySpent field
	 * @param s, a scientist to add
	 */
	public void updateScientist(Scientist s){
		scientistsPurchased.add(s);
		moneySpent = moneySpent + s.getCost();
	}
	
	/**
	 * update the list of equipment packages that were purchased, and the moneySpent field
	 * @param e, a package to add
	 */
	public void updatePackage(EquipmentPackage e){
		equipmentPacksPurchased.add(e);
		moneySpent = moneySpent + e.getCost();
	}
	
	/**
	 * update the list of laboratories that were purchased, and the moneySpent field
	 * @param l, a laboratory to add
	 */
	public void updateLaboratory(Laboratory l){
		laboratoriesPurchased.add(l);
		moneySpent = moneySpent + l.getCost();
	}
	
	/**
	 * update the list of completed experiments
	 * @param e, an experiment to add
	 * @throws IllegalArgumentException, if the experiment is not completed
	 */
	public void updateExperiments(Experiment e) throws IllegalArgumentException{
		if (e.getStatus().equals("complete")){
			completedExperiments.put(e.getID(), e);
		}
		else{
			throw new IllegalArgumentException("Experiment is not completed");
		}
	}
	
	/**
	 * @return a string representing the statistics fields
	 */
	public String toString(){
		StringBuilder str = new StringBuilder("Budget = ");
		str.append(budget);
		str.append('\n');
		str.append("Money Gained = ");
		str.append(moneyGained);
		str.append('\n');
		str.append("Money Spent = ");
		str.append(moneySpent);
		str.append('\n');
		str.append("Scientists Purchased:");
		str.append('\n');
		for (int i = 0 ; i < scientistsPurchased.size() ; i++){
			str.append(scientistsPurchased.get(i).toString());
			str.append('\n');
		}
		str.append("Equipment Packages Purchased:");
		str.append('\n');
		for (int i = 0 ; i < equipmentPacksPurchased.size() ; i++){
			str.append(equipmentPacksPurchased.get(i).toString());
			str.append('\n');
		}
		str.append("Laboratories Purchased:");
		str.append('\n');
		for (int i = 0 ; i < laboratoriesPurchased.size() ; i++){
			str.append(laboratoriesPurchased.get(i).toString());
			str.append('\n');
		}
		str.append("Completed Experiments:");
		str.append('\n');
		Set<Entry<Integer, Experiment>> s = completedExperiments.entrySet();
		Iterator<Entry<Integer, Experiment>> iterator = s.iterator();
		while (iterator.hasNext()){
			str.append(iterator.next().getValue().toString());
			str.append('\n');
		}
		return str.toString();
	}

}