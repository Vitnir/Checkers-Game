package task1;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CheckerServer extends UnicastRemoteObject implements ICheckerServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ILogic playOne;
	ILogic playTwo;
	int zug = 1;

	Board gui;
	int fieldSize = 8;
	int[][] board = new int[fieldSize][fieldSize];

	
	CheckerServer() throws RemoteException {
		board = new int[fieldSize][fieldSize];
	}

	void startGame() {
		for (int j = 0; j < fieldSize; j++) {
			for (int i = 0; i < fieldSize; i++) {
				if (j < 3 || j > fieldSize - 4) {
					if (j < 4) {
						if (j % 2 == 1) {
							if (i % 2 == 0) {
								board[i][j] = 1;
							}
						} else {
							if (i % 2 == 1) {
								board[i][j] = 1;
							}
						}
					} else {
						if (j % 2 == 1) {
							if (i % 2 == 0) {
								board[i][j] = 3;

							}
						} else {
							if (i % 2 == 1) {
								board[i][j] = 3;
							}
						}
					}
				}
			}
		}
	}

	public synchronized void register(String url) throws RemoteException {
		try {
			ILogic c;
			if (playOne == null) {
				c = (ILogic) Naming.lookup("//"+url+"/logic1");
				System.out.println("Spieler 1 verbunden");
				playOne = c;
				playOne.setPlayerNumber(1);
			} else if (playTwo == null) {
				c = (ILogic) Naming.lookup("//"+url+"/logic2");
				System.out.println("Spieler 2 verbunden");
				playTwo = c;
				playTwo.setPlayerNumber(2);
				startGame();
				System.out.println("Alle Spieler verbunden beginne Spiel");
				updateClientBoard();

			} else {
				System.out.println("Server ist voll");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
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
	}

	void setBoard(Board board) {
		this.gui = board;
	}

	public static void start () {
		try {
			Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			CheckerServer server = new CheckerServer();
			reg.bind("server", server);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	public void updateClientBoard() throws RemoteException {
		System.out.println(playOne + "wuerde gerne updaten");
		playOne.update(board, zug);
		playTwo.update(board, zug);
	}

	@Override
	public void updateBoard(int[][] a, boolean changePlayer) throws RemoteException {

		this.board = a;
		if (changePlayer) {
			if (zug == 1) {
				zug = 2;
			} else {
				zug = 1;
			}
		}
		updateClientBoard();
		if (checkWin(a)) {
			System.out.println("Gewinner festgestellt");
			System.exit(0);
		}

	}

	@Override
	public int freeSpot() throws RemoteException {
		if (playOne == null) {
			return 1;
		} else if (playTwo == null) {
			return 2;
		}
		return 0;
	}

	private boolean checkWin(int[][] a) throws RemoteException {
		int playeroneCounter = 0;
		int playertwoCounter = 0;
		for (int[] x : a) {
			for (int s : x) {
				if (s == 1 || s == 2) {
					playeroneCounter++;
				} else if (s == 3 || s == 4) {
					playertwoCounter++;
				}
			}
		}
		if (playeroneCounter == 0) {
			playOne.loose();
			playTwo.win();
			return true;
		} else if (playertwoCounter == 0) {
			playOne.win();
			playTwo.loose();
			return true;
		}
		return false;
	}

}
