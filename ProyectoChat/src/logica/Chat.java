/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import java.util.ArrayList;

/**
 * @author ACER
 */
public class Chat {
    private String codigo;
    private ArrayList<Mensaje> listaMensajes;

    public Chat(String codigo, ArrayList<Mensaje> listaMensajes) {
        this.codigo = codigo;
        this.listaMensajes = listaMensajes;
    }

    public String getCodigo() {
        return codigo;
    }

    public ArrayList<Mensaje> getListaMensajes() {
        return listaMensajes;
    }

    public void setListaMensajes(ArrayList<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }


}
