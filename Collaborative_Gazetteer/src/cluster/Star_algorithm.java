package cluster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import TAD.County;
import TAD.Expression;
import TAD.Group;
import TAD.Place;

public class Star_algorithm {

	private OntModel model;
	private final double similarity = 0.38;
	private List <OntClass> usedClass = new ArrayList<OntClass>();
	private HashMap<String,OntClass> classes = new HashMap<String,OntClass>();

	public ArrayList<Group> start_clustering(HashMap<Integer,Place> places) throws InstantiationException,
	IllegalAccessException, CloneNotSupportedException, IOException {


		findComposite(places);

		HashMap<Integer,Place> candidate_place = new HashMap<Integer,Place>();

		ArrayList<Group> group = new ArrayList<Group>();
		System.out.println("Used class = "+usedClass.size());
		for (OntClass e :usedClass) {
			// Split names according with regular expression
			for(int i=0;i<places.size();i++){
				if (places.get(i)!=null && contaisSomeClass(places.get(i).getTypes(),e.toString()) && !places.get(i).isUsed()) {
					//System.out.println(e.toString());
					Place candidate = places.get(i);
					places.remove(i);
					candidate_place.put(i,candidate);
				}else if(places.get(i)!=null && places.get(i).isUsed()){
					places.remove(i);
				}
			}
			System.out.println("Candidate_place: "+candidate_place.size());
			agroup(candidate_place, group);
			candidate_place.clear();
			System.out.println(e+"    "+group.size()+"  "+places.size());
		}

		while(places.size()>0){
			agroup(places, group);
		}


		System.out.println(group.size());
		classes.clear();
		usedClass.clear();
		model.close();

		return group;
	}

	public boolean contaisSomeClass(List<String> types, String type){
		if(types.contains(type))
			return true;
		return false;
	}

	private void agroup(HashMap<Integer,Place> candidate_place, ArrayList<Group> group){
		while (candidate_place.size() > 0) {			
			int index =  lookForGoldStandart(candidate_place); // get some centroid to start matching
			Place centroid = candidate_place.get(index);
			candidate_place.remove(index);// remove centroid from candidate places

			Group group_created = clustering_using_start(candidate_place,centroid);
			//System.out.println(candidate_place.size());

			//Verify others Groups

			boolean newGroup = verifyCreatedGroups(group_created,group);
			if(newGroup){				
				group_created.setCentroid(centroid);
				group_created.setRepository(centroid.getRepository());
				group.add(group_created);
			}
		}
	}

	private int  lookForGoldStandart(HashMap<Integer,Place> candidate_place) {
		Random rand = new Random();
		Set<Integer> key = candidate_place.keySet();
		Iterator it = key.iterator();
		while(it.hasNext()){
			int index = (Integer) it.next();
			if(candidate_place.get(index).isGoldStandart())
				return index;
		}
		Iterator it2 = key.iterator();
		int index = (Integer) it2.next();
		return index;
	}

	private boolean forward(List<String> tempArrayList, List<String>group){
		Bigram_Similarity bigram = new Bigram_Similarity();

		Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		set.addAll(tempArrayList);
		tempArrayList = new ArrayList<String>(set);

		Set<String> set2 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		set2.addAll(group);
		group = new ArrayList<String>(set);


		for(String t: tempArrayList){
			for(String p:group ){
				if(verific_county(bigram,new County(t), new County(p))){
					return true;
				}		
			}
		}
		return false;
	}

