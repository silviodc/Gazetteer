package Old_source;


import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat.Encoding;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * 
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
<Document>
  <Placemark>
    <Point>
      <coordinates>-122.0822035425683,37.42228990140251,0</coordinates>
    </Point>
  </Placemark>
  <Placemark>
    <Point>
      <coordinates>-121.0822035425683,33.42228990140251,0</coordinates>
    </Point>
  </Placemark>
</Document>
</kml>
 */

/**
 *
 * @author Silvio
 */


public class Dicionario {


    private static ArrayList<String> inuteis = new ArrayList<String>();
    private static ArrayList<Expressao> exp = new ArrayList<Expressao>();
    public static ArrayList<Semelhantes> nomes = new ArrayList<Semelhantes>();
    public static ArrayList<Semelhantes> nome = new ArrayList<Semelhantes>();
    public static ArrayList<Semelhantes> seme = new ArrayList<Semelhantes>();
    public static ArrayList<String> centroide = new ArrayList<String>();
    public static int total=0;
    public static int referenciado;
    public static double simi=0.4;
    public static int totalD=0;
    public static int amostra  =0;
    public static int grupos=0;
    public static Poligono poligono;   
    private static String caminho="amostra";
    private static int contLatfora;
     
    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, Exception {
       String poligo = "C:\\Users\\Silvio\\Documents\\Gazetteer\\ParqueJau.txt";
       Poligono geo = new Poligono();
       geo.criaPoligono(poligo);
       KMLGoogleEarth(geo);
       geo.lerPoligonos();
       ArrayList<Geonames> geonames = new ArrayList<Geonames>();
       Geonames g = new Geonames();
       geonames.addAll(g.nomes());
       DadosSpeciesLink excel = new DadosSpeciesLink();
       File arquivo = new File("C:\\Users\\Silvio\\Documents\\Gazetteer\\experimento\\IndividuosRDFgbif.txt"); //se já existir, será sobreescrito  
       BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo),"UTF-8")); 
       File aqv = new File("C:\\Users\\Silvio\\Documents\\Gazetteer\\experimento\\"+caminho+"gbif.txt"); //se já existir, será sobreescrito  
       BufferedWriter amost = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aqv),"UTF-8"));  
       File aq1 = new File("C:\\Users\\Silvio\\Documents\\Gazetteer\\experimento\\"+caminho+simi+"gbif.txt"); //se já existir, será sobreescrito   
       BufferedWriter amostg = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aq1),"UTF-8"));  
       lerToponimo("C:\\Users\\Silvio\\Documents\\NetBeansProjects\\Dicionario\\src\\toponimos.xml");
       preencheInuteis("C:\\Users\\Silvio\\Documents\\Gazetteer\\naoRelevante.txt");
       System.out.println("Executando simi"+simi);
     
     
        for (Expressao e : exp) {
            Expressao(e, excel);
            amostra=0;
            juntarSemelhantes(e,geo,geonames,bw,amost,amostg);
            
        }
       bw.close();
       
    }

    public static boolean verifica(String id){
        for(int i=0;i<centroide.size();i++){
            if(centroide.get(i).equals(id))
                return false;
        }
        return true;
    }
    
    public static void juntarSemelhantes(Expressao e,Poligono geo,ArrayList<Geonames>  geonames,BufferedWriter bw ,BufferedWriter amost,BufferedWriter amostg  ) throws IOException {
        ArrayList<Local> locais = new ArrayList<Local>();
      System.out.println("total no inicio: "+seme.size());
        for (int k = 0; k < seme.size(); k++) {
            String local = seme.get(k).getNome().toLowerCase().replaceAll("(?!\")\\p{Punct}", " ");
            local = inuteis(local);
         //   System.out.println(local);
            seme.get(k).setNome(local);
        }
        int k=0;
        while(seme.size()>1 && k<seme.size()) {
            Semelhantes s = seme.get(k);
            String local1 = s.getNome().replaceAll("\\W", "");
            String municipio = s.getMunicipio().toLowerCase().replaceAll("\\W", "");
            
                       
           // if (!municipio.equals("") && !municipio.equals("NÃOINFORMADO") && !municipio.equals("nãoinformado") && municipio!=null && !local1.equals("")) {
               //System.out.println(seme.size());
                String temp = nomes.get(k).getNome();
                // System.out.println(temp);
                locais.add(new Local(temp));   
                locais.get(locais.size() - 1).setMunicipio(s.getMunicipio());
                centroide.add(nomes.get(k).getId_especies());
                seme.remove(k);
                nomes.remove(k);
               
                for (int i =0; i < seme.size(); i++) {
                        String municipio1 = seme.get(i).getMunicipio().toLowerCase().replaceAll("\\W", "");
                        boolean muni = verificaMunicipio(municipio, municipio1);
                        String local = seme.get(i).getNome().replaceAll("\\W", "");
                        double similirity = 0;
                        similirity = jaccardSimilarity(local, local1);
                        if((similirity>simi) && (muni )){
                            locais.get(locais.size() - 1).setSemelhantes(new Semelhantes(nomes.get(i).getNome(), nomes.get(i).getId_especies(), nomes.get(i).getLatitude(), nomes.get(i).getLongitude(), nomes.get(i).getMunicipio(), similirity));
                            centroide.add(nomes.get(i).getId_especies());
                            seme.remove(i);
                            nomes.remove(i);
                            i--;
                        }
                    
                }             
                referenciaGeo(locais,geo,e);
                k=0;
          /*  }else{                    
                k++;
                if(k==seme.size()){
                   seme.remove(k-1);
                   nomes.remove(k-1);
                   k--;
                }
            }*/          
        }
      inuteis2(locais);
      nomesGeonames(geonames,locais);      
      individuos(locais,e,bw);
      amostra(locais,amost);
      amostraGrupos(locais,amostg);
      contagemCoord(locais);    
      System.out.println("Latitude fora do poligono"+contLatfora);
      System.out.println("GRUPOS"+grupos);
    }
    
    public static void referenciaGeo(ArrayList<Local> locais,Poligono geo, Expressao e){
        ArrayList<Geo> points = new ArrayList<Geo>();
        ArrayList<String> Municipio= new ArrayList<String>();
                for (int i = 0; i < locais.get(locais.size() - 1).getSemelhantes().size(); i++) {
                    
                    String lati = locais.get(locais.size()-1).getSemelhantes().get(i).getLatitude();
                    String longi = locais.get(locais.size()-1).getSemelhantes().get(i).getLongitude();
                    String mun =  locais.get(locais.size()-1).getSemelhantes().get(i).getMunicipio();
                    if(!mun.equals("NÃO INFORMADO") && !mun.equals("") && mun!=null){
                        Municipio.add(locais.get(locais.size()-1).getSemelhantes().get(i).getMunicipio());
                    }
                    float l1=0,l2=0 ;
                    if(lati != null && longi!= null){
                        l1 = transformaFloat(lati);                   
                        l2 = transformaFloat(longi);
                    
                    if (!lati.equals("") && !longi.equals("")) {
                        if(geo.insidePolygon(l1, l2)){
                          points.add(new Geo(l1,l2));
                        }else{
                            contLatfora++;
                        }
                   //     System.out.println("add ponto");
                    }
                    }
                }
                if(points.size()>1){
                    double distances[] = new double[points.size()];
                    for(int i=0;i<points.size();i++){
                        distances[i]=points.get(i).length();
                    }

                    Arrays.sort(distances);
                   
                    int indice=0;
                    int maior=0;
                    int index=0;
                    for(int j=0;j<distances.length;j++){
                        double first=distances[j];
                        for(int i=0;i<distances.length;i++){
                            if(distances[i]==first){
                                 indice++;
                            }
                            if(indice>maior){
                                maior = indice;
                                index = i;
                            }
                        }
                    }
                   
                   locais.get(locais.size()-1).setLatitude(points.get(index).getLatitude());
                   locais.get(locais.size()-1).setLongitude(points.get(index).getLongitude());
                   if(Municipio.size()>1 && e.getPoly().equals("1"))
                   locais.get(locais.size()-1).setMunicipio(Municipio.get((int)Municipio.size()/2));
                   for(int n=0;n<geo.getPolys().size();n++){
                       if(geo.dentro(geo.getPolys().get(n), points.get(index))){
                           locais.get(locais.size()-1).setPoly(geo.getPoligonos().get(n));
                           break;
                       }
                   }
                }
                
    }
     public static void nomesGeonames(ArrayList<Geonames> geonames, ArrayList<Local> locais){
         int cont =0;
       for(int j=0;j<locais.size();j++){
          for(int i=0; i<geonames.size();i++){
                String local = inuteis(geonames.get(i).getName().toLowerCase());
                double similirity = 0;
                String local1 = inuteis(locais.get(j).getNome());
                if(!local1.equals("")){
                    similirity = jaccardSimilarity(local, local1);

                    if((similirity>0.7)){
                          locais.get(j).setNome(geonames.get(i).getName());
                          locais.get(j).setLatitude(geonames.get(i).getLatitude());
                          locais.get(j).setLongitude(geonames.get(i).getLongitude());
                          cont ++;
                    }
                }
            }
         }
         System.out.println("Total Recuperado no Geonames"+cont);
    }

     public static void individuos(ArrayList<Local> locais,Expressao e,BufferedWriter bw ) throws IOException{
         String poly="";
        
         for(int i=0; i<locais.size();i++){
             boolean pol=false;
             if(locais.get(i).getLatitude()!=0){
                total++;
                String ind = "<!-- http://www.semanticweb.org/ontologies/Gazetter#/Geometry/"+(total)+" -->\n\n";
                ind +="\t<owl:NamedIndividual rdf:about=\"&Gazetter;/Geometry/"+(total)+"\">\n";
                ind +="\t\t<geosparql:asWKT rdf:datatype=\"http://www.opengis.net/ont/geosparql#wktLiteral\">\n";
                ind +="\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>Point("+locais.get(i).getLatitude()+" "+locais.get(i).getLongitude()+")]]>\n";
                ind +="\t\t</geosparql:asWKT> \n";
                ind +="\t</owl:NamedIndividual>\n\n";
               if(locais.get(i).getPoly()!=null){
                    total++;
                    ind += "<!-- http://www.semanticweb.org/ontologies/Gazetter#/Geometry/"+(total)+" -->\n\n";
                    ind +="\t\t<owl:NamedIndividual rdf:about=\"&Gazetter;/Geometry/"+(total)+"\">\n";
                    ind +="\t\t\t<geosparql:asWKT rdf:datatype=\"http://www.opengis.net/ont/geosparql#wktLiteral\">\n";
                    ind +="\t\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>"+locais.get(i).getPoly()+"]]>\n";
                    ind +="\t\t\t</geosparql:asWKT> \n";
                    ind +="\t\t</owl:NamedIndividual>\n\n";
                    pol=true;
                }
                
                
                ind+="<!-- http://www.semanticweb.org/ontologies/Gazetter#"+e.getIngles()+(total)+" -->\n";
                ind+="\t<owl:NamedIndividual rdf:about=\"&Gazetter;"+e.getIngles()+total+"\">\n";
                ind+="\t\t<rdf:type rdf:resource=\""+e.getOnt()+"\"/>\n";
                ind+="\t\t<Gazetter:locality>"+locais.get(i).getNome()+"</Gazetter:locality>\n";
                ind+="\t\t<Gazetter:county>"+locais.get(i).getMunicipio()+"</Gazetter:county>\n";
              
                if(pol){
                    ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total-1)+"\"/>\n";
                    ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total)+"\"/>\n";
                }else{
                      ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total)+"\"/>\n";
                }
                ind+="\t</owl:NamedIndividual>\n";
                bw.write(ind);
                bw.flush();
              
             }
         }
         
     }
       
    public static void gazetteer(ArrayList<Local> locais,String caminho,String nome,String feature) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        
        String driver = "org.postgresql.Driver";  
        String user   = "postgres";  
        String senha = "3edc4rfv";  
        String url      = "jdbc:postgresql://localhost:5432/Gazetteer";  
  
        try  
        {  
            Class.forName(driver);  
            Connection con = null;  
  
            con = (Connection) DriverManager.getConnection(url, user, senha);  
             Statement stm = con.createStatement();   
           
            String query = "INSERT INTO toponimo (nome,feature) values('"+nome+"','"+feature+"');";
             System.out.println(query);
             stm.execute(query);
             query =" SELECT idToponimo FROM toponimo ORDER BY idToponimo DESC LIMIT 1";
             ResultSet rs = stm.executeQuery(query);
            String resultado="";
            if (rs.next()) {
                resultado = rs.getString("idToponimo");
            }
            String coordenada="";
           //insert into locality (id_toponimo,localidade,municipio,coordenada,coordenada2,coordenada3,coordenada4) values(18,'Ilha do Careiro','Careiro','0.0 0.0','POINT(0.0 0.0)','0.0,0.0','POINT(0.0,0.0)')
            for(Local l:locais){           
                 if(l.getLatitude()!=0 && l.getLongitude()!=0){
                    coordenada = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT("+l.getLatitude()+" "+l.getLongitude()+")^^<http://www.opengis.net/ont/sf#wktLiteral>"; 
                    query = "insert into locality (id_toponimo,localidade,municipio,coordenada,poligono) values("+resultado+",'"+l.getNome()+"','"+l.getMunicipio()+"','"+coordenada+"','"+l.getPoly()+"')";
                    System.out.println(query);
                    stm.execute(query);
                 }
            }
            stm.close();
            con.close();
        }  
        catch (ClassNotFoundException ex)  
        {  
            System.err.print(ex.getMessage());  
        }   
        catch (SQLException e)  
        {  
            System.err.print(e.getMessage());  
        }  
        
    }
    
 
    
     public static void amostra(ArrayList<Local> locais,BufferedWriter bw) throws UnsupportedEncodingException, FileNotFoundException, IOException{
         Random gerador = new Random();  
         
          for(int j=0;j<locais.size();j++){
           int rand = gerador.nextInt(locais.size());
                if(locais.get(rand).getSemelhantes().size()<=100 && locais.get(rand).getSemelhantes().size()>=10 && amostra <10){
                    
                     bw.write("___________________________________________________________________________________\n");
                     String valor = locais.get(rand).getNome() + "; " + locais.get(rand).getLatitude() + "," + locais.get(rand).getLongitude() + " :" + locais.get(rand).getMunicipio()+"."+ locais.get(rand).getSemelhantes().size()+"\n" ;
                     bw.write(valor);
                 
                     bw.write("\n");
                     for(Semelhantes ss: locais.get(rand).getSemelhantes()){
                         bw.write(ss.getNome()+"\n\n");
                     }
                     bw.write("\n");
                     
                     bw.write("___________________________________________________________________________________\n");
                     bw.write("\n");
                     amostra++;
                     locais.remove(rand);
                }else if (amostra>=5){
                    break;
                }
          }
    }
    
    public static void amostraGrupos(ArrayList<Local> locais,BufferedWriter bw ) throws UnsupportedEncodingException, FileNotFoundException, IOException{
   
          Random gerador = new Random();
           for(int j=0;j<locais.size();j++){
                int rand = gerador.nextInt(locais.size());
                if(amostra <20){
                    
                     bw.write("_____________________________________\n");
                     String valor = locais.get(rand).getNome() + "; " + locais.get(rand).getLatitude() + "," + locais.get(rand).getLongitude() + " :" + locais.get(rand).getMunicipio()+"."+ locais.get(rand).getSemelhantes().size()+"\n" ;
                     bw.write(valor);
                     bw.write("\n");
                   
                     amostra++;
                     locais.remove(rand);
                }else if (amostra>=20){
                    break;
                }
                bw.flush();
           }
    }

    public static boolean verificaMunicipio(String municipio, String municipio1) {
        
        if (municipio.equals(municipio1) || municipio1.equals("") || municipio1.equals("não informado") ||municipio.equals(" ")||municipio1.equals(" ") ||municipio==null || municipio1==null) {
            return true;
        }
        return false;
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

    public static void preencheInuteis(String caminho) throws IOException {
        String nome = caminho;
        String palavra;
        FileReader arq = new FileReader(nome);
        BufferedReader lerArq = new BufferedReader(arq);
        while ((palavra = lerArq.readLine()) != null) {
            inuteis.add(palavra);
        }
    }

    public static String inuteis(String local) {
        String temp [] = local.split(" ");
        String ok="";
         for (String s : inuteis) {
              for(int k=0;k<temp.length;k++){
                    if(temp[k].equals(s) || temp[k].equals("") || temp[k].length()<=3)
                     temp[k] = "";
                }
            }
         
            for(int n=0;n<temp.length;n++){
                if(!temp[n].equals("") )
                    ok +=temp[n]+" ";
            }
            
        
        return ok;
    }
     
    public static void inuteis2(ArrayList <Local> locais) throws FileNotFoundException, IOException {
        String palavra ="";
         ArrayList<String> inuteis = new ArrayList<String>();
         FileReader arq = new FileReader("C:\\Users\\Silvio\\Documents\\Gazetteer\\naoRelevante.txt");
        BufferedReader lerArq = new BufferedReader(arq);
        while ((palavra = lerArq.readLine()) != null) {
            inuteis.add(palavra);
        }
        String temp [];
        
        for(Local l:locais){
            String ok ="";
            l.setNome(l.getNome().toLowerCase().replaceAll("(?!\")\\p{Punct}", " "));
            temp = l.getNome().split(" ");
             for (String s : inuteis) {
                for(int k=0;k<temp.length;k++){
                    if(temp[k].equals(s) || temp[k].equals(""))
                     temp[k] = "";
                }
            }
         
            for(int n=0;n<temp.length;n++){
                if(!temp[n].equals(""))
                    ok +=temp[n]+" ";
            }
            l.setNome(ok);
        }
        
        System.out.println("PASSOU");
        grupos+=locais.size();
        System.out.println(locais.size());
        System.out.println();
        
    }
    public static boolean repetido(String id){
        for(int i=0; i<nomes.size();i++){
            if(nomes.get(i).getId_especies().equals(id))
                return false;
        }
        return true;
    }

    public static void Expressao(Expressao expressao, DadosSpeciesLink excel) throws IOException {

        // cria o padrão de localidades
        nomes.clear();
        seme.clear();
        Pattern pattern = Pattern.compile(expressao.getExpressao());
        int cont =0;
        for (int i = 0; i < nome.size(); i++) {
            Matcher matcher = pattern.matcher(nome.get(i).getNome());

            // procura as ocorrências do padrão no texto
            while (matcher.find()) {
                String local = matcher.group();
                local = local.replaceAll("\"", "");
                local = local.replaceAll("'", "");
                local = local.replaceAll("\\.", "");
                local = local.replaceAll(",", "");
                local = local.replaceAll(";", "");
                local = local.replaceAll(":", "");
                boolean teste = verifica(nome.get(i).getId_especies());
                if(teste){
                    centroide.add(nome.get(i).getId_especies());
                    seme.add(new Semelhantes(local,nome.get(i).getId_especies(), nome.get(i).getLatitude(), nome.get(i).getLongitude(),nome.get(i).getMunicipio()));
                    nomes.add(new Semelhantes(nome.get(i).getNome(),nome.get(i).getId_especies(), nome.get(i).getLatitude(), nome.get(i).getLongitude(),nome.get(i).getMunicipio()));
                    cont++;
                }
            }
        }
    }

    public static void iniciaprocesso() {
        final Progress p = new Progress();
        p.setVisible(true);
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                return null;
            }

            @Override
            protected void done() {
                p.setVisible(false);
            }
        };
        worker.execute();
    }

    public static double jaccardSimilarity(String similar1, String similar2) {
        HashSet<String> h1 = new HashSet<String>();
        HashSet<String> h2 = new HashSet<String>();
        for (String s : similar1.split("\\s+")) {
            h1.add(s);
        }
        for (String s : similar2.split("\\s+")) {
            h2.add(s);
        }
        int sizeh1 = h1.size();
        //Retains all elements in h3 that are contained in h2 ie intersection
        h1.retainAll(h2);
        //h1 now contains the intersection of h1 and h2
        h2.removeAll(h1);
        //h2 now contains unique elements
        //Union 
        int union = sizeh1 + h2.size();
        int intersection = h1.size();
        return (double) intersection / union;
    }

    public static void lerToponimo(String caminho) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {

        File fXmlFile = new File(caminho);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        //optional, but recommended
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("toponimo");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String feature =eElement.getElementsByTagName("feature").item(0).getTextContent();
                String ont =eElement.getElementsByTagName("onty").item(0).getTextContent();
                String poly = eElement.getElementsByTagName("poly").item(0).getTextContent();
                exp.add(new Expressao(eElement.getElementsByTagName("local").item(0).getTextContent(), eElement.getElementsByTagName("expressao").item(0).getTextContent(),eElement.getElementsByTagName("nome").item(0).getTextContent(),feature,ont,poly));
            }
        }
    }

    private static void contagemCoord(ArrayList<Local> locais) {
        int cont=0;
        for(Local l:locais){
            if(l.getLatitude()!=0 && l.getLongitude()!=0){
                cont+=l.getSemelhantes().size();
            }
        }
        totalD+=cont;
        System.out.println("TOTAL DE LOCAIS com latitude recuperada: "+totalD);
    }

   public static void KMLGoogleEarth(Poligono geo) throws FileNotFoundException, UnsupportedEncodingException, IOException{
       System.out.println("Começou google earth");
       int cont=0;
           File aq1 = new File("C:\\Users\\Silvio\\Desktop\\coordenatesgbif.kml"); //se já existir, será sobreescrito   
         BufferedWriter amostg = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aq1),"UTF-8"));  
       String template ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<kml xmlns=\"http://www.opengis.net/kml/2.2\">  <Document> \n";
        amostg.write(template);
         amostg.flush();
        BancoDeDados bd = new BancoDeDados("root","123456","jdbc:mysql://127.0.0.1:3306/especies");
        bd.Conectar();
        String [][] res = null;
     //   String query = "select locality,id,latitude,longitude,county from occurrence where locality<>\"\" or (latitude<>\"\" and longitude<>\"\") and locality<>'.'";
          String query = "select locality,latitude,longitude,county from sheet1 where (latitude<>\"\" and longitude<>\"\") and locality like '%Parque Nacional do Jaú%'";
      
        
         System.out.println(query);
        try {
             res = bd.selecao(query);
            
        } catch (SQLException ex) {
           System.err.println("Erro ao buscar dados processados");
        }
       cont=0;
        for(int i=0;i<res.length;i++){                           
            //(String nome, String id_especies, String latitude, String longitude, String municipio)
        if(res[i][2]!=null && res[i][3]!=null &&(!res[i][2].equals("") && !res[i][3].equals(""))&&(!res[i][2].equals("0") && !res[i][3].equals("0"))){
                
          if(!geo.insidePolygon(transformaFloat(res[i][3]),transformaFloat(res[i][2])))
          {System.out.println(res[i][0]);
              template= "<Placemark>\n <Point>\n" +
                " <coordinates>"+res[i][3]+","+res[i][2]+"</coordinates>\n" +
                " </Point>\n </Placemark>\n";
                cont++;
                amostg.write(template);
                amostg.flush();
          } 
              }
           }
          template="  </Document>  </kml>";
         
         amostg.write(template);
         amostg.flush();
         amostg.close();
           System.out.println("ACABOU google earth");
           System.out.println(cont);
   }
}
