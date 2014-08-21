public class Laboratory {

	private String headOfLaboratory;
	private String specialization;
	private int numberOfScientists;
	private int cost;
	private int profitability; // cost / items = profitability

	/**
	 * empty constructor
	 */
	public Laboratory(){
		headOfLaboratory = "";
		specialization = "";
		numberOfScientists = 0;
		cost = 0;
		profitability = 0;
	}
	
	/**
	 * constructor
	 * @param name
	 * @param spec
	 * @param num
	 * @param co
	 */
	public Laboratory(String name, String spec, int num, int co) {
		headOfLaboratory = name;
		specialization = spec;
		numberOfScientists = num;
		cost = co;
		profitability = co/num;
	}
	
	/**
	 * copy constructor
	 * @param l, a laboratory to copy
	 */
	public Laboratory(Laboratory l){
		headOfLaboratory = l.getHeadName();
		specialization = l.getSpecialization();
		numberOfScientists = l.getNumOfScientists();
		cost = l.getCost();
		profitability = l.getProfitability();
	}

	/**
	 * @return name of the head of laboratory
	 */
	public String getHeadName(){
		return headOfLaboratory;
	}
	
	/**
	 * @return the laboratory's specialization
	 */
	public String getSpecialization(){
		return specialization;
	}
	
	/**
	 * @return number of scientists in the laboratory
	 */
	public int getNumOfScientists(){
		return numberOfScientists;
	}
	
	/**
	 * @return the cost of the laboratory
	 */
	public int getCost(){
		return cost;
	}
	
	/**
	 * @return the profitability of the laboratory
	 */
	public int getProfitability(){
		return profitability;
	}
	
	/**
	 * adds scientists to the laboratory
	 * @param s, scientist to add
	 */
	public void addScientist(Scientist s){
		numberOfScientists = numberOfScientists + 1;
	}
	
	/**
	 * @return a string representing the laboratory's fields
	 */
	public String toString(){
		StringBuilder str = new StringBuilder(headOfLaboratory);
		str.append(", ");
		str.append(specialization);
		str.append(", ");
		str.append(numberOfScientists);
		str.append(", ");
		str.append(cost);
		return str.toString();
	}
}