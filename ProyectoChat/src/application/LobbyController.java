package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.ConectarServer;
import logica.TuberiaMensajes;
import logica.Usuario;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;



/**
 * FXML Controller class del lobby
 *
 * @author ACER
 */
public class LobbyController {

    private static Main p;
    private Stage stage;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        p = new Main();
    }

    public void setP(Main p) {
        LobbyController.p = p;
        inicio();
    }

    @FXML
    private Label etiqueta;

    @FXML
    private Button salir;

    @FXML
    private Button registro;

    @FXML
    private PasswordField tfContrasena;

    @FXML
    private TextField tfUsuario;

    /**
     * Este metodo me manda los datos para verificar en el servidor
     *
     * @param event
     */
    @FXML
    void eventoBoton(ActionEvent event) throws Exception {
        String usuario = tfUsuario.getText();
        String contrasena = tfContrasena.getText();

        boolean valido = verificarUsuarioLobby(usuario, contrasena);
        if (valido) {

            p.abrirMensajes();
            ocultar(event);
        } else {
            tfContrasena.setText("");
            tfUsuario.setText("");
            System.out.println("Usuario o contraseña incorrecto");
            tfContrasena.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Usuario o contraseña incorrecto");
            alert.showAndWait();

        }
    }

    public boolean verificarUsuarioLobby(String usuario, String contrasena) {
        try {

            ConectarServer conexion = conectarServer(usuario, contrasena);
            Thread hilo = new Thread(conexion);
            hilo.setName("Conexion");
            hilo.start();
            TuberiaMensajes tuberia = conexion.getNewMen();
            Object mensaje = tuberia.recoger();

            switch (((String) mensaje)) {
                case "valido":
                    System.out.println("*****");
                    mensaje = tuberia.recoger();
                    String[] palabras = ((String) mensaje).split(":");
                    if (palabras[0].equals("$")) {
                        String palabra = palabras[1];
                        System.out.println(palabra);
                        String subPalabras[] = palabra.split(";");

                        Usuario user = new Usuario(subPalabras[0], subPalabras[1], subPalabras[2], subPalabras[3]);
                        p.getChat().setUser(user);
                        return true;
                    }
                default:
                    hilo.stop();
                    return false;
            }

        } catch (NullPointerException e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
        return false;

    }

    /**
     * Con este metodo sale del lobby
     *
     * @param event
     */
    @FXML
    void eventoSalir(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Cerrar sesion");
        alert.setContentText("Gracias por usar este programa");
        alert.showAndWait();
        ocultar(event);
    }




        public ConectarServer conectarServer(String usuario, String contrasena) {
        TuberiaMensajes tuberia = new TuberiaMensajes();
        try {
            Socket socket = new Socket("192.168.1.18", 5560);
            ConectarServer conexion = new ConectarServer(socket, tuberia, usuario);
            p.getChat().setConexion(conexion);
            p.getChat().setTuberia(tuberia);
            p.getChat().setConectado(true);
            conexion.getSalida().writeObject("@:" + usuario + ":" + contrasena);
            return conexion;

        } catch (IOException ex) {
            System.err.println("No se pudo conectar al servidor");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se pudo conectar al servidor");
            alert.showAndWait();
        }
        return null;

    }

    /**
     * Este metodo oculta la ventana
     *
     * @param even Event
     * @throws Exception Este
     */
    public void ocultar(ActionEvent even) throws Exception {
        this.stage.close();
    }

    private void inicio() {
        etiqueta.setText("Bienvenido al portal ");
    }

    void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Cerrar sesion");
            alert.setContentText("Gracias por usar este progama ");
            alert.showAndWait();
            System.out.println("Fin");
        });
        ;
    }

}