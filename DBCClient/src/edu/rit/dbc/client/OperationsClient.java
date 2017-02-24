package edu.rit.dbc.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;

import common.IOperations;

/**
 * Calls the remote service
 * 
 * @author Ashwin
 * 
 */
public class OperationsClient {
	static IOperations rmiProxy;

	public static void main(String args[]) throws RemoteException,
			NotBoundException {

		System.setSecurityManager(new java.rmi.RMISecurityManager());

		Registry registry = LocateRegistry.getRegistry();
		rmiProxy = (IOperations) registry.lookup("IOperations_proxy");

		// These calls are valid service calls
		if (rmiProxy.append("Liverpool", "FC").equals("LiverpoolFC")) {
			System.out.println("Successful");
		}

		if (rmiProxy.division(8, 1) == 8) {
			System.out.println("Successful");
		}

		if (rmiProxy.addition(1.1f, 0.0f) == 1.1f) {
			System.out.println("Successful");
		}

		if (rmiProxy.OR(true, false) == true) {
			System.out.println("Successful");
		}

		// These calls throw exceptions and are invalid calls
		try {
			rmiProxy.append("Liverpool", null);
		} catch (InputMismatchException e) {
			System.out.println("Exception caught successfully");
		}

		try {
			rmiProxy.division(8, 0);
		} catch (NumberFormatException e) {
			System.out.println("Exception caught successfully");
		}
		rmiProxy.addition(0, 0);
		try {
			rmiProxy.addition(0, 0);
		} catch (NumberFormatException e) {
			System.out.println("Exception caught successfully");
		}

		try {
			rmiProxy.OR(null, false);
		} catch (InputMismatchException e) {
			System.out.println("Exception caught successfully");
		}
	}
}
