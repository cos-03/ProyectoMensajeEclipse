package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logica.*;

/**
 * FXML controller Class de la ventana MensajeView
 * 
 * @author Juan David Cortes Cortes
 * @author Santiago Acero Ospina
 */
public class MensajeController1 implements Initializable {

	private static Main1 p;
	private TuberiaMensajes tuberiaMensajes;
	private Stage stage;

	@FXML
	private FlowPane userList;

	@FXML
	private FlowPane mensajeList;

	@FXML
	private TextField mensaje;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private ScrollPane scrollPaneUsuario;

	@FXML
	private Label chat_name;

	@FXML
	private Pane panelMensaje;

	private ArrayList<Mensaje> listaMensaje;
	private static ArrayList<Usuario> listaUsuario;
	private boolean banUltimo = false;
	private boolean conectado;
	private String codigoChat;
	private Thread hilo;
	private ArrayList<String> codigos;
	private ConectarServer conexion;
	private String fecha = "";
	private int numeroMensajes = 10;
	private boolean masMensaje;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		p = new Main1();
	}

	public void setP(Main1 p) {
		MensajeController1.p = p;
		inicio();

	}

	/**
	 * Este medoto es un crea un mensaje y lo envia, y agrega a la lista de mensajes
	 * 
	 * @param event
	 */
	public void eventoBotonMensaje(ActionEvent event) {
		if (conectado) {
			String texto = mensaje.getText();
			int posChat = p.getChat().getBase().posChat(codigoChat);

			if (!mensaje.getText().isEmpty() && p.getChat().getUserChat() != null) {
				mensaje.setText("");
				if (posChat == -1) {
					listaMensaje.clear();
					Chat chat = new Chat(codigoChat, listaMensaje);
					p.getChat().getBase().llenarHistorial(chat);
				}

				tuberiaMensajes.EnviarMensaje(
						"#:" + p.getChat().getUser().getId() + ";" + texto + ";" + p.getChat().getUserChat().getId());

				int posUsuario = p.getChat().getBase().posUsuario(p.getChat().getUserChat().getId());
				String estado = p.getChat().getBase().getListaUsuarios().get(posUsuario).getEstado();
				if (estado.equals("Nuevo mensaje")) {
					p.getChat().getBase().getListaUsuarios().get(posUsuario).setEstado("activo");
					userList.getChildren().setAll();
					agragarUsuarios();
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setHeaderText(null);
				alert.setTitle("Aviso");
				alert.setContentText("No selecciono ningun chat ");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setTitle("Aviso");
			alert.setContentText("No puede enviar mensajes, No esta conectado al servidor ");
			alert.showAndWait();
			mensaje.setText("");
		}

	}

	/**
	 * Este evento cierra la ventana
	 * 
	 * @param event
	 * @throws Exception
	 */
	@FXML
	public void eventoSalir(ActionEvent event) throws Exception {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setTitle("Cerrar sesion");
		alert.setContentText("Gracias por usar este progama ");
		alert.showAndWait();
		p.abrirLobby();
		ocultar(event);
	}

	@FXML
	public void EventoModificarCuenta(ActionEvent event) throws Exception {
		p.abrirModificar();
	}

	@FXML
	public void EventoEliminarCuenta(ActionEvent event) throws Exception {
		p.abrirEliminar();
	}

	/**
	 * Este metodo crea los codigos para identificar el chat de dos usuarios
	 * 
	 *
	 * @return
	 */
	public String crearCodigo(String id1, String id2) {
		String codigo = "";
		if (compararId(id1, id2)) {
			codigo += id1 + "" + id2;
		} else {
			codigo += id2 + "" + id1;
		}
		return codigo;
	}

	/**
	 * Este metodo compara los id para generar el codigo de chat
	 * 
	 * @param id1
	 * @param id2
	 * @return
	 */
	public boolean compararId(String id1, String id2) {
		int id1tamanio = id1.length();
		int id2tamanio = id2.length();
		if (id1tamanio < id2tamanio) {
			for (int i = 0; i < id1tamanio; i++) {
				if (id1.charAt(i) != id2.charAt(i)) {
					if (id1.charAt(i) > id2.charAt(i)) {
						return false;
					} else
						return true;
				} else
					continue;
			}
			return true;
		} else {
			for (int i = 0; i < id2tamanio; i++) {
				if (id1.charAt(i) != id2.charAt(i)) {
					if (id1.charAt(i) > id2.charAt(i)) {
						return false;
					} else
						return true;
				} else
					continue;
			}
			return false;
		}
	}

	/**
	 * Este metodo crea una lista entera de todos los codigos de los chats
	 */
	public void crearListaCodigosChat() {
		for (Chat men : p.getChat().getBase().getListaChatTotal()) {
			if (men.getListaMensajes().get(0).getDestinatario() == p.getChat().getUser().getName()
					|| men.getListaMensajes().get(0).getRemitente() == p.getChat().getUser().getName()) {

				codigos.add(men.getCodigo());
			}

		}
	}

	/**
	 * Este medoto abre los chat al presionar con el mouse
	 * 
	 *
	 * @param id
	 */
	public void abrirChat(String id) {
		Usuario chat = p.getChat().getBase().darUsuario(id);
		p.getChat().setUserChat(chat);
		chat_name.setText(chat.getName());
		codigoChat = crearCodigo(chat.getId(), p.getChat().getIdUser());
		System.out.println(codigoChat);
		int posChat = p.getChat().getBase().posChat(codigoChat);
		System.out.println(posChat);
		if (posChat != -1) {
			listaMensaje = p.getChat().getBase().getListaChatTotal().get(posChat).getListaMensajes();
			mensajeList.getChildren().setAll();
			numeroMensajes = 10;
			masMensaje = true;
			int posUsuario = p.getChat().getBase().posUsuario(id);
			String estado = p.getChat().getBase().getListaUsuarios().get(posUsuario).getEstado();
			if (estado.equals("Nuevo mensaje")) {
				p.getChat().getBase().getListaUsuarios().get(posUsuario).setEstado("activo");
				userList.getChildren().setAll();
				agragarUsuarios();
			}
			actualizarMensajes();
			scrollPane.setVvalue(1);
		} else {
			mensajeList.getChildren().setAll();
		}
		panelMensaje.setVisible(true);
	}

	/**
	 * Este metodo muestra los usuarios
	 */
	public void agragarUsuarios() {
		String idUser = p.getChat().getIdUser();
		listaUsuario = p.getChat().getBase().getListaUsuarios();
		for (Usuario userOther : listaUsuario) {
			if (idUser.equals(userOther.getId())) {
				continue;
			}
			Image iconoImage = new Image(userOther.getImage());
			ImageView icono = new ImageView();
			icono.setImage(iconoImage);
			icono.setFitHeight(50);
			icono.setFitWidth(50);
			Label name = new Label(userOther.getName());
			name.setTextFill(Color.rgb(0, 0, 0));

			Label estado = new Label();
			estado.setPrefWidth(200);
			estado.setTextFill(Color.rgb(0, 0, 0));
			if (userOther.getEstado().equals("activo"))
				estado.setText("En linea");
			else if (userOther.getEstado().equals("Nuevo mensaje")) {
				estado.setText("Nuevo mensaje");
				estado.setTextFill(Color.rgb(29, 228, 22));
			} else
				estado.setText("Desconectado");

			VBox info = new VBox(8, name, estado);
			info.setPadding(new Insets(2, 0, 0, 8));

			HBox h = new HBox(icono, info);
			h.setPadding(new Insets(3, 3, 3, 3));
			h.setCursor(Cursor.HAND);
			h.setOnMouseClicked((event) -> {
				abrirChat(userOther.getId());
			});

			userList.getChildren().add(h);
		}
	}

	/**
	 * Este metodo muestra mensajes en la ventana de mensajes
	 * 
	 * @param newMensaje
	 */
	public void anadirMensajeBox(Mensaje newMensaje) {
		scrollPane.setHmax(0);
		logica.Usuario sender = p.getChat().getBase().darUsuario(newMensaje.getRemitente());
		boolean isMio = newMensaje.getRemitente().equals(p.getChat().getIdUser());
		if (isMio) {
			sender = p.getChat().getUser();
		}
		assert sender != null;

		// Pone la fecha em el chat
		if (!this.fecha.equals((newMensaje.getFecha().split("_")[0]))) {
			this.fecha = (newMensaje.getFecha().split("_")[0]);
			Label fecha = new Label((newMensaje.getFecha().split("_")[0]));
			fecha.setFont(new Font("", 11));
			fecha.setAlignment(Pos.CENTER);
			fecha.setPrefWidth(450);
			HBox mensajeFecha = new HBox();
			mensajeFecha.setPrefWidth(450);
			mensajeFecha.setPadding(new Insets(10, 5, 10, 5));
			mensajeFecha.getChildren().add(fecha);
			mensajeList.getChildren().add(mensajeFecha);

		}

		Image iconoImage = new Image(sender.getImage());
		ImageView icono = new ImageView();
		icono.setImage(iconoImage);
		icono.setFitHeight(40);
		icono.setFitWidth(40);

		Label mensajeBurbuja = new Label(newMensaje.getContenido());
		mensajeBurbuja.setWrapText(true);
		mensajeBurbuja.setMaxWidth(220);
		mensajeBurbuja.setPadding(new Insets(6));
		mensajeBurbuja.setFont(new Font(14));

		HBox.setMargin(mensajeBurbuja, new Insets(8, 0, 0, 0));

		Label fecha = new Label((newMensaje.getFecha().split("_")[1]));
		fecha.setFont(new Font("", 11));
		fecha.setAlignment(Pos.BASELINE_CENTER);

		double[] puntos;

		if (isMio) {
			puntos = new double[] { 0.0, 5.0, 10.0, 0.0, 10.0, 10.0 };
		} else {
			puntos = new double[] { 0.0, 0.0, 0.0, 10.0, 10.0, 5.0 };
		}
		Polygon triangulo = new Polygon(puntos);
		HBox mensajeBox = new HBox();
		mensajeBox.setPrefWidth(450);
		mensajeBox.setPadding(new Insets(10, 5, 10, 5));
		if (isMio) {
			triangulo.setFill(Color.rgb(179, 231, 244));
			mensajeBurbuja.setStyle("-fx-background-color: rgb(179,231,244); -fx-background-radius: 16px;");
			HBox.setMargin(triangulo, new Insets(15, 10, 0, 0));
			mensajeBox.getChildren().addAll(fecha, mensajeBurbuja, triangulo, icono);
			mensajeBox.setAlignment(Pos.TOP_RIGHT);

		} else {
			triangulo.setFill(Color.rgb(29, 228, 22));
			mensajeBurbuja.setStyle("-fx-background-color: rgb(29,228,22); -fx-background-radius: 16px;");
			HBox.setMargin(triangulo, new Insets(15, 0, 0, 10));
			mensajeBox.getChildren().addAll(icono, triangulo, mensajeBurbuja, fecha);
		}
		mensajeList.getChildren().add(mensajeBox);
		banUltimo = scrollPane.getVvalue() == 1.0;
		// scrollPane.setVvalue(1);

	}

	/**
	 * Este metodo se ejecuta como un hilo, y actualiza las lista de mensajes y
	 * usuarios
	 * 
	 * @throws Exception
	 */
	public void hiloActualizar() {
		while (tuberiaMensajes.getActivo() && conectado) {

			Object mensaje = tuberiaMensajes.recoger();
			String[] palabras = ((String) mensaje).split(":");

			if (palabras.length >= 2) {
				if (palabras[0].equals("#")) {
					String[] subPalabras = palabras[1].split(";");
					Usuario user = p.getChat().getBase().darUsuario(subPalabras[0]);
					Usuario user2 = p.getChat().getBase().darUsuario(subPalabras[2]);
					String codigo = crearCodigo(user.getId(), user2.getId());
					int pos = p.getChat().getBase().posChat(codigo);
					if (pos == -1) {
						Chat chat = new Chat(codigo, new ArrayList<Mensaje>());
						p.getChat().getBase().getListaChatTotal().add(chat);
						pos = p.getChat().getBase().posChat(codigo);
					}
					if (pos != -1) {
						Mensaje newMensaje = new Mensaje(
								"" + (p.getChat().getBase().getListaChatTotal().get(pos).getListaMensajes().size() + 1),
								subPalabras[1], subPalabras[3], subPalabras[4]);
						newMensaje.setRemitente(user.getId());
						newMensaje.setDestinatario(user2.getId());
						p.getChat().getBase().getListaChatTotal().get(pos).getListaMensajes().add(newMensaje);

					}
					Platform.runLater(() -> {
						if(p.getChat().getUserChat()!=null) {
							if(p.getChat().getUserChat().getId().equals(user.getId())||p.getChat().getIdUser().equals(user.getId())) {
								mensajeList.getChildren().setAll();
								actualizarMensajes();
								System.out.print("siisisisis");
							}else{
								int posUsuario = p.getChat().getBase().posUsuario(subPalabras[0]);
								p.getChat().getBase().getListaUsuarios().get(posUsuario).setEstado("Nuevo mensaje");
								userList.getChildren().setAll();
								agragarUsuarios();
							};
						}else {
							int posUsuario = p.getChat().getBase().posUsuario(subPalabras[0]);
							p.getChat().getBase().getListaUsuarios().get(posUsuario).setEstado("Nuevo mensaje");
							userList.getChildren().setAll();
							agragarUsuarios();
						}
					});
				} else if (palabras[0].equals("%")) {

					int count = 1;
					p.getChat().getBase().getListaChatTotal().clear();
					for (int i = 1; i < palabras.length; i++) {
						String palabra = palabras[i];
						String subPalabras[] = palabra.split(";");
						String codigo = crearCodigo(subPalabras[0], subPalabras[2]);
						int pos = p.getChat().getBase().posChat(codigo);
						Mensaje newMensaje = new Mensaje("" + (count), subPalabras[1], subPalabras[3], subPalabras[4]);
						newMensaje.setRemitente(subPalabras[0]);
						newMensaje.setDestinatario(subPalabras[2]);
						if (pos == -1) {
							Chat chat = new Chat(codigo, new ArrayList<Mensaje>());
							p.getChat().getBase().getListaChatTotal().add(chat);
							pos = p.getChat().getBase().posChat(codigo);
						}
						p.getChat().getBase().getListaChatTotal().get(pos).getListaMensajes().add(newMensaje);
						count++;
					}

				} else if (palabras[0].equals("$")) {
					p.getChat().getBase().getListaUsuarios().clear();
					for (int i = 1; i < palabras.length; i++) {
						String palabra = palabras[i];
						String subPalabras[] = palabra.split(";");

						Usuario user = new Usuario(subPalabras[0], subPalabras[1], subPalabras[2], subPalabras[3]);
						p.getChat().getBase().getListaUsuarios().add(user);
					}

					Usuario user = p.getChat().getBase().darUsuario(p.getChat().getIdUser());
					user.setImage(System.getProperty("user.dir") + "/src/documentos/avatar-user.png");
					p.getChat().setUser(user);
					Platform.runLater(() -> {
						userList.getChildren().setAll();
						agragarUsuarios();
					});

				} else if (palabras[0].equals("*")) {
					if (palabras[1].equals("stop")) {
						conectado = false;
						continue;
					}
					if (palabras[1].equals("desconectado")) {
						conectado = false;
						String mostrarAlerta = "Servidor desconectado";
						Platform.runLater(() -> {

							Alert alert = new Alert(Alert.AlertType.INFORMATION);
							alert.setHeaderText(null);
							alert.setTitle("Aviso");
							alert.setContentText("" + mostrarAlerta);
							alert.showAndWait();
						});

					}
				} else if (palabras[0].equals("-@")) {
					Platform.runLater(() -> {
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText(null);
						alert.setTitle("Aviso");
						alert.setContentText(" " + palabras[1]);
						alert.showAndWait();
					});
				} else if (palabras[0].equals("-") || palabras[0].equals("-+")) {
					Platform.runLater(() -> {
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText(null);
						alert.setTitle("Aviso");
						alert.setContentText("" + palabras[1]);
						alert.showAndWait();
					});
				}
			}
		}
	}

	/**
	 * Este metodo llena la lista de mensaje
	 * 
	 * @param codigo
	 */
	public void llenarMensajes(String codigo) {

		int posChat = p.getChat().getBase().posChat(codigo);
		System.out.println(posChat);
		if (posChat != -1) {
			listaMensaje = p.getChat().getBase().getListaChatTotal().get(posChat).getListaMensajes();
		} else {
			listaMensaje = null;
		}
	}

	/**
	 * Este metodo llena la lista de usuarios
	 *
	 */
	public void llenarUsuario() {
		listaUsuario = p.getChat().getBase().getListaUsuarios();
	}

	/**
	 * Este metodo oculta la ventana y cierra los hilos activos
	 * 
	 * @param even
	 * @throws Exception
	 */
	public void ocultar(Event even) throws Exception {

		tuberiaMensajes.EnviarMensaje("*:stop");
		System.out.println("Hilos cerrados");
		p.getChat().getBase().getListaChatTotal().clear();
		p.getChat().getBase().getListaUsuarios().clear();
		Stage stage;
		stage = (Stage) (((Node) (even.getSource())).getScene().getWindow());
		stage.close();
	}

	/**
	 * Este metodo actualiza los mensajes
	 */
	public void actualizarMensajes() {

		if (listaMensaje.size() > numeroMensajes) {
			Label fecha = new Label("Mostrar mas");
			fecha.setFont(new Font("", 11));
			fecha.setAlignment(Pos.CENTER);
			fecha.setPrefWidth(450);
			HBox mensajeFecha = new HBox();
			mensajeFecha.setPrefWidth(450);
			mensajeFecha.setPadding(new Insets(10, 5, 10, 5));
			mensajeFecha.getChildren().add(fecha);
			mensajeFecha.setOnMouseClicked((event) -> {

				numeroMensajes += 10;
				mensajeList.getChildren().setAll();
				masMensaje = false;
				actualizarMensajes();

			});
			mensajeList.getChildren().add(mensajeFecha);
		}
		if (numeroMensajes > listaMensaje.size()) {
			for (Mensaje newMensaje : listaMensaje) {
				anadirMensajeBox(newMensaje);
				if (masMensaje)
					scrollPane.setVvalue(1);
			}
		} else {
			for (int i = listaMensaje.size() - numeroMensajes; i < listaMensaje.size(); i++) {
				Mensaje newMensaje = listaMensaje.get(i);
				anadirMensajeBox(newMensaje);
				if (masMensaje)
					scrollPane.setVvalue(1);
			}
		}

		this.fecha = "";
	}

	/**
	 * Este metodo inicia los datos
	 */
	public void inicio() {
		listaMensaje = new ArrayList<>();
		listaUsuario = new ArrayList<>();
		codigos = new ArrayList<>();
		conectado = p.getChat().isConectado();
		System.out.println(p.getChat().getUser());
		userList.setPadding(new Insets(5));

		scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {

			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (banUltimo) {
					scrollPane.setVvalue(1.0);
					banUltimo = false;
				}
			}

		});
		conectarServer();
		llenarUsuario();
		agragarUsuarios();
		System.out.println("Inicio Mensaje controller correctamente");
	}

	/**
	 * Este metodo se conecta al servidor
	 */
	public void conectarServer() {
		if (conectado) {
			conexion = p.getChat().getConexion();
			tuberiaMensajes = conexion.getNewMen();
			if (conexion != null) {
				hilo = new Thread(this::hiloActualizar);
				hilo.setName("AltualizarMensajes");
				hilo.start();
				System.out.println("Conectado");
			} else
				System.err.println("Error al conectar");
		} else
			System.out.println("No conectado");
	}

	public ArrayList<Mensaje> getListaMensaje() {
		return listaMensaje;
	}

	public void setStage(Stage primaryStage) {
		this.stage = primaryStage;
		stage.setOnCloseRequest((event) -> {

			tuberiaMensajes.EnviarMensaje("*:stop");
			System.out.println("Hilos cerrados");

			p.getChat().getBase().getListaChatTotal().clear();
			p.getChat().getBase().getListaUsuarios().clear();
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
