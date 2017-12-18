package br.usp.icmc.gazetteer.server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.usp.icmc.gazetteer.shared.User;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MySQLConnection{   

    private String _status;
    private String _url = "jdbc:mysql://localhost:3306/newpaper";
    private String _user = "root";
    private String _pass = "123456";
    
    /**
     * Constructor
     */
    public MySQLConnection() {
    }

    /**
     * Gets the connection for all of our commands
     * 
     * @return
     * @throws Exception
     */
    private Connection getConnection() throws Exception {
        //I like to use this setup where it converts datetimes of '00-00-0000' to null rather than error out.
        Properties props = new Properties();
        props.setProperty("user", _user);
        props.setProperty("password", _pass);
        props.setProperty("zeroDateTimeBehavior", "convertToNull");
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = DriverManager.getConnection(_url, props);
        return conn;
    }

    
    /**
     * Authenticates a user based on their username and password
     * @throws Exception
     */
    public User authenticateUser(String username, String password) throws Exception {
        User returnuser = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet result = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM user WHERE usuario =? AND senha =?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            result = pstmt.executeQuery();
            System.out.println("results  "+result.getRow());
            while (result.next()) {
            	
                returnuser = new User(result.getInt("idUser"), result.getString("usuario"), result.getString("senha"), result.getString("nome"),result.getString("type"));
            }
        } catch (SQLException ex) {
        	 Logger lgr = Logger.getLogger(MySQLConnection.class.getName());
             lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            // Cleanup
            result.close();
            pstmt.close();
            conn.close();
        }
        if(returnuser == null)
        	throw new SQLException("Usuario não encontrado");
        System.out.println(returnuser.getNome());
        return returnuser;
    }
}