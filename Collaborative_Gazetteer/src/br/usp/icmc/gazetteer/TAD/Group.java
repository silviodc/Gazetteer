/**
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.icmc.gazetteer.TAD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bbn.openmap.geo.OMGeo.Polygon;

public class Group implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private List<Place> places = new ArrayList<Place>();
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
	public  List<Place> getPlaces() {
		return places;
	}
	public void setPlaces(Set<Place> places) {
		this.places.addAll(places);
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
