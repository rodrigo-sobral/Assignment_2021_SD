package webserver.action;

import java.rmi.RemoteException;
import webserver.model.RMIConnection;

public class LoginAction extends Action{
	private static final long serialVersionUID = 4L;
	private String username = null, password = null;

	@Override
	public String execute() throws RemoteException{
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !this.username.equals("") && !this.password.equals("")) {
			System.out.println("username "+this.username+" password "+this.password);
			RMIConnection rmiserver = this.getRMIConnection();
			System.out.println("saiu yoo");
			boolean result = rmiserver.login_user(username, password);
			System.out.println("password: "+result);
			
			return SUCCESS;
			//this.getHeyBean().setUsername(this.username);
			//this.getHeyBean().setPassword(this.password);
			//session.put("username", username);
			//session.put("loggedin", true); // this marks the user as logged in
		}
		else
			return LOGIN;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
