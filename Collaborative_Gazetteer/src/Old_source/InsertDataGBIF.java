package Old_source;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class InsertDataGBIF {
     public static void main(String args[]) throws FileNotFoundException, IOException{
        int cont=0;
        int not=0;
        BancoDeDados bd = new BancoDeDados("root","123456","jdbc:mysql://127.0.0.1:3306/gbif");
        bd.Conectar();
        String arquivo = "C:\\Users\\Silvio\\Documents\\Gazetteer\\occurrence.txt";
        FileReader arq = new FileReader(arquivo);
        BufferedReader lerArq = new BufferedReader(arq);
        String linha="";
        String query="",temp,temp1;
           while((linha = lerArq.readLine())!=null){ 
               String values [] = linha.split("\t");
               switch(values.length ){
                   case (65):
                       if(values[52].equals("Amazonas")){
                        temp = values[56].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[53].replaceAll("\"", "");
                    query="insert into gbif (county,locality,longitude,latitude,state) values('"+temp1+"','"+temp+"','"+values[26]+"','"+values[25]+"','"+values[52]+"');";
                          try{
                   if(cont%10000==0)
                       bd.Desconetar();
                   if(cont%10000==0)
                        bd.Conectar();
                   
                   bd.insercao(query);
                    
                    cont++;
                     System.out.println(cont);
                   }catch(Exception ex){
                   System.out.println(query);
                   }
                       }
                        //System.out.println(query);
                 // bd.insercao(query);
                 
                   break;
                   
                   default:
                       System.out.println(values.length);
                   break;
               }
           
           }
           System.out.println(cont);
          
    }
}
