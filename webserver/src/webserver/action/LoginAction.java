package webserver.action;
import java.util.ArrayList;
import java.rmi.RemoteException;

import javassist.expr.NewArray;
import webserver.model.RMIConnection;

public class LoginAction extends Action{
	private static final long serialVersionUID = 4L;
	private String username = null, password = null;
	private String lista_mesas ="";

	@Override
	public String execute() throws RemoteException{
		ArrayList<String> mesas = new ArrayList<>();
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !this.username.equals("") && !this.password.equals("")) {
			System.out.println("username "+this.username+" password "+this.password);
			RMIConnection rmiserver = this.getRMIConnection();
			System.out.println("saiu yoo");
			boolean result = rmiserver.login_user(username, password);
			System.out.println("password: "+result);
			
			if (result){
				mesas = this.getRMIConnection().list_vote_table(); 
				for (int i = 0; i < mesas.size(); i++) {
					System.out.println(mesas.get(i));
					lista_mesas+=mesas.get(i)+"\n";
				}
				if (mesas.size()==0){
					lista_mesas+="Nenhuma Mesa de Voto Disponivel\n";
				}
				return SUCCESS;

			}else{
				return LOGIN;
			}
		}else{
			return LOGIN;
		}
			
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getLista_mesas() {
		return lista_mesas;
	}

	public void setLista_mesas(String lista_mesas) {
		this.lista_mesas = lista_mesas;
	}
}
