package classes;

import java.util.ArrayList;

public class Candidature {
    private String candidature_name;
    private ArrayList<User> candidates = new ArrayList<>();
    
    
    public String getCandidature_name() { return candidature_name; }
    public ArrayList<User> getCandidates() { return candidates; }

    public void setCandidature_name(String candidature_name) { this.candidature_name = candidature_name; }
    public void setCandidates(ArrayList<User> candidates) { this.candidates = candidates; }

    
}
