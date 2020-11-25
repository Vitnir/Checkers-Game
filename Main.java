package task1;

import java.rmi.registry.Registry;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	Board opponent;
	Registry reg;

	public void start(Stage prime) throws InterruptedException {
		WelcomeStage stage = new WelcomeStage(prime);
		stage.toString();
	}

	public static void main(String... args) {
		launch();
	}

}
