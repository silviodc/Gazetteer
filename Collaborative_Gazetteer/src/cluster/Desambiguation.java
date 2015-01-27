package cluster;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

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

	
	public void correlationBetweenPlaces(ArrayList<Place> places){
		
		
			OntModel model = OpenConnectOWL();
			
			ArrayList<String> objectProperties = new ArrayList<String>();
			ArrayList<String> objectsP = new ArrayList<String>();
	        ExtendedIterator<ObjectProperty> iter = model.listObjectProperties();
	        while (iter.hasNext()) {
	        	ObjectProperty thisClass = (ObjectProperty) iter.next();
	             ExtendedIterator label = thisClass.listLabels(null);

	             while (label.hasNext()) {
	               RDFNode thisLabel = (RDFNode) label.next();
	               
	               if(thisLabel.isLiteral()){
	            	   objectsP.add(thisLabel.toString());	
	            	   objectProperties.add(thisLabel.toString().split("http")[0].replaceAll("(?!\")\\p{Punct}", "").replaceAll("@en", ""));
	               	}
	             }
	        }
	        ArrayList<String>candidate = new ArrayList<String>();
	        
	    	for(int i=0; i<places.size();i++){
	    		String used="";
	    		int index=0;
				for(int j=0;j<objectProperties.size();j++){
					if(places.get(i).getLocation().contains(objectProperties.get(j))){
						candidate.add(objectProperties.get(j));
					}
				}
				if(candidate.size()>0){
					used=candidate.get(0);
					for(int j=1;j<candidate.size();j++){
						if(used.length()<candidate.get(j).length())
							used = candidate.get(j);
						
					}
					for(int j=1;j<objectProperties.size();j++){
						if(objectProperties.get(j).trim().equals(used))
							index = j;
					}
				}
				candidate.clear();
				if(!used.equals("")){
					places.get(i).setRelation(places.get(i+1));
					places.get(i).setRelationName(objectsP.get(index));
					System.out.println(places.get(i).getLocal()+" "+places.get(i).getRelationName()+" "+places.get(i).getRelation().getLocation());
				
				}
			}
		}
		
	
	private OntModel OpenConnectOWL(){
		 String path = new File("files"+File.separator+"Gazetteer_v_1_1.owl").getAbsolutePath();
		OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		java.io.InputStream in = FileManager.get().open(path);
		if(in == null){
			System.err.println("ERRO AO CARREGAR A ONTOLOGIA");
		}
		return (OntModel) mod.read(in,"");
	}
	
	private ResultSet ExecSPARQL(String query,String resource){
		QueryExecution qe = QueryExecutionFactory.sparqlService(resource, query);
		ResultSet results = qe.execSelect();
		return results;
	}
	
	private ResultSet ExecSPARQL(String query){
		Query qry = QueryFactory.create(query);
		QueryExecution qe = QueryExecutionFactory.create(query,OpenConnectOWL());
		ResultSet results = qe.execSelect();
		return results;
	}


	public void desambig(ArrayList<Place> ambiguoPlace, ArrayList<Group> group) {
		// TODO Auto-generated method stub
		
	}
	
	/*private boolean criarIndividuos(){
		OntClass classe = OpenConnectOWL().getComplementClass("");
		Individual individual = OpenConnectOWL().createIndividual( yourNameSpace + "individual2", classe);
	}*/
}
