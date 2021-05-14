package rmiserver.classes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Election implements Serializable {
    private static final long serialVersionUID = 1L;

    private String election_state;
    private String title, description;
    private LocalDateTime starting, ending;
    private int total_votes=0, blank_votes=0, null_votes=0;
    
    private ArrayList<String> college_restrictions= new ArrayList<>();
    private ArrayList<String> department_restrictions= new ArrayList<>();
    
    private ArrayList<Candidature> candidatures_to_election= new ArrayList<>();
    private ArrayList<Vote> voters= new ArrayList<>();

    public String getElectionState() { return election_state; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    
    public LocalDateTime getStarting() { return starting; }
    public String getStartingDateString() { return this.getStarting().getDayOfMonth()+"/"+this.getStarting().getMonthValue()+"/"+this.getStarting().getYear(); }
    public String getStartingHourString() { return this.getStarting().getHour()+":"+this.getStarting().getMinute(); }
    
    public LocalDateTime getEnding() { return ending; }
    public String getEndingDateString() { return this.getEnding().getDayOfMonth()+"/"+this.getEnding().getMonthValue()+"/"+this.getEnding().getYear(); }
    public String getEndingHourString() { return this.getEnding().getHour()+":"+this.getEnding().getMinute(); }
    
    public ArrayList<Vote> getVoters() { return voters; }
    public int getBlankVotes() { return blank_votes; }
    public int getNullVotes() { return null_votes; }
    public int getTotalVotes() { return total_votes; }
    
    public ArrayList<Candidature> getCandidatures_to_election() { return candidatures_to_election; }
    
    public ArrayList<String> getCollege_restrictions() { return college_restrictions; }
    public ArrayList<String> getDepartment_restrictions() { return department_restrictions; }
    
    /**
     * @param election_state must be: Estudante, Professor, Funcionario or Geral
     * @return true if election_state is correct, false otherwise
     */
    public boolean setElectionState(String election_state) { 
        if (election_state.compareTo("Estudante")==0 || election_state.compareTo("Professor")==0 || election_state.compareTo("Funcionario")==0) {
            this.election_state = election_state; 
            return true;
        } else return false;
    }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStarting(LocalDateTime starting) { this.starting = starting; }
    public void setEnding(LocalDateTime ending) { this.ending = ending; }
    public void setCandidatures_to_election(ArrayList<Candidature> candidatures_to_election) { this.candidatures_to_election = candidatures_to_election; }
    public void setCollege_restrictions(ArrayList<String> college_restrictions) { this.college_restrictions = college_restrictions; }
    public void setDepartment_restrictions(ArrayList<String> department_restrictions) { this.department_restrictions = department_restrictions; }

    public void incrementBlankVote(String voter_cc, String department_voted) { 
        this.blank_votes++; 
        this.total_votes++; 
        voters.add(new Vote(voter_cc, department_voted)); 
    }
    public void incrementNullVote(String voter_cc, String department_voted) { 
        this.null_votes++; 
        this.total_votes++; 
        voters.add(new Vote(voter_cc, department_voted)); 
    }
    public void incrementValidVote(int candidature_index, String voter_cc, String department_voted) { 
        Candidature candidature = candidatures_to_election.get(candidature_index);
        candidature.incrementCandidatureVotes(); 
        voters.add(new Vote(voter_cc, department_voted));
        this.total_votes++;
    }

    public boolean registVote(String voter_choice, String voter_cc, String department_voted) {
        if (voter_choice.isBlank()) { incrementBlankVote(voter_cc, department_voted); return true; }
        for (Vote vote : voters) if (vote.getVoter_cc().compareTo(voter_cc)==0) return false;
        try { 
            int candidature_index= Integer.parseInt(voter_choice)-1; 
            if (candidature_index>0 && candidature_index<=candidatures_to_election.size()) incrementValidVote(candidature_index, voter_cc, department_voted);
            else incrementNullVote(voter_cc, department_voted); 
            return true;
        } catch (Exception e) { incrementNullVote(voter_cc, department_voted); return true; }
    }
}



