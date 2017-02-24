package edu.rit.dbc.server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.IOperations;

/**
 * Remote Server, implements Service Interface
 * 
 * @author Ashwin
 * 
 */
public class OperationsServer extends UnicastRemoteObject implements IOperations {

	private static final long serialVersionUID = 8287875100727240474L;

	protected OperationsServer() throws RemoteException {
		super();
	}

	public static void main(String args[]) throws IOException,
			InstantiationException, IllegalAccessException {
		
		//LocateRegistry.createRegistry(1099);
		IOperations service = new OperationsServer();
		Class<?> service_interface = IOperations.class;
		String bind_name = "IOperations";
		new ProxyGenerator(service, bind_name, service_interface);
	}

	/**
	 * These functions are called remotely
	 */
	@Override
	public String append(String a, String b) throws RemoteException {

		return a + b;
	}

	@Override
	public int division(int a, int b) throws RemoteException, NotBoundException {
		return a / b;
	}

	@Override
	public float addition(float a, float b) throws RemoteException,
			NotBoundException {
		return a + b;
	}

	@Override
	public Boolean OR(Boolean a, Boolean b) throws RemoteException,
			NotBoundException {
		return a | b;
	}

}
