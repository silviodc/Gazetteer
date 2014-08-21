package read_files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Read_Biodiversity_files {

	private ArrayList<Repository> repository = new ArrayList<Repository>();
	
	public ArrayList<Repository> getRepository() {
		return repository;
	}

	public void setRepository(ArrayList<Repository> repository) {
		this.repository = repository;
	}

	public void start_read() throws InterruptedException, IOException, ParserConfigurationException, SAXException{
	
	    String path = new File("files"+File.separator+"Archives_location.xml").getAbsolutePath();
	 	read_repository(path);
		for(Repository r:repository){
			read_SpeciesLink_biodiversity_files(r);
		}
	}
	
	public void read_SpeciesLink_biodiversity_files(Repository repo) throws InterruptedException, UnsupportedEncodingException, FileNotFoundException{
		System.out.println("lendo: "+repo.getName()+" ...");
			
		 try{     
			 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(repo.getPath()), "UTF-8"));
  
	         while(br.ready()){  
	            String linha = br.readLine();  
	            String values [] = linha.split("\t");
	            String record []= dataTreated(values,repo.getColumns());
	           
	            if((!record[1].equals(" ") && !record[1].equals("")) && (!record[2].equals(" ") && !record[2].equals("")) && (!record[3].equals("") && !record[3].equals("0") && !record[3].equals("0.0")) && (!record[4].equals("") && !record[4].equals("0")&& !record[4].equals("0.0"))){
	            	repo.setAllrecords(repo.getAllrecords()+1);	  
	            	repo.getData().add(record);
	            }else if(!record[1].equals("") && !record[2].equals("")){
	            	repo.setOnlyLocalityAndCounty(repo.getOnlyLocalityAndCounty()+1);
	            	repo.getData().add(record); 
	            	  
	            }else if(!record[1].equals("") && record[2].equals("") ){
	            	repo.setOnlyPlace(repo.getOnlyPlace()+1);
	            	repo.getData().add(record);	            	
	            }else if(record[1].equals("") && !record[2].equals(""))
	            	repo.setOnlyCounty(repo.getOnlyCounty()+1);
	            else 
	            	repo.setNoRecord(repo.getNoRecord()+1);
	         }
	         br.close();  
	      }catch(IOException ioe){  
	         ioe.printStackTrace();  
	      } 
	}
	
	public String [] dataTreated(String values[],int columns[]){
		String temp,temp1;
		String query [] = new String[5];
             temp = values[columns[1]].replaceAll("\\p{Punct}|\\d","");  //PLACE
             temp = temp.replaceAll("NÃO INFORMADO", "");
             if(temp.contains("não determinado"))
            		 temp="";
             temp1 = values[columns[2]].replaceAll("\\p{Punct}|\\d","");  
             temp1= temp1.replaceAll("NÃO INFORMADO", "");
             query[0]=values[columns[0]];//DATE
             query[1]=temp;//PLACE
             query[2]=temp1;//COUNTY
             query[3]=values[columns[3]];//LATI
             query[4]=values[columns[4]];//LONG
             
  // Return in type: [DATE] [PLACE] [COUNTY] [LAT] [LONG]
		return query;
	}
	
	public void read_repository(String caminho) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		System.out.println(caminho);
        File fXmlFile = new File(caminho);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
       
        //optional, but recommended
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("repository");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            	int columns [] = new int [5];
                Element eElement = (Element) nNode;
                
                String filepath =eElement.getElementsByTagName("filepath").item(0).getTextContent();
                String name =eElement.getElementsByTagName("name").item(0).getTextContent();
                
                columns[0] = Integer.parseInt(eElement.getElementsByTagName("ColumDate").item(0).getTextContent());
                columns[1] = Integer.parseInt(eElement.getElementsByTagName("ColumPlace").item(0).getTextContent());
                columns[2] = Integer.parseInt(eElement.getElementsByTagName("ColumCounty").item(0).getTextContent());
                columns[3] = Integer.parseInt(eElement.getElementsByTagName("ColumLong").item(0).getTextContent());
                columns[4] = Integer.parseInt(eElement.getElementsByTagName("ColumLati").item(0).getTextContent());
                
                this.repository.add(new Repository(filepath,name,columns));               
            }
        }
    }
}
