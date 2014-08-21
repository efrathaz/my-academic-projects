public interface ScienceStore {
	/**
	 * 
	 * @param type, required type of equipment
	 * @param quantity, required number of items
	 * @return suitable equipment package
	 * @pre: EquipmentQuantity( @param type) >= @param quantity
	 * @post: EquipmentQuantity( @param type) <= @pre EquipmentQuantity(@param type) - @param quantity
	 */
	public EquipmentPackage buyEquipment(String type, int quantity);
	
	/**
	 * 
	 * @param spec, required specialization
	 * @return suitable scientist
	 * @pre: ScientistQuantity (@param spec) > 0
	 * @post: ScientistQuantity (@param spec) == @pre ScientistQuantity (@param spec) - 1
	 */
	public Scientist buyScientist(String spec);
	
	/**
	 * 
	 * @param spec, required specialization
	 * @return suitable Laboratory
	 * @pre: LaboratoryQuantity (@param spec) > 0
	 * @post: LaboratoryQuantity (@param spec) == @pre LaboratoryQuantity (@param spec) - 1
	 */
	public Laboratory buyLaboratory(String spec);
	
	/**
	 * 
	 * @param type, required type of equipment
	 * @param quantity, required number of items
	 * @return a package with enough items
	 * @pre: EquipmentQuantity(@param type) > @param quantity
	 * @post: this.equals( @pre this)
	 */
	public int findBestPackage(String type, int quantity);
	
	/**
	 * 
	 * @param spec, required specialization
	 * @return a scientist with the appropriate specialization
	 * @pre: ScientistQuantity(@param spec) > 0
	 * @post: this.equals( @pre this)
	 */
	public int findBestScientist(String spec);
	
	/**
	 * 
	 * @param spec, required specialization
	 * @return a Laboratory with the appropriate specialization
	 * @pre: LaboratoryQuantity(@param spec) > 0
	 * @post: this.equals( @pre this)
	 */
	public int findBestLaboratory(String spec);
	
	/**
	 * 
	 * @param type
	 * @return number of existing items of type 'type'
	 * @pre none
	 * @post none
	 */
	public int EquipmentQuantity(String type);
	
	/**
	 * 
	 * @param spec
	 * @return number of existing scientists with specialization 'spec'
	 * @pre none
	 * @post none
	 */
	public int ScientistQuantity(String spec);
	
	/**
	 * 
	 * @param spec
	 * @return number of existing laboratories with specialization 'spec'
	 * @pre none
	 * @post none
	 */
	public int LaboratoryQuantity(String spec);

	/**
	 * 
	 * @param e, equipment package to be added
	 * @pre none
	 * @post EquipmentQuantity( @param e.getName()) == @pre EquipmentQuantity( @param e.getName()) +  @param e.getItems()
	 */
	public void addPackage(EquipmentPackage e);
	
	/**
	 * 
	 * @param s, scientist to be added
	 * @pre none
	 * @post ScientistQuantity( @param s.getSpecialization) == @pre ScientistQuantity( @param s.getSpecialization)+1
	 */
	public void addScientist(Scientist s);
	
	/**
	 * 
	 * @param l, laboratory to be added
	 * @pre none
	 * @post LaboratoryQuantity( @param l.getSpecialization) == @pre LaboratoryQuantity( @param l.getSpecialization)+1
	 */
	public void addLab(Laboratory l);
	
	/**
	 * this method is overridden and can work only on copy objects from the implementing class type
	 * @param s, a store to be copied
	 * @pre none
	 * @post this.equals( @param s)
	 */
	public void copy(ScienceStore s);
	
}