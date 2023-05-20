
package logica;
/**
 * Esta clase es la que conecta a la base de datos
 *@author Felipe Valencia
 */
public class Aplicacion {
    private BaseDatosMensaje base;
    private String idUser;
    private Usuario user;
    private Usuario userChat;
    private TuberiaMensajes tuberia;
    private ConectarServer conexion;
    private boolean conectado;

    /*
     * Constructor de la clase
     */
    public Aplicacion() {
        this.base = new BaseDatosMensaje();
        user=base.getUser();        
    }
    
    /*
     * Constructor de la clase
     */
    public Aplicacion(Usuario usuario) {
        this.base = new BaseDatosMensaje();
        user=usuario;
    }

    /*
     * Retorna la base de datos
     */
    public BaseDatosMensaje getBase() {
        return base;
    }
    
    /*
     * Retorna el usuario principal
     */
    public Usuario getUser() {
        return user;
    }
    
    /*
     * Retorna el usuario del chat selecionado
     */
    public Usuario getUserChat() {
        return userChat;
    }

    /*
     * Pone el usuario del chat selecionado
     */
    public void setUserChat(Usuario chat) {
        this.userChat = chat;
    }
    
    /*
     * Retorna la tuberia de los mensajes
     */
    public TuberiaMensajes getTuberia() {
        return tuberia;
    }

    /*
     * Pone la tuberia de los mensajes
     */
	public void setTuberia(TuberiaMensajes tuberia) {
		this.tuberia = tuberia;
	}
	
	/*
	 * Retorna la conexion con el servidor
	 */
	public ConectarServer getConexion() {
		return conexion;
	}

	/*
	 * Pone la conexion con el servidor
	 */
	public void setConexion(ConectarServer conexion) {
		this.conexion = conexion;
	}
	
	/*
	 * Retorna si esta conectado
	 */
	public boolean isConectado() {
		return conectado;
	}

	/*
	 *Pone el estado esta conectado
	 */
	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}
	
	/*
	 * Pone el usuario
	 */
	public void setUser(Usuario user) {
		this.user = user;
	}
	
	public void setIdUser(String id) {
		idUser=id;
	}
	
	public String getIdUser() {
		return idUser;
	}
	
}
