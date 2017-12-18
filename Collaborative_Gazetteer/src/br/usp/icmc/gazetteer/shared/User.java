package br.usp.icmc.gazetteer.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable {
    
	private int id = 0;
    private String username = "";
    private String password = "";
    private String nome="";
    private String type="";
    
    private User() {
        //just here because GWT wants it.
    }
    
    /**
     * A user of the system
     * @param id
     * @param username
     * @param password
     */
    public User(int id, String username, String password,String nome,String type) {
        this.setId(id);
        this.setUserName(username);
        this.setPassword(password);
        this.setNome(nome);
        this.setType(type);
        
    }
    
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param username the username to set
     */
    public void setUserName(String username) {
        this.username = username;
    }

    /**
     * @return the username
     */
    public String getUserName() {
        return username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}    
}