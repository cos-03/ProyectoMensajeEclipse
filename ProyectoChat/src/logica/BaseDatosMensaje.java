/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;

/**
 * @author ACER
 */
public class BaseDatosMensaje {

    private ArrayList<Chat> listaChatTotal;
    private ArrayList<Usuario> listaUsuarios;
    private Usuario user;
    private TuberiaMensajes newMen;

    public BaseDatosMensaje() {

        iniciarDatos();

    }

    public void iniciarDatos() {
        listaChatTotal = new ArrayList<>();
        listaUsuarios = new ArrayList<>();
    }

    public void llenarHistorial(Chat chat) {
        if (listaChatTotal.isEmpty() || chat.getListaMensajes().isEmpty()) {
            listaChatTotal.add(chat);
        } else {
            boolean ban = false;
            for (Chat lista : listaChatTotal) {
                if (lista.getCodigo().equals(chat.getCodigo())) {
                    lista.setListaMensajes(chat.getListaMensajes());
                    ban = true;
                }
            }
            if (!ban) {
                listaChatTotal.add(chat);
            }

        }
    }

    public int posChat(String codigoChat) {
        int pos = 0;

        for (Chat chat : listaChatTotal) {
            if (chat.getCodigo().equals(codigoChat)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public Chat darChat(String codigoChat) {

        for (Chat chat : listaChatTotal) {
            if (chat.getCodigo().equals(codigoChat)) {
                return chat;
            }
        }
        return null;
    }

    public void actualizarListaChats() {

        CrearXml c = new CrearXml(System.getProperty("user.dir") + "/src/documentos/ListaChat.xml");
        try {
            c.crearListaChat(listaChatTotal);
            System.out.println("listaChat actualizada");
        } catch (TransformerException ex) {
            System.out.println("Error lista no actualizada");
        }
    }

    public void actualizarUsuario() {

        CrearXml c = new CrearXml(System.getProperty("user.dir") + "/src/documentos/Usuario.xml");
        try {
            c.crearUsuario(user);
            System.out.println("Usuario actualizada");
        } catch (TransformerException ex) {
            System.out.println("Error lista no actualizada");
        }
    }

    public void actualizarListaUsuario() {

        CrearXml c = new CrearXml(System.getProperty("user.dir") + "/src/documentos/ListaUsuario.xml");
        try {
            c.crearListaUsuario(listaUsuarios);
            System.out.println("listaUsuarios actualizada");
        } catch (TransformerException ex) {
            System.out.println("Error lista no actualizada");
        }
    }

    public int posUsuario(String id) {

        int pos = 0;
        for (Usuario user : listaUsuarios) {
            if (user.getId().equals(id)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public Usuario darUsuario(String id) {

        for (Usuario user : listaUsuarios) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public boolean getEstadoUltimoMensaje(String codigo) {
        ArrayList<Mensaje> lista = darChat(codigo).getListaMensajes();
        int pos = lista.size() - 1;
        return lista.get(pos).isVisto();
    }

    public int posMensaje(String codigoChat) {
        int pos = listaChatTotal.get(posChat(codigoChat)).getListaMensajes().size() - 1;
        return pos;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public ArrayList<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public ArrayList<Chat> getListaChatTotal() {
        return listaChatTotal;
    }

    public void setListaUsuarios(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public TuberiaMensajes getNewMen() {
        return newMen;
    }

}
