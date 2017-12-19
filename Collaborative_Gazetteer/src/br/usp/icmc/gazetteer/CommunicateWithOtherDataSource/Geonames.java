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
package br.usp.icmc.gazetteer.CommunicateWithOtherDataSource;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.bbn.openmap.geo.Geo;

import br.usp.icmc.gazetteer.Similarity.Metrics;
import br.usp.icmc.gazetteer.TAD.Place;

public class Geonames {

	private ArrayList<Place> geonamesPlaces= new ArrayList<Place>();
	
	public ArrayList<Place> getGeonamesPlaces(){
		return geonamesPlaces;
	}
	
	public void acessGeonames() throws Exception{		
		String path = new File("files"+File.separator+"trustCoordinates"+File.separator+"geonames.txt").getAbsolutePath();		
		 @SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
         while(br.ready()){  
            String linha = br.readLine();  
            String values [] = linha.split("\t");
            if(values[values.length-2].equals("America/Manaus")){
            	Geo geo = new Geo(transformFloat(values[4]),transformFloat(values[5]));
            	try{
            	geonamesPlaces.add(new Place(values[1],geo));
            	geonamesPlaces.get(geonamesPlaces.size()-1).setNameFilter(values[1]);
            	}catch(Exception ex){
            		ex.printStackTrace();
            		System.out.println(values[1]+" "+geo);
            	}
            }
         }
	}
	
	public void compareGeonames_and_Places(ArrayList<Place> places, String method) throws Exception{
		int count=0;
		Metrics metric = new Metrics(method);
		for(Place p:geonamesPlaces){
			for(Place pl:places){
				if(metric.getSimilarity(p.getLocation(), pl.getLocation())>=0.6){
					pl.setLocation(p.getLocation());
					pl.setGeometry(p.getGeometry());
				
					count++;
				}
			}
		}
		System.out.println("Number locations improved  "+count);
	}
	 private float transformFloat(String numero) {
		 	numero = numero.replaceAll(",", ".");
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
