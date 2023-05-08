/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

/**
 * @author ACER
 */
public class Aplicacion {
    private BaseDatosMensaje base;
    private Usuario user;
    private Usuario userChat;
    private TuberiaMensajes tuberia;
    private ConectarServer conexion;
    private boolean conectado;

    public Aplicacion() {
        this.base = new BaseDatosMensaje();
        user = base.getUser();

    }

    public Aplicacion(Usuario usuario) {
        this.base = new BaseDatosMensaje();
        user = usuario;
    }

    public BaseDatosMensaje getBase() {
        return base;
    }

    public Usuario getUser() {
        return user;
    }

    public Usuario getUserChat() {
        return userChat;
    }

    public void setUserChat(Usuario chat) {
        this.userChat = chat;
    }

    public TuberiaMensajes getTuberia() {
        return tuberia;
    }

    public void setTuberia(TuberiaMensajes tuberia) {
        this.tuberia = tuberia;
    }

    public ConectarServer getConexion() {
        return conexion;
    }

    public void setConexion(ConectarServer conexion) {
        this.conexion = conexion;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }


}
