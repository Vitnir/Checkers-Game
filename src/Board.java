
import java.rmi.RemoteException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Board {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	ArrayList<VBox> fields = new ArrayList<VBox>();
	ArrayList<Button> stones = new ArrayList<Button>();
	ArrayList<HBox> boxes = new ArrayList<HBox>();
	boolean debug = true;
	Stage prime;
	BorderPane bpane;
	VBox board;
	double max;
	double width;
	double height;
	double butSize;
	int fieldSize = 8;
	Logic logic;
	boolean amZug = true;
	String plOne = "white";
	String plTwo = "black";

	public Board(Stage prime, boolean debug) {
		this.logic = new Logic("white", "black", this);
		this.prime = prime;
		this.debug = debug;
		createBoard(prime);
	}

	public Board(Stage prime, boolean debug, String ip, String port) {
		this.logic = new Logic("white", "black", this, ip, port);
		this.prime = prime;
		this.debug = debug;
		createBoard(prime);
	}

	void start() {
		prime.show();
		propertyBinding();
		createStones();
		bpane.setLeft(createFieldNum());
		bpane.setBottom(createFieldChar());
	}

	MenuBar createMenueBar() {
		MenuBar menu = new MenuBar();
		Menu network = new Menu("Network");
		MenuItem serverStart = new MenuItem("Server");

		network.getItems().add(serverStart);
		Menu help = new Menu("Help");
		MenuItem info = new MenuItem("Info");

		Menu test = new Menu("Test");
		MenuItem reset = new MenuItem("Reset");
		MenuItem kingTest = new MenuItem("King Test");
		MenuItem win = new MenuItem("Win/Loose");
		MenuItem clear = new MenuItem("Clear");
		MenuItem printBoard = new MenuItem("Print Board");
		kingTest.setOnAction(e -> kingTest());
		win.setOnAction(e -> forceWin());
		reset.setOnAction(e -> reset());
		clear.setOnAction(e -> clear());
		printBoard.setOnAction(e -> printBoard());
		test.getItems().addAll(clear, reset, kingTest, win, printBoard);

		info.setOnAction(e -> infoStage());
		help.getItems().addAll(info);
		menu.getMenus().addAll(network, help);
		if (debug) {
			menu.getMenus().add(test);
		}
		return menu;
	}

	VBox createFieldNum() {
		VBox left = new VBox();
		if (debug) {
			for (int i = 0; i < 8; i++) {
				VBox field = new VBox(new Label("" + i));
				field.setPrefHeight((max / fieldSize));
				field.setPrefWidth((max / fieldSize) / 2);
				field.setStyle("-fx-background-color: grey;");
				field.setBorder(
						new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, null, new BorderWidths(2))));
				field.setAlignment(Pos.CENTER);
				left.getChildren().add(field);
			}
		} else {
			for (int i = 8; i > 0; i--) {
				VBox field = new VBox(new Label("" + i));
				field.setPrefHeight((max / fieldSize));
				field.setPrefWidth((max / fieldSize) / 2);
				field.setStyle("-fx-background-color: grey;");
				field.setAlignment(Pos.CENTER);
				left.getChildren().add(field);
			}
			for (Node x : left.getChildren()) {
				if (x instanceof VBox) {
					((VBox) x).prefHeightProperty().bind(prime.heightProperty().divide(fieldSize));
				}
			}
		}

		return left;
	}

	HBox createFieldChar() {
		HBox bot = new HBox();
		VBox first = new VBox();
		first.setPrefHeight((max / fieldSize) / 2);
		first.setPrefWidth((max / fieldSize) / 2);
		first.setStyle("-fx-background-color: grey;");
		first.setAlignment(Pos.CENTER);
		first.setMinWidth((max / fieldSize) / 2);
		first.setMaxWidth((max / fieldSize) / 2);
		if (debug)
			first.setBorder(
					new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, null, new BorderWidths(2))));
		bot.getChildren().add(first);
		if (debug) {
			for (int i = 0; i < 8; i++) {
				VBox field = new VBox(new Label("" + i));
				field.setPrefHeight((max / fieldSize) / 2);
				field.setPrefWidth((max / fieldSize));
				field.setStyle("-fx-background-color: grey;");
				field.setBorder(
						new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, null, new BorderWidths(2))));
				field.setAlignment(Pos.CENTER);
				bot.getChildren().add(field);
			}
		} else {
			char x = 'A';
			for (int i = 8; i > 0; i--) {
				VBox field = new VBox(new Label("" + x++));
				field.setPrefHeight((max / fieldSize) / 2);
				field.setPrefWidth((max / fieldSize));
				field.setStyle("-fx-background-color: grey;");
				field.setAlignment(Pos.CENTER);
				bot.getChildren().add(field);
			}
		}
		for (int i = 1; i < bot.getChildren().size(); i++) {
			if (bot.getChildren().get(i) instanceof VBox) {
				((VBox) bot.getChildren().get(i)).prefWidthProperty().bind(prime.widthProperty().divide(fieldSize));
			}
		}
		return bot;

	}

	void createBoard(Stage prime) {
		bpane = new BorderPane();
		board = new VBox();
		bpane.setTop(createMenueBar());
		bpane.setLeft(createFieldNum());

		Rectangle2D screen = Screen.getPrimary().getVisualBounds();
		width = screen.getMaxX();
		height = screen.getMaxY();
		max = Math.min(width, height);
		max /= 2;
		butSize = max / 8 / 2;
		for (int j = 0; j < 8; j++) {
			HBox hbox = new HBox();
			for (int i = 0; i < 8; i++) {
				VBox pane = new VBox();
				pane.setAlignment(Pos.CENTER);
				fields.add(pane);
				if (i % 2 == 0) {
					paneStyleEven(pane, j);
				} else {
					paneStyleUneven(pane, j);
				}
				hbox.getChildren().add(pane);
			}
			board.getChildren().add(hbox);
			boxes.add(hbox);
		}
		bpane.setCenter(board);
		prime.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				System.exit(0);
			}
		});
		prime.setScene((new Scene(bpane)));
		prime.setHeight(max);
		prime.setWidth(max);
		prime.setMinHeight(max * .8);
		prime.setMinWidth(max * .8);
		prime.setTitle("Checkers");
		Image icon = new Image(getClass().getResource("/icons/checkers.jpg").toExternalForm());
		prime.getIcons().add(icon);
	}

	void stoneStyle(Button but, String color, double opacity) {
		stoneStyle(but, color);
		but.setOpacity(opacity);
	}

	void stoneStyle(Button but, String color) {
		if (debug) {
			but.setStyle("-fx-background-color: " + color + ";" + "-fx-background-radius: " + butSize + "em; "
					+ "-fx-min-width: " + butSize + "px; " + "-fx-min-height: " + butSize + "px; " + "-fx-max-width: "
					+ butSize + "px; " + "-fx-max-height: " + butSize + "px;");
		} else {
			but.setStyle("-fx-background-color: " + color + ";" + "-fx-background-radius: " + butSize + "em; "
					+ "-fx-min-width: " + butSize + "px; " + "-fx-min-height: " + butSize + "px; " + "-fx-max-width: "
					+ butSize + "px; " + "-fx-max-height: " + butSize + "px;" + "-fx-text-fill: transparent;");
		}
		but.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				move(but);
			}
		});
		but.setOnDragEntered(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

			}
		});
	}

	void kingTest() {
		createKing(3, 4, "black");
		createKing(4, 3, "white");
	}

	void kingStyle(Button but, String color) {
		Image icon = new Image(getClass().getResource("/icons/crownb.png").toExternalForm());
		if (debug) {
			but.setStyle("-fx-background-color: " + color + ";" + "-fx-background-radius: " + butSize + "em; "
					+ "-fx-min-width: " + butSize + "px; " + "-fx-min-height: " + butSize + "px; " + "-fx-max-width: "
					+ butSize + "px; " + "-fx-max-height: " + butSize + "px; -fx-background-image: url('/"
					+ icon.getUrl() + "');-fx-background-size: " + butSize * 0.8 + " " + butSize * 0.6
					+ " ; -fx-background-position: center; -fx-background-repeat: no-repeat;");
		} else {
			but.setStyle("-fx-background-color: " + color + ";" + "-fx-background-radius: " + butSize + "em; "
					+ "-fx-min-width: " + butSize + "px; " + "-fx-min-height: " + butSize + "px; " + "-fx-max-width: "
					+ butSize + "px; " + "-fx-max-height: " + butSize + "px;"
					+ "-fx-text-fill:  transparent ; -fx-background-image: url('" + icon.getUrl()
					+ "');-fx-background-size: " + butSize * 0.8 + " " + butSize * 0.6
					+ " ; -fx-background-position: center; -fx-background-repeat: no-repeat;");
		}
		but.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				moveKing(but);
			}
		});

	}

	void printCords(int xx, int yy) {
		System.out.println("XX: " + xx + " Y: " + yy);
	}

	void moveKing(Button but) {
		if (amZug) {
			removePreview();
			int x = stoneGetX(but);
			int y = stoneGetY(but);
			if (but.getStyle().contains(plOne) && logic.getPlayerNumber() == 1) {
				int xx = x + 1;
				int yy = y + 1;
				boolean move = isFieldEmpty(xx, yy);
				System.out.print(move);
				while (move) {
					showKing(xx, yy, plOne, but);
					move = isFieldEmpty(++xx, ++yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plOne, but);
				}
				xx = x - 1;
				yy = y + 1;
				move = isFieldEmpty(xx, yy);
				while (move) {
					showKing(xx, yy, plOne, but);
					move = isFieldEmpty(--xx, ++yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plOne, but);
				}
				xx = x - 1;
				yy = y - 1;
				move = isFieldEmpty(xx, yy);
				while (move) {
					showKing(xx, yy, plOne, but);
					move = isFieldEmpty(--xx, --yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plOne, but);
				}
				xx = x + 1;
				yy = y - 1;
				move = isFieldEmpty(xx, yy);
				while (move) {
					showKing(xx, yy, plOne, but);
					move = isFieldEmpty(++xx, --yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plOne, but);
				}
			} else if (but.getStyle().contains(plTwo) && logic.getPlayerNumber() == 2) {
				int xx = x + 1;
				int yy = y + 1;
				boolean move = isFieldEmpty(xx, yy);
				System.out.print(move);
				while (move) {
					showKing(xx, yy, plTwo, but);
					move = isFieldEmpty(++xx, ++yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plTwo, but);
				}
				xx = x - 1;
				yy = y + 1;
				move = isFieldEmpty(xx, yy);
				while (move) {
					showKing(xx, yy, plTwo, but);
					move = isFieldEmpty(--xx, ++yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plTwo, but);
				}
				xx = x - 1;
				yy = y - 1;
				move = isFieldEmpty(xx, yy);
				while (move) {
					showKing(xx, yy, plTwo, but);
					move = isFieldEmpty(--xx, --yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plTwo, but);
				}
				xx = x + 1;
				yy = y - 1;
				move = isFieldEmpty(xx, yy);
				while (move) {
					showKing(xx, yy, plTwo, but);
					move = isFieldEmpty(++xx, --yy);
				}
				if (checkField(xx, yy)) {
					kingKick(xx, yy, plTwo, but);
				}
			}
		}
	}

	boolean isFieldEmpty(int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		} else {
			return !checkField(x, y);
		}
	}

	void move(Button but) {
		if (amZug) {
			removePreview();
			int x = stoneGetX(but);
			int y = stoneGetY(but);
			if (but.getStyle().contains(plOne) && logic.getPlayerNumber() == 1) {
				if (!checkField(x + 1, y + 1)) {
					showStone(x + 1, y + 1, plOne, but);
				} else {
					if (!checkField(x + 2, y + 2)) {
						kickStone(x + 1, y + 1, plOne, but);
					}
				}
				if (!checkField(x - 1, y + 1)) {
					showStone(x - 1, y + 1, plOne, but);
				} else {
					if (!checkField(x - 2, y + 2)) {
						kickStone(x - 1, y + 1, plOne, but);
					}
				}
			} else if (but.getStyle().contains(plTwo) && logic.getPlayerNumber() == 2) {
				if (!checkField(x + 1, y - 1)) {
					showStone(x + 1, y - 1, plTwo, but);
				} else {
					if (!checkField(x + 2, y - 2)) {
						kickStone(x + 1, y - 1, plTwo, but);
					}
				}
				if (!checkField(x - 1, y - 1)) {
					showStone(x - 1, y - 1, plTwo, but);
				} else {
					if (!checkField(x - 2, y - 2)) {
						kickStone(x - 1, y - 1, plTwo, but);
					}
				}
			}
		}
	}

	boolean kingKick(int x, int y, String color, Button previos) {
		if (x > 7 || x < 0 || y > 7 || y < 0) {
			return false;
		}
		Button but = getStone(x, y);
		int xx = stoneGetX(but) - stoneGetX(previos);
		int yy = stoneGetY(but) - stoneGetY(previos);
		if (xx < 0) {
			xx = -1;
		} else {
			xx = 1;
		}
		if (yy < 0) {
			yy = -1;
		} else {
			yy = 1;
		}
		if (!isMine(but, color)) {
			showKing(x + xx, y + yy, color, previos, but);
			return true;
		}
		return false;
	}

	boolean kickStone(int x, int y, String color, Button previos) {
		Button but = getStone(x, y);
		if (!isMine(but, color)) {
			if (color.equals(plOne)) {
				if (stoneGetX(but) - stoneGetX(previos) > 0) {
					showStone(x + 1, y + 1, color, previos, but);
				} else {
					showStone(x - 1, y + 1, color, previos, but);
				}
			} else {
				if (stoneGetX(but) - stoneGetX(previos) > 0) {
					showStone(x + 1, y - 1, color, previos, but);
				} else {
					showStone(x - 1, y - 1, color, previos, but);
				}
			}
		}
		return false;
	}

	Button getStone(int x, int y) {
		Button but = new Button();
		if (x >= 0 && x <= 8 && y >= 0 && y <= 8) {
			if (board.getChildren().get(y) instanceof HBox) {
				HBox hbox = (HBox) board.getChildren().get(y);
				if (hbox.getChildren().size() > 0) {
					if (hbox.getChildren().get(x) instanceof VBox) {
						VBox box = (VBox) hbox.getChildren().get(x);
						if (box.getChildren().size() > 0) {
							if (box.getChildren().get(0) instanceof Button) {
								but = (Button) box.getChildren().get(0);
								return but;
							}

						}
					}
				}
			}
		}
		return but;
	}

	boolean isMine(Button but, String color) {
		if (but.getStyle().contains(color)) {
			return true;
		}
		return false;
	}

	int stoneGetX(Button but) {
		String pos = but.getText();
		String posX = pos.substring(0, 1);
		int px = Integer.parseInt(posX);
		return px;
	}

	int stoneGetY(Button but) {
		String pos = but.getText();
		String posY = pos.substring(pos.length() - 1, pos.length());
		int py = Integer.parseInt(posY);
		return py;
	}

	String stoneGetPl(Button but) {
		if (but.getStyle().contains(plOne)) {
			return plOne;
		}
		return plTwo;
	}

	boolean removeStone(int x, int y) {
		if (x >= 0 && x <= 8 && y >= 0 && y <= 8) {
			if (board.getChildren().get(y) instanceof HBox) {
				HBox hbox = (HBox) board.getChildren().get(y);
				if (hbox.getChildren().get(x) instanceof VBox && hbox.getChildren().size() > 0) {
					VBox box = (VBox) hbox.getChildren().get(x);
					if (box.getChildren().get(0) instanceof Button) {
						box.getChildren().remove(0);
						logic.remove(x, y);
						return true;
					}
				}
			}
		}
		return false;
	}

	boolean addStone(int x, int y, String color, boolean king) {
		if (x >= 0 && x < 8 && y >= 0 && y < 8 && !king) {
			if (board.getChildren().get(y) instanceof HBox) {
				HBox hbox = (HBox) board.getChildren().get(y);
				if (hbox.getChildren().get(x) instanceof VBox) {
					VBox box = (VBox) hbox.getChildren().get(x);
					if (box.getChildren().size() == 0) {
						Button but = new Button("" + x + " " + y);
						stoneStyle(but, color);
						box.getChildren().add(but);
						logic.add(x, y, color, false);
						return true;
					}
				}
			}
		} else if (king) {
			createKing(x, y, color);
		}
		return false;
	}

	boolean createKing(int x, int y, String color) {
		if (board.getChildren().get(y) instanceof HBox) {
			HBox hbox = (HBox) board.getChildren().get(y);
			if (hbox.getChildren().get(x) instanceof VBox) {
				VBox box = (VBox) hbox.getChildren().get(x);
				if (box.getChildren().size() == 0) {
					Button but = new Button("" + x + " " + y);
					kingStyle(but, color);
					box.getChildren().add(but);
					logic.add(x, y, color, true);
					return true;
				}
			}
		}
		return false;
	}

	boolean showStone(int x, int y, String color, Button... previos) {
		if (x >= 0 && x < 8 && y >= 0 && y < 8) {
			if (board.getChildren().get(y) instanceof HBox) {
				HBox hbox = (HBox) board.getChildren().get(y);
				if (hbox.getChildren().get(x) instanceof VBox) {
					VBox box = (VBox) hbox.getChildren().get(x);
					if (box.getChildren().size() == 0) {
						Button but = new Button("" + x + " " + y);
						stoneStyle(but, color, 0.5);
						but.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent arg0) {
								removePreview();
								if (y == 7 || y == 0) {
									addStone(x, y, color, true);
								} else {
									addStone(x, y, color, false);
								}
								boolean kicked = true;
								if (previos.length > 1) {
									kicked = false;
								}
								if (previos.length > 0) {
									for (Button but : previos) {
										removeStone(stoneGetX(but), stoneGetY(but));
									}
								}
								try {
									logic.server.updateBoard(logic.board, kicked);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						});
						box.getChildren().add(but);
						return true;
					}
				}
			}
		}
		return false;
	}

	boolean showKing(int x, int y, String color, Button... previos) {
		if (x >= 0 && x < 8 && y >= 0 && y < 8) {
			if (board.getChildren().get(y) instanceof HBox) {
				HBox hbox = (HBox) board.getChildren().get(y);
				if (hbox.getChildren().get(x) instanceof VBox) {
					VBox box = (VBox) hbox.getChildren().get(x);
					if (box.getChildren().size() == 0) {
						Button but = new Button("" + x + " " + y);
						stoneStyle(but, color, 0.5);
						but.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent arg0) {
								removePreview();
								createKing(x, y, color);
								boolean kicked = true;
								if (previos.length > 1) {
									kicked = false;
								}
								if (previos.length > 0) {
									for (Button but : previos) {
										removeStone(stoneGetX(but), stoneGetY(but));
									}
								}
								try {
									logic.server.updateBoard(logic.board, kicked);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						});
						box.getChildren().add(but);
						return true;
					}
				}
			}
		}
		return false;
	}

	void removePreview() {
		for (int j = 0; j < fieldSize; j++) {
			for (int i = 0; i < fieldSize; i++) {
				if (board.getChildren().get(j) instanceof HBox) {
					HBox hbox = (HBox) board.getChildren().get(j);
					if (hbox.getChildren().size() > 0) {
						if (hbox.getChildren().get(i) instanceof VBox) {
							VBox box = (VBox) hbox.getChildren().get(i);
							if (box.getChildren().size() > 0) {
								if (box.getChildren().get(0) instanceof Button) {
									Button but = (Button) box.getChildren().get(0);
									if (but.getOpacity() == 0.5) {
										box.getChildren().remove(0);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	boolean checkField(int x, int y) {
		if (x >= 0 && x < 8 && y >= 0 && y < 8) {
			if (board.getChildren().get(y) instanceof HBox) {
				HBox hbox = (HBox) board.getChildren().get(y);
				if (hbox.getChildren().size() > 0) {
					if (hbox.getChildren().get(x) instanceof VBox) {
						VBox box = (VBox) hbox.getChildren().get(x);
						if (box.getChildren().size() > 0) {
							if (box.getChildren().get(0) instanceof Button) {
								Button but = (Button) box.getChildren().get(0);
								if (debug)
									System.out.println("auf Stein gestoßen " + stoneGetX(but) + " " + stoneGetY(but));
								return true;
							}
						}
					}
				}
			}
		}
		if (debug)
			System.out.println("Kein Stein gefunden");
		return false;
	}

	void propertyBinding() {
		for (VBox x : fields) {
			x.prefWidthProperty().bind(prime.widthProperty().divide(fieldSize));
			x.prefHeightProperty().bind(prime.heightProperty().divide(fieldSize));
			x.maxWidthProperty().bind(prime.heightProperty().divide(fieldSize));
			x.maxWidthProperty().bind(prime.widthProperty().divide(fieldSize));
		}
		for (Button x : stones) {
			x.prefHeightProperty().bind(prime.heightProperty().divide(fieldSize));
			x.prefWidthProperty().bind(prime.widthProperty().divide(fieldSize));
			x.maxWidthProperty().bind(prime.heightProperty().divide(fieldSize));
			x.maxWidthProperty().bind(prime.widthProperty().divide(fieldSize));
		}
	}

	void paneStyleUneven(VBox pane, int j) {
		if (j % 2 == 0) {
			pane.setStyle("-fx-background-color: silver;");
		} else {
			pane.setStyle("-fx-background-color: SaddleBrown;");
		}
		pane.setPrefHeight(max / fieldSize);
		pane.setPrefWidth(max / fieldSize);
	}

	void paneStyleEven(VBox pane, int j) {
		if (j % 2 == 0) {
			pane.setStyle("-fx-background-color: SaddleBrown;");
		} else {
			pane.setStyle("-fx-background-color: silver;");
		}
		pane.setPrefHeight(max / fieldSize);
		pane.setPrefWidth(max / fieldSize);
	}

	void infoStage() {
		Stage info = new Stage();
		VBox vbox = new VBox();
		Label para = new Label("PARA SS2019");
		Label credits = new Label("Lars Faier");
		vbox.getChildren().addAll(para, credits);
		vbox.setAlignment(Pos.CENTER);
		info.setScene(new Scene(vbox));
		info.setWidth(100);
		info.setHeight(100);
		info.initModality(Modality.APPLICATION_MODAL);
		info.getIcons().add(new Image("/icons/checkers.jpg"));
		info.show();
		vbox.setOnMouseClicked(e -> info.close());
	}

	void createStones() {
		for (int j = 0; j < fieldSize; j++) {
			for (int i = 0; i < fieldSize; i++) {
				if (j < 3 || j > fieldSize - 4) {
					if (j < 4) {
						if (j % 2 == 1) {
							if (i % 2 == 0) {
								HBox box = boxes.get(j);
								if (box.getChildren().get(i) instanceof VBox) {
									VBox vbox = (VBox) box.getChildren().get(i);
									Button but = new Button("" + i + " " + j);
									stoneStyle(but, "white");
									vbox.getChildren().add(but);
									logic.add(i, j, plOne, false);
								}
							}
						} else {
							if (i % 2 == 1) {
								HBox box = boxes.get(j);
								if (box.getChildren().get(i) instanceof VBox) {
									VBox vbox = (VBox) box.getChildren().get(i);
									Button but = new Button("" + i + " " + j);
									stoneStyle(but, "white");
									vbox.getChildren().add(but);
									logic.add(i, j, plOne, false);
								}
							}
						}
					} else {
						if (j % 2 == 1) {
							if (i % 2 == 0) {
								HBox box = boxes.get(j);
								if (box.getChildren().get(i) instanceof VBox) {
									VBox vbox = (VBox) box.getChildren().get(i);
									Button but = new Button("" + i + " " + j);
									stoneStyle(but, "black");
									vbox.getChildren().add(but);
									logic.add(i, j, plTwo, false);
								}
							}
						} else {
							if (i % 2 == 1) {
								HBox box = boxes.get(j);
								if (box.getChildren().get(i) instanceof VBox) {
									VBox vbox = (VBox) box.getChildren().get(i);
									Button but = new Button("" + i + " " + j);
									stoneStyle(but, "black");
									vbox.getChildren().add(but);
									logic.add(i, j, plTwo, false);
								}
							}
						}
					}
				}
			}
		}
		if (debug) {
			printBoard();
		}
	}

	void reset() {
		clear();
		createStones();
	}

	void clear() {
		for (int j = 0; j < fieldSize; j++) {
			for (int i = 0; i < fieldSize; i++) {
				if (board.getChildren().get(j) instanceof HBox) {
					HBox hbox = (HBox) board.getChildren().get(j);
					if (hbox.getChildren().size() > 0) {
						if (hbox.getChildren().get(i) instanceof VBox) {
							VBox box = (VBox) hbox.getChildren().get(i);
							if (box.getChildren().size() > 0) {
								if (box.getChildren().get(0) instanceof Button) {
									box.getChildren().remove(0);
								}
							}
						}
					}
				}
			}
		}
		logic.clear();
	}

	void updateBoard(int[][] matrix) {
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				switch (matrix[i][j]) {
				case 0:
					if (checkField(i, j)) {
						removeStone(i, j);
					}
					break;
				case 1:
					if (checkField(i, j)) {
						removeStone(i, j);
					}
					addStone(i, j, plOne, false);
					break;
				case 2:
					if (checkField(i, j)) {
						removeStone(i, j);
					}
					createKing(i, j, plOne);
					break;
				case 3:
					if (checkField(i, j)) {
						removeStone(i, j);
					}
					addStone(i, j, plTwo, false);
					break;
				case 4:
					if (checkField(i, j)) {
						removeStone(i, j);
					}
					createKing(i, j, plTwo);
					break;
				}
			}
		}
	}

	void createWinOrLoose(boolean win) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Stage erg = new Stage();
				erg.setWidth(250);
				erg.setHeight(50);
				Label you;
				if (win) {
					you = new Label("You won!");
				} else {
					you = new Label("You lost!");
				}
				you.setStyle("-fx-text-fill: RED; -fx-font-size: 30px;");
				VBox vbox = new VBox();
				vbox.setAlignment(Pos.CENTER);
				vbox.getChildren().add(you);
				Scene scene = new Scene(vbox);
				erg.setScene(scene);
				scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {
						switch (event.getCode()) {
						default:
							System.exit(0);
							break;
						}
					}
				});
				erg.initStyle(StageStyle.UNDECORATED);
				erg.initModality(Modality.APPLICATION_MODAL);
				erg.setX(prime.getX() + (((prime.getWidth() / 2)) - (erg.getWidth() / 2)));
				erg.setY(prime.getY() + (((prime.getHeight() / 2)) - (erg.getHeight() / 2)));
				erg.setAlwaysOnTop(true);
				erg.show();
			}
		});

	}

	void youWon() {
		createWinOrLoose(true);
	}

	void forceWin() {
		createWinOrLoose(true);
	}

	void youLost() {
		createWinOrLoose(false);
	}

	void printBoard() {
		logic.printBoard();
	}

}
