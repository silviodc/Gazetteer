package TAD;

import com.bbn.openmap.geo.Geo;

public class Place {

	private int year;
	private String location;
	private String nameFilter;
	private String county;
    private Geo geometry;
    private String type;
    private boolean used=false;
    private boolean ambiguo=false;
    
    public Place (String location, Geo geo){
    	this.location = location;
    	this.geometry = geo;
    }
    
	public Place(int year, String location, String nameFilter, String county,Geo geo ) {
		this.year = year;
		this.location = location;
		this.nameFilter = nameFilter;
		this.county = county;
		this.geometry = geo;
	}
	public Place(int year, String location, String nameFilter, String county) {
		this.year = year;
		this.location = location;
		this.nameFilter = nameFilter;
		this.county = county;
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
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
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

}
