public class EquipmentPackage implements Comparable<EquipmentPackage>{
	
	private String name;
	private int items; // number of items in the package
	private int cost;
	private int profitability; // cost / items = profitability
	
	/**
	 * empty constructor
	 */
	public EquipmentPackage(){
		name = "";
		items = 0;
		cost = 0;
		profitability = 0;
	}
	
	/**
	 * constructor
	 * @param n, name
	 * @param i, number of items
	 * @param c, cost
	 */
	public EquipmentPackage(String n, int i, int c){
		name = n;
		items = i;
		cost = c;
		profitability = c/i;
	}
	
	/**
	 * copy constructor
	 * @param e
	 */
	public EquipmentPackage(EquipmentPackage e){
		name = e.getName();
		items = e.getItems();
		cost = e.getCost();
		profitability = e.getProfitability();
	}
	
	/**
	 * @return the name of the package
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return number of items in the package
	 */
	public int getItems(){
		return items;
	}
	
	/**
	 * @return the cost of the package
	 */
	public int getCost(){
		return cost;
	}
	
	/**
	 * @return the profitability of the package
	 */
	public int getProfitability(){
		return profitability;
	}
	
	/**
	 * @return a string representing the fields of the package
	 */
	public String toString(){
		StringBuilder str = new StringBuilder(name);
		str.append(", ");
		str.append(items);
		str.append(", ");
		str.append(cost);
		return str.toString();
	}

	/**
	 * compares two packages by profitability
	 */
	@Override
	public int compareTo(EquipmentPackage e) {
		return profitability - e.getProfitability();
	}
}