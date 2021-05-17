package webserver.action;

import java.rmi.RemoteException;
import rmiserver.Department;

public class RegistVoteTable extends Action {
    public String selected_vote_table, vote_terminals, ask_vote_tables;

	@Override
	public String execute() throws RemoteException {
        if (selected_vote_table!=null && vote_terminals!=null && checkString(selected_vote_table) && checkStringInteger(vote_terminals)) {
            Department selected_department= getRMIConnection().getUniqueDepartment(selected_vote_table);
            selected_department.createVoteTable(Integer.parseInt(vote_terminals));
            if (getRMIConnection().updateDepartment(selected_department, true)) return SUCCESS;
            return ERROR;
        } return ERROR;
	}

    public String getDepartmentsWithVoteTable() throws RemoteException {
        ask_vote_tables="";
        for (Department department: getRMIConnection().getDepartmentsWithOrNotVoteTable(true)) 
            ask_vote_tables+= department.toString();
		if (ask_vote_tables.isEmpty()) return ERROR;
        return SUCCESS;
    }
    public String getDepartmentsWithNoVoteTable() throws RemoteException {
        ask_vote_tables="";
        for (Department department: getRMIConnection().getDepartmentsWithOrNotVoteTable(false)) 
            ask_vote_tables+= department.toString();
		if (ask_vote_tables.isEmpty()) return ERROR;
        return SUCCESS;
    }

    public String deleteVoteTable() throws RemoteException {
        if (selected_vote_table!=null && checkString(selected_vote_table)) {
            Department selected_department= getRMIConnection().getUniqueDepartment(selected_vote_table);
            selected_department.deleteVoteTable();
            if (getRMIConnection().updateDepartment(selected_department, false)) return SUCCESS;
            return ERROR;
        } return ERROR;
    }

    public boolean checkString(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkStringInteger(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0)) && !Character.isDigit(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }

    public void setSelected_vote_table(String selected_vote_table) {
        this.selected_vote_table = selected_vote_table;
    }
    public void setVote_terminals(String vote_terminals) {
        this.vote_terminals = vote_terminals;
    }
    public void setAsk_vote_tables(String ask_vote_tables) {
        this.ask_vote_tables = ask_vote_tables;
    }
}
