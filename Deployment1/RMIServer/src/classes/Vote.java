package classes;

import java.io.Serializable;

public class Vote implements Serializable {
    private static final long serialVersionUID = 1L;

    private String voter_cc, voter_depart;

    public Vote(String voter_cc, String voter_depart) { this.voter_cc= voter_cc; this.voter_depart= voter_depart; }

    public String getVoter_cc() { return voter_cc; }
    public String getVoter_depart() { return voter_depart; }
}