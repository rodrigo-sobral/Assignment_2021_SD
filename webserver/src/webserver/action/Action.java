package webserver.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.Map;

import webserver.model.RMIConnection;

public class Action extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    public Map<String, Object> session;

    public RMIConnection getRMIConnection() throws RemoteException {
        if(!session.containsKey("RMIConnection")) {
            setRMIConnection(new RMIConnection());
            System.out.println("associei um servidor");
        }
        else System.out.println("ja tinha o servidor");
        return (RMIConnection) session.get("RMIConnection");
    }

    public void setRMIConnection(RMIConnection bean) { session.put("RMIConnection", bean); }

    @Override
    public void setSession(Map<String, Object> session) { this.session = session; }
}
