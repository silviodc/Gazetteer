package put_data_in_Triple_Store;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

import eu.earthobservatory.org.StrabonEndpoint.client.*;

public class Insert_Triple_Store {
	
	
	public void insertData() throws IOException{
		
		SPARQLEndpoint end = new SPARQLEndpoint("127.0.0.1", 5432,"swiendpoint");
		end.setUser("postgres");
		end.setPassword("postgres");
		System.out.println(end.getConnectionURL());
		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("triples.nt"), "UTF-8"));
		 while(br.ready()){  
	        String query = "INSERT DATA {"+ br.readLine() +" }";
	        System.out.println(query);
			try {
				boolean ok = end.update(query);
				if(ok){
					System.out.println("works");
				}
				//UpdateRequest queryObj=UpdateFactory.create(query);
				//UpdateProcessor qexec=UpdateExecutionFactory.createRemoteForm(queryObj,"http://biomac.icmc.usp.br:8080/swiendpoint/query.jsp#2020554288");
				//qexec.execute();
				}catch (Exception e) {
					 e.printStackTrace();
				 }finally{
					 break;
				 }
		 }
	}
}
