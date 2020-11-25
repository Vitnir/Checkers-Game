
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javafx.application.Platform;

public class Logic implements ILogic {
	Board gui;
	int fieldSize = 8;
	int[][] board = new int[fieldSize][fieldSize];

	String plOne;
	String plTwo;
	int ownPlayerNumber;
	ICheckerServer server;

	Logic(String first, String sec, Board gui) {
		try {
			this.gui = gui;
			server = (ICheckerServer) Naming.lookup("server");
			try {
				// reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
				int x = server.freeSpot();
				if (x > 0) {
					String s = "logic" + x;
					UnicastRemoteObject.exportObject(this, Registry.REGISTRY_PORT + x + 32);
					Naming.rebind(s, this);
					server.register(getOwnIp() + ":1099");
				}
			} catch (RemoteException | MalformedURLException e) {
				e.printStackTrace();
			}
			this.plOne = first;
			this.plTwo = sec;
			// printBoard();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	Logic(String first, String sec, Board gui, String ip, String port) {
		try {
			this.gui = gui;
			server = (ICheckerServer) Naming.lookup("//" + ip + ":" + port + "/" + "server");
			try {
				// reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
				int x = server.freeSpot();
				if (x > 0) {
					String s = "logic" + x;
					UnicastRemoteObject.exportObject(this, Registry.REGISTRY_PORT + x + 32);
					Naming.rebind(s, this);
					server.register(getOwnIp() + ":1099");
				}
			} catch (RemoteException | MalformedURLException e) {
				e.printStackTrace();
			}
			this.plOne = first;
			this.plTwo = sec;
			// printBoard();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	void printBoard() {
		for (int j = 0; j < fieldSize; j++) {
			for (int i = 0; i < fieldSize; i++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("\n");
	}

	public void update(int[][] board, int spielerAmZug) throws RemoteException {
		this.board = board;
		if (gui == null) {
			System.out.println("Arbeite auf nullpointer");
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				gui.updateBoard(board);
				if (spielerAmZug == ownPlayerNumber) {
					gui.amZug = true;
				} else {
					gui.amZug = false;
				}

			}
		});

	}

	public void setPlayerNumber(int ownPlayerNumber) throws RemoteException {
		this.ownPlayerNumber = ownPlayerNumber;
	}

	public int getPlayerNumber() {
		return this.ownPlayerNumber;
	}

	void clear() {
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				board[i][j] = 0;
			}
		}
	}

	void remove(int x, int y) {
		board[x][y] = 0;
	}

	void add(int x, int y, int stone) {
		board[x][y] = stone;
	}

	void add(int x, int y, String color, boolean king) {
		if (color.equals(plOne)) {
			if (king) {
				add(x, y, 2);
			} else {
				add(x, y, 1);
			}
		} else {
			if (king) {
				add(x, y, 4);
			} else {
				add(x, y, 3);
			}
		}

	}

	void setBoard(Board board) {
		this.gui = board;
	}

	@Override
	public void loose() throws RemoteException {
		gui.youLost();

	}

	@Override
	public void win() throws RemoteException {
		gui.youWon();
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
