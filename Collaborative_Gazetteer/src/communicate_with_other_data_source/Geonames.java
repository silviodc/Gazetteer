package communicate_with_other_data_source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;






import cluster.Jaccard_Similarity;

import com.bbn.openmap.geo.Geo;

import TAD.Place;

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
            	}catch(Exception ex){
            		ex.printStackTrace();
            		System.out.println(values[1]+" "+geo);
            	}
            }
         }
	}
	
	public void compareGeonames_and_Places(ArrayList<Place> places) throws Exception{
		int count=0;
		Jaccard_Similarity jaccard = new Jaccard_Similarity();
		for(Place p:geonamesPlaces){
			for(Place pl:places){
				if(jaccard.jaccardSimilarity(p.getLocation(), pl.getNameFilter())>0.4){
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
