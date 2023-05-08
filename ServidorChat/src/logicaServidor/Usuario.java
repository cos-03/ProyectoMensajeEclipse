/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicaServidor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ACER
 */
public class Usuario implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String contrasena;
    private String ip;
    private String image;
    private String id;
    private String estado;
    private ArrayList<Mensaje> historialMensajes;

    
    
     public Usuario(String name, String contrasena, String id, String estado) {
		this.name = name;
		this.contrasena = contrasena;
		this.id = id;
		this.estado = estado;
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
    
    public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
    public void setIp(String ip) {
		this.ip = ip;
	}


	public String darUser() {
        return name + ";" + ip + ";" + id + ";" + estado;
    }
    
    
}
