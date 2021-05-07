package webserver.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;
import webserver.model.HeyBean;

public class RegistUser extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() {
		if(this.username != null && !username.equals("")) {
			session.put("username", username);
			session.put("loggedin", true); // this marks the user as logged in
			return SUCCESS;
		}
		else
			return LOGIN;
	}	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
