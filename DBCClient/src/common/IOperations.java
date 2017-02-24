package common;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Service Interface
 * @author Ashwin
 *
 */
public interface IOperations extends Remote {
	public String append(String a, String b) throws RemoteException;
	public int division(int a, int b) throws RemoteException, NotBoundException;
	public float addition(float a, float b) throws RemoteException, NotBoundException;
	public Boolean OR(Boolean a, Boolean b) throws RemoteException, NotBoundException;

}
