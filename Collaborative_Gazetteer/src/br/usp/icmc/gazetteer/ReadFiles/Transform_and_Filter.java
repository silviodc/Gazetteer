/*    This file is part of SWI Gazetteer.

    SWI Gazetteer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SWI Gazetteer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SWI Gazetteer.  If not, see <http://www.gnu.org/licenses/>.
    */

package br.usp.icmc.gazetteer.ReadFiles;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.TAD.Repository;

import com.bbn.openmap.geo.Geo;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Transform_and_Filter {

	
	
	public void transform_Repository_to_Place(ArrayList<Repository> repository) throws FileNotFoundException, IOException{
		
		 ArrayList<String> stop_words = read_stop_words("files"+File.separator+"stop_words.txt");
		 
		 for(Repository r: repository){
			 remove_stop_word(r.getPlaces(),stop_words,r.getName());
			 remove_common_name(r.getPlaces());
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
	 
	 public String deDup(String s) {
		    return new LinkedHashSet<String>(Arrays.asList(s.split(" "))).toString().replace(", ", " ");
		}

	 
	 public void remove_stop_word(ArrayList <Place> places,ArrayList<String> stop_words,String repository){
		    String temp [];
	        for(Place p:places){
	            String ok ="";
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
	            p.setNameFilter(deDup(ok));
	          //  System.out.println(ok);
	            p.setRepository(repository);
	        }        
	    }
		public OntModel loadOntology() {
			// String ontologyIRI =
			// "https://raw.githubusercontent.com/silviodc/Gazetteer/master/Collaborative_Gazetteer/files/Gazetteer_v_1_1.owl";

			String path = new File("files" + File.separator + "Gazetteer_v_1_1.owl")
			.getAbsolutePath();
			OntModel m = ModelFactory.createOntologyModel();
			InputStream in = FileManager.get().open(path);
			if (in == null)
				return null;

			return (OntModel) m.read(in, "");
		}

 
	 public void remove_common_name(ArrayList <Place> places){
		 List<String> labels = new ArrayList<String>();
		 OntModel model=null;
		 if(model==null)
				model = loadOntology();
			ExtendedIterator<OntClass> iter = model.listClasses();
			while (iter.hasNext()) {
				OntClass thisClass = (OntClass) iter.next();
				ExtendedIterator label = thisClass.listLabels(null);
				while (label.hasNext()) {
					RDFNode thisLabel = (RDFNode) label.next();
					if(thisLabel.isLiteral()){
						String labl = thisLabel.toString().split("http")[0].replaceAll("@en", "").replaceAll("@pt", "").toLowerCase();
						labl = Normalizer.normalize(labl, Normalizer.Form.NFD);  
						labl = labl.replaceAll("[^\\p{ASCII}]", "");
						labl = labl.replaceAll("^^", "");
						if(labl.contains("^^"))
							labl = labl.substring(0, labl.length()-2);
						//	fLogger.log(Level.SEVERE,(labl);
						labels.add(labl);
					}
				}
			}
			List<String> names = new ArrayList<String>();
			ExtendedIterator<ObjectProperty> iter2 = model.listObjectProperties();
			while (iter2.hasNext()) {
				ObjectProperty thisClass = (ObjectProperty) iter2.next();
				ExtendedIterator label = thisClass.listLabels(null);
				while (label.hasNext()) {
					RDFNode thisLabel = (RDFNode) label.next();
					if(thisLabel.isLiteral()){	
						names.add(thisLabel.toString().split("http")[0].replaceAll("(?!\")\\p{Punct}", "").replaceAll("@en", ""));
					}
				}
			}
			
			for(int i=0;i<places.size();i++){			
				String temp [] =places.get(i).getNameFilter().toLowerCase().split(" ");
				String nameFilter="";
				for(int n=0;n<temp.length-1;n++){

					String tipo = temp[n].toLowerCase().trim()+" "+temp[n+1].toLowerCase().trim();
					if(contaisString(labels,temp[n].toLowerCase())){
						temp[n]="";				
					}else if(contaisString(labels,tipo)){
						temp[n]="";					
					}else if(contaisString(names,temp[n])){
						temp[n]="";					
					}else if(contaisString(names,tipo)){
						nameFilter+=temp[n];
					}
				}
				nameFilter+=temp[temp.length-1];
				places.get(i).setNameFilter(nameFilter);
			}
	 }
	
	 private boolean contaisString(List<String> labels, String tipo) {
		for(String s:labels){
			if(s.equals(tipo))
				return true;
		}
		return false;
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
