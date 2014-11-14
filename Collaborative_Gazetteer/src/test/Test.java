package test;





import improve_coordinates.Summarize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;







import org.xml.sax.SAXException;

import com.bbn.openmap.tools.icon.OpenMapAppPartCollection.OpenMapAppPart.Poly;

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
		
		// TODO Auto-generated method stub
		Read_Biodiversity_files rb = new Read_Biodiversity_files();
		Transform_and_Filter tsf = new Transform_and_Filter();
		Out_Polygon out = new Out_Polygon();
		Star_algorithm start = new Star_algorithm();
		Summarize sumy = new Summarize();
		ArrayList<Group> group= new ArrayList<Group>();
		ArrayList<Place> all_place = new ArrayList<Place>();
		Poly poly;
		try {
			rb.read_Expression();
			rb.start_read();			
			tsf.transform_Repository_to_Place(rb.getRepository());	
			for(int i=0;i<rb.getRepository().size();i++){
				
				System.out.println("Statistics: <<<<<<<");
				System.out.println("All records: "+rb.getRepository().get(i).getNumbers().getAllrecords());
				System.out.println("No record:  "+rb.getRepository().get(i).getNumbers().getNoRecord());
				System.out.println("County: "+rb.getRepository().get(i).getNumbers().getOnlyCounty());
				System.out.println("Locality and County "+rb.getRepository().get(i).getNumbers().getOnlyLocalityAndCounty());
				System.out.println("Place "+rb.getRepository().get(i).getNumbers().getOnlyPlace());
				System.out.println("=========================");
				System.out.println("Repositorio "+rb.getRepository().get(i).getName()+" Fora do poligono "+out.count_out_Polygon(rb.getRepository().get(i).getPolygon(),rb.getRepository().get(i).getPlaces()));
				out.clean_noise_coordinates(rb.getRepository().get(i).getPolygon(),rb.getRepository().get(i).getPlaces());
				ArrayList<Place> cloned_places = (ArrayList) rb.getRepository().get(i).getPlaces().clone();
				int [][] years = Count_Coordinates.countDate(cloned_places,rb.getRepository().get(i).getPolygon());
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
			all_place.addAll(rb.getRepository().get(i).getPlaces());
		}

		System.out.println("Starting clustering ...");
	
		group.addAll(start.start_clustering(all_place,rb.getExp()));
		System.out.println("clustering done!!");
		
		System.out.println("Improve Coordinates....");
		sumy.referenciaGeo(group);	
		System.out.println("Improve Coordinates DONE!!");
		
		
		Desambiguation ds = new Desambiguation();
		ds.desambig(Star_algorithm.getAmbiguoPlace(),group);
		
		for(int i=0;i<rb.getRepository().size();i++)
			rb.getRepository().get(i).getPlaces().clear();
		
		int tmp =0;
		for(int i=0;i<rb.getRepository().size();i++){
			String name = rb.getRepository().get(i).getName();
			for(int k=0;k<group.size();k++){
				if(group.get(k).getRepository().equals(name)){
					rb.getRepository().get(i).getPlaces().addAll(group.get(k).getPlaces());
				}
			}
		}
		for(int i=0;i<rb.getRepository().size();i++){
			int relative_date [][]= Count_Coordinates.countDate(rb.getRepository().get(i).getPlaces(),rb.getRepository().get(i).getPolygon());
			Count_Coordinates.build_csv(relative_date,rb.getRepository().get(i).getName()+"New");
		}
		
	}

}
