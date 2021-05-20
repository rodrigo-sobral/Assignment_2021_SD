package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

import rmiserver.Department;
import rmiserver.classes.Candidature;
import rmiserver.classes.Election;
import rmiserver.classes.User;

public class UserActions extends Action {
	public String username, password, ask_vote_tables, cc_number, selected_vote_table, ask_elections, selected_election, ask_candidatures, voted_candidate;

	//	USER LOGIN
	@Override
	public String execute() throws RemoteException {
		if(username!=null && password!=null && !username.isBlank() && !username.isBlank()) {
			for (User user : getRMIConnection().getUsers()) {
				if (user.getName().compareTo(username)==0 && user.getPassword().compareTo(password)==0){
					System.out.println(user.getNome_id());
					System.out.println(user.getId_fb());
					ask_vote_tables="";
					ArrayList<Department> available_vote_tables= getRMIConnection().getDepartmentsWithOrNotVoteTable(true);
					for (Department department: available_vote_tables) 
						if (department.getActivatedVoteTable()) ask_vote_tables+= department.toString();
					if (ask_vote_tables.isEmpty()) ask_vote_tables+= "*Nenhuma Mesa de Voto Disponivel*";
    				saveData("ask_vote_tables", ask_vote_tables);
					saveLoggedUser(user);
					return LOGIN;
				}
			} return ERROR;
		} return ERROR;
	}

	//	SELECTING VOTE TABLE AND 
	public String checkVoteTable() throws RemoteException {
		if (selected_vote_table!=null && checkString(selected_vote_table)) {
			ArrayList<Election> available_elections= getRMIConnection().getElectionToVoteTable(selected_vote_table);
			if (available_elections!=null && !available_elections.isEmpty()) {
				ask_elections= "";
				User logged_user= getLoggedUser();
				for (Election election : available_elections) {
					if (logged_user.getUser_type().compareTo(election.getElectionState())==0)
						ask_elections += election.toString("running", false);
				}
				if (ask_elections.isEmpty()) return ERROR;
			} else return ERROR;
			for (Department department : getRMIConnection().getDepartmentsWithOrNotVoteTable(true)) {
				if (department.getActivatedVoteTable() && department.getName().compareTo(selected_vote_table)==0) {
					saveData("selected_vote_table", selected_vote_table);
					saveData("ask_elections", ask_elections);
					return SUCCESS;
				}
			} return ERROR;
		} return ERROR;
	}
	public String authenticateUser() throws RemoteException {
		if (cc_number!=null && selected_election!=null && checkStringInteger(cc_number) && checkString(selected_election)) {
			Election election= getRMIConnection().getElectionByName(selected_election, "running");
			if (election==null) return ERROR;
			User logged_user= getLoggedUser();
			if (logged_user.getCc_number().compareTo(cc_number)==0 && logged_user.getUser_type().compareTo(election.getElectionState())==0) {
				ask_candidatures="";
				for (Candidature candidature : election.getCandidatures_to_election()) ask_candidatures+= candidature.toString();
				saveData("ask_candidatures", ask_candidatures);
				saveElection(election);
				return LOGIN;
			} return ERROR;
		} return ERROR;
	}

	public String vote() throws RemoteException {
		if (voted_candidate!=null) {
			Election election= getSavedElection();
			User voter= getLoggedUser();
			if (election.registVote(voted_candidate, voter.getCc_number(), getSavedData("selected_vote_table"))) {
				if (getRMIConnection().sendVote(election)) return SUCCESS;
			} return ERROR;
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

	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setAsk_vote_tables(String ask_vote_tables) {
        this.ask_vote_tables = ask_vote_tables;
	}
	public void setCc_number(String cc_number) {
		this.cc_number = cc_number;
	}
	public void setVoted_candidate(String voted_candidate) {
		this.voted_candidate = voted_candidate;
	}
	public void setSelected_vote_table(String selected_vote_table) {
		this.selected_vote_table = selected_vote_table;
	}
	public void setAsk_elections(String ask_elections) {
		this.ask_elections = ask_elections;
	}
	public void setSelected_election(String selected_election) {
		this.selected_election = selected_election;
	}
	public void setAsk_candidatures(String ask_candidatures) {
		this.ask_candidatures = ask_candidatures;
	}
}
