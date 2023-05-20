
package logicaServidor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mensajes del chat
 * @author Felipe Valencia
 */
public class Mensaje{
    private String remitente;
    private String destinatario;
    private String codigoChat;
    private String contenido;
    private String fecha;
    private boolean visto; 
    
    public Mensaje(String id, String contenido,boolean vist,String fecha) {
        this.codigoChat = id;
        this.contenido = contenido;
        this.fecha =fecha;
        this.visto= vist;
    }

    public Mensaje(String id, String contenido,boolean vist) {
        this.codigoChat = id;
        this.contenido = contenido;
        DateFormat formato= new SimpleDateFormat("yyyy/MM/dd_hh/mm/ss");
        String f =""+formato.format(new Date().getTime());
        this.fecha=f;
        this.visto= vist;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getCodigoChat() {
        return codigoChat;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }
    
    public String getContenido() {
        return contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
    
    public String darMensaje() {
        return "#:"+remitente+";"+contenido+";"+destinatario+";"+fecha+";"+visto;
    }

    
    
    
    
    
    
}
