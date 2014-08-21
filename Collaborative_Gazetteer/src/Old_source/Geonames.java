package Old_source;

import java.sql.SQLException;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class Geonames {
    private String name;
    private float latitude;
    private float longitude;

    public Geonames(){}
    
    public Geonames(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    public ArrayList<Geonames> nomes(){
         BancoDeDados bd = new BancoDeDados("root","123456","jdbc:mysql://127.0.0.1:3306/geonames");
         ArrayList<Geonames> geo = new ArrayList<Geonames> ();
         bd.Conectar();
         String [][] res = null;
         String query = "SELECT geonames.`name`,geonames.latitude, geonames.longitude from geonames WHERE state LIKE \"%America/Manaus%\";";
       //  System.out.println(query);
        try {
             res = bd.selecao(query);
            
        } catch (SQLException ex) {
           System.err.println("Erro ao buscar dados processados");
        }
        bd.Desconetar();
            System.out.println("Numero Nomes Geonames"+res.length);
           for(int i=0;i<res.length;i++){                                              
                 geo.add( new Geonames(res[i][0],transformaFloat(res[i][1]),transformaFloat(res[i][2])));
                }
           return geo;
    }
      public static float transformaFloat(String numero) {
        float valor = 0;
        char n[] = numero.toCharArray();
        numero = "";
        for (int i = 0; i < n.length; i++) {
            if (n[i] == ',') {
                numero += ".";
            }
            numero += n[i];
        }
        try {
            valor = Float.parseFloat(numero);
        } catch (Exception e) {
            return 0;
        }
        return valor;
    }
}
