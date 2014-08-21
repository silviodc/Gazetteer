package Old_source;

/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class Expressao {
    
    private String nome;
    private String expressao;
    private String ingles;
    private String feature;
    private String ont;
    private String poly;
    public Expressao(String nome, String expressao, String ingles, String feature, String ont,String poly) {
        this.nome = nome;
        this.expressao = expressao;
        this.ingles = ingles;
        this.feature = feature;
        this.ont = ont;
        this.poly = poly;
    }

    public String getOnt() {
        return ont;
    }

    public void setOnt(String ont) {
        this.ont = ont;
    }

    public String getFeature() {
        return feature;
    }

    public String getPoly() {
        return poly;
    }

    public void setPoly(String poly) {
        this.poly = poly;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getIngles() {
        return ingles;
    }

    public void setIngles(String ingles) {
        this.ingles = ingles;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getExpressao() {
        return expressao;
    }

    public void setExpressao(String expressao) {
        this.expressao = expressao;
    }
    
    
}
