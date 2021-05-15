package webserver.action;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import rmiserver.classes.Election;

public class EditElection extends Action {
    private static final long serialVersionUID = 4L;
    private String old_title, new_title, new_description, new_start_date, new_start_hour, new_end_date, new_end_hour;
    private String unstarted_elections= "";
    private Inputs inputs;

	@Override
	public String execute() throws RemoteException {
        boolean changed=false;
        Election edited_election=null;
        if (old_title!=null && inputs.checkString(old_title)) {
            edited_election= getRMIConnection().getElectionByName(old_title);
            if (edited_election==null) return ERROR;
        } else return ERROR;
        if (new_title!=null && !new_title.isBlank()) {
            if (!inputs.checkString(new_title)) return ERROR;
            changed= true;
            edited_election.setTitle(new_title);
        }
        if (new_description!=null && !new_description.isBlank()) {
            edited_election.setDescription(new_description);
            changed= true;
        }
        if (new_start_date!=null && !new_start_date.isBlank()) {
            LocalDate temp_date1= inputs.checkDateFormat(new_start_date);
            if (temp_date1!=null && LocalDate.now().compareTo(temp_date1)>0) {
                edited_election.setStarting(LocalDateTime.of(temp_date1, edited_election.getStarting().toLocalTime()));
                changed= true;
            } else return ERROR;
        }
        if (new_start_hour!=null && !new_start_hour.isBlank()) {
            LocalTime temp_hour1= inputs.checkHourFormat(new_start_hour);
            if (temp_hour1!=null && (LocalDate.now().compareTo(edited_election.getStarting().toLocalDate())>0 || (LocalDate.now().compareTo(edited_election.getStarting().toLocalDate())==0 && LocalTime.now().compareTo(temp_hour1)>0) )) {
                edited_election.setStarting(LocalDateTime.of(edited_election.getStarting().toLocalDate(), temp_hour1));
                changed= true;
            } else return ERROR;
        }
        if (new_end_date!=null && !new_end_date.isBlank()) {
            LocalDate temp_date2= inputs.checkDateFormat(new_end_date);
            if (temp_date2!=null && LocalDate.now().compareTo(edited_election.getStarting().toLocalDate())>0) {
                edited_election.setEnding(LocalDateTime.of(temp_date2, edited_election.getEnding().toLocalTime()));
                changed= true;
            } else return ERROR;
        }
        if (new_end_hour!=null && !new_end_hour.isBlank()) {
            LocalTime temp_hour2= inputs.checkHourFormat(new_end_hour);
            if (temp_hour2!=null) {
                edited_election.setEnding(LocalDateTime.of(edited_election.getEnding().toLocalDate(), temp_hour2));
                changed= true;
            } else return ERROR;
        }
        if (!changed) return ERROR;
		boolean result= getRMIConnection().updateElection(edited_election);
		if (result) return SUCCESS;
		return ERROR;
	}

    public String getUnstartedElections() throws RemoteException {
        ArrayList<String> available_elections= getRMIConnection().getElectionsByState("unstarted");

        if (!available_elections.isEmpty()){
            for (String election : available_elections) unstarted_elections += election +"\n";
        } else unstarted_elections = "*Sem eleições para votar*";
        return SUCCESS;
    }

    public String getUnstarted_elections() {
        return unstarted_elections;
    }
    public void setUnstarted_elections(String unstarted_elections) {
        this.unstarted_elections = unstarted_elections;
    }
	public void setOld_title(String old_title) {
		this.old_title = old_title;
	}
    public void setNew_title(String new_title) {
		this.new_title = new_title;
	}
	public void setNew_description(String new_description) {
		this.new_description = new_description;
	}
	public void setNew_start_date(String new_start_date) {
		this.new_start_date = new_start_date;
	}
	public void setNew_start_hour(String new_start_hour) {
		this.new_start_hour = new_start_hour;
	}
	public void setNew_send_date(String new_end_date) {
		this.new_end_date = new_end_date;
	}
	public void setNew_end_hour(String new_end_hour) {
		this.new_end_hour = new_end_hour;
	}

}
