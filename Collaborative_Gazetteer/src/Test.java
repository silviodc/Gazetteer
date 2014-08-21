import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import count_and_statistic_analyze.Count_Coordinates;
import TAD.Place;
import read_files.Read_Biodiversity_files;
import read_files.Repository;
import read_files.Transform_and_Filter;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       Read_Biodiversity_files rb = new Read_Biodiversity_files();
       Place p=null;
       try {
		rb.start_read();
		Transform_and_Filter tsf = new Transform_and_Filter();
		ArrayList<Repository> newRepo = tsf.transform_Repository_to_Place(rb.getRepository());		
		int [][] years = Count_Coordinates.countDate(newRepo.get(0).getPlaces());
		for(int i=0;i<years.length;i++){
			System.out.println(years[i][0]+","+years[i][1]);
		}
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
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
	}

}
