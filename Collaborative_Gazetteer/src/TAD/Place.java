package TAD;

import com.bbn.openmap.geo.Geo;

public class Place {

	private int year;
	private String location;
	private String nameFilter;
	private String county;
    private Geo geometry;
        
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
	
	

}
