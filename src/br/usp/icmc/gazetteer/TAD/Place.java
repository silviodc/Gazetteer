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
import java.util.List;

import com.bbn.openmap.geo.Geo;


public class Place  implements Cloneable,Serializable {

	/**
	 * 
	 */
	private int index = -1;	//denotes which Cluster it belongs to
	
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int year;
	private int ID;
	private int interation;
	private String location;
	private String nameFilter;
	private County county;
    private Geo geometry;
    private List<String> types;
    private boolean used=false;
    private String repository="";  
    private boolean goldStandart;
    private String wktgoldStandart;
    public int getID() {
		return ID;
	}

	public int getInteration() {
		return interation;
	}

	public void setInteration(int interation) {
		this.interation = interation;
	}

	public void setID(int iD) {
		ID = iD;
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
   

	public Place(int year, String location, String nameFilter, County county,Geo geo, String repository, int index ) {
		super();
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
	
	
	public String getWktgoldStandart() {
		return wktgoldStandart;
	}
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	public void setWktgoldStandart(String wktgoldStandart) {
		this.wktgoldStandart = wktgoldStandart;
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

	public boolean getIspolygon() {
		return false;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public boolean isGoldStandart() {
		return goldStandart;
	}

	public void setGoldStandart(boolean goldStandart) {
		this.goldStandart = goldStandart;
	}

	//Sobreescreva o metodo clone.  
    public Place clone() throws CloneNotSupportedException{  
            return (Place) super.clone();  
     }

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}  
	


}
