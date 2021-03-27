package classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Candidature implements Serializable {
    private static final long serialVersionUID = 1L;

    private String candidature_name;
    private ArrayList<User> candidates = new ArrayList<>();
    private int candidature_votes=0;
    
    public String getCandidature_name() { return candidature_name; }
    public int getCandidature_votes() { return candidature_votes; }
    public ArrayList<User> getCandidates() { return candidates; }

    public void setCandidates(ArrayList<User> candidates) { this.candidates = candidates; }
    public void setCandidature_name(String candidature_name) { this.candidature_name = candidature_name;}
    public void incrementCandidatureVotes() { this.candidature_votes++; }
}
