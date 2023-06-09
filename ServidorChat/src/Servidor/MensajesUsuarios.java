
package Servidor;

/**
 * Esta clase cumple la funcion tuberia entre ConexionChat y EscucharUsuario.
 * @author Felipe Valencia
 */
public class MensajesUsuarios {
    
	private boolean mensajeNuevo=false;
	
	private Object mensaje;
	
	public synchronized Object recoger() {
		while(mensajeNuevo==false) {
			try {
				wait();
			}catch (InterruptedException e) {
				
			}
		}
		mensajeNuevo=false;
		notify();
		return mensaje;
	}
	
	public synchronized void lanzar(Object mensaje) {
		while(mensajeNuevo==true) {
			try {
				wait();
			}catch (InterruptedException e) {
				
			}
		}
		this.mensaje=mensaje;
		mensajeNuevo=true;
		notify();
	}

    public boolean isMensajeNuevo() {
        return mensajeNuevo;
    }
        
        
}