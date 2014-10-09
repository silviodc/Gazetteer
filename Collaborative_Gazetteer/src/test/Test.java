package test;





import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.*;


import org.xml.sax.SAXException;

import cluster.Star_algorithm;
import analyze_geographical_coordinates.Out_Polygon;
import TAD.Group;
import TAD.Place;
import TAD.Repository;
import communicate_with_other_data_source.Geonames;
import count_and_statistic_analyze.Count_Coordinates;
import read_files.Read_Biodiversity_files;
import read_files.Transform_and_Filter;

public class Test {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Read_Biodiversity_files rb = new Read_Biodiversity_files();
		Transform_and_Filter tsf = new Transform_and_Filter();
		Out_Polygon out = new Out_Polygon();
		Star_algorithm start = new Star_algorithm();
		
		
		try {
			rb.read_Expression();
			rb.start_read();			
			tsf.transform_Repository_to_Place(rb.getRepository());	
			for(int i=0;i<rb.getRepository().size();i++){
				ArrayList<Place> cloned_places = (ArrayList) rb.getRepository().get(i).getPlaces().clone();
				int [][] years = Count_Coordinates.countDate(cloned_places,rb.getRepository().get(i).getPolygon());
				Count_Coordinates.build_csv(years,rb.getRepository().get(i).getName());
				for(Repository r: rb.getRepository()){
					System.out.println("Repositorio "+r.getName()+" Fora do poligono "+out.count_out_Polygon(r.getPolygon(),r.getPlaces()));
				}
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
		System.out.println("Leu todos os arquivos");
	/*	Geonames g = new Geonames();
		try {
			System.out.println("Vai ler geonames");
			g.acessGeonames();
			for(Repository r : rb.getRepository()){
			
				g.compareGeonames_and_Places(r.getPlaces());
			}
			System.out.println("Terminou de ler o geonames");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		ArrayList<Group> g= new ArrayList<Group>();
		System.out.println("Numeros de lugares do repositorio: "+rb.getRepository().get(0).getPlaces().size());
		System.out.println("NUmero de expressoes: "+rb.getExp().size());
		try {
			g.addAll(start.start_clustering(rb.getRepository().get(0),rb.getExp()));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("passou");
		System.out.println(g.size());
		for(int i=0;i<g.size();i++){
			for(int j=0;j<g.get(i).getPlaces().size();j++){
		//		System.out.println(g.get(i).getPlaces().get(j).getLocation());
			}
		//	System.out.println("======================================================");
		}
	}

}
