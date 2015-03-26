package test;





import improve_coordinates.Summarize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
















import mapping_to_ontology.Mapping;

import org.xml.sax.SAXException;

import prepare_sample_to_check.Random_Sample;
import put_data_in_Triple_Store.Insert_Triple_Store;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.tools.icon.OpenMapAppPartCollection.OpenMapAppPart.Poly;

import communicate_with_other_data_source.DBpedia;
import communicate_with_other_data_source.Geonames;
import cluster.Desambiguation;
import cluster.Star_algorithm;
import analyze_geographical_coordinates.Out_Polygon;
import TAD.Group;
import TAD.Place;
import TAD.Repository;
import count_and_statistic_analyze.Count_Coordinates;
import read_files.Read_Biodiversity_files;
import read_files.Transform_and_Filter;

public class Test {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, CloneNotSupportedException, IOException {
		
		long inicio = System.currentTimeMillis()/1000;
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
	
		sample.checkAmoutCorrectBeforeSWI("corrected_link_before_swi.txt");
		
		ArrayList<Group> group= new ArrayList<Group>();
		ArrayList<Place> all_place = new ArrayList<Place>();
		try {
			rb.read_Expression();
			rb.start_read();			
			tsf.transform_Repository_to_Place(rb.getRepository());	
			
			
			
			for(int i=0;i<rb.getRepository().size();i++){
							
				//sample.corrected_link_before_swi(rb.getRepository().get(i).getPlaces(),"corrected_link_before_swi.txt");
				
				System.out.println("Statistics: <<<<<<<");
				System.out.println("All records: "+rb.getRepository().get(i).getNumbers().getAllrecords());
				System.out.println("No record:  "+rb.getRepository().get(i).getNumbers().getNoRecord());
				System.out.println("County: "+rb.getRepository().get(i).getNumbers().getOnlyCounty());
				System.out.println("Locality and County "+rb.getRepository().get(i).getNumbers().getOnlyLocalityAndCounty());
				System.out.println("Place "+rb.getRepository().get(i).getNumbers().getOnlyPlace());
				System.out.println("=========================");
				System.out.println("Repositorio "+rb.getRepository().get(i).getName()+" Fora do poligono "+out.count_out_Polygon(rb.getRepository().get(i).getPolygon(),rb.getRepository().get(i).getPlaces()));
				out.clean_noise_coordinates(rb.getRepository().get(i).getPolygon(),rb.getRepository().get(i).getPlaces());
				int lugar=0;
				for(Place pl: rb.getRepository().get(i).getPlaces()){
					if(pl.getGeometry()!=null)
						lugar++;
				}
				System.out.println("Quantidade de coordenadas: "+lugar);
				int [][] years = Count_Coordinates.countDate(rb.getRepository().get(i).getPlaces(),rb.getRepository().get(i).getPolygon());
				Count_Coordinates.build_csv(years,rb.getRepository().get(i).getName());		
				
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

		for(int i=0;i<rb.getRepository().size();i++){
			all_place.addAll((Collection<? extends Place>) rb.getRepository().get(i).getPlaces().clone());
		}
		all_place.addAll(geonames.getGeonamesPlaces());
		if(dbpedia.DBpediaWorks())
			all_place.addAll(dbpedia.pull_query());
		
		System.out.println("Starting clustering ...");
		group.addAll(start.start_clustering(all_place,rb.getExp()));
		System.out.println("clustering done!!");
				
		System.out.println("Improve Coordinates....");
		sumy.referenciaGeo(group,2014);	
		System.out.println("Improve Coordinates DONE!!");
		
		
		System.out.println("Mapping data ....");
		map.build_RDF(group);
		System.out.println("Mapping data DONE!!!");
		
		
		System.out.println("Preparing sample...");
		sample.random_Centroid(group, 50);
		sample.random_inner_Group(group, 30);
		System.out.println("Data sample DONE!!!");
		
		
		for(int i=0;i<rb.getRepository().size();i++)
			rb.getRepository().get(i).getPlaces().clear();
		
		int tmp =0;
		for(int i=0;i<rb.getRepository().size();i++){
			String name = rb.getRepository().get(i).getName();
			for(int k=0;k<group.size();k++){
				if(group.get(k).getRepository().equals(name)){
					rb.getRepository().get(i).getPlaces().addAll(group.get(k).getPlaces());
					rb.getRepository().get(i).getPlaces().add(group.get(k).getCentroid());
				}
			}
		}
		
		for(int i=0;i<rb.getRepository().size();i++){
			int relative_date [][]= Count_Coordinates.countDate(rb.getRepository().get(i).getPlaces(),rb.getRepository().get(i).getPolygon());
			Count_Coordinates.build_csv(relative_date,rb.getRepository().get(i).getName()+"New");
		}
			
		System.out.println("Preparing the sample to check coordinates after SWI");
		int lugar=0;
		for(int i=0;i<rb.getRepository().size();i++){
			for(Place pl: rb.getRepository().get(i).getPlaces()){
				if(pl.getGeometry()!=null)
					lugar++;
			}
			sample.corrected_link_before_swi(rb.getRepository().get(i).getPlaces(),"corrected_link_after_swi.txt");
		}
		System.out.println("Sample after SWI prepared");
		System.out.println("Amouth of coordinates after processing: "+lugar);
		System.out.println("Coordinates improved"+Summarize.improved);
		
		long fim = System.currentTimeMillis()/1000;
		/*
		System.out.println("Inserting data in triple store...");
		try {
			tripleStore.insertDataLocalhost();
	//		String queryString = "DELETE WHERE { <http://www.semanticweb.org/ontologies/Gazetter#5032> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \"13\"^^<http://www.w3.org/2001/XMLSchema#long> . } ";
		
		//	tripleStore.updateDataRemote(queryString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("Data inserted!!!");
		
	
	//	System.out.println(fim-inicio);
	}

}
