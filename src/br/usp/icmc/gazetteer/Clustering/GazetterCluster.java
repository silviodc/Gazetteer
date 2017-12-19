package br.usp.icmc.gazetteer.Clustering;
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
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;


import com.aliasi.cluster.Clusterer;
import com.aliasi.cluster.KMeansClusterer;
import com.aliasi.cluster.SingleLinkClusterer;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Distance;
import com.aliasi.util.Strings;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import br.usp.icmc.gazetteer.Similarity.Metrics;
import br.usp.icmc.gazetteer.TAD.County;
import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;


/**
 * <p>The Cluster is done using the library: https://mvnrepository.com/artifact/de.julielab/aliasi-lingpipe</p>
 * 
 * <p>Web site: http://alias-i.com/lingpipe/index.html</p>
 * 
 * <p><li>To do so, 
 *  1) We build small groups of data:
 *  <ul>1.1) Associate the annotation labels in our ontology to biodiversity data.</ul>
 *  <ul>1.2) Verify if the municipality mentioned is the same of IBGE or wasn't informed</ul>
 *  <ul>2) Cluster the data</ul></li>
 */
public class GazetterCluster {
	private static OntModel model;
	private static List <OntClass> usedClass = new ArrayList<OntClass>();
	private static HashMap<String,OntClass> classes = new HashMap<String,OntClass>();
	private static int epochs=10;


	public GazetterCluster() {}

	public static OntModel loadOntology() {

		String path = new File("files" + File.separator +"ontology"+ File.separator + "Gazetteer_v_1_1.owl")
				.getAbsolutePath();
		OntModel m = ModelFactory.createOntologyModel();
		InputStream in = FileManager.get().open(path);
		if (in == null)
			return null;

		return (OntModel) m.read(in, "");
	}

