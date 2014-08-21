import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args){
		
		ArrayList<EquipmentPackage> equipment = new ArrayList<EquipmentPackage>(readEquipment(args[2]));
		ArrayList<Scientist> scientists = new ArrayList<Scientist>(readScientists(args[3]));
		ArrayList<Laboratory> laboratories = new ArrayList<Laboratory>(readLaboratories(args[4]));
		ScienceStoreImpl store = new ScienceStoreImpl(equipment, scientists, laboratories);
		ChiefScientist chief = new ChiefScientist(store);
		ChiefScientistAssistant assistant = new ChiefScientistAssistant(chief);
		chief.setAssistant(assistant);
		readExperiments(chief, args[1]);
		readData(chief, args[0]);
		chief.startWorking();
	}
	
	/**
	 * this function reads the equipment input file
	 * @return an ArrayList of equipment packages that we read from the input file
	 */
	public static ArrayList<EquipmentPackage> readEquipment(String input){
		ArrayList<EquipmentPackage> equipment = new ArrayList<EquipmentPackage>();
		BufferedReader inputStream = null;
		try{
			inputStream = new BufferedReader(new FileReader(input));
			String line;
			while((line = inputStream.readLine()) != null){
				Scanner s = new Scanner(line);
				String name = s.next();
				int number = s.nextInt();
				int cost = s.nextInt();
				EquipmentPackage temp = new EquipmentPackage(name, number, cost);
				equipment.add(temp);
				s.close();
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			}
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		Collections.sort(equipment);
		return equipment;
	}
	
	/**
	 * this function reads the scientists input file
	 * @return an ArrayList of the scientists that we read from the input file
	 */
	public static ArrayList<Scientist> readScientists(String input){
		ArrayList<Scientist> scientists = new ArrayList<Scientist>();
		BufferedReader inputStream = null;
		try{
			inputStream = new BufferedReader(new FileReader(input));
			String line;
			while((line = inputStream.readLine()) != null){
				Scanner s = new Scanner(line);
				String name = s.next();
				String spec = s.next();
				int cost = s.nextInt();
				Scientist temp = new Scientist(name, spec, cost);
				scientists.add(temp);
				s.close();
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			}
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		Collections.sort(scientists);
		return scientists;
	}
	
	/**
	 * this function reads the laboratories input file
	 * @return an ArrayList of the laboratories that we read from the input file
	 */
	public static ArrayList<Laboratory> readLaboratories(String input){
		ArrayList<Laboratory> laboratories = new ArrayList<Laboratory>();
		BufferedReader inputStream = null;
		try{
			inputStream = new BufferedReader(new FileReader(input));
			String line;
			while((line = inputStream.readLine()) != null){
				Scanner s = new Scanner(line);
				String name = s.next();
				String spec = s.next();
				int num = s.nextInt();
				int cost = s.nextInt();
				Laboratory temp = new Laboratory(name, spec, num, cost);
				laboratories.add(temp);
				s.close();
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			}
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		return laboratories;
	}
	
	/**
	 * this function reads the experiments input file and update the ChiefScientist's experiments ArrayList
	 * @param chief, a ChiefScientist whose experiments ArrayList we are updating
	 */
	public static void readExperiments(ChiefScientist chief, String input){
		BufferedReader inputStream = null;
		try{
			inputStream = new BufferedReader(new FileReader(input));
			String line;
			while((line = inputStream.readLine()) != null){
				Scanner s = new Scanner(line);
				// parse fields
				s.useDelimiter("\t");
				int ID = s.nextInt();
				String pre = s.next();
				String spec = s.next();
				String equipment = s.next();
				int runtime = s.nextInt();
				int reward = s.nextInt();
				// parse pre-requirments
				Scanner preR = new Scanner(pre);
				ArrayList<Integer> preRequirments = new ArrayList<Integer>();
				while(preR.hasNext()){
					preRequirments.add(preR.nextInt());
				}
				if (preRequirments.get(0) == 0){
					preRequirments.clear();
				}
				preR.close();
				// parse equipment
				ArrayList<Item> items = new ArrayList<Item>();
				Scanner equip = new Scanner(equipment);
				equip.useDelimiter(" ");
				while(equip.hasNext()){
					String line1 = equip.next();
					Scanner s1 = new Scanner(line1);
					s1.useDelimiter(",");
					String name = s1.next();
					int quantity = s1.nextInt();
					Item temp = new Item(name, quantity);
					items.add(temp);
					s1.close();
				}
				equip.close();
				Collections.sort(items);
				Experiment temp = new Experiment(ID, spec, runtime, preRequirments, items, reward, "incomplete");
				chief.addExperiment(temp);
				s.close();
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			}
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
	
	/**
	 * this function read the following data from the input file: an initial budget,
	 * a list of items in the repository, and a list of laboratories
	 * @param chief, a ChiefScientist whose fields we are updating
	 */
	public static void readData(ChiefScientist chief, String input){
		BufferedReader inputStream = null;
		try{
			inputStream = new BufferedReader(new FileReader(input));
			String line = inputStream.readLine();
			line = inputStream.readLine();
			// read budget
			Scanner budg = new Scanner(line);
			int budget = budg.nextInt();
			chief.setBudget(budget);
			budg.close();
			line = inputStream.readLine();
			// read items to repository
			while (!(line = inputStream.readLine()).equals("Laboratories")){
				Scanner rep = new Scanner(line);
				String name = rep.next();
				int number = rep.nextInt();
				Item e = new Item(name, number);
				chief.addToRepository(e);
				rep.close();
			}
			// read laboratories
			while((line = inputStream.readLine()) != null){
				Scanner lab = new Scanner(line);
				String name = lab.next();
				String spec = lab.next();
				int number = lab.nextInt();
				Laboratory l = new Laboratory(name, spec, number, 0);
				chief.addHeadOfLaboratory(l);
				lab.close();
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			}
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
}