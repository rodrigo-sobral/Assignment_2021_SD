package webserver.action;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import rmiserver.classes.Election;

public class EditElection extends Action {
    private String selected_election, new_description, new_start_date, new_start_hour, new_end_date, new_end_hour;    

	@Override
	public String execute() throws RemoteException {
        boolean changed=false;
        Election edited_election=null;
        if (selected_election!=null && checkString(selected_election)) {
            edited_election= getRMIConnection().getElectionByName(selected_election, "unstarted");
            if (edited_election==null) return ERROR;
        } else return ERROR;
        if (new_description!=null && !new_description.isBlank()) {
            edited_election.setDescription(new_description);
            changed= true;
        }
        if (new_start_date!=null && !new_start_date.isBlank()) {
            LocalDate temp_date1= checkDateFormat(new_start_date);
            if (temp_date1!=null && LocalDate.now().compareTo(temp_date1)<=0) {
                edited_election.setStarting(LocalDateTime.of(temp_date1, edited_election.getStarting().toLocalTime()));
                changed= true;
            } else return ERROR;
        }
        if (new_start_hour!=null && !new_start_hour.isBlank()) {
            LocalTime temp_hour1= checkHourFormat(new_start_hour);
            if (temp_hour1!=null && (LocalDate.now().compareTo(edited_election.getStarting().toLocalDate())<0 || (LocalDate.now().compareTo(edited_election.getStarting().toLocalDate())==0 && LocalTime.now().compareTo(temp_hour1)<0) )) {
                edited_election.setStarting(LocalDateTime.of(edited_election.getStarting().toLocalDate(), temp_hour1));
                changed= true;
            } else return ERROR;
        }
        if (new_end_date!=null && !new_end_date.isBlank()) {
            LocalDate temp_date2= checkDateFormat(new_end_date);
            if (temp_date2!=null && temp_date2.compareTo(edited_election.getStarting().toLocalDate())>0) {
                edited_election.setEnding(LocalDateTime.of(temp_date2, edited_election.getEnding().toLocalTime()));
                changed= true;
            } else return ERROR;
        }
        if (new_end_hour!=null && !new_end_hour.isBlank()) {
            LocalTime temp_hour2= checkHourFormat(new_end_hour);
            if (temp_hour2!=null) {
                edited_election.setEnding(LocalDateTime.of(edited_election.getEnding().toLocalDate(), temp_hour2));
                changed= true;
            } else return ERROR;
        }
        if (!changed) return ERROR;
		boolean result= getRMIConnection().updateElection(edited_election, false);
		if (result) return SUCCESS;
		return ERROR;
	}

    public void setSelected_election(String selected_election) {
        this.selected_election = selected_election;
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
	public void setNew_end_date(String new_end_date) {
		this.new_end_date = new_end_date;
	}
	public void setNew_end_hour(String new_end_hour) {
		this.new_end_hour = new_end_hour;
	}


    public boolean checkString(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    
    public LocalDate checkDateFormat(String date) {
        try { return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")); } 
        catch (Exception e) { return null; }
    }

    public LocalTime checkHourFormat(String hour) {
        try { return LocalTime.parse(hour); } 
        catch (Exception e) { return null; }
    }
}
