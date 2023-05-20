package Servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import logicaServidor.Chat;
import logicaServidor.Mensaje;
import logicaServidor.Usuario;
/**
 * Esta clase cumple la funcion mantener la conexion con los clientes.
 * @author Felipe Valencia
 */
public class ConexionChat extends Thread {

	private Servidor server;
	private Socket socket;
	private ObjectInputStream entradaDatos;
	private ObjectOutputStream salidaDatos;
	private MensajesUsuarios mensajesNuevos;
	private Usuario user;
	private int nivel;
	private boolean conectado;

	public ConexionChat(Socket socket, MensajesUsuarios mensajesUsuarios) {
		this.socket = socket;
		mensajesNuevos = mensajesUsuarios;
		try {
			entradaDatos = new ObjectInputStream(this.socket.getInputStream());
			salidaDatos = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException ex) {
			System.out.println("Error el Threre " + Thread.currentThread().getName()
					+ " al crear los stream de entrada y salida : " + ex.getMessage());
		}
	}

	@Override
	public void run() {
		Object mensajeRecibido;
		nivel = 0;
		conectado = true;
		
		/*
		 * Toma los mensajes enviados por el cliente y hace la instruccion deseada
		 */
		while (conectado) {
			if (nivel == 0) {
				try {
					mensajeRecibido = entradaDatos.readObject();
					String[] palabras = ((String) mensajeRecibido).split(":");
					System.out.println("*");
					if (palabras[0].equals("@")) {
						System.out.println("**");
						if (server.verificarUser(palabras[1], palabras[2])) {
							System.out.println("***");
							if (!userActivo(palabras[1])) {

								System.out.println("****");
								user = server.darUsuario(palabras[1]);
								user.setIp(socket.getInetAddress().getHostAddress());
								salidaDatos.writeObject("*:autenticado");
								System.out.println("Cliente: "+user.getName()+" autenticado");
								int pos = server.posUsuario(palabras[1]);
								server.getListaUsuarios().get(pos).setEstado("activo");
								String chats = datosChats();
								salidaDatos.writeObject(chats);

								String usuarios = datosUsuarios();
								mensajesNuevos.lanzar(usuarios);

								System.out.println("chats enviados a:" + user.getName());
								System.out.println("Usuarioa activos enviados a:" + user.getName());

								this.setName("Cliente: "+user.getName());

								nivel++;
							} else {
								salidaDatos.writeObject("*:no autenticado");
								salidaDatos.writeObject("*:stop");
								server.getListaHilos().remove(this);
								conectado = false;

							}
						} else {
							salidaDatos.writeObject("*:no autenticado");
							salidaDatos.writeObject("*:stop");
							server.getListaHilos().remove(this);
							conectado = false;

						}
					} else if (palabras[0].equals("&")) {
						if (server.posUsuario(palabras[2]) == -1) {
							Usuario user = new Usuario(palabras[1], palabras[3], palabras[2], "inactivo");
							server.getListaUsuarios().add(user);
							server.actualizarListaUsuario();
							salidaDatos.writeObject("*:autenticado");
							System.out.println("Usuario registrado: "+ user.getName());
						}else {
							salidaDatos.writeObject("*:no autenticado");
							salidaDatos.writeObject("*:stop");
							server.getListaHilos().remove(this);
							conectado = false;
						}
					} else {
						salidaDatos.writeObject("*:no autenticado");
						salidaDatos.writeObject("*:stop");
						server.getListaHilos().remove(this);
						conectado = false;

					}

				} catch (Exception ex) {
					try {
						salidaDatos.writeObject("*:stop");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					server.getListaHilos().remove(this);
					conectado = false;
				}

			} else if (nivel == 1) {
				try {

					mensajeRecibido = entradaDatos.readObject();
					String[] palabras = ((String) mensajeRecibido).split(":");

					if (palabras[0].equals("*")) {

						if (palabras[1].equals("stop")) {
							salidaDatos.writeObject(mensajeRecibido);
							int pos = server.posUsuario(user.getId());
							server.getListaUsuarios().get(pos).setEstado("inactivo");
							String usuarios = datosUsuarios();
							mensajesNuevos.lanzar(usuarios);
							conectado = false;
							server.actualizarListaUsuario();
							server.getListaHilos().remove(this);
							
						}
						continue;
					} else if (palabras[0].equals("#")) {
						String[] subPalabras = palabras[1].split(";");
						String codigo = crearCodigo(subPalabras[0], subPalabras[2]);
						System.out.println("Recibido de "+Thread.currentThread().getName() + ":  " + mensajeRecibido);
						int posChat = server.posChat(codigo);
						if (posChat == -1) {
							ArrayList<Mensaje> listaMensaje = new ArrayList<>();
							Chat chat = new Chat(codigo, listaMensaje);
							server.getListaChatTotal().add(chat);
							posChat = server.posChat(codigo);
						}
						Mensaje newMensaje = new Mensaje(
								"" + (server.getListaChatTotal().get(posChat).getListaMensajes().size() + 1),
								subPalabras[1], false);

						newMensaje.setRemitente(subPalabras[0]);
						newMensaje.setDestinatario(subPalabras[2]);
						server.getListaChatTotal().get(posChat).getListaMensajes().add(newMensaje);
						server.actualizarListaChats();
						salidaDatos.writeObject(newMensaje.darMensaje());
						mensajesNuevos.lanzar(newMensaje.darMensaje());

					}else if (palabras[0].equals("-+")) {
						String idUser=palabras[1];
						String contrasena=palabras[2];
						String nombre= palabras[3];
						String nuevaContrasena= palabras[4];
						boolean ban= true;
						for(Usuario user:server.getListaUsuarios()) {
							if(user.getId().equals(idUser)) {
								if(user.getContrasena().equals(contrasena)) {
									int pos= server.posUsuario(idUser);
									server.getListaUsuarios().get(pos).setName(nombre);
									server.getListaUsuarios().get(pos).setContrasena(nuevaContrasena);
									server.actualizarListaUsuario();
									salidaDatos.writeObject("-+:Usuario modificado con exito");
									mensajesNuevos.lanzar(datosUsuarios());
									ban=false;
								}
							}
						}
						if(ban) {
							salidaDatos.writeObject("-+:No se pudo completar la operacion datos incorrectos");
						}
						
					}else if (palabras[0].equals("-")) {
						String idUser=palabras[1];
						String contrasena=palabras[2];
						String nombre= palabras[3];
						boolean ban= true;
						for(Usuario user:server.getListaUsuarios()) {
							if(user.getId().equals(idUser)) {
								if(user.getContrasena().equals(contrasena)) {
									int pos= server.posUsuario(idUser);
									server.getListaUsuarios().get(pos).setName(nombre);
									server.actualizarListaUsuario();
									salidaDatos.writeObject("-:Usuario modificado con exito");
									mensajesNuevos.lanzar(datosUsuarios());
									ban=false;
								}
							}
						}
						if(ban) {
							salidaDatos.writeObject("-:No se pudo completar la operacion datos incorrectos");
						}
						
					}else if (palabras[0].equals("-@")) {
						String idUser=palabras[1];
						String contrasena=palabras[2];
						boolean ban= true;
						for(Usuario user:server.getListaUsuarios()) {
							if(user.getId().equals(idUser)) {
								if(user.getContrasena().equals(contrasena)) {
									int pos= server.posUsuario(idUser);
									server.getListaUsuarios().remove(pos);
									server.actualizarListaUsuario();
									salidaDatos.writeObject("-@:Usuario eliminado con exito");
									mensajesNuevos.lanzar(datosUsuarios());
									System.out.println("Usuario eliminado: "+user.getName());
									salidaDatos.writeObject("*:stop");
									int posUser = server.posUsuario(user.getId());
									server.getListaUsuarios().get(posUser).setEstado("inactivo");
									String usuarios = datosUsuarios();
									mensajesNuevos.lanzar(usuarios);
									conectado = false;
									server.getListaHilos().remove(this);
									ban=false;
								}
							}
						}
						if(ban) {
							salidaDatos.writeObject("-@:No se pudo completar la operacion datos incorrectos");
						}
						
					}
				} catch (Exception e) {
					System.err.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.");
					System.err.println(e);
					int pos = server.posUsuario(user.getId());
					server.getListaUsuarios().get(pos).setEstado("inactivo");
					String usuarios = datosUsuarios();
					mensajesNuevos.lanzar(usuarios);
					conectado = false;
					server.getListaHilos().remove(this);
					mensajesNuevos.lanzar("*:stop");
					try {
						entradaDatos.close();
						salidaDatos.close();
						socket.close();
						
					} catch (IOException ex2) {
						System.err.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
					}
				}
			}
		}
	}
	
	/*
	 * crea un codigo para identificar los chats
	 */
	public String crearCodigo(String id1, String id2) {
		String codigo = "";
		if (compararId(id1, id2)) {
			codigo += id1 + "" + id2;
		} else {
			codigo += id2 + "" + id1;
		}
		return codigo;
	}

	public boolean compararId(String id1, String id2) {
		int id1tamanio = id1.length();
		int id2tamanio = id2.length();
		if (id1tamanio < id2tamanio) {
			for (int i = 0; i < id1tamanio; i++) {
				if (id1.charAt(i) != id2.charAt(i)) {
					if (id1.charAt(i) > id2.charAt(i)) {
						return false;
					} else
						return true;
				} else
					continue;
			}
			return true;
		} else {
			for (int i = 0; i < id2tamanio; i++) {
				if (id1.charAt(i) != id2.charAt(i)) {
					if (id1.charAt(i) > id2.charAt(i)) {
						return false;
					} else
						return true;
				} else
					continue;
			}
			return false;
		}
	}
	
	
	/*
	 * Retorna un string  con la informacion de los Usuarios
	 */
	public String datosUsuarios() {
		String usuarios = "$";
		for (Usuario user : server.getListaUsuarios()) {
			if(userActivo(user.getId())) {
				usuarios += ":" + user.darUser();
			}
		}
		for (Usuario user : server.getListaUsuarios()) {
			if(!userActivo(user.getId())) {
				usuarios += ":" + user.darUser();
			}
		}
		return usuarios;
	}

	/*
	 * Retorna un string  con la informacion de los chat
	 */
	public String datosChats() {
		String chats = "%";
		for (Chat chat : server.getListaChatTotal()) {
			String remitente = chat.getListaMensajes().get(0).getRemitente();
			String destinatario = chat.getListaMensajes().get(0).getDestinatario();
			if (user.getId().equals(remitente) || user.getId().equals(destinatario)) {

				for (Mensaje m : chat.getListaMensajes()) {
					chats += ":" + m.getRemitente() + ";" + m.getContenido() + ";" + m.getDestinatario() + ";"
							+ m.getFecha() + ";" + m.isVisto();
				}
			}
		}

		return chats;
	}

	/*
	 * Verifica si un usuario ya esta activo
	 */
	public boolean userActivo(String id) {
		for (Usuario user : server.getListaUsuarios()) {
				if (user.getId().equals(id)) {
					if(user.getEstado().equals("activo"))
						return true;
				}
		}
		return false;
	}

	public ObjectOutputStream getSalidaDatos() {
		return salidaDatos;
	}

	public void setServer(Servidor server) {
		this.server = server;
	}

	public Usuario getUser() {
		return user;
	}

	public boolean isConectado() {
		return conectado;
	}

}
