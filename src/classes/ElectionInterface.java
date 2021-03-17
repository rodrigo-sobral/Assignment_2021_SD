package classes;

import java.util.Date;

public interface ElectionInterface {
    
    public String getTitle();
    public String getDescription();
    public Date getStarting();
    public Date getEnding();
    public User getRestrictions();
    public String getElection_type();

    public void setTitle(String title);
    public void setDescription(String description);
    public void setStarting(Date starting);
    public void setEnding(Date ending);
    public void setRestrictions(User restrictions);
    public boolean setElection_type(String election_type);
}
