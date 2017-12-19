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

public class Statistics implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	private int allrecords;
	private int onlyLocalityAndCounty;
	private int onlyPlace;
	private int onlyCounty;
	private int noRecord;
	private int out_polygon;
	private int[][] coordinates_per_year;
	
		
	public int[][] getCoordinates_per_year() {
		return coordinates_per_year;
	}
	public void setCoordinates_per_year(int[][] coordinates_per_year) {
		this.coordinates_per_year = coordinates_per_year;
	}
	public int getAllrecords() {
		return allrecords;
	}
	public void setAllrecords(int allrecords) {
		this.allrecords = allrecords;
	}
	public int getOnlyLocalityAndCounty() {
		return onlyLocalityAndCounty;
	}
	public void setOnlyLocalityAndCounty(int onlyLocalityAndCounty) {
		this.onlyLocalityAndCounty = onlyLocalityAndCounty;
	}
	public int getOnlyPlace() {
		return onlyPlace;
	}
	public void setOnlyPlace(int onlyPlace) {
		this.onlyPlace = onlyPlace;
	}
	public int getOnlyCounty() {
		return onlyCounty;
	}
	public void setOnlyCounty(int onlyCounty) {
		this.onlyCounty = onlyCounty;
	}
	public int getNoRecord() {
		return noRecord;
	}
	public void setNoRecord(int noRecord) {
		this.noRecord = noRecord;
	}
	
	public int getOut_polygon() {
		return out_polygon;
	}
	public void setOut_polygon(int out_polygon) {
		this.out_polygon = out_polygon;
	}
	public void print_information(){
        System.out.println("Todos registros: "+allrecords);
        System.out.println("Somente local e municipio: "+onlyLocalityAndCounty);
        System.out.println("Somente local: "+onlyPlace);
        System.out.println("Somente municipio: "+onlyCounty);
        System.out.println("Nenhum registro: "+noRecord);
	}
}
