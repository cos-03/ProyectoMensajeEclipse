
package logica;

import java.util.ArrayList;
/**
 * Esta clase es la base de Datos.
 * @author Felipe Valencia
 */
public class BaseDatosMensaje {

	private ArrayList<Chat> listaChatTotal;
	private ArrayList<Usuario> listaUsuarios;
	private Usuario user;
	private TuberiaMensajes newMen;

	public BaseDatosMensaje() {

		iniciarDatos();

	}

	public void iniciarDatos() {
			listaChatTotal =new ArrayList<>();
			listaUsuarios=new ArrayList<>();
	}
	
	/*
	 * Este metodo remplaza un chat, y en caso de que no exista lo agrega a la lista
	 */
	public void llenarHistorial(Chat chat) {
		if (listaChatTotal.isEmpty() || chat.getListaMensajes().isEmpty()) {
			listaChatTotal.add(chat);
		} else {
			boolean ban = false;
			for (Chat lista : listaChatTotal) {
				if (lista.getCodigo().equals(chat.getCodigo())) {
					lista.setListaMensajes(chat.getListaMensajes());
					ban = true;
				}
			}
			if (!ban) {
				listaChatTotal.add(chat);
			}

		}
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
	 * Rerorna la posicion del Usuario en la lista
	 */
	public int posUsuario(String id) {

		int pos = 0;
		for (Usuario user : listaUsuarios) {
			if (user.getId().equals(id)) {
				return pos;
			}
			pos++;
		}
		return -1;
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
	
	public Mensaje getUltimoMensaje(String codigo) {
		ArrayList<Mensaje>lista=darChat(codigo).getListaMensajes();
		int pos= lista.size()-1;
		return lista.get(pos);
	}
	
	public int posMensaje(String codigoChat) {
		int pos=listaChatTotal.get(posChat(codigoChat)).getListaMensajes().size() - 1;
		return pos;
	}
	
	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public ArrayList<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	public ArrayList<Chat> getListaChatTotal() {
		return listaChatTotal;
	}
	public void setListaUsuarios(ArrayList<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	public TuberiaMensajes getNewMen() {
		return newMen;
	}

}
