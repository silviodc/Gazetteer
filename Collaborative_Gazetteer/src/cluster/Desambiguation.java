package cluster;

import java.io.File;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import TAD.Group;
import TAD.Place;

public class Desambiguation {


	private OntModel model;
	
	public Collection<? extends Place> resolveCompositePlaces(ArrayList<Place> ambiguoPlace) throws CloneNotSupportedException {
	//	System.out.println("PROCESSING COMPOSITE!");
		ArrayList<Place> places = new ArrayList<Place>();
		ArrayList<Place> novos = new ArrayList<Place>();
		if(model == null)
			model = loadOntology();

		ArrayList<String> objectProperties = new ArrayList<String>();
		ArrayList<String> classes = new ArrayList<String>();
		
		/*
		 * Get the object properties
		 * and labels referent these objecties
		 */
		
		ExtendedIterator<ObjectProperty> itera = model.listObjectProperties();
		while (itera.hasNext()) {
			ObjectProperty thisProperty = (ObjectProperty) itera.next();
			ExtendedIterator label = thisProperty.listLabels(null);
			while (label.hasNext()) {
				RDFNode thisLabel = (RDFNode) label.next();
				if(thisLabel.isLiteral()){
					String labl = thisLabel.toString().split("http")[0].replaceAll("(?!\")\\p{Punct}", "").replaceAll("@en", "");
					objectProperties.add(labl.toLowerCase());
				}
			
			}
		}
		
		/*
		 * get the class from ontology
		 */
		ExtendedIterator<OntClass> iter = model.listClasses();
		while (iter.hasNext()) {
			OntClass thisClass = (OntClass) iter.next();
			ExtendedIterator label = thisClass.listLabels(null);
			while (label.hasNext()) {
				RDFNode thisLabel = (RDFNode) label.next();
				if(thisLabel.isLiteral()){
					String labl = thisLabel.toString().split("http")[0].replaceAll("(?!\")\\p{Punct}", "").replaceAll("@en", "");
					classes.add(labl.toLowerCase());
				}
			}
		 }
		boolean relation=false;
		Place antigo=null,novo=null;
		while (ambiguoPlace.size() > 0) {
			Place temp1 = ambiguoPlace.get(0).clone();
			String query = temp1.getLocation();
			int index=0;
			String word="";
			String temp [] = query.split(" ");
			//System.out.println("NAME: "+ query+"_____________INICIO_________________");
			
			for(int i=0;i<temp.length;i++){
				int verify = findStop(temp[i],objectProperties,classes);
				if(verify>=1)
					index++;
				if(index>=2 && verify==2){					
					Place pl = new Place(word);
					//System.out.println(pl.getLocation());
					pl.setRelationName(findRelation(word,objectProperties));
					antigo = pl;
					if(relation)
						antigo.setRelation(pl);
					relation = true;
					insertInformation(pl,temp1,word);
					word="";	
					word+=temp[i]+" ";
					index=1;
					novos.add(pl);
				}else{
					word+=temp[i]+" ";
				}
			}
		//	System.out.println(word);
			Place pl = new Place(word);
			novos.add(pl);
			places.add(temp1);
			if(novos.size()>1){
				places.addAll(novos);
			}
			novos.clear();
			insertInformation(pl,temp1,word);
			if(relation)
				antigo.setRelation(pl);
//			System.out.println("COUNT______"+places.size()+"____WAITING__"+ambiguoPlace.size());
			ambiguoPlace.remove(0);
		}
		System.out.println("DONE COMPOSITE!");
		return places;
	}
	
	private void insertInformation(Place pl, Place temp1, String word){

		pl.setCounty(temp1.getCounty());
		pl.setFather(temp1);
		pl.setNameFilter(word);
		pl.setPartOf(true);
		pl.setRepository(temp1.getRepository());
		pl.setYear(temp1.getYear());
		pl.setGeometry(temp1.getGeometry());
	}

	private int findStop(String temp, ArrayList<String> objectProperties,ArrayList<String> classes){
		boolean placeType=false;
		for(int k=0;k<objectProperties.size();k++){
			if(objectProperties.get(k).toLowerCase().equals(temp.toLowerCase()))
				return 1;
								
		}
		for(int k=0;k<classes.size();k++){
			if(classes.get(k).toLowerCase().equals(temp.toLowerCase()))
				return 2;
		}
		return -1;
	}
	
	private String findRelation(String temp, ArrayList<String> objectProperties){
		for(int k=0;k<objectProperties.size();k++){
			String labl = objectProperties.get(k);
			if(temp.toLowerCase().contains(labl.toLowerCase())){
					return labl;
			}
							
		}
		return null;
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


}
