package com.example.marcelo.pesquisacarro;

import java.io.Serializable;

/**
 * Created by marcelo on 22/08/15.
 */
public class User implements Serializable {

    private String usuario;
    private String senha;
    private Boolean islogin = false;
    private static final long serialVersionUID = 46543440;

    public User(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getIslogin() {
        return islogin;
    }

    public void setIslogin(Boolean islogin) {
        this.islogin = islogin;
    }
}
