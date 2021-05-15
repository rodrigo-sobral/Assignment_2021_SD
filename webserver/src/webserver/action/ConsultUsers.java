package webserver.action;

import java.rmi.RemoteException;

public class ConsultUsers extends Action {
    private static final long serialVersionUID = 4L;

    private String ask_users= "";

	@Override
	public String execute() throws RemoteException {
        return SUCCESS;
	}

    public String getAsk_users() {
        return ask_users;
    }
    public void setAsk_users(String ask_users) {
        this.ask_users = ask_users;
    }


}
