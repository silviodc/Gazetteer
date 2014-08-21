package Old_source;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Particular
 */
public class BancoDeDados {

    private Connection conexao;
    private String usuario;
    private String senha;
    private String url ;
    private Statement stm;

    public BancoDeDados(String usuario, String senha, String url) {
        setUsuario(usuario);
        setSenha(senha);
        setUrl(url);

    }

    public Connection getConexao() {
        return conexao;
    }

    public void setConexao(Connection conexao) {
        this.conexao = conexao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    //tenta a conexao com o banco de dados
    public boolean Conectar(){
        try{
           Class.forName("com.mysql.jdbc.Driver");  
            conexao = (Connection) DriverManager.getConnection(url, usuario,senha);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }catch( ClassNotFoundException ex){
            ex.printStackTrace();
            return false;
        }
    }

    //desconecta com o banco
    public boolean Desconetar(){
        try{
            conexao.close();
            return true;
        }catch (SQLException ex) {
            return false;
        }
    }


    public boolean insercao(String query) throws SQLException{
        
        try{
            stm = (Statement) conexao.createStatement();
            stm.executeUpdate(query);
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return true;
    }

    public ResultSet formulario(String query) throws SQLException{
        ResultSet rs=null;
        try {
                stm = (Statement) conexao.createStatement();
                rs = stm.executeQuery(query);
                } catch (SQLException ex) {
                throw new SQLException();
           }
         return rs;
    }

     //executa uma busca no banco
    public String[][] selecao(String query) throws SQLException{
            try {
                if(!conexao.isClosed())
                    stm = (Statement) conexao.createStatement();
                else{
                    Conectar();
                    stm = (Statement) conexao.createStatement();
                }
                ResultSet rs = stm.executeQuery(query);
                ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();

                int cols = rsmd.getColumnCount();
                rs.last();
                int rows = rs.getRow();
                rs.beforeFirst();

                String[][] resultado = new String[rows][cols];
                while (rs.next()) {
                    for(int i = 0; i < cols; i++){
                        resultado[rs.getRow()-1][i] = rs.getString(i+1);
                    }
                }
                return resultado;
           } catch (SQLException ex) {
                throw new SQLException();
           }
    }

}