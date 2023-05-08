package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import logica.Aplicacion;
import javafx.stage.Stage;
import java.io.IOException;



public class Main extends Application {
    private final Aplicacion chat;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/vista/Lobby.fxml"));
            AnchorPane root = (AnchorPane) fxml.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            this.primaryStage = primaryStage;
            LobbyController control = (LobbyController) fxml.getController();
            control.setStage(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public FXMLLoader abrirVentana(String ulr) throws IOException {
        FXMLLoader fxmllLoader = new FXMLLoader(getClass().getResource(ulr));
        AnchorPane root = (AnchorPane) fxmllLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        stage.setTitle("Chat");
        primaryStage = stage;
        return fxmllLoader;
    }

    public void abrirMensajes() throws Exception {

        MensajeController mat = (MensajeController) abrirVentana("/vista/MensajeView.fxml").getController();
        mat.setStage(primaryStage);
        mat.setP(this);
    }

    public void abrirLobby() throws Exception {
        LobbyController mat = (LobbyController) abrirVentana("/vista/Lobby.fxml").getController();
        mat.setStage(primaryStage);
        mat.setP(this);

    }


    public Main() {
        chat = new Aplicacion();
    }

    public Aplicacion getChat() {
        return chat;
    }

}
