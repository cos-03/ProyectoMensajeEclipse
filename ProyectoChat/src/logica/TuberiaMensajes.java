package logica;

import java.io.IOException;
/**
 * Esta clase cumple la funcion tuberia entre conectarChat y el hilo actualizar.
 * @author Felipe Valencia
 */
public class TuberiaMensajes {

	private boolean mensajeNuevo = false;
	private ConectarServer cs;
	private Boolean activo = true;
	private BaseDatosMensaje base;

	private Object mensaje;

	public TuberiaMensajes() {
		super();
		
	}

	public synchronized Object recoger() {
		System.out.println("Esperando...");
		while (mensajeNuevo == false) {
			try {
				wait();
			} catch (InterruptedException e) {

			}
		}
		System.out.println("Objeto recogido");
		System.out.println(mensaje);
		mensajeNuevo=false;
		notify();
		return mensaje;
	}

	public synchronized void lanzarServer(Object mensaje) {
		System.out.println("Objecto lanzado");
		while (mensajeNuevo == true) {
			try {
				wait();
			} catch (InterruptedException e) {

			}
		}
		this.mensaje=mensaje;
		mensajeNuevo = true;
		notify();
	}

	public void EnviarMensaje(Object mensaje) {
		System.out.println("Enviando: " + mensaje);
		try {
			cs.getSalida().writeObject(mensaje);
		} catch (IOException ex) {
		}
	}

	public void setCs(ConectarServer cs) {
		this.cs = cs;
	}

	public Boolean getActivo() {
		return activo;
	}

	public boolean isMensajeNuevo() {
		return mensajeNuevo;
	}

	public void setMensajeNuevo(boolean mensajeNuevo) {
		this.mensajeNuevo = mensajeNuevo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public BaseDatosMensaje getBase() {
		return base;
	}

	public void setBase(BaseDatosMensaje base) {
		this.base = base;
	}
}
