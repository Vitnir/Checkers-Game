
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CheckerServerGui extends Application {

	Button startServer;

	public static void main(String... args) {
		launch();
	}

	@Override
	public void start(Stage prime) throws Exception {
		createServerStage();
	}

	void createServerStage() {
		Label yourIp = new Label("Server IP: " + getOwnIp());
		yourIp.setStyle("-fx-text-fill: black; -fx-font-size: 20px;");
		VBox welcome = new VBox();
		welcome.setAlignment(Pos.CENTER);
		HBox port = new HBox();
		Label lPort = new Label("Port: 1099");
		port.getChildren().addAll(lPort);
		HBox ip = new HBox();
		Label status = new Label("offline");
		ip.getChildren().addAll(status);
		labelStyle(status, lPort);
		status.setStyle("-fx-text-fill: red;");
		HBox buttons = new HBox();
		startServer = new Button("Start Server");
		startServer.setOnAction(e -> startServer(status));
		startServer.setStyle("-fx-text-fill: white; -fx-background-color: red;");
		buttonStyle(startServer);
		boxStyle(buttons, ip, port);
		buttons.getChildren().addAll(startServer);
		welcome.getChildren().addAll(yourIp, ip, port, buttons);
		Scene scene = new Scene(welcome);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				System.exit(0);
			}
		});
		Image icon = new Image(getClass().getResource("/icons/crownb.png").toExternalForm());
		stage.getIcons().add(icon);
		stage.setY(0);
		stage.show();
	}

	public String getOwnIp() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

			String ip = in.readLine();
			return ip;
		} catch (Exception ex) {
			System.out.println("Error: can't get Ip.");
		}
		return "localhost";
	}

	void boxStyle(HBox... hboxes) {
		for (HBox hbox : hboxes) {
			hbox.setAlignment(Pos.CENTER);
		}
	}

	void buttonStyle(Button... buts) {
		for (Button but : buts) {
			but.setPrefWidth(100);
			but.setPrefHeight(25);
		}
	}

	void labelStyle(Label... labels) {
		for (Label label : labels) {
			label.setStyle("-fx-text-fill: Black; -fx-font-size: 20px;");
		}
	}

	void startServer(Label status) {
//		Thread thread;
//		thread = new Thread() {
//
//		};
//		thread.start();
		CheckerServer.start();
		status.setText("online");
		status.setStyle("-fx-text-fill: green;");
		startServer.setDisable(true);
		startServer.setText("running..");
		startServer.setStyle("-fx-text-fill: white; -fx-background-color: green;");
	}
}
