package TAD;

import java.util.ArrayList;

public class Group {
	
	private ArrayList<Place> places = new ArrayList<Place>();
	private Expression exp;
	private Statistics stats;
	private String repository;
	private Place centroid;
		
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
	public Expression getExp() {
		return exp;
	}
	public void setExp(Expression exp) {
		this.exp = exp;
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
	
}
