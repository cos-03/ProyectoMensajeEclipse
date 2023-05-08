/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author ACER
 */
public class EscucharUsuarios extends Thread {

	private MensajesUsuarios mensajesUsuarios;
	private ArrayList<ConexionChat> listaHilos;
	private Servidor server;

	EscucharUsuarios(Servidor aThis) {
		this.server = aThis;
		mensajesUsuarios = server.getMensajesUsuarios();
		listaHilos = server.getListaHilos();
	}

	@Override
	public void run() {
		while (true) {
			Object obj = mensajesUsuarios.recoger();
			String[] listaPalabras = ((String) obj).split(":");
			int tamanio = listaPalabras.length;
			if (tamanio >= 2) {
				if(listaPalabras[0].equals("#")) {
					String subPalabras[]=listaPalabras[1].split(";");
					for (ConexionChat cc : listaHilos) {
						if (cc.isConectado()) {
							if (cc.getUser().getId().equals(subPalabras[2])) {
								try {
									cc.getSalidaDatos().writeObject(obj);
								} catch (IOException ex) {

								}
							}
						}
					}
				}
				else if(listaPalabras[0].equals("$")) {
					for (ConexionChat cc : listaHilos) {
						try {
							cc.getSalidaDatos().writeObject(obj);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else if(listaPalabras[0].equals("!")) {
					for (ConexionChat cc : listaHilos) {
						if (cc.isConectado()) {
							if (!cc.getUser().getId().equals(listaPalabras[1])) {
								try {
									cc.getSalidaDatos().writeObject(obj);
								} catch (IOException ex) {
									System.out.println(ex);
								}
								System.out.println(obj+" Enviado a "+cc.getUser().getName());
							}
						}
					}
				}
			} else {
				for (ConexionChat cc : listaHilos) {

					try {
						System.out.println(cc.getUser().getId());
						cc.getSalidaDatos().writeObject(obj);
					} catch (IOException ex) {

					}
				}
			}
		}
	}
}
