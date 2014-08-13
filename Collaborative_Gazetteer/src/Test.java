import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import read_files.Read_Biodiversity_files;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       Read_Biodiversity_files rb = new Read_Biodiversity_files();
       try {
		rb.read_SpeciesLink_biodiversity_files();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
