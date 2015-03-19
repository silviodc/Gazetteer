package cluster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import TAD.Expression;
import TAD.Group;
import TAD.Place;

public class Star_algorithm {

	private OntModel model;
	private final double similarity = 0.6;

	public ArrayList<Group> start_clustering(ArrayList<Place> places,
			ArrayList<Expression> exp) throws InstantiationException,
			IllegalAccessException, CloneNotSupportedException, IOException {

		findComposite(places);

		ArrayList<Place> candidate_place = new ArrayList<Place>();
		ArrayList<Place> composite = new ArrayList<Place>();
		ArrayList<Group> group = new ArrayList<Group>();
		for (Expression e : exp) {
			Pattern pattern = Pattern.compile(e.getExpression());
			// Split names according with regular expression
			for (Place pl : places) {
				Matcher matcher = pattern.matcher(pl.getNameFilter());
				while (matcher.find()) {
					if (!pl.isAmbiguo() && !pl.isUsed()) { // look if the name
															// is used or
															// ambiguous
						candidate_place.add(pl);
						pl.setUsed(true);
					} else if (!pl.isUsed()) {
						candidate_place.add(pl);
						pl.setUsed(true);
					}
				}
			}
			agroup(candidate_place, e, group);
			candidate_place.clear();
		}
		return group;
	}

	private void agroup(ArrayList<Place> candidate_place, Expression e,
			ArrayList<Group> group) throws CloneNotSupportedException {
		while (candidate_place.size() > 0) {
			Random rand = new Random();
			Place centroid = candidate_place.get(rand.nextInt(candidate_place
					.size())); // get some centroid to start matching
			candidate_place.remove(centroid);// remove centroid from candidate
												// places
			Group group_created = clustering_using_start(candidate_place,
					centroid);
			group_created.setExp(e);
			group_created.setCentroid(centroid);
			group_created.setRepository(centroid.getRepository());
			group.add(group_created);
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



	


	public Group clustering_using_start(ArrayList<Place> candidate_place,
			Place centroid) throws CloneNotSupportedException {

		// CLUSTERING!!! using star

		Group local_group = new Group();
		local_group.getPlaces().add(centroid);

		Bigram_Similarity jaccard = new Bigram_Similarity(); // try resolve
																// matching
																// using jaccard
																// similarity
																// metric

		for (int i = 0; i < candidate_place.size(); i++) {
			double value = jaccard.stringSimilarityScore(
					jaccard.bigram(centroid.getNameFilter()),
					jaccard.bigram(candidate_place.get(i).getNameFilter()));
			if (value >= similarity
					&& verific_county(candidate_place.get(i).getCounty(),
							centroid.getCounty())) {
				local_group.getPlaces().add(candidate_place.get(i).clone());
				candidate_place.remove(candidate_place.get(i));
			}
		}
		return local_group;
	}

	private boolean verific_county(String county, String county1) {

		if (county.equals(county1) || county1.equals("")
				|| county1.equals("não informado") || county.equals(" ")
				|| county1.equals(" ") || county == null || county1 == null) {
			return true;
		}
		return false;
	}

	public ArrayList<String> loadOntologyClass() {
		ArrayList<String> classes = new ArrayList<String>();
		ExtendedIterator<OntClass> iter = model.listClasses();
		while(iter.hasNext()){
			OntClass cl = iter.next();
			classes.add(cl.toString());
		}
		return classes;
	}
	
	private void findComposite(ArrayList<Place> places) throws CloneNotSupportedException {
		ArrayList<Place> ambiguoPlace = new ArrayList<Place>();
		Desambiguation ds = new Desambiguation();
		if(model==null)
			model = loadOntology();
		HashMap<String,OntClass> classes = new HashMap<String,OntClass>();
		ExtendedIterator<OntClass> iter = model.listClasses();
		while (iter.hasNext()) {
				OntClass thisClass = (OntClass) iter.next();
				ExtendedIterator label = thisClass.listLabels(null);
				while (label.hasNext()) {
					RDFNode thisLabel = (RDFNode) label.next();
					if(thisLabel.isLiteral()){
						String labl = thisLabel.toString().split("http")[0].replaceAll("(?!\")\\p{Punct}", "").replaceAll("@en", "");
						classes.put(labl, thisClass);
					}
				}
		 }
		for(int i=0;i<places.size();i++){
			int index=0;
			String temp [] =places.get(i).getLocation().toLowerCase().split(" "); 
			for(int n=0;n<temp.length;n++){
					if(classes.get(temp[n].toLowerCase().trim())!=null)
						index++;							
						if(index >= 2){
							places.get(i).setAmbiguo(true);
							break;
						}							
			}
		}

		for (int j = 0; j < places.size(); j++) {
			if (places.get(j).isAmbiguo()) {
				ambiguoPlace.add(places.get(j));
			}
		}
		places.addAll(ds.resolveCompositePlaces(ambiguoPlace));

	}
}
