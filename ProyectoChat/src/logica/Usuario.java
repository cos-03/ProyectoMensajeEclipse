/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

/**
 *Esta clase representa a los diferentes personas del chat
 * @author Felipe Valencia
 */


public class Usuario {
    private String name;
    private String ip;
    private String image;
    private String id;
    private String estado;

     public Usuario(String name, String ip, String id, String estado) {
        this.name = name;
        this.ip = ip;
        this.id = id;
        this.estado = estado;
        this.image=System.getProperty("user.dir")+"/src/documentos/avatar-chat.png";
    }
    
    
    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
    

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    public void setImage(String image) {
		this.image = image;
	}


	@Override
    public String toString() {
        return "Usuario{" + "name=" + name + ", ip=" + ip + ", image=" + image + ", id=" + id + ", estado=" + estado + ", historialMensajes=" +'}';
    }
    
    
}
