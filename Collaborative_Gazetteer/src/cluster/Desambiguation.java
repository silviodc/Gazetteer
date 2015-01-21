package cluster;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import TAD.Group;
import TAD.Place;
public class Desambiguation {

	
	public void desambig(ArrayList<Place> ambiguos,ArrayList<Group>descovered){
		
		for(Place pl:ambiguos){
			System.out.println(pl.getLocation());
		}
		
		
		/*
		String query ="PREFIX owl: <http://www.w3.org/2002/07/owl#>"
				+ " SELECT DISTINCT ?class "
				+ "WHERE {   ?class a owl:Class ."
				+ "   FILTER (!isblank(?class)) "
				+ "} limit 100";
		
		String resource = "http://biomac.icmc.usp.br:8080/parliament/sparql";
		
		ResultSet r = ExecSPARQL(resource,query);
		while(r.hasNext()){
			System.out.println(r.next());
		}*/
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
	
	/*private boolean criarIndividuos(){
		OntClass classe = OpenConnectOWL().getComplementClass("");
		Individual individual = OpenConnectOWL().createIndividual( yourNameSpace + "individual2", classe);
	}*/
}
