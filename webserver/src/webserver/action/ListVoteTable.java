package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ListVoteTable extends Action{
	private String lista_mesas ="";

	@Override
	public String execute() throws RemoteException{
        System.out.println("entrouaqui");
		// any username is accepted without confirmation (should check using RMI)
        ArrayList<String> mesas = this.getRMIConnection().list_vote_table(); 
        for (int i = 0; i < mesas.size(); i++) {
            System.out.println(mesas.get(i));
            lista_mesas+=mesas.get(i)+"\n";
        }

        return SUCCESS;
    }
	
	public String getLista_mesas() {
        return lista_mesas;
    }
    public void setLista_mesas(String lista_mesas) {
        this.lista_mesas = lista_mesas;
    }
}
