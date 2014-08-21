package Old_source;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class InsetData {
    
    public static void main(String args[]) throws FileNotFoundException, IOException{
        int cont=0;
        int not=0;
        BancoDeDados bd = new BancoDeDados("root","123456","jdbc:mysql://127.0.0.1:3306/gbif");
        bd.Conectar();
        String arquivo = "C:\\Users\\Silvio\\Documents\\Gazetteer\\INPA.txt";
        FileReader arq = new FileReader(arquivo);
        BufferedReader lerArq = new BufferedReader(arq);
        String linha="";
        String query="",temp,temp1;
           while((linha = lerArq.readLine())!=null){ 
               String values [] = linha.split("\t");
               switch(values.length){
                   case (50):
                        temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into inpa (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   //System.out.println(query);
                 // bd.insercao(query);
                 
                   break;
                   case (49):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (48):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (47):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (46):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (45):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (44):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (41):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (38):
                        temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                        temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   case (35):
                         temp = values[32].replaceAll("\"", "");
                        temp = temp.replaceAll("\'", "");
                        temp = temp.replace('\\', ' ');
                        temp = temp.replaceAll("//", "");
                         temp1 = values[31].replaceAll("\"", "");
                        query="insert into specieslink (county,locality,longitude,latitude) values('"+temp1+"','"+temp+"','"+values[33]+"','"+values[34]+"');";
                   break;
                   default:
                       System.out.println(values.length);
                   break;
               }
               try{
                    bd.insercao(query);
                    cont++;
                   }catch(Exception ex){
                   System.out.println(query);
                   }
           }
           System.out.println(cont);
          
    }
}
