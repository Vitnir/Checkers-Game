package task1;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILogic extends Remote {
	void update(int[][] board, int spielerAmZug) throws RemoteException;

	public void setPlayerNumber(int ownPlayerNumber) throws RemoteException;
	
	public void loose() throws RemoteException;
	
	public void win() throws RemoteException;
	

}
