/*
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






import java.util.logging.Level;

import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.cluster.Similarity;
import br.usp.icmc.gazetteer.cluster.Star_algorithm;

import com.bbn.openmap.geo.Geo;

public class Geonames {

	private ArrayList<Place> geonamesPlaces= new ArrayList<Place>();
	
	public ArrayList<Place> getGeonamesPlaces(){
		return geonamesPlaces;
	}
	
	public void acessGeonames() throws Exception{		
		String path = new File("files"+File.separator+"geonames.txt").getAbsolutePath();		
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
            		Star_algorithm.fLogger.log(Level.SEVERE,values[1]+" "+geo);
            	}
            }
         }
	}
	
	public void compareGeonames_and_Places(ArrayList<Place> places) throws Exception{
		int count=0;
		Similarity jaccard = new Similarity();
		for(Place p:geonamesPlaces){
			for(Place pl:places){
				if(jaccard.stringSimilarityScore(jaccard.bigram(p.getLocation()), jaccard.bigram(pl.getNameFilter()))>=0.6){
					pl.setLocation(p.getLocation());
					pl.setGeometry(p.getGeometry());
				
					count++;
				}
			}
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"Number locations improved  "+count);
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
