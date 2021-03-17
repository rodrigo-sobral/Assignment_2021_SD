package classes;

import java.util.Date;

public class Election implements ElectionInterface {
    
    private String election_type;
    private String title, description;
    private Date starting, ending;
    private User restrictions;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getStarting() { return starting; }
    public Date getEnding() { return ending; }
    public User getRestrictions() { return restrictions; }
    public String getElection_type() { return election_type; }
    
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStarting(Date starting) { this.starting = starting; }
    public void setEnding(Date ending) { this.ending = ending; }
    public void setRestrictions(User restrictions) { this.restrictions = restrictions; }
    /**
     * @param election_type must be: Estudante, Professor, Funcionario or Geral
     * @return true if election_type is correct, false otherwise
     */
    public boolean setElection_type(String election_type) { 
        if (election_type=="Estudante" || election_type=="Professor" || election_type=="Funcionario" || election_type=="Geral") {
            this.election_type = election_type; 
            return true;
        } else return false;
    }

}
