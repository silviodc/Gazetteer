package Old_source;


import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import com.bbn.openmap.geo.OMGeo.Polygon;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */




public class Poligono {
   OMGeo.Polygon poly;
   ArrayList<Polygon> polys = new ArrayList<Polygon>();
   ArrayList<String> poligonos = new ArrayList<String>();

    public Polygon getPoly() {
        return poly;
    }

    public void setPoly(Polygon poly) {
        this.poly = poly;
    }

    public ArrayList<Polygon> getPolys() {
        return polys;
    }

    public void setPolys(ArrayList<Polygon> polys) {
        this.polys = polys;
    }

    public ArrayList<String> getPoligonos() {
        return poligonos;
    }

    public void setPoligonos(ArrayList<String> poligonos) {
        this.poligonos = poligonos;
    }
   
   public boolean dentro (OMGeo.Polygon g , Geo  p){
       return g.isPointInside(p);
   }
    
   public boolean insidePolygon(float x, float y){       
       return poly.isPointInside(new Geo(x,y));
   }
   
   public void lerPoligonos() throws FileNotFoundException, IOException{
        String arquivo = "C:\\Users\\Silvio\\Documents\\Gazetteer\\poligonos.wtk";
        ArrayList<Float> p1 =new ArrayList<Float>();
        ArrayList<Float> p2 =new ArrayList<Float>();
        FileReader arq = new FileReader(arquivo);
        BufferedReader lerArq = new BufferedReader(arq);
        String linha="";
           while((linha = lerArq.readLine())!=null){ 
               if(!linha.equals(""))
               poligonos.add(linha);
           }
           float temp=0,temp1=0;
       for(int k=0; k<poligonos.size();k++){
           String polig = poligonos.get(k).replaceAll("POLYGON", "");
            polig = polig.replaceAll("\\(", "");
            polig = polig.replaceAll("\\)", "");         
            String vetor [] = polig.split(",");
            try{
                for(int i=1;i<vetor.length;i++){
                    String [] coordenada = vetor[i].split(" ");
                     temp =transformaFloat(coordenada[coordenada.length-2]);
                     temp1 = transformaFloat(coordenada[coordenada.length-1]);
                    p1.add(temp);
                    p2.add(temp1);
                }     
                Geo listGeo[] = new Geo [p1.size()];
                for(int i=0;i<p1.size();i++){
                    listGeo[i]= new Geo(p2.get(i),p1.get(i));
                }
                polys.add(new Polygon(listGeo));
                p1.clear();
                p2.clear();
            }catch(Exception ex){                       
                poligonos.remove(k);
                k--;
            }
           
       }
        
   }
    public void criaPoligono(String poligo) throws FileNotFoundException, IOException{
       
        String s = poligo;
        String linha;
        ArrayList<Float> p1 =new ArrayList<Float>();
        ArrayList<Float> p2 =new ArrayList<Float>();
            FileReader arq = new FileReader(s);
            BufferedReader lerArq = new BufferedReader(arq);
            int cont=0;
            while((linha = lerArq.readLine())!=null){ 
                
                String vetor [] = linha.split(" ");
                for(int i=0;i<vetor.length;i++){
                    String [] coordenada = vetor[i].split(",");
                    cont++;
                    float temp =transformaFloat(coordenada[0]);
                    float temp1 = transformaFloat(coordenada[1]);
                    p1.add(temp);
                    p2.add(temp1);
                  
                }
                    
           }
            Geo listGeo[] = new Geo [p1.size()];
            for(int i=0;i<p1.size();i++){
                listGeo[i]= new Geo(p2.get(i),p1.get(i));
            }
       poly = new Polygon(listGeo);
       // System.out.println(poly.isPointInside(new Geo(-2,-59)));
       // System.out.println(poly.isPointInside(new Geo(-4,-50)));
         
    }
    
     public float transformaFloat(String numero) {
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
