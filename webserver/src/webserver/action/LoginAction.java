package webserver.action;

public class LoginAction extends Action {
	private static final long serialVersionUID = 4L;
	private String username = null, password = null;

	@Override
	public String execute() {
		if(this.username != null && this.password != null && !this.username.isBlank() && !this.password.isBlank()) {
			System.out.println("username "+this.username+" password "+this.password);
			if (this.username.equals("filipa") && this.password.equals("capela")){
				return SUCCESS;
			} 
			//VERIFICAR NO RMI A PASS O USERNAME
			else return LOGIN;
		} else return LOGIN;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
