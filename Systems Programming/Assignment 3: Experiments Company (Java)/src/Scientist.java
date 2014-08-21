public class Scientist implements Comparable<Scientist>{

	private String name;
	private String specialization;
	private int cost;
	
	/**
	 * empty constructor
	 */
	public Scientist(){
		name = "";
		specialization = "";
		cost = 0;
	}
	
	/**
	 * constructor
	 * @param n, name
	 * @param spec, specialization
	 * @param co, cost
	 */
	public Scientist(String n, String spec, int co){
		name = n;
		specialization = spec;
		cost = co;
	}
	
	/**
	 * copy constructor
	 * @param s, a scientist to copy
	 */
	public Scientist(Scientist s){
		name = s.getName();
		specialization = s.getSpecialization();
		cost = s.getCost();
	}
	
	/**
	 * @return the name of the scientist
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return the specialization of the scientist
	 */
	public String getSpecialization(){
		return specialization;
	}
	
	/**
	 * @return the cost of the scientist
	 */
	public int getCost(){
		return cost;
	}
	
	/**
	 * @return a string representing the scientist's fields
	 */
	public String toString(){
		StringBuilder str = new StringBuilder(name);
		str.append(", ");
		str.append(specialization);
		str.append(", ");
		str.append(cost);
		return str.toString();
	}

	@Override
	public int compareTo(Scientist s) {
		return cost - s.getCost();
	}
}