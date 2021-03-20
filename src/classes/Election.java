package classes;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Election {
    
    private String election_type;
    private String title, description;
    private LocalDateTime starting, ending;
    private ArrayList<Candidature> candidatures_to_election= new ArrayList<>();
    private boolean running=false, finished=false;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStarting() { return starting; }
    public String getStartingString() { 
        return this.getStarting().getDayOfMonth()+"/"+this.getStarting().getMonthValue()+"/"+this.getStarting().getYear();
    }
    public LocalDateTime getEnding() { return ending; }
    public String getEndingString() { 
        return this.getEnding().getDayOfMonth()+"/"+this.getEnding().getMonthValue()+"/"+this.getEnding().getYear();
    }
    public String getElection_type() { return election_type; }
    public ArrayList<Candidature> getCandidatures_to_election() { return candidatures_to_election; }
    public boolean isRunning() { return running; }
    public boolean isFinished() { return finished; }
    
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
    public void setRunning(boolean running) { this.running = running; }
    public void setFinished(boolean finished) { this.finished = finished; }
}
