/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

/**
 * @author ACER
 */
public class Usuario {
    private String name;
    private String ip;
    private String image;
    private String id;
    private String estado;

    public Usuario(String name, String id, String estado) {
        this.name = name;
        this.image = "C:\\Users\\ACER\\eclipse-workspace\\ProyectoMensajeEclipse\\src\\documentos/Enviado_1.jpg";
        this.id = id;
        this.estado = estado;
    }

    public Usuario(String name, String ip, String id, String estado) {
        this.name = name;
        this.ip = ip;
        this.id = id;
        this.estado = estado;
        this.image = "C:\\Users\\ACER\\eclipse-workspace\\ProyectoMensajeEclipse\\src\\documentos/Enviado_1.jpg";
    }


    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Usuario{" + "name=" + name + ", ip=" + ip + ", image=" + image + ", id=" + id + ", estado=" + estado + ", historialMensajes=" + '}';
    }


}
