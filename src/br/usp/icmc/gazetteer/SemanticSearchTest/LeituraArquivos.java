/**
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.icmc.gazetteer.SemanticSearchTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Silvio
 */
public class LeituraArquivos {
// "C:\\Users\\Silvio\\ICMC USP\\en.qrels.126-175.2011.txt"
// "C:\\Users\\Silvio\\ICMC USP\\en.topics.126-175.2011.txt"
    private String caminho1;
    private String caminho2;
    private static ArrayList<Documento> documentos = new ArrayList<Documento>();
    private static ArrayList<String> querys = new ArrayList<String>();

    
    public LeituraArquivos(String caminho1,String caminho2){
        this.setCaminho1(caminho1);
        this.setCaminho2(caminho2);
    }
    public LeituraArquivos(){}
    
    public static ArrayList<Documento> getDocumentos() {
        return documentos;
    }

    public static void setDocumentos(ArrayList<Documento> documentos) {
        LeituraArquivos.documentos = documentos;
    }

    public static ArrayList<String> getQuerys() {
        return querys;
    }

    public static void setQuerys(ArrayList<String> querys) {
        LeituraArquivos.querys = querys;
    }
    
    public  void leitura(String caminho1,String  caminho2) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException{
        LeituraArquivos la = new LeituraArquivos();
        la.lerArq(caminho1);
            la.lerQuery(caminho2);
   
    }
    
         //metodo para transformar um int em uma string
    public int transformaInt(String numero){
        int valor = 0;
        char n[] = numero.toCharArray();
        try{
            valor = Integer.parseInt(numero);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"ERRO AO CONVERTER O INTEIRO");
            return 0;
        }
        return valor;
    }
    
     public  void lerArq(String nome) {
         
        try { 
            FileReader arq = new FileReader(nome);
            BufferedReader lerArq = new BufferedReader(arq);
            String linha=""; 
            while((linha = lerArq.readLine())!=null){ 
                  String [] values = linha.split(" ");
                  documentos.add(new Documento(transformaInt(values[0]),values[2],transformaInt(values[3])));
            }
            arq.close(); 
        } catch (IOException e) { 
           System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        } 

   }
     
     public void lerQuery(String caminho) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException{
          
              File fXmlFile = new File(caminho);
              DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
              DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
              Document doc = dBuilder.parse(fXmlFile);

              //optional, but recommended
              doc.getDocumentElement().normalize();

              NodeList nList = doc.getElementsByTagName("top");

              for (int temp = 0; temp < nList.getLength(); temp++) {

                      Node nNode = nList.item(temp);

                      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                             Element eElement = (Element) nNode;
                             querys.add(eElement.getElementsByTagName("desc").item(0).getTextContent());

                      }
              }
         
  }
	public String getCaminho2() {
		return caminho2;
	}
	public void setCaminho2(String caminho2) {
		this.caminho2 = caminho2;
	}
	public String getCaminho1() {
		return caminho1;
	}
	public void setCaminho1(String caminho1) {
		this.caminho1 = caminho1;
	}
  
}
