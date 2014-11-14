package cluster;

import java.io.InputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import TAD.Group;
import TAD.Place;
public class Desambiguation {

	
	public void desambig(ArrayList<Place> pl,ArrayList<Group>descovered){
		String name="";
		String ontologyIRI = "http://www.semanticweb.org/ontologies/Gazetter.owl";

		
		OntModel m = ModelFactory.createOntologyModel();
		InputStream in = FileManager.get().open("Ontobio_01072013.owl");
		if (in == null)
			System.out.println("File not found");
		m.read(in, null);
		OntClass c = m.getOntClass(ontologyIRI + "#" + name);
		Individual ind = m.getIndividual(ontologyIRI + "#" + name);
		String type = "";
		
	}
}
