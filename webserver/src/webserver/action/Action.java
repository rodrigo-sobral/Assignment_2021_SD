package webserver.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import rmiserver.classes.Election;
import rmiserver.classes.User;

import java.rmi.RemoteException;
import java.util.Map;

import webserver.model.RMIConnection;

public class Action extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    public Map<String, Object> session;

    public RMIConnection getRMIConnection() throws RemoteException {
        if(!session.containsKey("RMIConnection")) setRMIConnection(new RMIConnection());
        return (RMIConnection) session.get("RMIConnection");
    }

    public void setRMIConnection(RMIConnection bean) { session.put("RMIConnection", bean); }
    
    public void saveData(String type, String data) { session.put(type, data); }
    public String getSavedData(String type) { return (String) session.get(type); }

    public void saveLoggedUser(User user) { session.put("LoggedUser", user); }
    public User getLoggedUser() { return (User) session.get("LoggedUser"); }

    public void saveElection(Election election) { session.put("VotedElection", election); }
    public Election getSavedElection() { return (Election) session.get("VotedElection"); }

    @Override
    public void setSession(Map<String, Object> session) { this.session = session; }
}
