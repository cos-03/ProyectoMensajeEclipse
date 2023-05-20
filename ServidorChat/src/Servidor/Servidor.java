package Servidor;

import logicaServidor.LeerXml;
import logicaServidor.Usuario;
import logicaServidor.Chat;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import logicaServidor.CrearXml;
import org.xml.sax.SAXException;

/**
 * Servidor para el chat.
 * @author Felipe Valencia
 */
public class Servidor {

	private MensajesUsuarios mensajesUsuarios;
	private ArrayList<ConexionChat> listaHilos;
	private ArrayList<Chat> listaChatTotal;
	private ArrayList<Usuario> listaUsuarios;

	/*
	 * Actualiza los datos desde los archivos xml
	 */
	private void leerArchios() {
		LeerXml l, l2;
		try {
			l = new LeerXml(System.getProperty("user.dir") + "/src/documentos/ListaChat.xml");
			l2 = new LeerXml(System.getProperty("user.dir") + "/src/documentos/ListaUsuario.xml");

			listaChatTotal = l.leerListaChats();
			listaUsuarios = l2.leerListaUsuarios();
		} catch (SAXException ex) {

		} catch (ParserConfigurationException ex) {

		} catch (IOException ex) {

		}
	}

	/**
	 * Este metodo inicia el servidor
	 */
	public void menu() {
		int puerto = 5560;
		int maximoConexiones = 30;
		ServerSocket servidor = null;
		Socket socket = null;
		leerArchios();
		listaHilos = new ArrayList<>();

		try {
			// Se crea el serverSocket
			servidor = new ServerSocket(puerto, maximoConexiones);
			mensajesUsuarios = new MensajesUsuarios();
			EscucharUsuarios escuchar = new EscucharUsuarios(this);
			escuchar.start();

			while (true) {
				System.out.println("Servidor a la espera de conexiones.");
				
				socket = servidor.accept();
				System.out.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " conectado.");

				ConexionChat cc = new ConexionChat(socket, mensajesUsuarios);
				cc.setServer(this);
				cc.start();
				listaHilos.add(cc);
			}
		} catch (IOException ex) {
			System.err.println("Error " + ex.getMessage());
		} finally {
			try {
				socket.close();
				servidor.close();
			} catch (IOException ex) {
				System.err.println("Error al cerrar el servidor: " + ex.getMessage());
			}
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Servidor server = new Servidor();
		server.menu();

	}

	public MensajesUsuarios getMensajesUsuarios() {
		return mensajesUsuarios;
	}

	public ArrayList<ConexionChat> getListaHilos() {
		return listaHilos;
	}

	public ArrayList<Chat> getListaChatTotal() {
		return listaChatTotal;
	}

	public ArrayList<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	/*
	 * verifica que el usuario y contraceña estan registradas en el sistema
	 */
	public boolean verificarUser(String id, String contrasena) {
		Usuario user = darUsuario(id);
		if (user != null) {
			if (user.getContrasena().equals(contrasena))
				return true;
		}
		return false;
	}
	

	/*
	 * Rerorna el usuario que tenga ese id
	 */
	public Usuario darUsuario(String id) {

		for (Usuario user : listaUsuarios) {
			if (user.getId().equals(id)) {
				return user;
			}
		}
		return null;
	}
	
	/*
	 * Rerorna la posicion del Usuario en la lista
	 */
	public int posUsuario(String id) {
		int pos=-1;
		for (Usuario user : listaUsuarios) {
			pos++;
			if (user.getId().equals(id)) {
				return pos;
			}
		}
		return -1 ;
	}


	/*
	 * Rerorna la posicion del chat en la lista
	 */
	public int posChat(String codigoChat) {
		int pos = 0;

		for (Chat chat : listaChatTotal) {
			if (chat.getCodigo().equals(codigoChat)) {
				return pos;
			}
			pos++;
		}
		return -1;
	}


	/*
	 * Rerorna chat que tenga ese codigo
	 */
	public Chat darChat(String codigoChat) {

		for (Chat chat : listaChatTotal) {
			if (chat.getCodigo().equals(codigoChat)) {
				return chat;
			}
		}
		return null;
	}
	
	/*
	 * Actualiza los nuevos datos en el archivo xml
	 */
	public void actualizarListaChats() {

		CrearXml c = new CrearXml(System.getProperty("user.dir") + "/src/documentos/ListaChat.xml");
		try {
			c.crearListaChat(listaChatTotal);
			System.out.println("listaChat actualizada");
		} catch (TransformerException ex) {
			System.out.println("Error lista no actualizada");
		}
	}
	
	/*
	 * Actualiza los nuevos datos en el archivo xml
	 */
	public void actualizarListaUsuario() {

		CrearXml c = new CrearXml(System.getProperty("user.dir") + "/src/documentos/ListaUsuario.xml");
		try {
			c.crearListaUsuario(listaUsuarios);
			System.out.println("listaUsuarios actualizada");
		} catch (TransformerException ex) {
			System.out.println("Error lista no actualizada");
		}
	}

}
