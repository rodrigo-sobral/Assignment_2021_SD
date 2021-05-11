package webserver.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.Map;

import src.webserver.model.RMIConnection;

public class Action extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    Map<String, Object> session;


    public RMIConnection getRMIConnection() throws RemoteException {
        if(!session.containsKey("RMIConnection"))
            this.setRMIConnection(new RMIConnection());
        return (RMIConnection) session.get("RMIConnection");
    }

    public void setRMIConnection(RMIConnection bean) { this.session.put("RMIConnection", bean); }

    @Override
    public void setSession(Map<String, Object> session) { this.session = session; }
}
