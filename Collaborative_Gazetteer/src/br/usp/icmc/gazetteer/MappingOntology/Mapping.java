/*
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
package br.usp.icmc.gazetteer.MappingOntology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.cluster.Star_algorithm;

import com.bbn.openmap.geo.Geo;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
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
	private OntClass county;
	private String basePrefix;
	private String geoSparqlP;
	
	public void rebuildModel(){
		model = OpenConnectOWL();
        geo = model.getOntClass("http://www.opengis.net/ont/sf#Geometry");
        feature = model.getOntClass("http://www.geonames.org/ontology#Feature");
        county = model.getOntClass("http://schema.org/City");
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
	     Star_algorithm.fLogger.log(Level.SEVERE,group.size()+" <<< total Grupos >>>>>"+total);
         for(Group rep:group){
        //	
        	  	 Place p = rep.getCentroid();
        	    // Star_algorithm.fLogger.log(Level.SEVERE,""+p.getLocation()+""+p.getCounty()+""+p.getGeometry());
        	  	 int date[] = Date(rep.getPlaces());
       	  	  
        	  		 Individual pl1;
        			 total++;
        			 OntClass type = useMoreGeneric(p.getTypes());
        			 pl1 = model.createIndividual(basePrefix+total, type);
        			 
					 int index = insertIndividualsWithRelation(pl1,p,rep.getPlaces().size(),total);
					 total = index;
	        		 if(p.getGeometry()!=null){
	        			 coords=total+1;
	        			 Individual geo1 = model.createIndividual(basePrefix+coords,geo );
	        			 ObjectProperty geoRelation  = model.createObjectProperty( geoSparqlP+"hasGeometry");
	        			 insertGeo(geo1, p.getGeometry(),date,p.isGoldStandart(),p.getWktgoldStandart());
	        			 pl1.addProperty(geoRelation, geo1);
	        			 total+=2;
	        		 }
	        	//	 Star_algorithm.fLogger.log(Level.SEVERE,total+" <<< total RDF");
   		 }
         Star_algorithm.fLogger.log(Level.SEVERE,group.size()+" <<< total Grupos >>>>>"+total);
         writeNtriples("triples");
         classes.clear();
         model.close();
     }
		
	private int[] Date(ArrayList<Place> places) {
		places.remove(null);
		if(places.size()<=0)
			return null;
		int min = places.get(0).getYear();
		for(int i=1;i<places.size();i++){
			if(min>places.get(i).getYear() && places.get(i).getYear()<2015)
				min = places.get(i).getYear();
		}
		int max = places.get(0).getYear();
		for(int j=1;j<places.size();j++){
			if(max<places.get(j).getYear()&& places.get(j).getYear()<2015)
				max = places.get(j).getYear();
		}
		if(min>=2015 || max>=2015 || min<=0 || max<=0)
			return null;
		
		return new int[]{min,max};
	}
		public void writeNtriples(String name){
			
			 try {
				OutputStream out = new FileOutputStream(name+".nt");
				model.write(out,"N-TRIPLES");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}

		private OntClass useMoreGeneric(List<String> cl) {
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
	 
	 
	 private void insertGeo(Individual geo, Geo geometry,int date[],boolean gold, String wkt){
		 String ponto ="";
		 if(gold){
			 ponto = wkt;
			// Star_algorithm.fLogger.log(Level.SEVERE,"GOLLDD STANDART!!!");
		 }else{
			 ponto ="POINT ("+geometry.toString().replaceAll("Geo", "").replace("[", "").replace("]", "").replaceAll(",", " ")+");"+WktLiteral.CRS84;
		 }
		 TypeMapper.getInstance().registerDatatype(WktLiteral.wktLiteralType);
		 geo.addLiteral(model.getProperty("http://www.opengis.net/ont/geosparql#asWKT"), ResourceFactory.createTypedLiteral(ponto, WktLiteral.wktLiteralType));
		 ObjectProperty relation  = model.createObjectProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		 if(ponto.split(" ")[0].equals("POINT"))
			 geo.addProperty(relation, "<http://www.opengis.net/ont/sf#Point>");
		 else if (ponto.split(" ")[0].equals("POLYGON"))
			 geo.addProperty(relation, "<http://www.opengis.net/ont/sf#Polygon>");
		 else if(ponto.split(" ")[0].equals("LINESTRING"))
			 geo.addProperty(relation, "http://www.opengis.net/ont/sf#LineString");
		 else if(ponto.split(" ")[0].equals("MULTIPOLYGON"))
			 geo.addProperty(relation, "<http://www.opengis.net/ont/sf#MultiPolygon>");
		 if(gold && date!=null)
			 geo.addLiteral(model.getProperty(model.getNsPrefixURI("")+"date"),date[0]);
		 else if(date!=null){
			 geo.addLiteral(model.getProperty(model.getNsPrefixURI("")+"date"),date[1]);
		 }
		  
	 }
	 
	 private int insertIndividualsWithRelation(Individual pl1, Place p,int common,int coords){
		 int index=coords;
		 if(!p.getLocation().equals("")){
			pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"locality"),p.getLocation());
		 }		
		if(p.getCounty()!=null && p.getCounty().getNome()!=null && !p.getCounty().getNome().equals("")){
	 		index = insertCounty(pl1,p,coords+1);	 			
		 }
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"agreement"),0);
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"contributors"),0);
		 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"ntriples"),common);
		 if(p.isGoldStandart()){
			 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"infotype"),"IBGE");
		 }else{
			 pl1.addLiteral(model.getProperty(model.getNsPrefixURI("")+"infotype"),"computed");
		 }
		 return index;
	 }
	 private int insertCounty(Individual pl1, Place p,int coords) {
		 Individual pl2=null;
		 pl2 = model.getIndividual(basePrefix+"county"+p.getCounty().hashCode());
		 System.out.println(basePrefix+"county"+p.getCounty().hashCode());
		 if(pl2==null){
			 pl2 = model.createIndividual(basePrefix+"county"+p.getCounty().hashCode(),county);
			 pl2.addLiteral(model.getProperty(model.getNsPrefixURI("")+"county"),p.getCounty().getNome());
		 	 if( p.getCounty().getURI()!=null &&  !p.getCounty().getURI().equals("")){
		 		 ObjectProperty relation  = model.createObjectProperty(model.getNsPrefixURI("")+"sameAs");
		 		 pl2.addProperty(relation, ResourceFactory.createResource(p.getCounty().getURI()));
		 		 relation = model.createObjectProperty(model.getNsPrefixURI("")+"part_of");
		 		 pl2.addProperty(relation, ResourceFactory.createResource(p.getCounty().getURI()));
		 		}
		 	 if(p.getCounty()!=null && p.getCounty().getPoint()!=null){
		 		coords++;
		 		Individual geo1 = model.createIndividual(basePrefix+coords,geo );
     			ObjectProperty geoRelation  = model.createObjectProperty( basePrefix+"hasGeometry");
		 	    if(!p.getCounty().getWktPolygon().equals("")){
		 			insertGeo(geo1, p.getCounty().getPoint(),new int[]{2015,2015},true,p.getCounty().getWktPolygon());
		 		}else{
		 			insertGeo(geo1, p.getCounty().getPoint(),new int[]{2015,2015},false,p.getCounty().getWktPolygon());
		 		}
		 	    pl2.addProperty(geoRelation, geo1);
		 	 }
		 	 ObjectProperty countyRelation  = model.createObjectProperty( basePrefix+"hasCounty");
		 	 pl1.addProperty(countyRelation, pl2);
		 }else{
			 ObjectProperty countyRelation  = model.createObjectProperty( basePrefix+"hasCounty");
			 pl1.addProperty(countyRelation, pl2);
		 }
		 return coords;
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
