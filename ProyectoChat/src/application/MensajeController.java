package application;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logica.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MensajeController implements Initializable {

    private static Main p;
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

    private ArrayList<Mensaje> listaMensaje;
    private static ArrayList<Usuario> listaUsuario;
    private boolean banUltimo = false;
    private boolean conectado;
    private String codigoChat;
    private Thread hilo;
    private ArrayList<String> codigos;
    private ConectarServer conexion;
    private boolean inicio = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        p = new Main();
    }

    public void setP(Main p) {
        MensajeController.p = p;
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
                Mensaje newMensaje = new Mensaje(
                        "" + (p.getChat().getBase().getListaChatTotal().get(posChat).getListaMensajes().size() + 1),
                        texto, false);

                newMensaje.setRemitente(p.getChat().getUser().getId());
                newMensaje.setDestinatario(p.getChat().getUserChat().getId());
                tuberiaMensajes.EnviarMensaje("#:" + p.getChat().getUser().getName() + ";" + newMensaje.getContenido()
                        + ";" + p.getChat().getUserChat().getId() + ";" + codigoChat);
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

    /**
     * Este metodo crea los codigos para identificar el chat de dos usuarios
     *
     * @param userChat
     * @param user
     * @return
     */
    public String crearCodigo(Usuario userChat, Usuario user) {
        String codigo = "";
        if (userChat.getName().charAt(0) < user.getName().charAt(0)) {
            codigo += userChat.getName().charAt(0) + "" + user.getName().charAt(0);
        } else {
            codigo += user.getName().charAt(0) + "" + userChat.getName().charAt(0);
        }
        return codigo;
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
     * @param event
     * @param id
     */
    public void abrirChat(Event event, String id) {
        Usuario chat = p.getChat().getBase().darUsuario(id);
        p.getChat().setUserChat(chat);
        chat_name.setText(chat.getName());
        codigoChat = crearCodigo(chat, p.getChat().getUser());
        System.out.println(codigoChat);
        int posChat = p.getChat().getBase().posChat(codigoChat);
        System.out.println(posChat);
        if (posChat != -1) {
            listaMensaje = p.getChat().getBase().getListaChatTotal().get(posChat).getListaMensajes();

            System.out.println(listaMensaje.toString());
            mensajeList.getChildren().setAll();
            actualizarMensajes();
            scrollPane.setVvalue(1);
            int pos = listaUsuario.indexOf(chat);
            if (pos != -1) {
                p.getChat().getBase().getListaUsuarios().get(pos).setEstado("En linea");
                int posMensaje = p.getChat().getBase().posMensaje(codigoChat);
                if (posMensaje != -1)
                    p.getChat().getBase().getListaChatTotal().get(posChat).getListaMensajes().get(posMensaje)
                            .setVisto(true);
            }
            userList.getChildren().setAll();
            agragarUsuarios();
        } else {
            mensajeList.getChildren().setAll();
        }

    }

    /**
     * Este metodo muestra los usuarios
     */
    public void agragarUsuarios() {
        Usuario user = p.getChat().getUser();
        listaUsuario = p.getChat().getBase().getListaUsuarios();
        for (Usuario userOther : listaUsuario) {
            if (user.getId().equals(userOther.getId())) {
                continue;
            }
            String codigo = crearCodigo(userOther, user);

            Image iconoImage = new Image(userOther.getImage());
            ImageView icono = new ImageView();
            icono.setImage(iconoImage);
            icono.setFitHeight(50);
            icono.setFitWidth(50);
            Label name = new Label(userOther.getName());
            name.setTextFill(Color.rgb(255, 255, 255));
            Label estado = new Label(userOther.getEstado());
            estado.setPrefWidth(200);
            if (inicio) {
                boolean estadoMensaje = p.getChat().getBase().getEstadoUltimoMensaje(codigo);

                if (!estadoMensaje) {
                    estado.setTextFill(Color.rgb(29, 228, 22));
                    estado.setText("Nuevo mensaje");
                } else {
                    estado.setTextFill(Color.rgb(255, 255, 255));
                }

                inicio = true;
            }
            VBox info = new VBox(8, name, estado);
            info.setPadding(new Insets(2, 0, 0, 8));

            HBox h = new HBox(icono, info);
            h.setPadding(new Insets(3, 3, 3, 3));
            h.setCursor(Cursor.HAND);
            h.setOnMouseClicked((event) -> {
                abrirChat(event, userOther.getId());
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
        Usuario sender = p.getChat().getBase().darUsuario(newMensaje.getRemitente());
        boolean isMio = newMensaje.getRemitente().equals(p.getChat().getUser().getId());
        if (isMio) {
            sender = p.getChat().getUser();
        }
        assert sender != null;
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

        double[] puntos;

        if (isMio) {
            puntos = new double[]{0.0, 5.0, 10.0, 0.0, 10.0, 10.0};
        } else {
            puntos = new double[]{0.0, 0.0, 0.0, 10.0, 10.0, 5.0};
        }
        Polygon triangulo = new Polygon(puntos);
        HBox mensajeBox = new HBox();
        mensajeBox.setPrefWidth(450);
        mensajeBox.setPadding(new Insets(10, 5, 10, 5));
        if (isMio) {
            triangulo.setFill(Color.rgb(179, 231, 244));
            mensajeBurbuja.setStyle("-fx-background-color: rgb(179,231,244); -fx-background-radius: 16px;");
            HBox.setMargin(triangulo, new Insets(15, 10, 0, 0));
            mensajeBox.getChildren().addAll(mensajeBurbuja, triangulo, icono);
            mensajeBox.setAlignment(Pos.TOP_RIGHT);

        } else {
            triangulo.setFill(Color.rgb(29, 228, 22));
            mensajeBurbuja.setStyle("-fx-background-color: rgb(29,228,22); -fx-background-radius: 16px;");
            HBox.setMargin(triangulo, new Insets(15, 0, 0, 10));
            mensajeBox.getChildren().addAll(icono, triangulo, mensajeBurbuja);
        }
        mensajeList.getChildren().add(mensajeBox);
        banUltimo = scrollPane.getVvalue() == 1.0;

    }

    /**
     * Este metodo se ejecuta como un hilo, y actualiza las lista de mensajes y
     * usuarios
     */
    public void hiloActualizar() {
        while (tuberiaMensajes.getActivo() && conectado) {

            Object mensaje = tuberiaMensajes.recoger();
            if (mensaje.equals("STOP")) {
                conectado = false;
                continue;
            }
            String[] palabras = ((String) mensaje).split(":");
            if (palabras.length >= 2) {
                if (palabras[0].equals("#")) {
                    String[] subPalabras = palabras[1].split(";");
                    Usuario user = p.getChat().getBase().darUsuario(subPalabras[0]);
                    Usuario user2 = p.getChat().getBase().darUsuario(subPalabras[2]);
                    String codigo = subPalabras[3];
                    int pos = p.getChat().getBase().posChat(codigo);
                    if (pos != -1) {
                        Mensaje newMensaje = new Mensaje(
                                "" + (p.getChat().getBase().getListaChatTotal().get(pos).getListaMensajes().size() + 1),
                                subPalabras[1], false);
                        newMensaje.setRemitente(user.getId());
                        newMensaje.setDestinatario(user2.getId());
                        p.getChat().getBase().getListaChatTotal().get(pos).getListaMensajes().add(newMensaje);
                        p.getChat().getBase().actualizarListaChats();
                    }
                    Platform.runLater(() -> {
                        userList.getChildren().setAll();
                        mensajeList.getChildren().setAll();
                        actualizarMensajes();
                        agragarUsuarios();
                    });
                } else if (palabras[0].equals("%")) {
                    int count = 1;
                    for (int i = 1; i < palabras.length; i++) {
                        String palabra = palabras[i];
                        String subPalabras[] = palabra.split(";");
                        String codigo = subPalabras[3];
                        int pos = p.getChat().getBase().posChat(codigo);
                        Mensaje newMensaje = new Mensaje("" + (count), subPalabras[1], false);
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
                    p.getChat().getBase().actualizarListaChats();

                    Platform.runLater(() -> {
                        userList.getChildren().setAll();
                        mensajeList.getChildren().setAll();
                        actualizarMensajes();
                        agragarUsuarios();
                    });
                } else if (palabras[0].equals("$")) {

                    for (int i = 1; i < palabras.length; i++) {
                        String palabra = palabras[i];
                        String subPalabras[] = palabra.split(";");

                        Usuario user = new Usuario(subPalabras[0], subPalabras[1], subPalabras[2], subPalabras[3]);
                        p.getChat().getBase().getListaUsuarios().add(user);
                    }

                    Platform.runLater(() -> {
                        userList.getChildren().setAll();
                        mensajeList.getChildren().setAll();
                        actualizarMensajes();
                        agragarUsuarios();
                    });
                } else if (palabras[0].equals("!")) {
                    String palabra = palabras[1];
                    System.out.println(palabra);

                    int pos = p.getChat().getBase().posUsuario(palabra);
                    p.getChat().getBase().getListaUsuarios().remove(pos);

                    Platform.runLater(() -> {
                        userList.getChildren().setAll();
                        mensajeList.getChildren().setAll();
                        actualizarMensajes();
                        agragarUsuarios();
                    });
                }
            } else {
                System.out.println(mensaje);
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Aviso");
                    alert.setContentText((String) mensaje);
                    alert.showAndWait();
                });
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
    public void ocultar(ActionEvent even) throws Exception {

        tuberiaMensajes.EnviarMensaje("STOP");
        System.out.println("Hilos cerrados");
        int count = 0;
        for (Chat chat : p.getChat().getBase().getListaChatTotal()) {
            p.getChat().getBase().getListaChatTotal().get(count).getListaMensajes().clear();
        }
        p.getChat().getBase().actualizarListaChats();
        Stage stage;
        stage = (Stage) (((Node) (even.getSource())).getScene().getWindow());
        stage.close();
    }

    /**
     * Este metodo actualiza los mensajes
     */
    public void actualizarMensajes() {
        for (Mensaje newMensaje : listaMensaje) {
            anadirMensajeBox(newMensaje);
        }
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
                tuberiaMensajes.EnviarMensaje("&");
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

            tuberiaMensajes.EnviarMensaje("STOP");
            System.out.println("Hilos cerrados");
            int count = 0;
            for (Chat chat : p.getChat().getBase().getListaChatTotal()) {
                p.getChat().getBase().getListaChatTotal().get(count).getListaMensajes().clear();
            }
            p.getChat().getBase().actualizarListaChats();
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
