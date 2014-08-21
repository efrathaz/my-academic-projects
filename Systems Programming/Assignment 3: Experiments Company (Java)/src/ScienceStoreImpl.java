import java.util.ArrayList;

public class ScienceStoreImpl implements ScienceStore {

	private ArrayList<EquipmentPackage> packages;
	private ArrayList<Scientist> scientists;
	private ArrayList<Laboratory> laboratories;
	
	public ScienceStoreImpl(){
		packages = new ArrayList<EquipmentPackage>();
		scientists = new ArrayList<Scientist>();
		laboratories = new ArrayList<Laboratory>();
	}
	
	public ScienceStoreImpl(ArrayList<EquipmentPackage> e, ArrayList<Scientist> s, ArrayList<Laboratory> l){
		packages = new ArrayList<EquipmentPackage>();
		for (int i = 0 ; i < e.size() ; i++){
			EquipmentPackage temp = new EquipmentPackage(e.get(i));
			packages.add(temp);
		}
		scientists = new ArrayList<Scientist>();
		for (int i = 0 ; i < s.size() ; i++){
			Scientist temp = new Scientist(s.get(i));
			scientists.add(temp);
		}
		laboratories = new ArrayList<Laboratory>();
		for (int i = 0 ; i < l.size() ; i++){
			Laboratory temp = new Laboratory(l.get(i));
			laboratories.add(temp);
		}
	}
	
	
	@Override
	public EquipmentPackage buyEquipment(String type, int quantity) {
		int i = findBestPackage(type, quantity);
		if (i >= 0){
			EquipmentPackage temp = packages.get(i);
			packages.remove(i);
			return temp;
		}
		return null;
	}

	@Override
	public Scientist buyScientist(String spec) {
		int i = findBestScientist(spec);
		if (i >= 0){
			Scientist temp = scientists.get(i);
			scientists.remove(i);
			return temp;
		}
		return null;
	}

	@Override
	public Laboratory buyLaboratory(String spec) {
		int i = findBestLaboratory(spec);
		if (i >= 0){
			Laboratory temp = laboratories.get(i);
			laboratories.remove(i);
			return temp;
		}
		return null;
	}

	@Override
	public int findBestPackage(String type, int quantity){
		for (int i = 0 ; i < packages.size() ; i++){
			if (packages.get(i).getName().equals(type) && packages.get(i).getItems() >= quantity){
				return i;
			}
		}
		for (int i = 0 ; i < packages.size(); i++){
			if (packages.get(i).getName().equals(type)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int findBestScientist(String spec){
		for (int i = 0 ; i < scientists.size() ; i++){
			if (scientists.get(i).getSpecialization().equals(spec)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int findBestLaboratory(String spec){
		for (int i = 0 ; i < laboratories.size() ; i++){
			if (laboratories.get(i).getSpecialization().equals(spec)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int EquipmentQuantity(String type){
		int ans = 0;
		for (int i = 0 ; i < packages.size() ; i++){
			if (packages.get(i).getName().equals(type)){
				ans = ans + packages.get(i).getItems();
			}
		}
		return ans;
	}
	
	@Override
	public int ScientistQuantity(String spec){
		int ans = 0;
		for (int i = 0 ; i < scientists.size() ; i++){
			if (scientists.get(i).getSpecialization().equals(spec)){
				ans++;
			}
		}
		return ans;
	}
	
	@Override
	public int LaboratoryQuantity(String spec){
		int ans = 0;
		for (int i = 0 ; i < laboratories.size() ; i++){
			if (laboratories.get(i).getSpecialization().equals(spec)){
				ans++;
			}
		}
		return ans;
	}
	
	@Override
	public void addPackage(EquipmentPackage e){
		packages.add(e);
	}
	
	@Override
	public void addScientist(Scientist s){
		scientists.add(s);
	}
	
	@Override
	public void addLab(Laboratory l){
		laboratories.add(l);
	}
	
	@Override
	public void copy(ScienceStore s){
		if(s instanceof ScienceStoreImpl){
			ScienceStoreImpl newS = (ScienceStoreImpl)s;	
			packages = new ArrayList<EquipmentPackage>(newS.packages);
			scientists = new ArrayList<Scientist>(newS.scientists);
			laboratories = new ArrayList<Laboratory>(newS.laboratories);
		}
	}
	
	@Override
	public boolean equals(Object s){
		boolean ans = false;
		if (s instanceof ScienceStoreImpl){
			ans = packages.equals(((ScienceStoreImpl)s).packages) && scientists.equals(((ScienceStoreImpl)s).scientists) && laboratories.equals(((ScienceStoreImpl)s).laboratories);
		}
		return ans;
	}
}