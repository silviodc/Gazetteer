package br.usp.icmc.gazetteer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.icmc.gazetteer.client.GazetteerService;
import br.usp.icmc.gazetteer.shared.Locality;
import br.usp.icmc.gazetteer.shared.Out_Polygon;
import br.usp.icmc.gazetteer.shared.User;

import com.bbn.openmap.geo.Geo;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class GazetteerServiceImpl extends RemoteServiceServlet implements GazetteerService{

	/**
	 * Autor Silvio
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean isRead = false;

	private com.hp.hpl.jena.query.Query  sparql;
	private final String URL_endpoint = "http://biomac.icmc.usp.br:8080/swiendpoint/Query";
	private  final double treshold=0.4;


	private static HashMap<Integer,Locality> result;
	private static List<Locality> resultGeo;
	private static List<String> classes;
	private static String [] valueCoord;
	ArrayList<String> stop_words = new ArrayList<String>();
	private static OntModel model;
	private final int listShow=5;
	private static int count =0;
	private static HashMap<String,OntClass> ontClasses = new HashMap<String,OntClass>();
	private static HashMap<String,OntProperty> ontPropertiess = new HashMap<String,OntProperty>();
	@Override
	public List<Locality> findPlacesWithOutCoord() {
		SPRQLQuery spql = new SPRQLQuery();
		List<Locality> lista = new ArrayList<Locality>();

		if(!spql.askService())
			return lista;

		if(result == null)
			result = spql.makeSPARQLQuery();

		for(int i=0; i< result.size(); i++){
			lista.add(result.get(count));
			count++;
			if(i>listShow || i==result.size())
				break;			
		}
		//	System.out.println(lista.size());
		return lista;
	}

	public void loadClasses(){
		ExtendedIterator<OntProperty> iter = model.listAllOntProperties();
		while (iter.hasNext()) {
			OntProperty thisClass = (OntProperty) iter.next();
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
					ontPropertiess.put(labl, thisClass);
				}
			}
		}
		Set<String> keys = ontPropertiess.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String input = it.next();
			System.out.println(input+"  "+ontPropertiess.get(input));
		}

	}
	public void loadProperties(){
		ExtendedIterator<OntClass> iter = model.listClasses();
		while (iter.hasNext()) {
			OntClass thisClass = (OntClass) iter.next();
			ExtendedIterator label = thisClass.listLabels(null);
			while (label.hasNext()) {
				RDFNode thisLabel = (RDFNode) label.next();
				if(thisLabel.isLiteral()){
					if(!thisLabel.toString().contains("geosparql")){
						String labl = thisLabel.toString().split("http")[0].replaceAll("@en", "").replaceAll("@pt", "").toLowerCase();
						labl = Normalizer.normalize(labl, Normalizer.Form.NFD);  
						labl = labl.replaceAll("[^\\p{ASCII}]", "");
						labl = labl.replaceAll("^^", "");
						if(labl.contains("^^"))
							labl = labl.substring(0, labl.length()-2);
						//	System.out.println(labl);
						ontClasses.put(labl, thisClass);
					}
				}
			}
		}
		Set<String> keys = ontClasses.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String input = it.next();
			System.out.println(input+"  "+ontClasses.get(input));
		}
	}


	private OntClass useMoreGeneric(List<OntClass> cl) {
		return cl.get(0);
	}
	private OntClass getMoreSpecifc(List<OntClass> cl) {
		System.out.println(cl.size());
		OntClass first = cl.get(0);
		boolean find = false;
		for(int i=1;i<cl.size();i++){
			//Answer true if the given class is a sub-class of this class.
			if(cl.get(i).hasSubClass(first)){
				first = cl.get(i);
				find = true;
				//Answer true if the given class is a sub-class of this class.
			}else if(first.hasSubClass(cl.get(i))){
				first = cl.get(i);
				find = true;
			}
		}
		if(find)
			return first;		
		if(!find && cl.size()>=1)
			return cl.get(cl.size()-1);
		return null;
	}


	@Override
	public List<Locality> searchLocalities(String search) throws Exception {
		SPRQLQuery spql = new SPRQLQuery();
		List<String> parameters=new ArrayList<String>();
		parameters.add("instance");
		parameters.add("date");
		parameters.add("locality");
		parameters.add("contributors");
		parameters.add("agreement");			
		parameters.add("wkt");
		parameters.add("county");
		parameters.add("triplas");
		parameters.add("geo1");
		List<Locality> result = new ArrayList<Locality>();
		System.out.println(search);

		HashMap<OntClass,String> identified = new HashMap<OntClass,String>();

		System.out.println("vai iniciar a busca");
		if(model==null){
			model = loadOntology();
			loadClasses();
			loadProperties();
		}
		List<OntClass> cl = new ArrayList<OntClass>();
		List<OntProperty> properties = new ArrayList<OntProperty>();
		cl.addAll(findClasses(search));
		properties.addAll(findProperties(search));
		if(cl.size()>1){
			identified = linkClassePlaceNames(search,properties,cl);
			OntClass moreGeneric = useMoreGeneric(cl);
			OntClass moreSpecific = getMoreSpecifc(cl);
			System.out.println("MoreGeneric:  "+moreGeneric.toString());
			System.out.println("MoreSpecific: "+moreSpecific.toString());
			result.addAll(findIntersection(moreGeneric,moreSpecific,properties,identified,parameters));
			result.addAll(findProximity(moreGeneric,moreSpecific,properties,identified,search,parameters));
			cl.remove(moreSpecific);
			
		}else{
			result.addAll(findSinglePlace(cl,search.toLowerCase(),parameters));
		}

		return result;
	}

	private Collection<? extends OntClass> findClasses(String search) {
		List<OntClass> cl = new ArrayList<OntClass>();
		String temp [] =search.toLowerCase().split(" "); 
		for(int n=0;n<temp.length-1;n++){
			String tipo = temp[n].toLowerCase().trim()+" "+temp[n+1].toLowerCase().trim();
			if(ontClasses.containsKey(tipo)){
				cl.add(ontClasses.get(tipo));
			}else if(ontClasses.containsKey(temp[n])){
				cl.add(ontClasses.get(temp[n]));
			}
		}
		return cl;
	}

	private Collection<? extends OntProperty> findProperties(String search) {
		List<OntProperty> properties = new ArrayList<OntProperty>();
		String temp [] =search.toLowerCase().split(" "); 
		for(int n=0;n<temp.length-1;n++){
			String tipo = temp[n].toLowerCase().trim()+" "+temp[n+1].toLowerCase().trim();
			if(ontPropertiess.containsKey(tipo)){
				properties.add(ontPropertiess.get(tipo));
			}else if(ontPropertiess.containsKey(temp[n])){
				properties.add(ontPropertiess.get(temp[n]));
			}
		}

		return properties;
	}

	private boolean notProperty(List<OntProperty> pro,String word){
		for(OntProperty p:pro){
			ExtendedIterator label = p.listLabels(null);
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
					if(word.equalsIgnoreCase(labl))
						return false;
				}
			}
		}
		return true;
	}

	private boolean notClasse(String word, List<OntClass> classes){
		for(OntClass p:classes){
			ExtendedIterator label = p.listLabels(null);
			while (label.hasNext()) {
				RDFNode thisLabel = (RDFNode) label.next();
				if(thisLabel.isLiteral()){
					if(!thisLabel.toString().contains("geosparql")){
						String labl = thisLabel.toString().split("http")[0].replaceAll("@en", "").replaceAll("@pt", "").toLowerCase();
						labl = Normalizer.normalize(labl, Normalizer.Form.NFD);  
						labl = labl.replaceAll("[^\\p{ASCII}]", "");
						labl = labl.replaceAll("^^", "");
						if(labl.contains("^^"))
							labl = labl.substring(0, labl.length()-2);
						if(word.equalsIgnoreCase(labl)){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private String removeStopWord(String name){
		String temp[] = name.split(" ");
		String place ="";
		for(int i=0;i<temp.length;i++){		
			if(not_stop_word(temp[i],stop_words))
				place+=temp[i]+" ";
		}
		if(place.length()>1)
			return place.substring(0, place.length()-1);
		else
			return "";
	}
	
	private HashMap<OntClass,String> linkClassePlaceNames(String search,List<OntProperty> property,List<OntClass>classes) throws FileNotFoundException, IOException {
		HashMap<OntClass,String> identified = new HashMap<OntClass,String>();
		String temp[] = search.split(" ");
		stop_words = read_stop_words("files"+File.separator+"stop_words.txt");
		String place ="";
		OntClass fist=null,atual= classes.get(0);
		for(int i=0;i<temp.length;i++){				
			if(not_stop_word(temp[i],stop_words) && notClasse(temp[i],classes) && notProperty(property,temp[i]) && notNumber(temp[i])){
				place += temp[i] +" ";
			}else if (!notClasse(temp[i],classes) && !place.trim().equalsIgnoreCase("")){
				identified.put(fist, place);
				place = "";
			} else if (!notClasse(temp[i],classes)){
				fist = atual;
				int index = classes.indexOf(fist)+1;
				if(index<classes.size()){
					atual = classes.get(index);				
				}

			}	
		}		
		identified.put(atual,place);

		return identified;
	}

	private boolean notNumber(String string) {
		try{
			Pattern p = Pattern.compile("-?\\d+");
			Matcher m = p.matcher(string);
			while (m.find()) {
				Integer.parseInt(m.group());
				return false;
			}
		}catch(Exception e){

		}
		return true;
	}

	public ArrayList<String> read_stop_words(String file_path) throws FileNotFoundException, IOException {
		String word ="";
		ArrayList<String> uselles = new ArrayList<String>();
		File arq = new File(file_path).getAbsoluteFile();
		BufferedReader lines =  new BufferedReader(new InputStreamReader(new FileInputStream(file_path), "UTF-8"));
		while ((word = lines.readLine()) != null) {
			uselles.add(word);
		}
		return uselles;
	}

	public boolean  not_stop_word(String word,ArrayList<String> stop_words){
		String temp [];
		String ok ="";
		String localtemp = Normalizer.normalize(word, Normalizer.Form.NFD);  
		localtemp = localtemp.replaceAll("(?!\")\\p{Punct}", "");
		for (String s : stop_words) {
			if(localtemp.equals(s))
				return false;
		}

		return true;     
	}
	public Collection<? extends Locality> findSinglePlace(List<OntClass> cl,String search,List<String> parameters) throws Exception {
		//try find objects from the first type found
		System.out.println("SINGLE SEARCH!!");
		
		
		List<Locality> list = new ArrayList<Locality>();
		String queryString="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#> "
				+ " PREFIX geof: <http://www.opengis.net/def/function/geosparql/>"
				+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#> "
				+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
		if(!cl.isEmpty()){
			queryString+= " SELECT (COUNT(?s) AS ?NumOfTriples)  WHERE{ ?s a <"+cl.get(0).toString()+">. ";
		}else{
			queryString+= " SELECT (COUNT(?s) AS ?NumOfTriples)  WHERE{ ?s ?p ?o .";
		}
		queryString+= " ?s swi:locality ?local1 . "
				+ " ?s geo:hasGeometry ?geo1 . "
				+ " ?geo1 geo:asWKT ?wkt1 . }";
		System.out.println(queryString);
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

		ResultSet results= queryExecution.execSelect();

		int limit=0;
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			limit=soln.get("NumOfTriples").asLiteral().getInt();
		}
		int offset=0;
		int count=0;
		while(count<limit){
			count +=100;
			 queryString="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#> "
					+ " PREFIX geof: <http://www.opengis.net/def/function/geosparql/>"
					+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#> "
					+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
			if(!cl.isEmpty()){
				queryString+= " SELECT * WHERE{ ?s a <"+cl.get(0).toString()+">. ";
			}else{
				queryString+= " SELECT *  WHERE{ ?s ?p ?o .";
			}
			queryString+= " ?s swi:locality ?local1 . "
					+ " ?s geo:hasGeometry ?geo1 . "
					+ " ?geo1 geo:asWKT ?wkt1 . } limit "+limit+" offset "+offset;
			System.out.println(queryString);
			 query = QueryFactory.create(queryString) ;
			 queryExecution=new QueryEngineHTTP(URL_endpoint,query);

			 results= queryExecution.execSelect();
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;
				String place = soln.get("local1").asLiteral().getString();
				place = removeStopWord(place);
				if(similarityBettewenplaces(search,place)){
					System.out.println(soln);
				}
		}
		
	
			//				Literal local1 = soln.getLiteral("?local1");
			//				String[] place = {local1.getString()};
			//				HashMap<String,String> triplestore=new HashMap<String,String>();
			//				triplestore.put("locality","?local1");
			//				triplestore.put("geo", "?wkt1");
			//				
			//					System.out.println("Sao similares"+place);
			//					list.add(createLocal(triplestore,parameters));
			//				}
		}
		return list;
	}

	public List<Locality> findProximity(OntClass moreGeneric, OntClass moreSpecific,List<OntProperty> property,HashMap<OntClass, String> identified,String search, List<String> parameters) throws Exception{
		String queryString="";
		List<Locality> result = new ArrayList<Locality>();

		String diretional [] ={"southeast","southwest","northwest","northeast","west","south","north","east"};

		for(OntProperty ot:property){
			System.out.println(ot.toString().split("#")[1]);
			if(ot.toString().split("#")[1].equals("near")){
				int distance = findistance(search,property);
				if(distance==0)
					distance =300;
				String wkt = pointMoreSpecific(identified,moreSpecific);
				System.out.println(wkt);
				if(wkt!=null){
					//calcule distance
					queryString="PREFIX geof:<http://www.opengis.net/def/function/geosparql/>"
							+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
							+ "PREFIX geo:<http://www.opengis.net/ont/geosparql#>"
							+ "PREFIX opengis: <http://www.opengis.net/def/uom/OGC/1.0/>"
							+ "PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#>"
							+ "SELECT *";
					if(!moreGeneric.equals(ontClasses.get("locais"))){
						queryString += " WHERE { ?s a <"+moreGeneric.toString()+"> ."
								+ "        ?s geo:hasGeometry ?s1 ."
								+ "	?s1 geo:asWKT ?o1 ."
								+ "        ?s swi:locality ?locality .";

						if(identified.containsKey(moreGeneric)){
							queryString+= " FILTER regex(str(?locality), \"locality\"). "
									+ "	 FILTER(geof:sfWithin(?o1, geof:buffer("+wkt+", "+distance+", opengis:metre))).}";
						}else{
							queryString += "	 FILTER(geof:sfWithin(?o1, geof:buffer("+wkt+", "+distance+", opengis:metre))).}";
						}
					}else{
						queryString += " WHERE { ?s geo:hasGeometry ?s1 ."
								+ "	?s1 geo:asWKT ?o1 ."
								+ "        ?s swi:locality ?locality ."
								+ "	 FILTER(geof:sfWithin(?o1, geof:buffer("+wkt+", "+distance+", opengis:metre))).}";
					}


					System.out.println(queryString);
					com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
					QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

					ResultSet results= queryExecution.execSelect();

					while(results.hasNext()) {
						QuerySolution soln = results.nextSolution() ;
						System.out.println(soln);
						//		System.out.println(soln);
						HashMap<String,String> tripleStore=new HashMap<String,String>();

						//		if(similarityBettewenplaces(search,place))
						//	result.add(createLocal(tripleStore, parameters));

					}			
				}
			}
		}
		return result;
	}
	private Locality createLocal(HashMap<String, String> tripleStore,List<String> parameters) {
		Locality l = new Locality();
		Iterator<String> iterator2 = parameters.iterator();
		try{
			while(iterator2.hasNext()){
				String value2 = iterator2.next();
				if(value2.equalsIgnoreCase("geo1")){
					l.setIdGeo(tripleStore.get(value2));
				}else if(value2.equals("instance")){ 
					l.setIdTriple(tripleStore.get(value2));
				}else if(value2.equals("date"))
					l.setDate(tripleStore.get(value2));
				else if(value2.equals("locality")) 
					l.setLocality(tripleStore.get(value2));

				else if(value2.equals("contributors")) 
					l.setContributors(Integer.parseInt(tripleStore.get(value2)));

				else if(value2.equals("agreement")) 
					l.setAgreeCoordinate(Integer.parseInt(tripleStore.get(value2)));

				else if(value2.equals("wkt")){
					String point = tripleStore.get(value2).replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");
					point = point.substring(7, point.length()-2);
					float x = transformFloat(point.split(" ")[0]);
					float y = transformFloat(point.split(" ")[1]);
					l.setGeometry(x+" "+y);
				}

				else if(value2.equals("county"))
					l.setCounty(tripleStore.get(value2));

				else if(value2.equals("triplas"))
					l.setNtriplas(tripleStore.get(value2));


			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(l.getLocality()+"  -- "+l.getGeometry()+" -- "+l.getNtriplas());

		return l;
	}

	public float transformFloat(String numero) {
		float valor = 0;
		char n[] = numero.toCharArray();
		numero = "";
		for (int i = 0; i < n.length; i++) {
			if (n[i] == ',') {
				numero += ".";
			}
			numero += n[i];
		}
		try {
			valor = Float.parseFloat(numero);
		} catch (Exception e) {
			return 0;
		}
		return valor;
	}


	private String pointMoreSpecific(HashMap<OntClass, String> identified, OntClass morespecifc) throws Exception {

		List<Locality> result = new ArrayList<Locality>();
		String join="";
		Set<OntClass>  key = identified.keySet();
		Iterator<OntClass> it = key.iterator();
		String	queryString ="";
		while(it.hasNext()){
			OntClass temp = it.next();
			if(morespecifc.equals(temp)){
				join = identified.get(temp);
				queryString="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#> "
						+ " PREFIX geof: <http://www.opengis.net/def/function/geosparql/>"
						+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#> "
						+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
						+ " SELECT * WHERE{ ?s a <"+temp.toString()+">. "
						+ "  ?s swi:county ?c."
						+ " ?s swi:hasGeometry ?geo ."
						+ " ?geo geo:asWKT ?wkt. " ;
			}
		}
		if(identified.containsKey(morespecifc)){
			String temp = identified.get(morespecifc);
			temp = temp.substring(0, temp.length()-1);
			queryString+=" FILTER(STRSTARTS(lcase(STR(?c)), lcase(\""+temp+"\"))). }";
		}else{
			queryString+="}";
		}

		System.out.println(queryString);
		if(!queryString.equalsIgnoreCase("")){
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
			QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

			ResultSet results= queryExecution.execSelect();

			while(results.hasNext()) {

				QuerySolution soln = results.nextSolution() ;
				//	System.out.println(soln);
				Literal local1 = soln.getLiteral("?c");

				String place = local1.getString().replaceAll("<http://www.w3.org/2001/XMLSchema#string>", "");
				place = removeStopWord(place);
				HashMap<String,String> triplestore=new HashMap<String,String>();
				triplestore.put("locality","?c");
				triplestore.put("geo", "?wkt");
				if(similarityBettewenplaces(join,place)){
					Locality l = new Locality();
					l.setGeometry(soln.get("wkt").asLiteral().getString());
					if(l.getGeometry().contains("POLYGON"))
						l.setGeometry(changeCoords(l.getGeometry()));
					System.out.println(l.getGeometry());
					return "\""+l.getGeometry()+";http://www.opengis.net/def/crs/EPSG/0/4326\"^^<http://strdf.di.uoa.gr/ontology#WKT>";
				}

			}
		}
		return null;
	}

	private String changeCoords(String geometry) {
		Out_Polygon out = new Out_Polygon();		 
		return out.buildPolygon(geometry,true);
	}

	private int findistance(String search,List<OntProperty> property) {
		List<Integer> values = new ArrayList<Integer>();
		try{
			String [] temp = search.split(" ");
			for(int i=0;i<temp.length;i++){
				Pattern p = Pattern.compile("-?\\d+");
				Matcher m = p.matcher(temp[i]);
				while (m.find()) {
					values.add(Integer.parseInt(m.group()));
				}
			}
		}catch(Exception e){

		}
		int distance =0;
		for(int temp:values){
			distance+=temp;
		}
		String temp[] = search.split(" ");
		for(int i=0;i<temp.length;i++){
			if(temp[i].equals("km")){
				distance=distance*1000;
				break;
			}
		}
		return distance;
	}


	public Locality searchSingle( HashMap<OntClass, String> identified,OntClass moreGeneric) throws Exception{
		String queryString="";
		Locality l = new Locality();
		if (moreGeneric.toString().equals("http://schema.org/City")){
			String wkt = pointMoreSpecific(identified,moreGeneric);
			l.setGeometry(wkt);
			return l; 
		}else if(identified.containsKey(moreGeneric)){

			String temp = identified.get(moreGeneric);
			queryString=" PREFIX geo: <http://www.opengis.net/ont/geosparql#> "
					+ " PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
					+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#> "
					+ " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ " SELECT (COUNT(?instance2) AS ?NumOfTriples) WHERE{  ?instance a <"+moreGeneric.toString()+"> . "
					+ " ?instance2 geo:hasGeometry ?geo1 . }";
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
			QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);
			ResultSet results= queryExecution.execSelect();
			int limit=0;
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;
				limit=soln.get("NumOfTriples").asLiteral().getInt();
			}
			int offset=0;
			int count=0;
			while(count<limit){
				count +=100;
				queryString =" PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
						+ " PREFIX geof: <http://www.opengis.net/def/function/geosparql/>"
						+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#>"
						+ " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ " SELECT * WHERE{"
						+ " ?instance2 a <"+moreGeneric.toString()+"> ."
						+ " ?instance2 geo:hasGeometry ?geo2 ."
						+ " ?instance2 swi:locality ?local2 . "
						+ " ?instance2 geo:hasGeometry ?geo2 . "
						+ " ?geo2 geo:asWKT ?wkt2 .} limit"+count+" offset "+offset;
				query = QueryFactory.create(queryString) ;
				queryExecution=new QueryEngineHTTP(URL_endpoint,query);
				results= queryExecution.execSelect();
				while(results.hasNext()) {
					QuerySolution soln = results.nextSolution() ;
					String name=soln.get("local2").asLiteral().getString();
					String geo = soln.get("wkt2").asLiteral().getString();
					name = removeStopWord(name);
					if(similarityBettewenplaces(name,identified.get(moreGeneric))){
						if(geo.contains("POLYGON")){
							l.setIdTriple(soln.get("instance2").asResource().getURI());
							l.setIdGeo(soln.get("geo2").asResource().getURI());
							l.setLocality(name);
							l.setGeometry(geo);
							if(!moreGeneric.toString().equals("http://schema.org/State")){
								l.setGeometry(changeCoords(l.getGeometry()));							 	
							}
							l.setGeometry("\""+l.getGeometry()+";http://www.opengis.net/def/crs/EPSG/0/4326\"^^<http://strdf.di.uoa.gr/ontology#WKT>");
							count = limit+1;
							break;
						}
					}
				}
				offset+=count;
			}
			return l;
		}
		return null;
	}

	public List<Locality> findIntersection(OntClass moreGeneric,OntClass moreSpecific,List<OntProperty> properties, HashMap<OntClass, String> identified, List<String> parameters2) throws Exception{
		List<Locality> result = new ArrayList<Locality>();
		boolean execute = false;
		for(OntProperty ot:properties){

			if(ot.toString().split("#")[1].equals("sfWithin") || ot.toString().split("#")[1].equals("south")){
				System.out.println("=======DD "+ot.toString().split("#")[1]);
				execute = true;
			}
		}
		if(properties.isEmpty())
			execute = true;
		if(execute){
			Locality local1 = searchSingle(identified,moreGeneric);
			Locality local2 = searchSingle(identified,moreSpecific);

			String queryString="";
			queryString=" PREFIX geo: <http://www.opengis.net/ont/geosparql#> "
					+ " PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
					+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#> "
					+ " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
			if(!moreGeneric.equals(ontClasses.get("locais")) && local1==null){
				queryString += " SELECT * WHERE{  ?instance a <"+moreGeneric.toString()+"> . "
						+ " ?instance geo:hasGeometry ?geo1 . "
						+ " ?instance swi:locality ?local1 . "
						+ " ?instance geo:hasGeometry ?geo1 . "
						+ " ?geo1 geo:asWKT ?wkt1 . ";
			}else if(local1==null){
				queryString	+= " SELECT * WHERE{  "
						+ " ?instance geo:hasGeometry ?geo1 . "
						+ " ?instance swi:locality ?local1 . "
						+ " ?instance geo:hasGeometry ?geo1 . "
						+ " ?geo1 geo:asWKT ?wkt1 . ";
			}

			if(!moreSpecific.equals(ontClasses.get("locais")) && local2==null){
				queryString+=" ?instance2 a <"+moreSpecific.toString()+"> . "
						+ " ?instance2 geo:hasGeometry ?geo2 . "
						+ " ?instance2 swi:locality ?local2 . "
						+ " ?instance2 geo:hasGeometry ?geo2 . "
						+ " ?geo2 geo:asWKT ?wkt2 . ";
			}else if(local2==null){
				queryString+=" ?instance2 geo:hasGeometry ?geo2 . "
						+ " ?instance2 swi:locality ?local2 . "
						+ " ?instance2 geo:hasGeometry ?geo2 . "
						+ " ?geo2 geo:asWKT ?wkt2 . ";
			}
			if(local1==null)
				queryString+= " FILTER(geof:sfIntersects(?wkt1,"+local2.getGeometry()+" )). }";
			else if(local2==null)
				queryString+= " FILTER(geof:sfIntersects(?wkt2,"+local1.getGeometry()+" )). }";
			else
				queryString+= " FILTER(geof:intersects(?wkt1,?wkt2)). }";
			System.out.println(queryString);
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
			QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

			ResultSet results= queryExecution.execSelect();

			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;
				System.out.println(soln);
			}
		}
		return result;
	}


	private boolean similarityBettewenplaces(String search, String place) throws Exception {

		Bigram bigram = new Bigram();
		if(bigram.stringSimilarityScore(bigram.bigram(search), bigram.bigram(place))>=treshold){
			return true;
		}
		System.out.println(place+"   "+bigram.stringSimilarityScore(bigram.bigram(search), bigram.bigram(place)));

		return false;
	}

	public OntModel loadOntology(){
		//String ontologyIRI = "https://raw.githubusercontent.com/silviodc/Gazetteer/master/Collaborative_Gazetteer/files/Gazetteer_v_1_1.owl";

		String ontologyIRI = "files"+File.separator+"Gazetteer_v_1_1.owl";
		File f = new File(ontologyIRI);
		System.out.println(f.getAbsolutePath());
		OntModel m = ModelFactory.createOntologyModel();
		InputStream in = FileManager.get().open(ontologyIRI);
		if (in == null) return null;

		return (OntModel) m.read(in,"");
	}

	@Override
	public Integer insertLocality(Locality locality) {
		SPRQLQuery spql = new SPRQLQuery();
		Out_Polygon out = new Out_Polygon();
		Geo p;
		if(locality==null)
			return 0;
		System.out.println(locality.getGeometry());
		if(locality.getGeometry().contains("POINT")){
			String value = locality.getGeometry().replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");			
			value = value.substring(6, value.length()-2);
			double y = out.transformFloat(value.split(" ")[0]);
			double x = out.transformFloat(value.split(" ")[1]);
			locality.setGeometry("POINT("+x+" "+y+")");		  	
			System.out.println(locality.getGeometry());
		}

		if(!spql.askService())
			return 0;
		if(!locality.isUpdateGeo()){
			int individual = spql.getIndex();
			String query = "DELETE WHERE{ <"+locality.getIdTriple()+"> ?p ?o .} ; ";
			query += " INSERT DATA { <"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \""+locality.getContributors()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
			query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#agreement> \""+locality.getAgreeCoordinate()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
			query +="<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#infotype> \"user\"^^<http://www.w3.org/2001/XMLSchema#string> . ";
			query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#locality> \""+locality.getLocality()+"\"^^<http://www.w3.org/2001/XMLSchema#string> . ";
			query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#county> \""+locality.getCounty()+"\"^^<http://www.w3.org/2001/XMLSchema#string> . ";
			query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#ntriples> \""+locality.getNtriplas()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
			query += "<"+locality.getIdTriple()+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+locality.getType()+"> . ";
			query+="<"+locality.getIdTriple()+"> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://www.semanticweb.org/ontologies/Gazetter#"+(individual)+"> . ";
			query += " <http://www.semanticweb.org/ontologies/Gazetter#"+(individual)+"> <http://www.opengis.net/ont/geosparql#asWKT> \""+locality.getGeometry()+";http://www.opengis.net/def/crs/EPSG/0/4326\"^^<http://strdf.di.uoa.gr/ontology#WKT> . ";
			query += "<http://www.semanticweb.org/ontologies/Gazetter#"+(individual)+"> <http://www.semanticweb.org/ontologies/Gazetter#date> \""+locality.getDate()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
			query +="<http://www.semanticweb.org/ontologies/Gazetter#"+(individual)+">  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#Geometry>  . }";
			query+=" }";
			System.out.println(query);
			spql.insertDataEndpoint(query);
		}else{
			int individual = spql.getTempIndex();
			individual++;
			String val [] = locality.getIdTriple().split("#");
			String tripleID = val[0]+"/temp/"+val[1];
			String query = "INSERT DATA {"
					+ " GRAPH <http://swigazetteer/temp> { <"+tripleID+"> <http://swigazetteer/temp/ontology/hasGeometry> <http://swigazetteer/temp/id/"+individual+"> ."
					+ "	<http://swigazetteer/temp/id/"+individual+">  <http://www.opengis.net/ont/geosparql#asWKT> \""+locality.getGeometry()+";http://www.opengis.net/def/crs/EPSG/0/4326\"^^<http://strdf.di.uoa.gr/ontology#WKT> ."
					+ " <http://swigazetteer/temp/id/"+individual+">  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> \"<http://www.opengis.net/ont/sf#Point>\" ."
					+ " <http://swigazetteer/temp/id/"+individual+">  <http://swigazetteer/temp#date> \""+locality.getDate()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .	} }";

			System.out.println(query);
			spql.insertDataEndpoint(query);
			Geo center = spql.getCentertemp(tripleID);
			if(center!=null){
				String geo = "POINT("+center.getLatitude()+" "+center.getLongitude()+")";
				query ="DELETE WHERE{ <"+locality.getIdGeo()+">  <http://www.opengis.net/ont/geosparql#asWKT> ?a . ";
				query+="<"+locality.getIdTriple()+">  <http://www.semanticweb.org/ontologies/Gazetter#agreement> ?b . ";
				query+="<"+locality.getIdTriple()+">  <http://www.semanticweb.org/ontologies/Gazetter#infotype> ?c . ";
				query+="<"+locality.getIdTriple()+">  <http://www.semanticweb.org/ontologies/Gazetter#locality> ?d . ";
				query+="<"+locality.getIdTriple()+">  <http://www.semanticweb.org/ontologies/Gazetter#county> ?e . }; ";
				query += "INSERT DATA { <"+locality.getIdGeo()+"> <http://www.opengis.net/ont/geosparql#asWKT> \""+geo+";http://www.opengis.net/def/crs/EPSG/0/4326\"^^<http://strdf.di.uoa.gr/ontology#WKT> . ";
				query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#agreement> \""+locality.getAgreeCoordinate()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
				query +="<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#infotype> \"user\"^^<http://www.w3.org/2001/XMLSchema#string> . ";
				query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#locality> \""+locality.getLocality()+"\"^^<http://www.w3.org/2001/XMLSchema#string> . ";
				query += "<"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#county> \""+locality.getCounty()+"\"^^<http://www.w3.org/2001/XMLSchema#string> . ";
				query += "<"+locality.getIdTriple()+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+locality.getType()+"> . }";

				System.out.println(query);
				spql.insertDataEndpoint(query);
			}
		}


		return 1;
	}

	@Override
	public void agreeLinkedData(Locality locality) {
		SPRQLQuery spql = new SPRQLQuery();
		String query = "DELETE where{ <"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#agreement> ?o .}; "
				+ "INSERT DATA { <"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#agreement> \""+locality.getAgreeCoordinate()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . }";
		System.out.println(query);
		spql.insertDataEndpoint(query);
		query = "DELETE where{ <"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#contributors> ?o .}; "
				+ "INSERT DATA { <"+locality.getIdTriple()+"> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \""+locality.getContributors()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . }";	
		System.out.println(query);
		spql.insertDataEndpoint(query);
	}

	@Override
	public void updateUser(String[] user) {
		String update = "UPDATE user SET nome='"+user[1]+"', usuario='"+user[2]+"',senha='"+user[3]+"',type='"+user[4]+"' WHERE idUser="+user[0];

	}

	@Override
	public void insertUser(String[] user) {
		String insert = "INSERT INTO table_name (nome, usuario, senha,type) VALUES ('"+user[0]+"', '"+user[1]+"', '"+user[2]+"','"+user[3]+"')";

	}

	@Override
	public User getUser(String login, String senha) throws Exception {
		MySQLConnection mysql = new MySQLConnection();
		System.out.println("vai acessar o banco");
		return mysql.authenticateUser(login, senha);
	}


	@Override
	public List<Float[]> getPolygons(){
		final List<Float[]> points = new ArrayList<Float[]>();
		Out_Polygon out = new Out_Polygon();
		SPRQLQuery spql = new SPRQLQuery();

		if(!spql.askService())
			return points;

		if(resultGeo == null){

			resultGeo=spql.getGeometriesInsideTripleStore();
		}

		System.out.println("Carregou dados");
		try {
			out.setPoly(out.buildPolygon(new File("files"+File.separator+"Amazonas_polygon.txt").getAbsolutePath()));
		} catch (IOException e) {
			System.out.println("Erro ao carregar o poligono");
			e.printStackTrace();
		}

		//  	File ff =new File("ponts.kml");
		//  	try {
		//		FileWriter fw = new FileWriter(ff);

		System.out.println("Vai construir os pontos");
		//create a semi-random grid of features to be clustered
		Iterator<Locality> iterator = resultGeo.iterator();
		while(iterator.hasNext()){
			Locality loc =  iterator.next();
			//	System.out.println(loc.getGeometry());
			if(loc.getGeometry().contains("POINT")){
				String value = loc.getGeometry().replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");
				//     	fw.write(value+"\n");
				value = value.substring(7, value.length()-2);
				float x = out.transformFloat(value.split(" ")[0]);
				float y = out.transformFloat(value.split(" ")[1]);

				if(out.insidePolygon(out.getPoly(), x, y))
					points.add(new Float []{ y,x});
			}
		}

		//	for(int i=0;i<points.size();i++){
		//		fw.write("<Point> <coordinates>"+points.get(i)[0]+","+points.get(i)[1]+",0</coordinates> </Point>\n");}
		//	fw.close();
		//	}catch(Exception ex){}
		System.out.println("Voltando os pontos");
		return points;
	}

	@Override
	public List<String> getOntTypes() {
		SPRQLQuery spql = new SPRQLQuery();
		if(classes==null){
			classes = spql.getTypes();
		}
		return classes;
	}

	@Override
	public String[] infoServer() {
		if(!isRead){
			SPRQLQuery spql = new SPRQLQuery();
			if(spql.askService() && valueCoord==null){
				String [] valueCoord = spql.getInfo();

				return valueCoord;
			}
		}
		return valueCoord;
	}

}
