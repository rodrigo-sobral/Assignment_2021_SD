package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;


public class ConsultFinishedElections extends Action {
    private static final long serialVersionUID = 4L;

    private String finished_elections= "";
    //private Inputs inputs;

	@Override
	public String execute() throws RemoteException {
        return SUCCESS;
	}

    public String getFinishedElections() throws RemoteException {
        ArrayList<String> available_elections= getRMIConnection().getElectionsByState("finished");

        if (!available_elections.isEmpty()){
            for (String election : available_elections) finished_elections += election +"\n";
        } else finished_elections = "*Sem eleições para votar*";
        return SUCCESS;
    }

    
    public String getFinished_elections() {
        return finished_elections;
    }
    public void setFinished_elections(String finished_elections) {
        this.finished_elections = finished_elections;
    }

}
