package edu.rit.dbc.server;

import java.io.Serializable;
import java.util.InputMismatchException;


public class Contract_IOperations implements Serializable {

	private static final long serialVersionUID = 4944995321525047065L;
	
	public Contract_IOperations(){}
	
	public boolean checkContract_addition(Object[] b){
		if((float)b[1]==0.0f && (float)b[0]==0.0f){
			throw new ContractException(new NumberFormatException("Both arguments are zero"));
		}
		else {
			return true;
		}
	}
	
	public boolean checkContract_division(Object[] b){
		if((int)b[1]==0.0f){
			throw new NumberFormatException("Divide by Zero");
		}
		else {
			return true;
		}
	}
	
	public boolean checkContract_OR(Object[] b){
		if((Boolean)b[0]==null || (Boolean)b[1]==null){
			throw new InputMismatchException("Booleans are null");
		}
		else {
			return true;
		}
	}
	
	public boolean checkContract_append(Object[] b){
		if((String)b[0]==null || (String)b[1]==null){
			throw new InputMismatchException("Strings are null");
		}
		else {
			return true;
		}
	}
	
	public boolean checkContract_merge(Object[] b){
		if((char)b[0]==0||(char)b[1]==0){
			throw new InputMismatchException("Chars are 0");
		}
		else {
			return true;
		}
	}
}
