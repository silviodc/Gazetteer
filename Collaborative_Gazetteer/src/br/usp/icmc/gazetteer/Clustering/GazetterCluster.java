package br.usp.icmc.gazetteer.Clustering;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import br.usp.icmc.gazetteer.TAD.County;
import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.cluster.Similarity;
import weka.clusterers.SimpleKMeans;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class GazetterCluster {
	private static OntModel model;
	private static List <OntClass> usedClass = new ArrayList<OntClass>();
	private static HashMap<String,OntClass> classes = new HashMap<String,OntClass>();

	private static final Random random = new Random();
	public  List<Place> allPoints;
	public final int k;
	private SimpleKMeans kmeans = new SimpleKMeans();

	/**@param pointsFile : the csv file for input points
	 * @param k : number of clusters
	 */
	public GazetterCluster( ) {}

	public static OntModel loadOntology() {
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

	public static void findComposite(HashMap<Integer,Place> places) throws CloneNotSupportedException {
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
					classes.put(labl, thisClass);
				}
			}
		}
		List<OntClass> cl = new ArrayList<OntClass>();
		for(int i=0;i<places.size();i++){			
			String temp [] =places.get(i).getLocation().toLowerCase().split(" "); 
			for(int n=0;n<temp.length-1;n++){

				String tipo = temp[n].toLowerCase().trim()+" "+temp[n+1].toLowerCase().trim();
				if(classes.get(temp[n].toLowerCase().trim())!=null && !cl.contains(classes.get(temp[n].toLowerCase().trim())) ){
					cl.add(classes.get(temp[n].toLowerCase().trim()));					
				}else if(classes.get(tipo)!=null && !cl.contains(classes.get(tipo))){
					cl.add(classes.get(tipo));					
				}										
			}	
			usedClass.addAll(verifyContainClass(usedClass,cl));
			places.get(i).setTypes(getManyTypes(cl));			
			cl.clear();

		}
		usedClass.addAll(verifyContainClass(usedClass,cl));
		cl.clear();
		classes.clear();
		//classesStartAlgorithm();

	}
	public static List<String> getManyTypes(List<OntClass> test){
		List<String> types = new ArrayList<String>();
		for(OntClass t : test){
			types.add(t.toString());
		}
		if(types.size()==0)
			types.add(classes.get("feature").toString());
		return types;
	}

	public static List<OntClass> verifyContainClass(List<OntClass> used,List<OntClass> test){
		List<OntClass> temp = new ArrayList<OntClass>();

		if(used.size()==0)
			test.add(classes.get("feature"));
		for(OntClass t: test){
			if(!used.contains(t)){
				temp.add(t);
			}
		}
		return temp;
	}
	
	private static boolean verific_county( County county, County county1) {
		Similarity jaccard = new Similarity();
		if (county == null || county1 == null || county1.getNome().equals("")	|| county1.getNome().equals("n�o informado") || county.getNome().equals(" ")
				|| county1.getNome().equals(" ") || county.getNome().equals("n�o informado")) {
			return true;
		}else{
			double value = jaccard.stringSimilarityScore(jaccard.bigram(county1.getNome()),jaccard.bigram(county.getNome()));	
			if(value>=0.7)
				return true;
		}
		return false;
	}
	
	public static boolean agroup(FileWriter writer,ArrayList<Place> all_place,ArrayList<Group> group1) throws IOException{
		int kvalue = (int) Math.sqrt(all_place.size()/2);
		if(kvalue<2)
			kvalue=2;
		if(all_place.size()>2){
			GazetterCluster kMeans = new GazetterCluster(all_place,kvalue);
			List<GazetterCluster> pointsClusters = new ArrayList<GazetterCluster>();
			pointsClusters.addAll(kMeans.getPointsClusters());
			for (int i = 0 ; i < kMeans.k; i++){
				writer.write("Cluster " + i + ": " + pointsClusters.get(i).getCentroid().getLocation()+"\n");
				Group p = new Group();
				p.setRepository(pointsClusters.get(i).getCentroid().getRepository());
				p.setCentroid(pointsClusters.get(i).getCentroid());
				for(int j=0;j<pointsClusters.get(i).getPoints().size();j++){
					if(pointsClusters.get(i).getPoints().get(j).getCounty()!=null)
					writer.write("\t\t\t" + pointsClusters.get(i).getPoints().get(j).getLocation()+" Municipality: "+pointsClusters.get(i).getPoints().get(j).getCounty().getNome()+" Geo: "+pointsClusters.get(i).getPoints().get(j).getGeometry()+" id: "+pointsClusters.get(i).getPoints().get(j).getID()+"\n");
					else
						writer.write("\t\t\t" + pointsClusters.get(i).getPoints().get(j).getLocation()+" Geo: "+pointsClusters.get(i).getPoints().get(j).getGeometry()+" id: "+pointsClusters.get(i).getPoints().get(j).getID()+"\n");
					
				}
				p.setPlaces((ArrayList<Place>) pointsClusters.get(i).getPoints());
				kMeans.allPoints.clear();
				kMeans.pointClusters.clear();
				group1.add(p);	
			}//end k means cluster
			pointsClusters.clear();
		}else{
			return false;
		}
		return true;
	}

	public static ArrayList<Group> buildKmeans(HashMap<Integer,Place> candidate_place,ArrayList<County>municipality) throws Exception{
		
		findComposite(candidate_place);
		ArrayList<Group> group1 = new ArrayList<Group>();
		File file = new File("kmeans.txt");
		ArrayList<Place> all_place = new ArrayList<Place>();
		// creates the file
		file.createNewFile();
		// creates a FileWriter Object
		FileWriter writer = new FileWriter(file); 
		int max = candidate_place.size();
		for (OntClass e :usedClass) {
			for(int k=0;k<municipality.size();k++){
				all_place.clear();
				for(int i=0; i<max;i++) {
					if (candidate_place.get(i)!=null && verific_county(candidate_place.get(i).getCounty(),municipality.get(k)) && contaisSomeClass(candidate_place.get(i).getTypes(),e.toString())) {
						Place candidate = candidate_place.get(i);
						all_place.add(candidate);
						candidate_place.remove(i);
					}
				}
				if(!agroup(writer,all_place,group1)){
					for(int j=0;j<all_place.size();j++)
					candidate_place.put(all_place.get(j).getID(), all_place.get(j));
				}
			}//end municipality
		}//end classes
        if(candidate_place.size()>0){
        	for(int k=0;k<municipality.size();k++){
				all_place.clear();
	        	Set<Integer> iterator = candidate_place.keySet();
	        	Iterator<Integer> it = iterator.iterator();
	        	while(it.hasNext()){
	        		int index = it.next();	        	
	        		if (candidate_place.get(index)!=null && verific_county(candidate_place.get(index).getCounty(),municipality.get(k))) 
	        				all_place.add(candidate_place.get(index));
				}
	        	for(int i=0;i<all_place.size();i++)
	        		candidate_place.remove(all_place.get(i));
	        	if(!agroup(writer,all_place,group1)){
					for(int j=0;j<all_place.size();j++)
					candidate_place.put(all_place.get(j).getID(), all_place.get(j));
				}
        	}
        	all_place.clear();
        	Set<Integer> iterator = candidate_place.keySet();
        	Iterator<Integer> it = iterator.iterator();
        	while(it.hasNext()){
        		all_place.add(candidate_place.get(it.next()));
        	}
        	for(int i=0;i<all_place.size();i++)
        		candidate_place.remove(all_place.get(i));
        	if(!agroup(writer,all_place,group1)){
				for(int j=0;j<all_place.size();j++)
				candidate_place.put(all_place.get(j).getID(), all_place.get(j));
			}
        	
        }
		System.out.println("Candidate Place: "+candidate_place.size());
		writer.close();
		return group1;
	}

	
	public static boolean contaisSomeClass(List<String> types, String type){
		Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		set.addAll(types);
		types = new ArrayList<String>(set);
		if(types.contains(type))
			return true;
		return false;
	}


}
