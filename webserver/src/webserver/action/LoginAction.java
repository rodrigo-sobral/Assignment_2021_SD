package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

import rmiserver.Department;

public class LoginAction extends Action {
	public String username, password, ask_vote_tables, cc_number, selected_vote_table;

	@Override
	public String execute() throws RemoteException {
		if(username!=null && password!=null && !username.isBlank() && !username.isBlank()) {
			if (getRMIConnection().userLogin(username, password)) {
				ask_vote_tables="";
				ArrayList<Department> available_vote_tables= getRMIConnection().getDepartmentsWithOrNotVoteTable(true);
				for (Department department: available_vote_tables) 
					ask_vote_tables+= department.toString();
				if (ask_vote_tables.isEmpty()) ask_vote_tables+= "*Nenhuma Mesa de Voto Disponivel*";
				return LOGIN;
			} return ERROR;
		} return ERROR;
	}

	public String authenticateUser() throws RemoteException {
		System.out.println(username+" "+password+" "+cc_number);
		if (cc_number!=null && checkStringInteger(cc_number) ) {
			if (getRMIConnection().authenticateUser(cc_number, username, password)) return LOGIN;
			return ERROR;
		} return ERROR;
	}
	public String checkVoteTable() throws RemoteException {
		if (selected_vote_table!=null) {
			for (Department department : getRMIConnection().getDepartmentsWithOrNotVoteTable(true)) {
				if (department.getName().compareTo(selected_vote_table)==0) return SUCCESS;
			} return ERROR;
		} return ERROR;
	}

	public boolean checkStringInteger(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0)) && !Character.isDigit(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }

	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setAsk_vote_tables(String ask_vote_tables) {
        this.ask_vote_tables = ask_vote_tables;
	}
	public String getCc_number() {
		return cc_number;
	}
	public void setCc_number(String cc_number) {
		this.cc_number = cc_number;
	}
	public void setSelected_vote_table(String selected_vote_table) {
		this.selected_vote_table = selected_vote_table;
	}

}
