package analyze_geographical_coordinates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import com.bbn.openmap.geo.OMGeo.Polygon;

public class Out_Polygon {
	
	public boolean insidePolygon(OMGeo.Polygon poly, float x, float y){       
	       return poly.isPointInside(new Geo(x,y));
	}
	
	 public OMGeo.Polygon buildPolygon(String path) throws FileNotFoundException, IOException{
	       
	        String line;
	        ArrayList<Float> p1 =new ArrayList<Float>();
	        ArrayList<Float> p2 =new ArrayList<Float>();
	        File arq = new File(path).getAbsoluteFile();
	        BufferedReader read =  new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
	           
	        while((line = read.readLine())!=null){ 
	                
	             String vetor [] = line.split(" ");
	             for(int i=0;i<vetor.length;i++){
	                    String [] coordinate = vetor[i].split(",");
	                    float temp =transformFloat(coordinate[0]);
	                    float temp1 = transformFloat(coordinate[1]);
	                    p1.add(temp);
	                    p2.add(temp1);
	                }
	        }
	        Geo listGeo[] = new Geo [p1.size()];
	        for(int i=0;i<p1.size();i++){
	                listGeo[i]= new Geo(p2.get(i),p1.get(i));
	        }
	        OMGeo.Polygon poly = new Polygon(listGeo);    
	        return poly;
	    }
	    
	 private float transformFloat(String numero) {
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
