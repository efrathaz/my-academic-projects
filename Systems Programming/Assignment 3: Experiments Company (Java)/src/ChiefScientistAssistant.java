public class ChiefScientistAssistant implements Runnable {

	private ChiefScientist chief;
	private boolean readyToContinue;
	
	/**
	 * empty constructor
	 */
	public ChiefScientistAssistant(){
		chief = new ChiefScientist();
		readyToContinue = true;
	}
	
	/**
	 * constructor
	 * @param c, a ChiefScientist 
	 */
	public ChiefScientistAssistant(ChiefScientist c){
		chief = c;
		readyToContinue = true;
	}
	
	/**
	 * this function changes the value of the "Ready to Continue" flag 
	 * @param b, a value to be updated
	 */
	public void setFlag(boolean b){
		readyToContinue = b;
	}
	
	/**
	 * this function buys equipment from the store and adds it to the repository
	 * also, it updates the statistics that a new package has been bought
	 * @param type, type of package needed
	 * @param quantity, number of items of that type
	 */
	public void buyEquipment(String type, int quantity){
		int number = 0;
		while (number < quantity){
			EquipmentPackage e = chief.getStore().buyEquipment(type, quantity);
			if (e != null){
				Item i = new Item(e.getName(), e.getItems());
				chief.addToRepository(i);
				chief.getStatistics().updatePackage(e);
				chief.getStatistics().setBudget(0 - e.getCost());
				number = number + e.getItems();
			}
			else{
				break;
			}
		}
	}
	
	/**
	 * this function buys a new scientist for a specific lab and updates the statistics
	 * @param l, head of laboratory to add a scientist to
	 */
	public void buyScientist(HeadOfLaboratory l){
		Scientist s = chief.getStore().buyScientist(l.getLab().getSpecialization());
		l.getLab().addScientist(s);
		chief.getStatistics().updateScientist(s);
		chief.getStatistics().setBudget(0 - s.getCost());
	}
	
	/**
	 * this function buys a new laboratory from the store and adds it to the HeadOfLaboratory list in the chief
	 * also, it updates the statistics that a new laboratory has been bought
	 * @param specialization
	 * @return
	 */
	public HeadOfLaboratory buyLaboratory(String specialization){
		Laboratory lab = chief.getStore().buyLaboratory(specialization);
		if (lab != null){
			chief.getStatistics().updateLaboratory(lab);
			chief.getStatistics().setBudget(0 - lab.getCost());
			return chief.addHeadOfLaboratory(lab);
		}
		return null;
	}
	
	private HeadOfLaboratory findLab(String spec){
		for (int i = 0 ; i < chief.getHeadsList().size() ; i++){
			if (chief.getHeadsList().get(i).getLab().getSpecialization().equals(spec)){
				return chief.getHeadsList().get(i);
			}
		}
		return null;
	}
	
	@Override
	/**
	 * this function goes over the list of experiments waiting to be executed, checks for pre-requirments
	 * for every experiment, if all the pre-requirments are completed, makes sure there is enough equipment
	 * and fins a laboratory, and then execute the experiments.
	 * this goes on until all experiments are done, then shuts down all running applications and prints the statistics
	 */
	public void run() {
		boolean done = false;
		while (!done){
			done = true;
			synchronized (this) {
				if (!readyToContinue){
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			readyToContinue = false;
			// go over the list of experiments
			for (int i = 0 ; i < chief.getExperiments().size() ; i++){
				Experiment exp = chief.getExperiments().get(i);
				// find an experiment with no pre-requirments
				done = done && (exp.getStatus().equals("complete"));
				if (exp.getStatus().equals("incomplete") && exp.noPrerequirments(chief.getStatistics())){
					// check if there is enough equipment in the repository for the experiment. if not - buy.
					for (int j = 0 ; j < exp.getRequiredEquipment().size() ; j++){
						int hasToBuy = chief.getRepository().outOfStock(exp.getRequiredEquipment().get(j));
						if (hasToBuy > 0){
							buyEquipment(exp.getRequiredEquipment().get(j).getName(), hasToBuy);
						}
					}
					// find a laboratory
					HeadOfLaboratory lab = findLab(exp.getSpecialization());
					if (lab == null){
						lab = buyLaboratory(exp.getSpecialization());
					}
					// send experiment to the head of laboratory for execution
					RunnableExperiment runExp = new RunnableExperiment(exp, chief.getRepository(), chief);
					lab.execute(runExp);
				}
			}
		}
		// shut down all running applications
		for (int i = 0 ; i < chief.getHeadsList().size() ; i++){
			chief.getHeadsList().get(i).shutDown();
		}
		System.out.println(chief.getStatistics().toString());
	}

}