	private boolean verifyCreatedGroups(Group group_created,ArrayList<Group> group){
		List<Place> tempArrayList = new ArrayList<Place>();

		tempArrayList.addAll( group_created.getPlaces());
		tempArrayList.add(group_created.getCentroid());

		Bigram_Similarity bigram = new Bigram_Similarity(); // try resolve matching using bigram similarity metric

		for(int i=0;i<group.size();i++){

			group.get(i).getPlaces().remove(null);
			tempArrayList.remove(null);

			boolean forward = forward(group_created.getCounty(),group.get(i).getCounty());
			if(forward){
				List<String> listGroup = new ArrayList<String>();
				for(Place p: group.get(i).getPlaces())
					listGroup.add(p.getNameFilter());

				Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				set.addAll(listGroup);
				listGroup = new ArrayList<String>(set);


				List<String> listCreated = new ArrayList<String>();

				for(Place p: tempArrayList){
					if(p==null){
						System.out.println("ALGUM ERRO NOS PONTEIROS");
						System.out.println(tempArrayList.get(0).getLocation()+" "+tempArrayList.size());
					}else if(p.getNameFilter()==null){
						System.out.println("ERRO NA STRING");
					}else{
						listCreated.add(p.getNameFilter());

					}
				}

				Set<String> set2 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				set2.addAll(listCreated);
				listCreated = new ArrayList<String>(set2);

				Iterator <String> it = listGroup.iterator();
				while(it.hasNext()){
					String nome = (String) it.next();
					Iterator <String> it2 = listCreated.iterator();
					while(it2.hasNext()){
						String nome2 = (String) it2.next();	
						if(bigram.stringSimilarityScore(bigram.bigram(nome),bigram.bigram(nome2))>similarity){
							System.out.println(">>>>>>"+nome+"  --- "+nome2);
							group.get(i).getPlaces().addAll(group_created.getPlaces());
							group.get(i).getPlaces().add(group_created.getCentroid());
							return false;
						}
					}
				}
			}
		}
		return true;
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

	public Group clustering_using_start(HashMap<Integer,Place> candidate_place,
			Place centroid){

		// CLUSTERING!!! using star
		centroid.setUsed(true);
		Group local_group = new Group();
		local_group.getPlaces().add(centroid);

		Bigram_Similarity jaccard = new Bigram_Similarity(); // try resolve matching using jaccard similarity metric

		for (int i = 0; i < candidate_place.size(); i++) {
			if(candidate_place.get(i)!=null){

				double value = jaccard.stringSimilarityScore(jaccard.bigram(centroid.getNameFilter()),jaccard.bigram(candidate_place.get(i).getNameFilter()));
				boolean county = verific_county(jaccard,candidate_place.get(i).getCounty(),centroid.getCounty());			
				if (value >= similarity	&& county) {
					Place candidate = candidate_place.get(i);
					candidate.setUsed(true);
					candidate_place.remove(i); //remove place similar  from list
					local_group.getPlaces().add(candidate); //add similar place in a new group
					if(candidate.getCounty()!=null)
						local_group.getCounty().add(candidate.getCounty().getNome());
				}			
			}
		}

		return local_group;
	}

	private boolean verific_county(Bigram_Similarity jaccard, County county, County county1) {

		if (county == null || county1 == null || county1.getNome().equals("")	|| county1.getNome().equals("não informado") || county.getNome().equals(" ")
				|| county1.getNome().equals(" ")) {
			return true;
		}else{
			double value = jaccard.stringSimilarityScore(jaccard.bigram(county1.getNome()),jaccard.bigram(county.getNome()));	
			if(value>=0.7)
				return true;
		}
		return false;
	}

	public ArrayList<OntClass> loadOntologyClass() {
		ArrayList<OntClass> classes = new ArrayList<OntClass>();
		ExtendedIterator<OntClass> iter = model.listClasses();
		while(iter.hasNext()){
			OntClass cl = iter.next();
			classes.add(cl);
		}
		return classes;
	}

	private void findComposite(HashMap<Integer,Place> places) throws CloneNotSupportedException {
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
					//	System.out.println(labl);
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
		cl.add(classes.get("feature"));
		usedClass.addAll(verifyContainClass(usedClass,cl));
		cl.clear();


	}
	public List<String> getManyTypes(List<OntClass> test){
		List<String> types = new ArrayList<String>();
		for(OntClass t : test){
			types.add(t.toString());
		}
		if(types.size()==0)
			types.add(classes.get("feature").toString());
		return types;
	}

	public List<OntClass> verifyContainClass(List<OntClass> used,List<OntClass> test){
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

}
