/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicaServidor; 


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException; 

/**
 *
 * @author ACER
 */
public class LeerXml {
    
  
    private Document doc;

    public LeerXml(String ruta) throws SAXException, ParserConfigurationException, IOException {

    	File file = new File(ruta);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.parse(file);
    }
            
    public ArrayList<Chat> leerListaChats() {
        ArrayList<Chat> lista = new ArrayList<>();
        doc.getDocumentElement().normalize();
        
        NodeList list = doc.getElementsByTagName("chat");
        for (int i = 0; i < list.getLength(); i++) {
            Chat chat =null;
            ArrayList<Mensaje> historial = new ArrayList<>();
            Element chatElement = (Element) list.item(i);
            String codigo=chatElement.getAttribute("codigoChat");
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) list.item(i);
                NodeList listMensaje = element.getElementsByTagName("Mensaje");
                for (int j = 0; j < listMensaje.getLength(); j++) {
                    Element mensaje = (Element) listMensaje.item(j);
                    String codigoChat = mensaje.getAttribute("codigo");
                    String contenido = mensaje.getElementsByTagName("contenido").item(0).getTextContent();
                    String estadoVisto =mensaje.getElementsByTagName("estadoVisto").item(0).getTextContent();
                    String remitente = mensaje.getElementsByTagName("remitente").item(0).getTextContent();
                    String destinatario = mensaje.getElementsByTagName("destinatario").item(0).getTextContent();
                    String fecha=mensaje.getElementsByTagName("fecha").item(0).getTextContent();
                    Mensaje ms = new Mensaje(codigoChat, contenido,Boolean.parseBoolean(estadoVisto),fecha);
                    ms.setRemitente(remitente);
                    ms.setDestinatario(destinatario);
                    historial.add(ms);
                }
                
            }
            chat= new Chat(codigo, historial);
            lista.add(chat);
        }
        return lista;
    }
 
    public ArrayList<Usuario> leerListaUsuarios(){

        ArrayList<Usuario> lista = new ArrayList<>();
        doc.getDocumentElement().normalize();

        NodeList list = doc.getElementsByTagName("Usuario");
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) list.item(i);
                Usuario user;
                String id = element.getAttribute("id");
                String estado = element.getElementsByTagName("estado").item(0).getTextContent();
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                String contrasena = element.getElementsByTagName("contrasena").item(0).getTextContent();
                user = new Usuario(name, contrasena, id, estado);
                lista.add(user);
            }
        }
        return lista;
    }
    
    public Usuario leerUsuario() {
        
        doc.getDocumentElement().normalize();
        Element raiz = doc.getDocumentElement();
        Usuario user;
        String id =raiz.getAttribute("id");
        String estado = raiz.getElementsByTagName("estado").item(0).getTextContent();
        String name = raiz.getElementsByTagName("name").item(0).getTextContent();
        String image = raiz.getElementsByTagName("imagen").item(0).getTextContent();
        user = new Usuario(name, image, id, estado);
        return user;
    }
}
