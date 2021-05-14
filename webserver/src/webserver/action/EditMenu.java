package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class EditMenu extends Action {
    private static final long serialVersionUID = 4L;
    private String elections_list = "";


	@Override
	public String execute() throws RemoteException {
        ArrayList<String> aux_elections= this.getRMIConnection().getElectionsNames("unstarted");

        if (!aux_elections.isEmpty()) {
            for (String election : aux_elections) elections_list += election +"\n";
            return SUCCESS;
        }
        elections_list = "*Sem eleições para votar*";
        return ERROR;
	}

    public String getElections_list() {
        return elections_list;
    }
    public void setElections_list(String elections_list) {
        this.elections_list = elections_list;
    }
    
}
