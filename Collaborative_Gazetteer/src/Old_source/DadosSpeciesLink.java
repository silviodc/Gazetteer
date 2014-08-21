package Old_source;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class DadosSpeciesLink {
    
    
    private File arquivo; // arquivo .xls que será lido
    
    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    
    
    public DadosSpeciesLink() {
 
                
         BancoDeDados bd = new BancoDeDados("root","123456","jdbc:mysql://127.0.0.1:3306/especies");
         bd.Conectar();
         String [][] res = null;
    //   String query = "select locality,id,latitude,longitude,county from occurrence where locality<>\"\" or (latitude<>\"\" and longitude<>\"\") and locality<>'.'";
      //  System.out.println(query);
            String query = "select locality,RM_INDEX,latitude,longitude,county from sheet1 where locality<>\"\" or (latitude<>\"\" and longitude<>\"\") and locality<>''";
        try {
            System.out.println(query);
             res = bd.selecao(query);
            
        } catch (SQLException ex) {
           System.err.println("Erro ao buscar dados processados");
        }
            System.out.println(res.length);
           for(int i=0;i<res.length;i++){                           
                    //(String nome, String id_especies, String latitude, String longitude, String municipio)
               if(res[i][4]==null){
                   res[i][4] ="";
               }
                   Dicionario.nome.add( new Semelhantes (res[i][0],res[i][1],res[i][2],res[i][3],res[i][4]));
                    
                }
          
    }
}
