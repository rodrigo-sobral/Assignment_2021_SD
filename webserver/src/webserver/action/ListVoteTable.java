package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

import webserver.model.RMIConnection;

public class ListVoteTable extends Action{
	private ArrayList<String> lista_mesas;


	@Override
	public String execute() throws RemoteException{
		// any username is accepted without confirmation (should check using RMI)
		
	}
	
	public ArrayList<String> getLista_mesas() {
        return lista_mesas;
    }
}
