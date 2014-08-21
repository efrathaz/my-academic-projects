import java.util.ArrayList;

public class Repository {

	private ArrayList<Item> items;
	
	/**
	 * empty constructor
	 */
	public Repository(){
		items = new ArrayList<Item>();
	}
	
	/**
	 * this function checks if an item is in stock and if we have to buy more of it
	 * @param it, item
	 * @return the number of items of the same type that we have to buy
	 */
	public int outOfStock(Item it){
		int i = 0;
		while (i < items.size() && !items.get(i).getName().equals(it.getName())){
			i++;
		}
		int inStock = 0;
		if (i < items.size()){
			inStock = items.get(i).getQuantity();
		}
		int needed = it.getQuantity();
		return (needed - inStock);
	}
	
	/**
	 * add item to the repository. if there is already an item of the same type, add to it's quantity.
	 * else, create a new item in the repository
	 * @param e, an item to add
	 */
	public void add(Item e){
		int i;
		for (i = 0 ; i < items.size() && !e.getName().equals(items.get(i).getName()) ; i++){
			// find an item of the same kind
		}
		if (i < items.size() && e.getName().equals(items.get(i).getName())){
			items.get(i).add(e.getQuantity());
		}
		else{
			Item newItem = new Item(e.getName(), e.getQuantity());
			items.add(newItem);
		}
	}

	/**
	 * @return the ArrayList of all the items in the repository
	 */
	public ArrayList<Item> getItems(){
		return items;
	}
	
	/**
	 * borrow an item from the repository
	 * @param it, an item to borrow
	 */
	public void borrowItem(Item it){
		for (int i = 0 ; i < items.size() ; i++){
			if (items.get(i).getName().equals(it.getName())){
				items.get(i).borrowItem(it.getQuantity());
			}
		}
	}
	
	/**
	 * return an item to the repository
	 * @param it, an item to return
	 */
	public void returnItem(Item it){
		for (int i = 0 ; i < items.size() ; i++){
			if (items.get(i).getName().equals(it.getName())){
				items.get(i).returnItem(it.getQuantity());
			}
		}
	}
	
}