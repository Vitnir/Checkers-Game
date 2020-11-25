package task1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeStage {
	Stage prime;
	Stage stage;

	WelcomeStage(Stage prime) {
		this.prime = prime;
		createWelcomeStage();
	}

	void createWelcomeStage() {
		Label yourIp = new Label("Your IP: " + getOwnIp());
		yourIp.setStyle("-fx-text-fill: black; -fx-font-size: 20px;");
		VBox welcome = new VBox();
		welcome.setAlignment(Pos.CENTER);
		HBox port = new HBox();
		TextField tPort = new TextField();
		Label lPort = new Label("Port: ");
		port.getChildren().addAll(lPort, tPort);
		HBox ip = new HBox();
		TextField tServer = new TextField();
		Label lIp = new Label("IP: ");
		ip.getChildren().addAll(lIp, tServer);
		labelStyle(lIp, lPort);
		HBox buttons = new HBox();
//		Button startServer = new Button("Start Server");
//		startServer.setOnAction(e -> startServer(tServer.getText(), tPort.getText()));
//		startServer.setStyle("-fx-text-fill: white; -fx-background-color: red;");
		Button start = new Button("Join Game");
		start.setOnAction(e -> joinGame(tServer.getText(), tPort.getText()));
		buttonStyle(start);
		boxStyle(buttons, ip, port);
		buttons.getChildren().addAll(start);
		welcome.getChildren().addAll(yourIp, ip, port, buttons);
		Scene scene = new Scene(welcome);
		stage = new Stage();
		stage.setScene(scene);
		Image icon = new Image(getClass().getResource("/icons/crown.png").toExternalForm());
		stage.getIcons().add(icon);
		stage.show();
	}

	void joinGame(String server, String port) {
		if (server.equals("")) {
			server="localhost";
		} if (port.equals("")) {
			port="1099";
		}
		Board board = new Board(prime, false,server,port);
		stage.close();
		board.start();
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
}
