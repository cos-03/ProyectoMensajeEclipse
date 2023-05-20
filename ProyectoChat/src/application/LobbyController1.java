
package application;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.ConectarServer;
import logica.TuberiaMensajes;

/**
 * FXML Controller class del lobby
 *
 * @author Felipe Valencia
 */
public class LobbyController1 implements Initializable {

	private static Main1 p;
	private Stage stage;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		p = new Main1();
	}

	public void setP(Main1 p) {
		LobbyController1.p = p;
		inicio();
	}
	
	@FXML
    private Label etiqueta;

    @FXML
    private PasswordField tfContrasena;

    @FXML
    private TextField IdUsuario;

    @FXML
    private Button salir;

    @FXML
    private Button registrarse;

    @FXML
    private TextField registroName;

    @FXML
    private TextField otravezContrasena;

    @FXML
    private TextField contrasena;

    @FXML
    private TextField tfUsuario;

    @FXML
    private Button registro;


    @FXML
    void eventoBotonRegistrar(ActionEvent event) {
    	String nameUsuario=registroName.getText();
    	String idUsuario=IdUsuario.getText();
    	String contrasena=this.contrasena.getText();
    	String otraContrasena=otravezContrasena.getText();
    	if(!contrasena.equals("")&&contrasena.equals(otraContrasena)) {
    		if(!nameUsuario.equals("")&&!idUsuario.equals("")) {
    			registrarUsuario(nameUsuario, idUsuario, otraContrasena);
    			registroName.setText("");
    			IdUsuario.setText("");
    			this.contrasena.setText("");
    			otravezContrasena.setText("");
    		}else {
    			Alert alert = new Alert(Alert.AlertType.INFORMATION);
    			alert.setHeaderText(null);
    			alert.setTitle("Aviso");
    			alert.setContentText("Llene todos los espacios");
    			alert.showAndWait();
    		}
    	}else {
    		Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setTitle("Aviso");
			alert.setContentText("Llene todos los espacios");
			alert.showAndWait();
    	}

    }

	/**
	 * Este metodo me manda los datos para verificar en el servidor
	 *
	 */
	@FXML
	void eventoBotonEntrar(ActionEvent event) throws Exception {
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
	/*
	 * Verifica al usuario
	 */
	public boolean verificarUsuarioLobby(String usuario, String contrasena) {
		try {

			ConectarServer conexion = conectarServer(usuario, contrasena);
			Thread hilo = new Thread(conexion);
			hilo.setName("Conexion");
			hilo.start();
			TuberiaMensajes tuberia = conexion.getNewMen();
			if (tuberia != null) {
				Object mensaje = tuberia.recoger();
				if (mensaje != null) {
					String[] palabras = ((String) mensaje).split(":");
					if (palabras[0].equals("*")) {
						switch (palabras[1]) {
							case "autenticado":
								System.out.println("*****");
								p.getChat().setIdUser(usuario);
								return true;
							default:
								try {
									conexion.getSalida().writeObject("*:salio del programa");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								tuberia.recoger();
								return false;
						}
					} else
						return false;

				}
			}
		} catch (NullPointerException e) {
			System.err.println("Error: "+e.getMessage());
		}
		return false;

	}
	
	public void registrarUsuario(String usuario, String id , String contrasena) {
		try {

			ConectarServer conexion ;
			Socket socket = new Socket("localhost", 5560);
			TuberiaMensajes tuberia = new TuberiaMensajes();
			conexion = new ConectarServer(socket, tuberia, usuario);
			conexion.getSalida().writeObject("&:" + usuario+":"+id + ":" + contrasena);
			Thread hilo = new Thread(conexion);
			hilo.setName("Conexion");
			hilo.start();
			if (tuberia != null) {
				Object mensaje = tuberia.recoger();
				String[] palabras = ((String) mensaje).split(":");
				if (palabras[0].equals("*")) {
					Alert alert;
					switch (palabras[1]) {
					case "autenticado":
						alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText(null);
						alert.setTitle("Aviso");
						alert.setContentText("Registro exitoso");
						alert.showAndWait();
						conexion.getSalida().writeObject("*:stop");
						tuberia.recoger();
						return;
					default:
						alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText(null);
						alert.setTitle("Aviso");
						alert.setContentText("El registro no fue exitoso, ya existe este id: "+id);
						alert.showAndWait();
						conexion.getSalida().writeObject("*:stop");
						tuberia.recoger();
					}
				} else
					tuberia.EnviarMensaje("*:stop");

			}
		} catch (NullPointerException | IOException e) {
			System.err.println("Error: ");
			e.printStackTrace();
		}

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
		alert.setContentText("Gracias por usar este progama ");
		alert.showAndWait();
		ocultar(event);
	}
	
	/*
	 * Se conecta al servidor
	 */
	public ConectarServer conectarServer(String usuario, String contrasena) {
		TuberiaMensajes tuberia = new TuberiaMensajes();
		try {
			Socket socket = new Socket("localhost", 5560);
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
	public void ocultar(Event even) throws Exception {
		this.stage.close();
	}

	private void inicio() {
		etiqueta.setText("Bienvenido al mejor chat ");
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

	}

}