package mapping_to_ontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import TAD.Group;
import TAD.Place;

import com.bbn.openmap.geo.Geo;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Mapping {
	
	private static HashMap<String,OntClass> classes = new HashMap<String,OntClass>();
	
	private  OntModel model ;
	private OntClass geo;
	private OntClass feature;
	private String basePrefix;
	private String geoSparqlP;
	
	public void rebuildModel(){
		model = OpenConnectOWL();
        geo = model.getOntClass("http://www.opengis.net/ont/sf#Geometry");
        feature = model.getOntClass("http://www.geonames.org/ontology#Feature");
        basePrefix=model.getNsPrefixURI("");
        geoSparqlP = model.getNsPrefixURI("geosparql");
        /*
		 * get the class from ontology
		 */
		ExtendedIterator<OntClass> iter = model.listClasses();
		while (iter.hasNext()) {
			OntClass thisClass = (OntClass) iter.next();
			classes.put(thisClass.toString(),thisClass);
			
		 }
	}
	public  void build_RDF(ArrayList<Group> group) throws IOException{
      	 
         int total=1;           
         int coords=0;
	     rebuildModel();
         for(Group rep:group){
        	  	 Place p = rep.getCentroid();
        	  		 Individual pl1;
        			 total++;
        			 OntClass type = useMoreGeneric(p.getTypes());
        			 System.out.println(type.toString());
        			  pl1 = model.createIndividual(basePrefix+total, type);
					 insertIndividualsWithRelation(pl1,p,rep.getPlaces().size());
	        		 if(p.getGeometry()!=null){
	        			 coords=total+1;
	        			 Individual geo1 = model.createIndividual(basePrefix+coords,geo );
	        			 ObjectProperty geoRelation  = model.createObjectProperty( geoSparqlP+"hasGeometry");
	        			 insertGeo(geo1, p.getGeometry());
	        			 pl1.addProperty(geoRelation, geo1);
	        			 total++;
	        		 }
   		 }
      
         writeNtriples(total+"");
         classes.clear();
         model.close();
     }
		
		public void writeNtriples(String name){
			
			 try {
				OutputStream out = new FileOutputStream(name+".nt");
				model.write(out,"N-TRIPLES");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private OntClass useMoreGeneric(List<String> cl) {
			System.out.println(cl.get(0));
			System.out.println(classes.get(cl.get(0)).toString());
			OntClass first = classes.get(cl.get(0));
			
			boolean find = false;
			for(int i=1;i<cl.size();i++){
				if(classes.get(cl.get(i)).hasSuperClass(first)){
					first = classes.get(cl.get(i));
					find = true;
				}else if(first.hasSubClass(classes.get(cl.get(i)))){
					find = true;
				}
			}
			if(find)
				return first;
			
			if(!find && cl.size()>=1)
				return classes.get(cl.get(0));
			
			return feature;
		}
	 
	 
	 private void insertGeo(Individual geo, Geo geometry){
		 String ponto ="POINT("+geometry.toString().replaceAll("Geo", "").replace("[", "").replace("]", "").replaceAll(",", " ")+");"+WktLiteral.CRS84;
		 TypeMapper.getInstance().registerDatatype(WktLiteral.wktLiteralType);
		 geo.addLiteral(model.getProperty("http://www.opengis.net/ont/geosparql#asWKT"), ResourceFactory.createTypedLiteral(ponto, WktLiteral.wktLiteralType));
		 ObjectProperty relation  = model.createObjectProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		 geo.addProperty(relation, "<http://www.opengis.net/ont/sf#Point>");
	 }
	 
	 private void insertIndividualsWithRelation(Individual pl1, Place p,int common){
		 if(!p.getLocation().equals("")){
			pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"locality"),p.getLocation());
		 }
		 if(p.getCounty()!=null && p.getCounty().getNome()!=null && !p.getCounty().getNome().equals(""))
			 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"county"),p.getCounty().getNome());
		 	if(p.getCounty()!=null){
		 		ObjectProperty relation  = model.createObjectProperty(model.getNsPrefixURI("")+"sameAs");
		 		if( p.getCounty().getURI()!=null &&  !p.getCounty().getURI().equals("")){
		 			pl1.addProperty(relation, p.getCounty().getURI());
		 			relation = model.createObjectProperty(model.getNsPrefixURI("")+"part_of");
		 			pl1.addProperty(relation, p.getCounty().getURI());
		 		}
		 		
		 }
		 if(p.getYear()<=2015)
			 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"date"),p.getYear());
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"agreement"),0);
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"contributors"),0); 
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"ntriples"),common);
		 
		 
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
