/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author ACER
 */
public class Mensaje {
    private String remitente;
    private String destinatario;
    private String codigoChat;
    private String contenido;
    private String fecha;
    private boolean visto;

    public Mensaje(String id, String contenido, boolean vist, String fecha) {
        this.codigoChat = id;
        this.contenido = contenido;
        this.fecha = fecha;
        this.visto = vist;
    }

    public Mensaje(String id, String contenido, boolean vist) {
        this.codigoChat = id;
        this.contenido = contenido;
        this.fecha = "" + new Timestamp(new Date().getTime());
        this.visto = vist;
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

    @Override
    public String toString() {
        return "Mensaje{" + "remitente=" + remitente + ", destinatario=" + destinatario + ", codigoChat=" + codigoChat + ", contenido=" + contenido + ", fecha=" + fecha + '}';
    }


}
