package webserver.action;

import java.rmi.RemoteException;

import rmiserver.classes.User;

public class RegistUser extends Action {
	private String user_type;    //  Professor, Estudante ou Funcionario
    private String name, password, address, phone_number;
    private String cc_number, cc_shelflife;
    private String college, department;

	@Override
	public String execute() throws RemoteException {
		if (user_type!=null && name!=null && password!=null && address!=null && phone_number!=null && cc_number!=null && cc_shelflife!=null && college!=null && department!=null) {
			if (!checkString(name) || !checkPassword(password) || !checkString(address) || !checkPhoneNumber(phone_number) || !checkStringInteger(cc_number) || !checkValidity(cc_shelflife) || !checkString(college) || !checkString(department)) 
				return ERROR;
			if (user_type.compareTo("Estudante")==0 || user_type.compareTo("Professor")==0 || user_type.compareTo("Funcionario")==0) {
                User new_user = new User(user_type, name, password, address, phone_number, cc_number, cc_shelflife, college, department);
				if (getRMIConnection().registUser(new_user)) return SUCCESS;
				return ERROR;
            } return ERROR;
		} return ERROR;
	}	

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public void setCc_number(String cc_number) {
		this.cc_number = cc_number;
	}
	public void setCc_shelflife(String cc_shelflife) {
		this.cc_shelflife = cc_shelflife;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public void setDepartment(String department) {
		this.department = department;
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
