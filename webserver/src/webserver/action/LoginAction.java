package webserver.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;
import webserver.model.RMIConnection;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() {
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !this.username.equals("") && !this.password.equals("")) {
			System.out.println("username "+this.username+" password "+this.password);
			if (this.username.equals("filipa") && this.password.equals("capela")){
				return SUCCESS;
			}

			//VERIFICAR NO RMI A PASS O USERNAME
			else{
				return LOGIN;
			}
			//this.getHeyBean().setUsername(this.username);
			//this.getHeyBean().setPassword(this.password);
			//session.put("username", username);
			//session.put("loggedin", true); // this marks the user as logged in
		}
		else
			return LOGIN;
	}
	
	public void setUsername(String username) {
		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password; // what about this input? 
	}
	
	public RMIConnection getHeyBean() {
		if(!session.containsKey("heyBean")) {
			try { this.setHeyBean(new RMIConnection()); }
			catch (RemoteException e) {  e.printStackTrace(); }
		} return (RMIConnection) session.get("heyBean");
	}

	public void setHeyBean(RMIConnection connection) {
		this.session.put("HeyBean", connection);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
