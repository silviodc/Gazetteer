package TAD;


import com.bbn.openmap.geo.Geo;

public class Place implements Cloneable {

	private int year;
	private int ID;
	private String location;
	private String nameFilter;
	private County county;
    private Geo geometry;
    private String type;
    private String local;
    private boolean used=false;
    private boolean ambiguo=false;
    private String repository;
    private boolean partOf;
    private Place father;
    private Place relation;
    private String relationName;
    
    
    
    public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public boolean isPartOf() {
		return partOf;
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public Place getRelation() {
		return relation;
	}

	public void setRelation(Place relation) {
		this.relation = relation;
	}

	public void setPartOf(boolean partOf) {
		this.partOf = partOf;
	}

	public Place getFather() {
		return father;
	}

	public void setFather(Place father) {
		this.father = father;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public Place (String location, Geo geo){
    	this.location = location;
    	this.geometry = geo;
    }
    
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Place(int year, String location, String nameFilter, County county,Geo geo, String repository, int index ) {
		this.year = year;
		this.location = location;
		this.nameFilter = nameFilter;
		this.county = county;
		this.geometry = geo;
		this.repository=repository;
		this.ID = index;
	}
	public Place( String location ) {
		this.location = location;
		
	}
		
	
	public Place(int year, String location, County county,Geo geo, String repository ) {
		this.year = year;
		this.location = location;
		this.county = county;
		this.geometry = geo;
		this.repository=repository;
	}
	
	public Place(int year, String location, String nameFilter, County county,String repository, int index) {
		this.year = year;
		this.location = location;
		this.nameFilter = nameFilter;
		this.county = county;
		this.repository=repository;
		this.ID = index;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNameFilter() {
		return nameFilter;
	}
	public void setNameFilter(String nameFilter) {
		this.nameFilter = nameFilter;
	}
	public County getCounty() {
		return county;
	}
	public void setCounty(County county) {
		this.county = county;
	}
	public Geo getGeometry() {
		return geometry;
	}
	public void setGeometry(Geo geometry) {
		this.geometry = geometry;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean getIspolygon() {
		// TODO Auto-generated method stub
		return false;
	}
	public String getGeoBuilded() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isAmbiguo() {
		return ambiguo;
	}

	public void setAmbiguo(boolean ambiguo) {
		this.ambiguo = ambiguo;
	}
	
	//Sobreescreva o metodo clone.  
    public Place clone() throws CloneNotSupportedException{  
            return (Place) super.clone();  
     }  

}
