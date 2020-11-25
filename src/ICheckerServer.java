
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICheckerServer extends Remote {
	public void updateBoard(int[][] a, boolean changePlayer) throws RemoteException;

	public void register(String url) throws RemoteException;

	public int freeSpot() throws RemoteException;
}
