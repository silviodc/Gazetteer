package put_data_in_Triple_Store;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

import TAD.Expression;
import TAD.Place;
import TAD.Repository;

public class Insert_Triple_Store {
	
	
	public void insertData() throws IOException{
		
		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("triples.nt"), "UTF-8"));
		 while(br.ready()){  
	        String query = "INSERT DATA {"+ br.readLine() +" }";
	        System.out.println(query);
			try {
				UpdateRequest queryObj=UpdateFactory.create(query);
				UpdateProcessor qexec=UpdateExecutionFactory.createRemoteForm(queryObj,"http://biomac.icmc.usp.br:8080/swiendpoint/query.jsp#2020554288");
				qexec.execute();
				}catch (Exception e) {
					 e.printStackTrace();
				 }
		 }
	}
}
