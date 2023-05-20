
package logica;

import java.io.*;
import java.net.*;

/**
 * Esta clase cumple la funcion conectarse al servidor
 *
 */
public class ConectarServer implements Runnable {

	private Socket socket;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private TuberiaMensajes tuberia;
	
	private boolean conectado;
	private int nivel;

	public ConectarServer(Socket socket, TuberiaMensajes tuberia,String usuario) {
		this.socket = socket;
		this.tuberia = tuberia;
		this.tuberia.setCs(this);
		conectado = true;
		nivel=0;
		try {
			this.salida = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Error al crear la salida del sacket: "+e.getMessage());
		}
	}
	
	

	public ObjectInputStream getEntrada() {
		return entrada;
	}
	
	public ObjectOutputStream getSalida() {
		return salida;
	}

	public TuberiaMensajes getNewMen() {
		return tuberia;
	}

	@Override
	public void run() {
		if (conectado) {
			try {
				System.err.println("Inicio hilo conexion chat");
				entrada = new ObjectInputStream(socket.getInputStream());
				while (conectado) {
					
					System.out.println("Esperar......");
					Object obj = entrada.readObject();
					String palabras[]=((String)obj).split(":");
					if(nivel==0) {
						tuberia.lanzarServer(obj);
						
						nivel++;
					
					}else if(nivel==1) {
						if(palabras[0].equals("*")) {
							if(palabras[1].equals("stop")) {
								tuberia.lanzarServer(obj);
								conectado=false;
								System.err.println("cerrrado");
								continue;
							}
						}else if(palabras[0].equals("#")) {
							tuberia.lanzarServer(obj);
						}else if(palabras[0].equals("%")) {
							tuberia.lanzarServer(obj);
						}else if(palabras[0].equals("$")) {
							tuberia.lanzarServer(obj);
						}
					}

				}

			} catch (Exception e) {
				tuberia.lanzarServer("*:desconectado");
				conectado=false;
			}finally {
				tuberia.lanzarServer("*:desconectado;");
				conectado=false;
			}
		}
	}

}
