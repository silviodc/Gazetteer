package mapping_to_ontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bbn.openmap.geo.Geo;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;

import TAD.Expression;
import TAD.Group;
import TAD.Place;
import TAD.Repository;

public class Mapping {
	private OntModel model;
	HashMap<String,OntClass> classes = new HashMap<String,OntClass>();
	
	
	public  void build_RDF(ArrayList<Group> group) throws IOException{
      	 
         int total=10;           
         int coords=0;
         model = OpenConnectOWL();
         OntClass geo = model.getOntClass("http://www.opengis.net/ont/sf#Geometry");
         OntClass feature = model.getOntClass("http://www.geonames.org/ontology#Feature");
         String basePrefix=model.getNsPrefixURI("");
         String geoSparqlP = model.getNsPrefixURI("geosparql");
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
 					classes.put(labl.toLowerCase(),thisClass);
 				}
 			}
 		 }
         for(Group rep:group){
        	  	 Place p = rep.getCentroid();
        	  	         			
        		 System.out.println(total);
        		 if(p.isAmbiguo()){
        			 /*Individual pl1=null,pl2=null;
        			 //System.out.println(model.getNsPrefixURI("")+total);
        			 OntClass type = whatClass(p.getLocation());
        			 if(type != null){
        				 System.out.println(type);
        				 pl1 = model.createIndividual(basePrefix+total, type);
        			 }else{
        				  pl1 = model.createIndividual(basePrefix+total, feature);	 
        			 }
        			 ObjectProperty relation  = model.createObjectProperty( basePrefix+p.getRelationName());
        			 ObjectProperty geoRelation  = model.createObjectProperty( geoSparqlP+"hasGeometry");
        			 type = whatClass(p.getRelation().getLocation());
        			 total++;
        			 if(type != null){
        				  pl2 = model.createIndividual( basePrefix+total,type);
        			 }else{
        				  pl2 = model.createIndividual( basePrefix+total, feature);	 
        			 }
        			 pl1.addProperty(relation, pl2);
        			 insertIndividualsWithRelation(pl1,p);
        			 insertIndividualsWithRelation(pl2,p.getRelation());
        			 if(p.getGeometry()!=null){
        				 total++;
        				 Individual geo1 = model.createIndividual(basePrefix+total,geo);
		        		 pl1.addProperty(geoRelation, geo1);
        			 	 insertGeo(geo1, p.getGeometry());
        			 }
        			 if(p.getRelation().getGeometry()!=null){
        				 total++;
        				 Individual geo2 = model.createIndividual(basePrefix+total,geo);
        			 	 pl2.addProperty(geoRelation, geo2);
        			 	 insertGeo(geo2, p.getRelation().getGeometry());
        			 }*/
        		 }else{
        			 Individual pl1;
        			 total++;
        			 OntClass type = whatClass(p.getLocation());
					if(type!=null){
						pl1 = model.createIndividual(basePrefix+total, type);
					}else{
						  pl1 = model.createIndividual(basePrefix+total, feature);
					}
        			 insertIndividualsWithRelation(pl1,p);
	        		 if(p.getGeometry()!=null){
	        			 total++;
	        			 Individual geo1 = model.createIndividual(basePrefix+total,geo );
	        			 ObjectProperty geoRelation  = model.createObjectProperty( geoSparqlP+"hasGeometry");
	        			 insertGeo(geo1, p.getGeometry());
	        			 pl1.addProperty(geoRelation, geo1);
	        		 }
        		 }
        	 }
         
         writeNtriples();
     }
		
		public void writeNtriples(){
			
			 try {
				OutputStream out = new FileOutputStream("triples.rdf");
				model.write(out,"RDF/XML");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	 
	 private OntClass whatClass(String name){
		 OntClass ant=null;
		 String temp [] = name.split(" ");
		 int count =0;
		 for(int j=0;j<temp.length;j++){
			 if(ant!=null){
				 if(!ant.equals(classes.get(temp[j].toLowerCase())))
					 count++;
			 }
			if( ant == null && classes.get(temp[j].toLowerCase())!=null)
				count++;
		}
		 if(count <2){
			 for(int j=0;j<temp.length;j++){
					if(classes.get(temp[j].toLowerCase())!=null)
						 return classes.get(temp[j].toLowerCase());
					}
		 } 
		return classes.get("Feature");
	 }
	 
	 private void insertGeo(Individual geo, Geo geometry){
		 String ponto ="<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>Point("+geometry.toString().replaceAll("Geo", "").replace("[", "").replace("]", "")+")]]>";
		 TypeMapper.getInstance().registerDatatype(WktLiteral.wktLiteralType);
		 geo.addLiteral(model.getProperty("http://www.opengis.net/ont/geosparql#asWKT"), ResourceFactory.createTypedLiteral(ponto, WktLiteral.wktLiteralType));
		 
	 }
	 
	 private void insertIndividualsWithRelation(Individual pl1, Place p){
		 
		 if(!p.getLocation().equals("")){
			pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"locality"),p.getLocation());
		 }
		 if(!p.getCounty().equals(""))
			 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"county"),p.getCounty());
		 if(p.getYear()<=2015)
			 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"date"),p.getYear());
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"agreement"),0);
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"contributors"),0);        			 
		 
		 
	 }
	 private OntModel OpenConnectOWL() {
			String path = new File("files" + File.separator + "Gazetteer_v_1_1.owl")
			.getAbsolutePath();
			OntModel mod = ModelFactory
					.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
			java.io.InputStream in = FileManager.get().open(path);
			if (in == null) {
				System.err.println("ERRO AO CARREGAR A ONTOLOGIA");
			}
			return (OntModel) mod.read(in, "");
		}
}
