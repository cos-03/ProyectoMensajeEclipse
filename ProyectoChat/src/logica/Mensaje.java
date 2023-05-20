
package logica;


/**
 * Mensajes del chat
 * @author Juan David Cortes Cortes
 * @author Santiago Acero Ospina
 */
public class Mensaje{
    private String remitente;
    private String destinatario;
    private String codigoChat;
    private String contenido;
    private String fecha;
    private String visto; 
    
    public Mensaje(String id, String contenido, String fecha, String vist) {
        this.codigoChat = id;
        this.contenido = contenido;
        String dataHora[]=fecha.split("_");
        String hora[]=dataHora[1].split("/");
        this.fecha=dataHora[0]+"_"+hora[0]+":"+hora[1];
        this.visto= String.valueOf(vist);
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
        return Boolean.parseBoolean(visto);
    }

    public void setVisto(String visto) {
        this.visto = visto;
    }
    
    public String darMensaje() {
        return "#:"+remitente+";"+contenido+";"+destinatario+";"+fecha+";"+visto;
    }






    
}
