package read_files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;

import com.bbn.openmap.geo.Geo;

import TAD.Group;
import TAD.Place;
import TAD.Repository;

public class Transform_and_Filter {

	
	
	public void transform_Repository_to_Place(ArrayList<Repository> repository) throws FileNotFoundException, IOException{
		
		 ArrayList<String> stop_words = read_stop_words("files"+File.separator+"stop_words.txt");
		 
		 for(Repository r: repository){
			 remove_stop_word(r.getPlaces(),stop_words,r.getName());
		 }
		 		
	}
	 public ArrayList<String> read_stop_words(String file_path) throws FileNotFoundException, IOException {
		 String word ="";
         ArrayList<String> uselles = new ArrayList<String>();
         File arq = new File(file_path).getAbsoluteFile();
         BufferedReader lines =  new BufferedReader(new InputStreamReader(new FileInputStream(file_path), "UTF-8"));
         while ((word = lines.readLine()) != null) {
        	 uselles.add(word);
         }
         return uselles;
	 }
	 public void remove_stop_word(ArrayList <Place> places,ArrayList<String> stop_words,String repository){
	        String temp [];
	        for(Place p:places){
	            String ok ="";
	            p.setLocal(p.getLocation());
	            String localtemp = Normalizer.normalize(p.getLocation(), Normalizer.Form.NFD);  
				 localtemp = localtemp.replaceAll("(?!\")\\p{Punct}", "");
	             temp = localtemp.split(" ");
	             for (String s : stop_words) {
	                for(int k=0;k<temp.length;k++){
	                    if(temp[k].equals(s) || temp[k].equals(""))
	                     temp[k] = " ";
	                }
	            }
	         
	            for(int n=0;n<temp.length;n++){
	                if(!temp[n].equals(""))
	                    ok +=temp[n]+" ";
	            }
	            p.setNameFilter(ok);
	          //  System.out.println(ok);
	            p.setRepository(repository);
	        }        
	    }
	
	 public float transformaFloat(String numero) {
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
	public ArrayList<Place> getRepClustered(ArrayList<Group> g, String name) {
		ArrayList<Place> places = new ArrayList<Place>();
		for(int i=0;i<g.size();i++){
			for(int j=0;j<g.get(i).getPlaces().size();j++){
				if(g.get(i).getPlaces().get(j).getRepository().equals(name)){
					places.add(g.get(i).getPlaces().get(j));
				}
			}
		}
		return places;
	}
}
