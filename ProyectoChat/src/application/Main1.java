package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import logica.Aplicacion;


public class Main1 extends Application {
	private Aplicacion chat;
	private Stage primaryStage;
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxml= new FXMLLoader(getClass().getResource("/vista/Lobby1.fxml"));
			AnchorPane root = (AnchorPane)fxml.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			this.primaryStage= primaryStage;
			LobbyController1 control= (LobbyController1)fxml.getController();
			control.setStage(primaryStage);
		} catch(Exception e) {
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
        primaryStage=stage;
        return fxmllLoader;
    }
	
	public void abrirMensajes() throws Exception {

        MensajeController1 mat = (MensajeController1) abrirVentana("/vista/MensajeView1.fxml").getController();
        mat.setStage(primaryStage);
        mat.setP(this);
    }
	
	public void abrirLobby() throws Exception {
        LobbyController1 mat = (LobbyController1) abrirVentana("/vista/Lobby1.fxml").getController();
        mat.setStage(primaryStage);
        mat.setP(this);
        
    }
	
	public void abrirModificar() throws Exception {
        ConfiguracionController mat = (ConfiguracionController) abrirVentana("/vista/ModificarView.fxml").getController();
        mat.setP(this);
        
    }

	public void abrirEliminar() throws Exception {
        ConfiguracionController mat = (ConfiguracionController) abrirVentana("/vista/EliminarView.fxml").getController();
        mat.setP(this);
        
    }
	
	public Main1() {
        chat = new Aplicacion();
    }

    public Aplicacion getChat() {
        return chat;
    }
    
}
