package Old_source;

/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class Semelhantes {
    
    private String nome;
    private String id_especies;
    private String latitude;
    private String longitude;
    private String municipio;
    private double peso;

    public Semelhantes(String nome, String id_especies, String latitude, String longitude, String municipio) {
        this.nome = nome;
        this.id_especies = id_especies;
        this.latitude = latitude;
        this.longitude = longitude;
        this.municipio = municipio;
    }

        public Semelhantes(String nome, String id_especies, String latitude, String longitude, String municipio,double peso) {
        this.nome = nome;
        this.id_especies = id_especies;
        this.latitude = latitude;
        this.longitude = longitude;
        this.municipio = municipio;
        this.peso = peso;
    }

    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId_especies() {
        return id_especies;
    }

    public void setId_especies(String id_especies) {
        this.id_especies = id_especies;
    }

    public String getLatitude() {
        return latitude;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
    
    
    
}
