package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ListVoteTable extends Action{
    private String mesa_vote;

	@Override
	public String execute() throws RemoteException{
        System.out.println("entrouaqui");
		// any username is accepted without confirmation (should check using RMI)
        ArrayList<String> mesas = this.getRMIConnection().list_vote_table(); 
        mesas = this.getRMIConnection().list_vote_table(); 
		for (int i = 0; i < mesas.size(); i++) {
            if (mesa_vote.compareTo(mesas.get(i))==0){
                return SUCCESS;
            }
		}
        return ERROR;
    }
	
	public String getMesa_vote() {
        return mesa_vote;
    }

    public void setMesa_vote(String mesa_vote) {
        this.mesa_vote = mesa_vote;
    }
}
