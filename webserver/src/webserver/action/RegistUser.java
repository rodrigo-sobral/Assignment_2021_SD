package webserver.action;

import rmiserver.classes.*;
import rmiserver.*;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;
import java.rmi.RemoteException;
import rmiserver.classes.User;

public class RegistUser extends Action {
	private static final long serialVersionUID = 4L;
	private String user_type;    //  Professor, Estudante ou Funcionario
    private String name, password, address, phone_number;
    private String cc_number, cc_shelflife;
    private String college, department;

	@Override
	public String execute() {
		System.out.println("entrou");
		if (user_type!=null && name!=null && password!=null && address!=null && phone_number!=null && cc_number!=null && cc_shelflife!=null && college!=null && department!=null) {
			System.out.println("aceitou"+user_type+password+address+phone_number);
			if (user_type.compareTo("Estudante")==0 || user_type.compareTo("Professor")==0 || user_type.compareTo("Funcionario")==0) {
                //User new_user = new User(user_type, name, password, address, phone_number, cc_number, cc_shelflife, college, department);
				//String result= this.getRMIConnection().registUser(college, department, new_user);
				//if (result.split(":")[0].compareTo("200")==0) return SUCCESS;
				//else return ERROR;
				return SUCCESS;
            } return ERROR;
		} return ERROR;
	}	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
