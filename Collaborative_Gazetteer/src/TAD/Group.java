package TAD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.geo.OMGeo.Polygon;

public class Group implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private ArrayList<Place> places = new ArrayList<Place>();
	private List<String> County = new ArrayList<String>();
	private String type;
	private Statistics stats;
	private String repository;
	private Place centroid;
	private Polygon poly;
		
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	public ArrayList<Place> getPlaces() {
		return places;
	}
	public void setPlaces(ArrayList<Place> places) {
		this.places = places;
	}
	public String getExp() {
		return type;
	}
	public void setExp(String exp) {
		this.type = exp;
	}
	public Statistics getStats() {
		return stats;
	}
	public void setStats(Statistics stats) {
		this.stats = stats;
	}
	public Place getCentroid() {
		return centroid;
	}
	public void setCentroid(Place centroid) {
		this.centroid = centroid;
	}
	public Polygon getPoly() {
		return poly;
	}
	public void setPoly(Polygon poly) {
		this.poly = poly;
	}
	public List<String> getCounty() {
		return County;
	}
	public void setCounty(List<String> county) {
		County = county;
	}
	
}
