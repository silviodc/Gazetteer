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
package br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import com.bbn.openmap.geo.OMGeo.Polygon;

import br.usp.icmc.gazetteer.TAD.Place;

public class Out_Polygon {
	
	public boolean insidePolygon(OMGeo.Polygon poly, float x, float y){       
	       return poly.isPointInside(new Geo(x,y));
	}
	
	public boolean insidePolygon(OMGeo.Polygon poly, Geo point){       
	       return poly.isPointInside(point);
	}
	
	public int count_out_Polygon(OMGeo.Polygon poly, ArrayList<Place> places){
		int out_polygon=0;
		for(Place p: places){
			if(p.getGeometry()!=null)
				if(!insidePolygon(poly,p.getGeometry())){
					out_polygon++;	
				}
		}
		
		return out_polygon;
	}
	
	public void clean_noise_coordinates(OMGeo.Polygon poly, ArrayList<Place> places){
		for(int i=0;i<places.size();i++){
			if(places.get(i).getGeometry()!=null)
				if(!insidePolygon(poly,places.get(i).getGeometry())){
					places.get(i).setGeometry(null);	
				}
		}
		
	}
	
	
	 public static OMGeo.Polygon buildPolygon(String path) throws FileNotFoundException, IOException{
	       
	        String line;
	        ArrayList<Float> p1 =new ArrayList<Float>();
	        ArrayList<Float> p2 =new ArrayList<Float>();
	        @SuppressWarnings("resource")
			BufferedReader read =  new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
	           
	        while((line = read.readLine())!=null){ 
	                
	             String vetor [] = line.split(" ");
	             for(int i=0;i<vetor.length;i++){
	                    String [] coordinate = vetor[i].split(",");
	                    //System.out.println(coordinate[0]+"  "+coordinate[1]);
	                    float temp =transformFloat(coordinate[0].trim());
	                    float temp1 = transformFloat(coordinate[1].trim());
	                    p1.add(temp);
	                    p2.add(temp1);
	                }
	        }
	        Geo listGeo[] = new Geo [p1.size()];
	        for(int i=0;i<p1.size();i++){
	                listGeo[i]= new Geo(p2.get(i),p1.get(i));
	        }
	        OMGeo.Polygon poly = new Polygon(listGeo);    
	        return poly;
	    }
	    
	 public static float transformFloat(String numero) {
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
