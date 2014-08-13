package read_files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Read_Biodiversity_files {

	private String path;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	private int allrecords;
	private int onlyLocalityAndCounty;
	private int onlyPlace;
	private int onlyCounty;
	private int noRecord;
	
	
	public void read_SpeciesLink_biodiversity_files() throws InterruptedException, UnsupportedEncodingException, FileNotFoundException{
		
		File arq = new File("C:\\Users\\Silvio\\Desktop\\INPA.txt"); //se já existir, será sobreescrito  
	    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arq),"UTF-8")); 
		
		 String arquivo = "C:\\Users\\Silvio\\Desktop\\speciesLink_all_30928.txt";
		 try{     
			 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
  
	         while(br.ready()){  
	            String linha = br.readLine();  
	            String values [] = linha.split("\t");
	            String record []= dataTreated(values);
	           
	            if(!record[1].equals("") && !record[2].equals("") && (!record[3].equals("") && !record[3].equals("0")) && (!record[4].equals("") && !record[4].equals("0"))){
	            	allrecords++;	           
	            }else if(!record[1].equals("") && !record[2].equals("")){
	            	onlyLocalityAndCounty++;
	            	 String	escrever = record[0]+", "+record[1]+", "+record[2]+", "+record[3]+", "+record[4]+"\n";
	 	            bw.write(escrever);
	 	            bw.flush();
	            }else if(!record[1].equals("") && record[2].equals("") )
	            	onlyPlace++;
	            else if(record[1].equals("") && !record[2].equals(""))
	            	onlyCounty++;
	            else 
	            	noRecord++;
	         }
	         br.close();  
	         bw.close();
	         System.out.println("Leitura Finalizada");
	         System.out.println("Todos registros: "+allrecords);
	         System.out.println("Somente local e municipio: "+onlyLocalityAndCounty);
	         System.out.println("Somente local: "+onlyPlace);
	         System.out.println("Somente municipio: "+onlyCounty);
	         System.out.println("Nenhum registro: "+noRecord);
	      }catch(IOException ioe){  
	         ioe.printStackTrace();  
	      }  //108357
	}
	
	public String [] dataTreated(String values[]){
		String temp,temp1;
		String query [] = new String[5];
             temp = values[32].replaceAll("\"", "");
             temp = temp.replaceAll("\'", "");
             temp = temp.replace('\\', ' ');
             temp = temp.replaceAll("//", "");
             temp = temp.replaceAll("NÃO INFORMADO", "");
             if(temp.contains("não determinado"))
            		 temp="";
             temp1 = values[31].replaceAll("\"", "");
             temp1= temp1.replaceAll("NÃO INFORMADO", "");
             query[0]=values[23];query[1]=temp;query[2]=temp1; query[3]=values[33];query[4]=values[34];
  
		return query;
	}
}