	public static void findComposite(List<Place> places) throws CloneNotSupportedException {


		if(model==null)
			model = loadOntology();


		ExtendedIterator<OntClass> iter = model.listClasses();
		while (iter.hasNext()) {
			OntClass thisClass = (OntClass) iter.next();
			ExtendedIterator<?> label = thisClass.listLabels(null);
			while (label.hasNext()) {
				RDFNode thisLabel = (RDFNode) label.next();
				if(thisLabel.isLiteral()){
					String labl = thisLabel.toString().split("http")[0].replaceAll("@en", "").replaceAll("@pt", "").toLowerCase();
					labl = Normalizer.normalize(labl, Normalizer.Form.NFD);  
					labl = labl.replaceAll("[^\\p{ASCII}]", "");
					labl = labl.replaceAll("^^", "");
					if(labl.contains("^^"))
						labl = labl.substring(0, labl.length()-2);
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

	private static boolean verific_county( County county, County county1, String method) {
		Metrics metric = new Metrics(method);
		if (county == null || county1 == null || county1.getNome().equals("")	|| county1.getNome().equals("n�o informado") || county.getNome().equals(" ")
				|| county1.getNome().equals(" ") || county.getNome().equals("n�o informado")) {
			return true;
		}else{
			double value = metric.getSimilarity(county.getNome(), county.getNome());	
			if(value>=0.6)
				return true;
		}
		return false;
	}

	public boolean agroup(FileWriter writer,Set<Place> all_place,List<Group>  group1,String method, String cluster) throws Exception{

		if(all_place.size()<2)
			return false;

		int kvalue = (int) Math.sqrt(all_place.size()/2);
		if(kvalue<2)
			kvalue=2;
		TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE;;
		PlaceFeatureExtractor<Place> extractor = new PlaceFeatureExtractor<Place>(factory);



		Distance<Place> metrics = new Metrics(method);
		Clusterer<Place> clustering = null;
		if(cluster.equalsIgnoreCase("star"))
			clustering = new SingleLinkClusterer<Place>(metrics);
		else if(cluster.equalsIgnoreCase("kmeans"))
			clustering = new KMeansClusterer<Place>(extractor,kvalue,epochs,false,0.1);


		if(clustering==null)
			throw new Exception("A cluster method must to be specified");

		System.out.println("IT CAN TAKE SOME TIME");

		Set<Set<Place>> result = clustering.cluster(all_place);
		System.out.println(result.size()+" clusters found");
		for(Set<Place> clusters : result) {
			Group g = new Group();
			Place centroid = getMostOccured(clusters);
			g.setPlaces(clusters);
			g.setRepository(centroid.getRepository());
			g.setCentroid(centroid);
			writer.write("Cluster " + clusters.hashCode() + ": " + g.getCentroid().getLocation()+"\n");
			for(Place pl : clusters){
				if(pl.getCounty()!=null)
					writer.write("\t\t\t" +pl.getLocation()+" Municipality: "+pl.getCounty().getNome()+" Geo: "+pl.getGeometry()+" id: "+pl.getID()+"\n");
				else
					writer.write("\t\t\t" + pl.getLocation()+" Geo: "+pl.getGeometry()+" id: "+pl.getID()+"\n");

			}
			group1.add(g);
		}

		return true;
	}

	private Place getMostOccured(Set<Place> clusters) {
		Map<String,Integer> occurences = new HashMap<>();
		for(Place p: clusters)
			if(occurences.containsKey(p.getLocation()))
				occurences.put(p.getLocation(), occurences.get(p.getLocation())+1);
			else
				occurences.put(p.getLocation(), 1);

		int max = Integer.MIN_VALUE;
		String betterPlace=Strings.EMPTY_STRING;
		for(Entry<String, Integer> entry:occurences.entrySet()) {
			if(entry.getValue()>max) {
				max = entry.getValue();
				betterPlace = entry.getKey();
			}
		}

		for(Place p: clusters)
			if(p.getLocation().equalsIgnoreCase(betterPlace))
				return p;
		return null;
	}

	public ArrayList<Group> buildCluster(List<Place> candidate_place,ArrayList<County>municipality,String method,String cluster) throws Exception{

		findComposite(candidate_place);
		ArrayList<Group> group1 = new ArrayList<Group>();
		List<Place> toRemove = new ArrayList<>();
		File file = new File("files"+File.separator+"results"+File.separator+cluster+"_"+method+".txt");
		Set<Place> all_place = new HashSet<>();
		// creates the file
		file.createNewFile();
		// creates a FileWriter Object
		FileWriter writer = new FileWriter(file); 
		int max = candidate_place.size();
		for (OntClass e :usedClass) {
			for(int k=0;k<municipality.size();k++){
				all_place.clear();
				for(int i=0; i<max;i++) {
					if (candidate_place.get(i)!=null && verific_county(candidate_place.get(i).getCounty(),municipality.get(k),method) && contaisSomeClass(candidate_place.get(i).getTypes(),e.toString())) {
						Place candidate = candidate_place.get(i);
						all_place.add(candidate);
					}
				}
				candidate_place.removeAll(all_place);
				if(all_place.size()>0)
					System.out.println("Trying to cluster: "+all_place.size()+" entries.... OWLClass:"+e.getLocalName()+" Municipality "+municipality.get(k).getNome());

				if(!agroup(writer,all_place,group1,method,cluster)){
					candidate_place.addAll(all_place);
				}
				max = candidate_place.size();
			}//end municipality
		}//end classes
		if(candidate_place.size()>0){
			for(int k=0;k<municipality.size();k++){
				all_place.clear();
				Iterator<Place> it = candidate_place.iterator();
				while(it.hasNext()){
					Place pl = it.next();	        	
					if (pl!=null && verific_county(pl.getCounty(),municipality.get(k),method)) 
						all_place.add(pl);
				}
				for(Place p: all_place)
					candidate_place.remove(p);
				if(!agroup(writer,all_place,group1,method,cluster)){
					for(Place p: all_place)
						candidate_place.remove(p);
				}
			}
			all_place.clear();
			all_place.addAll(candidate_place);

			for(Place p: all_place)
				candidate_place.remove(p);
			if(!agroup(writer,all_place,group1,method,cluster)){
				for(Place p: all_place)
					candidate_place.remove(p);
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
