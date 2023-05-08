package Servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import logicaServidor.Chat;
import logicaServidor.Mensaje;
import logicaServidor.Usuario;

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
		// Se apunta a la lista de observadores de mensajes

		while (conectado) {
			if (nivel == 0) {
				try {
					mensajeRecibido = entradaDatos.readObject();
					String[] palabras = ((String) mensajeRecibido).split(":");
					if (palabras[0].equals("@")) {

						if (server.verificarUser(palabras[1], palabras[2])) {

							if (!userActivo(palabras[1])) {

								System.out.println("****");
								user = server.darUsuario(palabras[1]);
								user.setIp(socket.getInetAddress().getHostAddress());
								salidaDatos.writeObject("valido");
								System.out.println("valido");
								salidaDatos.writeObject("$:"+user.darUser());
								mensajesNuevos.lanzar("$:"+user.darUser());
								this.setName(user.getName());
								nivel++;
							} else {
								salidaDatos.writeObject("invalido");
								server.getListaHilos().remove(this);
								conectado = false;

							}
						} else {
							salidaDatos.writeObject("invalido");
							server.getListaHilos().remove(this);
							conectado = false;

						}
					} else {
						salidaDatos.writeObject("invalido");
						server.getListaHilos().remove(this);
						conectado = false;

					}

				} catch (Exception ex) {
					conectado = false;
				}

			} else if (nivel == 1) {
				try {
					mensajeRecibido = entradaDatos.readObject();
					
					String chats = "%";
					for (Chat chat : server.getListaChatTotal()) {
						String remitente = chat.getListaMensajes().get(0).getRemitente();
						String destinatario = chat.getListaMensajes().get(0).getDestinatario();
						if (user.getId().equals(remitente) || user.getId().equals(destinatario)) {

							for (Mensaje m : chat.getListaMensajes()) {
								chats += ":" + m.getRemitente() + ";" + m.getContenido() + ";" + m.getDestinatario()
										+ ";" + chat.getCodigo();
							}
						}
					}
					salidaDatos.writeObject(chats);
					
					String usuarios = "$";
					for (ConexionChat cc : server.getListaHilos()) {
							usuarios +=":"+cc.getUser().darUser();
					}
					salidaDatos.writeObject(usuarios);
					
					System.out.println("chats enviados a:" +user.getName());
					System.out.println("Usuarioa activos enviados a:" +user.getName());
					nivel++;
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					conectado=false;
				}
			} else if (nivel == 2) {
				try {
					// Lee un mensaje enviado por el cliente
					mensajeRecibido = entradaDatos.readObject();
					String[] palabras = ((String) mensajeRecibido).split(":");
					if (mensajeRecibido.equals("STOP")) {
						salidaDatos.writeObject("STOP");
						mensajesNuevos.lanzar("!:"+user.getId());
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						conectado = false;
						server.getListaHilos().remove(this);
						
						continue;
					} else if (palabras[0].equals("#")) {
						String[] subPalabras = palabras[1].split(";");
						System.out.println(Thread.currentThread().getName() + ":  " + mensajeRecibido);
						int posChat = server.posChat(subPalabras[3]);
						if (posChat == -1) {
							ArrayList<Mensaje> listaMensaje = new ArrayList<>();
							Chat chat = new Chat(subPalabras[3], listaMensaje);
							server.getListaChatTotal().add(chat);
						}
						Mensaje newMensaje = new Mensaje(
								"" + (server.getListaChatTotal().get(posChat).getListaMensajes().size() + 1),
								subPalabras[1], false);

						newMensaje.setRemitente(subPalabras[0]);
						newMensaje.setDestinatario(subPalabras[2]);
						server.getListaChatTotal().get(posChat).getListaMensajes().add(newMensaje);
						server.actualizarListaChats();
						salidaDatos.writeObject(mensajeRecibido);
						mensajesNuevos.lanzar(mensajeRecibido);

					}
				} catch (Exception e) {
					System.err.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.");
					System.err.println(e);
					conectado = false;
					server.getListaHilos().remove(this);
					mensajesNuevos.lanzar("?:"+"$:"+user.darUser());
					try {
						entradaDatos.close();
						salidaDatos.close();
					} catch (IOException ex2) {
						System.err.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
					}
				}
			}
		}
	}

	public String crearCodigo(Usuario userChat, Usuario user) {
		String codigo = "";
		if (userChat.getName().charAt(0) < user.getName().charAt(0)) {
			codigo += userChat.getName().charAt(0) + "" + user.getName().charAt(0);
		} else {
			codigo += user.getName().charAt(0) + "" + userChat.getName().charAt(0);
		}
		return codigo;
	}

	public boolean userActivo(String id) {
		for (ConexionChat cc : server.getListaHilos()) {
			if (!cc.equals(this)) {
				if (cc.getUser().getId().equals(id)) {
					return true;
				}
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
