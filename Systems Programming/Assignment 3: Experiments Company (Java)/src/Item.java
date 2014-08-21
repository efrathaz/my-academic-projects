import java.util.concurrent.Semaphore;


public class Item implements Comparable<Item>{

	private String name;
	private int quantity;
	private Semaphore available;
	
	/**
	 * empty constructor
	 */
	public Item(){
		name = "";
		quantity = 0;
		available = new Semaphore(0, true);
	}
	
	/**
	 * constructor
	 * @param n, type of item
	 * @param q, quantity
	 */
	public Item(String n, int q){
		name = n;
		quantity = q;
		available = new Semaphore(q, true);
	}
	
	/**
	 * copy constructor
	 * @param i, an item to copy
	 */
	public Item(Item i){
		name = i.name;
		quantity = i.quantity;
		available = i.available;
	}
	
	/**
	 * @return the name of the item
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return the quantity of the item
	 */
	public int getQuantity(){
		return quantity;
	}

	/**
	 * @return number of available to borrow items 
	 */
	public int getAvailable(){
		return available.availablePermits();
	}
	
	/**
	 * add more items to the quantity
	 * @param i, number to add to quantity
	 */
	public void add(int i){
		quantity = quantity +i;
		available.release(i);
	}
	
	/**
	 * borrow this item from the repository
	 * @param i, number of items to borrow
	 */
	public void borrowItem(int i){
		try {
			available.acquire(i);	
		}
		catch (InterruptedException e){
			System.out.println("Borrowing Interrupted");
		}
	}
	
	/**
	 * return the item to the repository
	 * @param i, number of items to return
	 */
	public void returnItem(int i){
		available.release(i);
	}

	/**
	 * @return a string representing the item's fields
	 */
	public String toString(){
		StringBuilder str = new StringBuilder(name);
		str.append("(");
		str.append(quantity);
		str.append(")");
		return str.toString();
	}
	
	/**
	 * compare two items by name
	 */
	@Override
	public int compareTo(Item it) {
		return name.compareTo(it.getName());
	}
}