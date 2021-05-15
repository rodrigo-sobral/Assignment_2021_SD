package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

import rmiserver.classes.Election;


public class ConsultElections extends Action {
    private static final long serialVersionUID = 4L;

    private String ask_elections= "";

	@Override
	public String execute() throws RemoteException {
        return SUCCESS;
	}

    public String getUnstartedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("unstarted");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("unstarted", false);
        } else return ERROR;
        return SUCCESS;
    }
    public String getDetailedUnstartedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("unstarted");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("unstarted", true);
        } else return ERROR;
        return SUCCESS;
    }

    public String getFinishedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("finished");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("finished", false);
        } else return ERROR;
        return SUCCESS;
    }
    public String getDetailedFinishedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("finished");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("finished", true);
        } else return ERROR;
        return SUCCESS;
    }

	public String getAsk_elections() {
		return ask_elections;
	}
	public void setAsk_elections(String ask_elections) {
		this.ask_elections = ask_elections;
	}

}
