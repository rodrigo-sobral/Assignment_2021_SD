import java.io.Serializable;
import java.util.ArrayList;

public class MCServer implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<MCClient> vote_terminals;

    public MCServer(int num_vote_terminals) {
        this.vote_terminals = new ArrayList<>(num_vote_terminals);
    }

    public ArrayList<MCClient> getVote_terminals() { return vote_terminals; }
    public void setVote_terminals(ArrayList<MCClient> vote_terminals) { this.vote_terminals = vote_terminals; }

}
