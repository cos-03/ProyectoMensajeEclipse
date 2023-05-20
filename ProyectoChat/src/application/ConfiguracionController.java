package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ConfiguracionController implements Initializable {

	@FXML
	private TextField nameUsuario;

	@FXML
	private TextField contraNuevaOtra;

	@FXML
	private TextField idUsuario;

	@FXML
	private TextField contraAnti;

	@FXML
	private TextField contraNueva;

	private static Main1 p;

	public void setP(Main1 p) {
		ConfiguracionController.p = p;
	}

	@FXML
	void EventoModificarCuenta(ActionEvent event) {
		String nameUser = nameUsuario.getText();
		String idUser = idUsuario.getText();
		String contrasena = contraAnti.getText();
		String contrasenaNueva = this.contraNueva.getText();
		String otraContrasena = contraNuevaOtra.getText();

		if (!nameUser.equals("") && !contrasena.equals("")) {

			if (contrasena.equals(otraContrasena)) {
				if (contrasenaNueva.equals(otraContrasena)) {
					p.getChat().getTuberia()
							.EnviarMensaje("-+:" + idUser + ":" + contrasena + ":" + nameUser + ":" + contrasenaNueva);
				}
			} else {
				p.getChat().getTuberia().EnviarMensaje("-:" + idUser + ":" + contrasena + ":" + nameUser);
				
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setTitle("Aviso");
			alert.setContentText("Llene todos los espacios o al menos cambio de nombre, id Usuario y Contraseña");
			alert.showAndWait();
		}
		nameUsuario.setText("");
		idUsuario.setText("");
		contraAnti.setText("");
		contraNueva.setText("");
		contraNuevaOtra.setText("");
	}

	@FXML
	public void EventoEliminarCuenta() {
		String idUser = idUsuario.getText();
		String contrasena = contraAnti.getText();

		if (!idUser.equals("") && !contrasena.equals("")) {
			p.getChat().getTuberia().EnviarMensaje("-@:" + idUser + ":" + contrasena);
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setTitle("Aviso");
			alert.setContentText("Llene todos los espacios o al menos cambio de nombre, id Usuario y Contraseña");
			alert.showAndWait();
		}
		idUsuario.setText("");
		contraAnti.setText("");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		p = new Main1();
	}

}
