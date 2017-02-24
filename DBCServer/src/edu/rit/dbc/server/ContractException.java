package edu.rit.dbc.server;

public class ContractException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6196768362918626096L;
	
	public ContractException(RuntimeException e){
		super(e);
	}
	
}
