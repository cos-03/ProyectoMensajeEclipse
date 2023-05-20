
package logicaServidor;


/**
 *Esta clase representa a los diferentes personas del chat
 *@author Felipe Valencia
 */
public class Usuario{

	private String name;
	private String contrasena;
    private String ip;
    private String id;
    private String estado;

    
    
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
    
    
    
    public void setName(String name) {
		this.name = name;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String darUser() {
        return name + ";" + ip + ";" + id + ";" + estado;
    }
    
    
}
