package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import rmiserver.classes.Election;

public class ConsultElections extends Action {
    private String ask_elections= "";

	@Override
	public String execute() throws RemoteException { return SUCCESS; }

    public String getUnstartedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("unstarted");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("unstarted", false);
        } else return ERROR;
        saveData("ask_elections", ask_elections);
        saveData("ask_elections_type", "unstarted");
        return SUCCESS;
    }
    public String getDetailedUnstartedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("unstarted");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("unstarted", true);
        } else return ERROR;
        saveData("ask_elections", ask_elections);
        saveData("ask_elections_type", "unstarted");
        return SUCCESS;
    }
    public String getDetailedRunningElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("running");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("running", true);
        } else return ERROR;
        saveData("ask_elections", ask_elections);
        saveData("ask_elections_type", "running");
        return SUCCESS;
    }
    public String getDetailedFinishedElections() throws RemoteException {
        ArrayList<Election> available_elections= getRMIConnection().getElectionsByState("finished");
        ask_elections= "";
        if (!available_elections.isEmpty()) {
            for (Election election : available_elections) ask_elections += election.toString("finished", true);
        } else return ERROR;
        saveData("ask_elections", ask_elections);
        saveData("ask_elections_type", "finished");
        return SUCCESS;
    }

	public String getAsk_elections() {
		return ask_elections;
	}
	public void setAsk_elections(String ask_elections) {
		this.ask_elections = ask_elections;
	}

}
