package Old_source;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.bbn.openmap.geo.OMGeo;
import java.util.ArrayList;

/**
 *
 * @author Silvio
 */
public class Local {
    
    private String nome;
    private String municipio;
    private double latitude;
    private double longitude;
    private String poly;
    
    private ArrayList <Semelhantes> semelhantes = new ArrayList<Semelhantes>();

    Local(String local) {
       this.nome = local;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Semelhantes> getSemelhantes() {
        return semelhantes;
    }

    public String getPoly() {
        return poly;
    }

    public void setPoly(String poly) {
        this.poly = poly;
    }

    public void setSemelhantes(Semelhantes semelhantes) {
        this.semelhantes.add(semelhantes);
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    
    
    
    
}
