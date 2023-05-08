/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicaServidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ACER
 */
public class CrearXml {

    private String ruta;
    private Document doc;

    public CrearXml(String ruta) {
        try {
            this.ruta = ruta;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            Path path = Paths.get(ruta);
            if (!Files.exists(path)) {
                Formatter a = new Formatter(ruta);
                a.close();
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CrearXml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }

    }

    public void crearListaChat(ArrayList<Chat> lista) throws TransformerConfigurationException, TransformerException {
        Element rootElemen = doc.createElement("listaChat");
        doc.appendChild(rootElemen);
        for (int i = 0; i < lista.size(); i++) {
            Chat chat = lista.get(i);
            Element chatElement = doc.createElement("chat");
            chatElement.setAttribute("codigoChat", chat.getCodigo());
            
            rootElemen.appendChild(chatElement);
            ArrayList<Mensaje>historial=chat.getListaMensajes();
            for (int j = 0; j < historial.size(); j++) {
                Element mensaje = doc.createElement("Mensaje");
                chatElement.appendChild(mensaje);
                mensaje.setAttribute("codigo", historial.get(j).getCodigoChat());
                Element contenido = doc.createElement("contenido");
                contenido.setTextContent(historial.get(j).getContenido());
                mensaje.appendChild(contenido);
                
                Element fecha = doc.createElement("fecha");
                fecha.setTextContent("" + historial.get(j).getFecha());
                mensaje.appendChild(fecha);
                Element estado = doc.createElement("estadoVisto");
                estado.setTextContent("" + historial.get(j).isVisto());
                mensaje.appendChild(estado);
                Element remitente = doc.createElement("remitente");
                remitente.setTextContent("" + historial.get(j).getRemitente());
                mensaje.appendChild(remitente);
                Element destinatario = doc.createElement("destinatario");
                destinatario.setTextContent("" + historial.get(j).getDestinatario());
                mensaje.appendChild(destinatario);
            }
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource s = new DOMSource(doc);
        StreamResult r = new StreamResult(new File(ruta));
        t.transform(s, r);
        System.out.println("Proceso completo archivo actualizado");
    }

    public void crearUsuario(Usuario user) throws TransformerConfigurationException, TransformerException {
        Element rootElement = doc.createElement("Usuario");
        doc.appendChild(rootElement);
        rootElement.setAttribute("id", "" + user.getId());
        Element name = doc.createElement("name");
        name.setTextContent(user.getName());

        Element contrasena = doc.createElement("imagen");
        contrasena.setTextContent(user.getContrasena());
        rootElement.appendChild(contrasena);
        rootElement.appendChild(name);
        
        Element estado = doc.createElement("estado");
        estado.setTextContent(user.getEstado());
        rootElement.appendChild(estado);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource s = new DOMSource(doc);
        StreamResult r = new StreamResult(new File(ruta));
        t.transform(s, r);
        System.out.println("Proceso completo archivo actualizado");
    }

    public void crearListaUsuario(ArrayList<Usuario> lista) throws TransformerConfigurationException, TransformerException {
        Element rootElement = doc.createElement("ListaUsuarios");
        doc.appendChild(rootElement);
        for (Usuario user : lista) {
            Element usuario = doc.createElement("Usuario");

            usuario.setAttribute("id", "" + user.getId());
            rootElement.appendChild(usuario);
            Element name = doc.createElement("name");
            name.setTextContent(user.getName());
            usuario.appendChild(name);
            Element imagen = doc.createElement("imagen");
            imagen.setTextContent(user.getImage());
            usuario.appendChild(imagen);

            Element estado = doc.createElement("estado");
            estado.setTextContent(user.getEstado());
            usuario.appendChild(estado);

        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource s = new DOMSource(doc);
        StreamResult r = new StreamResult(new File(ruta));
        t.transform(s, r);
        System.out.println("Proceso completo");

    }
}
