package classes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Election implements Serializable {
    private static final long serialVersionUID = 1L;

    private String election_type;
    private String title, description;
    private LocalDateTime starting, ending;
    private int total_votes=0, blank_votes=0, null_votes=0;
    
    private ArrayList<String> college_restrictions= new ArrayList<>();
    private ArrayList<String> department_restrictions= new ArrayList<>();
    
    private ArrayList<Candidature> candidatures_to_election= new ArrayList<>();

    public String getElection_type() { return election_type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    
    public LocalDateTime getStarting() { return starting; }
    public String getStartingDateString() { return this.getStarting().getDayOfMonth()+"/"+this.getStarting().getMonthValue()+"/"+this.getStarting().getYear(); }
    public String getStartingHourString() { return this.getStarting().getHour()+":"+this.getStarting().getMinute(); }
    
    public LocalDateTime getEnding() { return ending; }
    public String getEndingDateString() { return this.getEnding().getDayOfMonth()+"/"+this.getEnding().getMonthValue()+"/"+this.getEnding().getYear(); }
    public String getEndingHourString() { return this.getEnding().getHour()+":"+this.getEnding().getMinute(); }
    
    public int getBlankVotes() { return blank_votes; }
    public int getNullVotes() { return null_votes; }
    public int getTotalVotes() { return total_votes; }
    
    public ArrayList<Candidature> getCandidatures_to_election() { return candidatures_to_election; }
    
    public ArrayList<String> getCollege_restrictions() { return college_restrictions; }
    public ArrayList<String> getDepartment_restrictions() { return department_restrictions; }
    
    /**
     * @param election_type must be: Estudante, Professor, Funcionario or Geral
     * @return true if election_type is correct, false otherwise
     */
    public boolean setElection_type(String election_type) { 
        if (election_type.compareTo("Estudante")==0 || election_type.compareTo("Professor")==0 || election_type.compareTo("Funcionario")==0) {
            this.election_type = election_type; 
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

    public void incrementBlankVote() { this.blank_votes++; this.total_votes++; }
    public void incrementNullVote() { this.null_votes++; this.total_votes++; }
    public void incrementValidVote(String candidature_name) { 
        for (Candidature candidature : candidatures_to_election) {
            if (candidature.getCandidature_name().compareTo(candidature_name)==0) { 
                candidature.incrementCandidatureVotes(); 
                this.total_votes++; 
                return; 
            }
        }
    }


}
