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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;

public class Repository implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private String path;
	private String name;
	private String path_polygon;
	private int columns[];
    private Statistics numbers;
	private ArrayList<Place> places = new  ArrayList<Place>();
	private OMGeo.Polygon polygon;
	
	public Repository(){}
	
	public Repository(String path, String name, int[] columns,String path_polygon) {
		super();
		this.path = path;
		this.name = name;
		this.columns = columns;
		this.path_polygon = path_polygon;
	}
	
	public OMGeo.Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(OMGeo.Polygon polygon) {
		this.polygon = polygon;
	}

	public ArrayList<Place> getPlaces() {
		return places;
	}

	public void setPlaces(ArrayList<Place> places) {
		this.places = places;
	}

	public String getPath() {
		return path;
	}
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setPath(String path) {
		this.path = path;
	}
	public int[] getColumns() {
		return columns;
	}
	public void setColumns(int[] columns) {
		this.columns = columns;
	}
	
	public void setData(String[] data) {
		int index = Integer.parseInt(data[5]);
		
		County c;
        
		if(data[2]==null || data[2].equals("null"))
        	c = new County("");
        else
        	c = new County(data[2]);
        
		if(!data[3].equals("")&& !data[4].equals("") && !data[0].equals("")){
			float x =transformFloat(data[3]);
            float y = transformFloat(data[4]);            
			//constructor (int year, String location, String nameFilter, String county, Geo geometry)
            Geo geometry = new Geo(x,y);
             Place ptemp = new Place(Integer.parseInt(data[0]),data[1]," ",c,geometry,this.name,index);
             ptemp.setRepository(this.name);
			 places.add(ptemp);
			 }else{
				 Place ptemp =new Place(Integer.MAX_VALUE,data[1]," ",c,this.name,index);	 
				 ptemp.setRepository(this.name);
				 places.add(ptemp);
			 }
	}
	public Statistics getNumbers() {
		return numbers;
	}
	public void setNumbers(Statistics numbers) {
		this.numbers = numbers;
	}
	public void build_polygon_to_repository() throws FileNotFoundException, IOException{
		Out_Polygon poly = new Out_Polygon();
		polygon = poly.buildPolygon(path_polygon);
	}

	private float transformFloat(String numero) {
	        float valor = 0;
	        char n[] = numero.toCharArray();
	        numero = "";
	        for (int i = 0; i < n.length; i++) {
	            if (n[i] == ',') {
	                numero += ".";
	            }
	            numero += n[i];
	        }
	        try {
	            valor = Float.parseFloat(numero);
	        } catch (Exception e) {
	            return 0;
	        }
	        return valor;
	    }
}
