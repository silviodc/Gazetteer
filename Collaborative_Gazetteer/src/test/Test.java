package test;

import improve_coordinates.Summarize;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import mapping_to_ontology.Mapping;

import org.xml.sax.SAXException;

import prepare_sample_to_check.Random_Sample;
import prepare_sample_to_check.SemanticQuery;
import put_data_in_Triple_Store.Insert_Triple_Store;
import read_files.Read_Biodiversity_files;
import read_files.Transform_and_Filter;
import TAD.Group;
import TAD.Place;
import analyze_geographical_coordinates.Out_Polygon;
import cluster.Star_algorithm;
import communicate_with_other_data_source.Build_Polygons_using_IBGE;
import communicate_with_other_data_source.DBpedia;
import communicate_with_other_data_source.Geonames;
import count_and_statistic_analyze.Count_Coordinates;

public class Test {
	private static final Logger fLogger =  Logger.getLogger(Group.class.getPackage().getName());
	private static final float similarity = (float) 0.38;
	public static void main(String[] args) throws Exception {

		long inicio = System.currentTimeMillis() / 1000;
		SemanticQuery q = new SemanticQuery();
		Random_Sample sample = new Random_Sample();
		Geonames geonames = new Geonames();
		DBpedia dbpedia = new DBpedia();
		Read_Biodiversity_files rb = new Read_Biodiversity_files();
		Transform_and_Filter tsf = new Transform_and_Filter();
		Out_Polygon out = new Out_Polygon();
		Star_algorithm start = new Star_algorithm();
		Summarize sumy = new Summarize();
		Mapping map = new Mapping();
		Insert_Triple_Store tripleStore = new Insert_Triple_Store();
		Build_Polygons_using_IBGE ibge = new Build_Polygons_using_IBGE();
		//sample.checkAmoutCorrectBeforeSWI("corrected_link_before_swi.txt");

		ArrayList<Group> group = new ArrayList<Group>();
		ArrayList<Place> all_place = new ArrayList<Place>();
		HashMap<Integer,Place> candidate_place = new HashMap<Integer,Place>();
		
		all_place.addAll(Build_Polygons_using_IBGE.loadNationalParks());
		all_place.addAll(Build_Polygons_using_IBGE.loadReservas());
		
		File clustered = new File("cluster"+similarity+".ser");
		if(!clustered.exists()){
			try {
				rb.read_Expression();
				rb.start_read();
				tsf.transform_Repository_to_Place(rb.getRepository());

				for (int i = 0; i < rb.getRepository().size(); i++) {

					// sample.corrected_link_before_swi(rb.getRepository().get(i).getPlaces(),"corrected_link_before_swi.txt");

					System.out.println("Statistics: <<<<<<<");
					System.out.println("All records: "
							+ rb.getRepository().get(i).getNumbers()
							.getAllrecords());
					System.out.println("No record:  "
							+ rb.getRepository().get(i).getNumbers().getNoRecord());
					System.out.println("County: "
							+ rb.getRepository().get(i).getNumbers()
							.getOnlyCounty());
					System.out.println("Locality and County "
							+ rb.getRepository().get(i).getNumbers()
							.getOnlyLocalityAndCounty());
					System.out
					.println("Place "
							+ rb.getRepository().get(i).getNumbers()
							.getOnlyPlace());
					System.out.println("=========================");
					System.out.println("Repositorio "
							+ rb.getRepository().get(i).getName()
							+ " Fora do poligono "
							+ out.count_out_Polygon(rb.getRepository().get(i)
									.getPolygon(), rb.getRepository().get(i)
									.getPlaces()));
					out.clean_noise_coordinates(rb.getRepository().get(i)
							.getPolygon(), rb.getRepository().get(i).getPlaces());
					int lugar = 0;
					for (Place pl : rb.getRepository().get(i).getPlaces()) {
						if (pl.getGeometry() != null)
							lugar++;
					}
					System.out.println("Quantidade de coordenadas: " + lugar);
					int[][] years = Count_Coordinates.countDate(rb.getRepository()
							.get(i).getPlaces(), rb.getRepository().get(i)
							.getPolygon());
					Count_Coordinates.build_csv(years, rb.getRepository().get(i)
							.getName());

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Read all files DONE!!");

			for (int i = 0; i < rb.getRepository().size(); i++) {
				all_place.addAll(rb.getRepository().get(i).getPlaces());
			}
			for (int i = 0; i < rb.getRepository().size(); i++)
				rb.getRepository().get(i).getPlaces().clear();
			System.out.println("Amount places: " + all_place.size());
			all_place.addAll(geonames.getGeonamesPlaces());
			
			if (dbpedia.DBpediaWorks()) {
				all_place.addAll(dbpedia.pull_query());
				dbpedia.getMunicipalityFromAmazonas(all_place);
			}
			
			for(int i=0;i<all_place.size();i++){
				candidate_place.put(i, all_place.get(i));
			}
						
			
			System.out.println("Starting clustering ...");
			group.addAll(start.start_clustering(candidate_place));
			System.out.println("clustering done!!");

			System.out.println("Improve Coordinates....");
			sumy.referenciaGeo(group);
			System.out.println("Improve Coordinates DONE!!");

			System.out.println("Writing file in disk");
			writeCluster(group,"cluster"+similarity+".ser");
			System.out.println("File wrote");

		}else{
			System.out.println("Reading file in disk");
			group.addAll(extracted("cluster"+similarity+".ser"));
			System.out.println("File read");
			System.out.println(group.size());
			System.out.println(group.get(0).getPlaces().size());
			for(int i=0;i<group.get(0).getPlaces().size();i++){
				System.out.println(group.get(0).getPlaces().get(i).getLocation());
			}
		}

		System.out.println("Mapping data ....");
		map.build_RDF(group);
		System.out.println("Mapping data DONE!!!");

		int tmp = 0;
		for (int i = 0; i < rb.getRepository().size(); i++) {
			String name = rb.getRepository().get(i).getName();
			for (int k = 0; k < group.size(); k++) {
				if (group.get(k).getRepository().equals(name)) {
					rb.getRepository().get(i).getPlaces()
					.addAll(group.get(k).getPlaces());
					rb.getRepository().get(i).getPlaces()
					.add(group.get(k).getCentroid());
				}
			}
		}

		for (int i = 0; i < rb.getRepository().size(); i++) {
			int relative_date[][] = Count_Coordinates.countDate(rb
					.getRepository().get(i).getPlaces(), rb.getRepository()
					.get(i).getPolygon());
			Count_Coordinates.build_csv(relative_date, rb.getRepository()
					.get(i).getName()
					+ "New"+similarity);
		}

		System.out.println("Preparing sample...");
		sample.random_Centroid(group, 50,""+similarity);
		sample.random_inner_Group(group, 30,""+similarity);
		System.out.println("Data sample DONE!!!");

		System.out
		.println("Preparing the sample to check coordinates after SWI");
		int lugar = 0;
		for (int i = 0; i < rb.getRepository().size(); i++) {
			for (Place pl : rb.getRepository().get(i).getPlaces()) {
				if (pl.getGeometry() != null)
					lugar++;
			}
			sample.corrected_link_before_swi(rb.getRepository().get(i)
					.getPlaces(), "corrected_link_after_swi"+similarity+".txt");
		}
		System.out.println("Sample after SWI prepared");
		System.out.println("Amouth of coordinates after processing: " + lugar);
		System.out.println("Coordinates improved" + Summarize.improved);

		long fim = System.currentTimeMillis() / 1000;
		System.out.println("Inserting data in triple store...");
		try {
			tripleStore.insertDataLocalhost();
			// String queryString =
			// "DELETE WHERE { <http://www.semanticweb.org/ontologies/Gazetter#5032> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \"13\"^^<http://www.w3.org/2001/XMLSchema#long> . } ";

			// tripleStore.updateDataRemote(queryString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Data inserted!!!");
		//		System.out.println("Building sample semantic query");
		// q.testQuery();
		// q.prepareSample("semanticquery.txt");
		// q.verifyPlacesInsideJauPark("MERDA.txt");
		// System.out.println(fim-inicio);
	}

	private static void writeCluster(ArrayList<Group> group,String path){
		try{ 
			OutputStream file = new FileOutputStream(path);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);

			output.writeObject(group);
			output.close();
		}  
		catch(IOException ex){
			fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
		}
	}



	private static ArrayList<Group> extracted(String path) {


		try{
			//use buffering
			InputStream file = new FileInputStream(path);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream (buffer);
			try{
				//deserialize the List
				ArrayList <Group> group = (ArrayList<Group>)input.readObject();
				//display its data
				return group;
			}
			finally{
				input.close();
			}
		}
		catch(ClassNotFoundException ex){
			fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
		}
		catch(IOException ex){
			fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
		}
		return null;
	}

}
