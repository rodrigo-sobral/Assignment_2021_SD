package webserver.action;

import java.rmi.RemoteException;

import rmiserver.classes.User;

public class RegistVoteTable extends Action {
	private static final long serialVersionUID = 4L;

	@Override
	public String execute() throws RemoteException {
		return SUCCESS;
	}
	
	public boolean checkString(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkPassword(String s) {
        if (s.isBlank()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isSpaceChar(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkPhoneNumber(String s) {
        if (s.isBlank() || s.length()!=9) return false;
        if (Character.compare(s.charAt(0), '9')!=0 || (Character.compare(s.charAt(1), '1')!=0 && Character.compare(s.charAt(1), '2')!=0 && Character.compare(s.charAt(1), '3')!=0 && Character.compare(s.charAt(1), '6')!=0)) 
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkStringInteger(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0)) && !Character.isDigit(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkValidity(String s) {
        if (s.isBlank() || s.length()!=5) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i==2 && Character.compare(s.charAt(2),'-')!=0) return false;
            else if (i!=2 && !Character.isDigit(s.charAt(i))) return false;
        } 
        int aux0= Integer.parseInt(s.split("-")[0]), aux1= Integer.parseInt(s.split("-")[1]);
        if (aux0>0 && aux0<=12 && aux1>=21) return true;
        else return false;
    }
}
