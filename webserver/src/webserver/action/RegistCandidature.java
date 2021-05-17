package webserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;

import rmiserver.classes.Candidature;
import rmiserver.classes.Election;
import rmiserver.classes.User;

public class RegistCandidature extends Action {
    public String title, ask_elections, ask_users, election, candidates;

    @Override
	public String execute() throws RemoteException {
		if(title!=null && election!=null && candidates!=null && checkString(title) && checkString(election)) {
            ArrayList<Integer> candidates_ids= checkSeveralIntegerOptions(candidates);
            if (candidates_ids==null) return ERROR;
            ArrayList<User> all_users= getRMIConnection().getUsers();
            Election selected_election= getRMIConnection().getElectionByName(election, "unstarted");
            Candidature new_candidature= new Candidature();
            
            new_candidature.setCandidature_name(title);
            for (Integer integer : candidates_ids) new_candidature.getCandidates().add(all_users.get(integer));
            
            selected_election.getCandidatures_to_election().add(new_candidature);
            if (getRMIConnection().updateElection(selected_election, true)) return SUCCESS;
            return ERROR;
        } return ERROR;
    }

    public String getElectionsUsers() throws RemoteException {
        ask_elections= "";
        ask_users="";
        for (Election election : getRMIConnection().getElectionsByState("unstarted"))
            ask_elections+= election.toString("unstarted", true);
        
        ArrayList<User> all_users= getRMIConnection().getUsers();
        for (int i = 0; i<all_users.size(); i++) ask_users += "ID: "+(i+1)+"\n"+all_users.get(i).toString();
        
        if (ask_users.isEmpty() || ask_elections.isEmpty()) return ERROR;
        return SUCCESS;
    }

    public boolean checkString(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    
    public ArrayList<Integer> checkSeveralIntegerOptions(String candidates_list) {
        try {
            ArrayList<Integer> candidates= new ArrayList<>();
            for (String temp_option : candidates_list.split(",")) candidates.add(Integer.parseInt(temp_option)-1);
            return candidates;
        } catch (Exception e) { return null; }
    }


    public void setTitle(String title) {
        this.title = title;
    }
    public void setAsk_elections(String ask_elections) {
        this.ask_elections = ask_elections;
    }
    public void setAsk_users(String ask_users) {
        this.ask_users = ask_users;
    }
    public void setElection(String election) {
        this.election = election;
    }
    public void setCandidates(String candidates) {
        this.candidates = candidates;
    }
}
