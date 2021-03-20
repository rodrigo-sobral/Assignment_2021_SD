import java.util.ArrayList;

public class MCServer {
    private ArrayList<MCClient> vote_terminals= new ArrayList<>();

    public ArrayList<MCClient> getVote_terminals() { return vote_terminals; }

    public void setVote_terminals(ArrayList<MCClient> vote_terminals) { this.vote_terminals = vote_terminals; }
}
