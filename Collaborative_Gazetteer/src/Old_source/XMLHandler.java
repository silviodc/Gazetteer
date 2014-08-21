package Old_source;


import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
class XMLHandler extends DefaultHandler {

    /** o galho atual */  
    private StringBuffer galhoAtual = new StringBuffer(200);  
  
    /** o valor da tag atual */  
    private StringBuffer valorAtual = new StringBuffer(100); 
    
    public XMLHandler() {
    }
    /** comeca um documento novo */  
    @Override
    public void startDocument() {  
        System.out.print("iniciando");  
    }  

    /** termina o documento */  
    @Override
    public void endDocument() {  
        System.out.print("\nterminando");  
    }  
    /** comeca uma tag nova */  
    @Override
    public void startElement(String uri, String localName, String tag, Attributes atributos) {  

        // seta o galho atual  
        galhoAtual.append("/" + tag);  
        System.out.println("___________________________________");
        // mostra a tag  
        System.out.print(  
            "\n<"  
                + galhoAtual.substring(1)  
                + (atributos.getLength() != 0 ? "" : "")  
                + ">");  

        // limpa a tag atual  
        valorAtual.delete(0, valorAtual.length());  

    }  
    /** termina uma tag */  
    @Override
    public void endElement(String uri, String localName, String tag) {  

        // mostra o valor  
        System.out.print(valorAtual.toString().trim());  
        // e limpa  
        valorAtual.delete(0, valorAtual.length());  

        // seta o galho atual  
        galhoAtual.delete(  
            galhoAtual.length() - tag.length() - 1,  
            galhoAtual.length());  

    }  
    /** recebe os dados de uma tag */  
    @Override  
    public void characters(char[] ch, int start, int length) {  
  
    // adiciona ao valor atual  
    valorAtual.append(ch, start, length);  
  
}  
}
