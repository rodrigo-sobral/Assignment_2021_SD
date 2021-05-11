package classes;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user_type;    //  Professor, Estudante ou Funcionario
    private String name, password, address, phone_number;
    private String cc_number, cc_shelflife;
    private String college, department;

    public User() { }
    public User(String user_type, String name, String password, String address, String phone_number, String cc_number, String cc_shelflife, String college, String department) {
        if (user_type!=null) setUser_type(user_type);
        if (name!=null) setName(name);
        if (password!=null) setPassword(password);
        if (address!=null) setAddress(address);
        if (phone_number!=null) setPhone_number(phone_number);
        if (cc_number!=null) setCc_number(cc_number);
        if (cc_shelflife!=null) setCc_shelflife(cc_shelflife);
        if (college!=null) setCc_shelflife(college);
        if (department!=null) setCc_shelflife(department);
    }
    
    public String getUser_type() { return user_type; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public String getPhone_number() { return phone_number;}
    public String getCc_number() { return cc_number; }
    public String getCc_shelflife() { return cc_shelflife; }
    public String getCollege() { return college; }
    public String getDepartment() { return department; }
    
    public boolean setUser_type(String user_type) { 
        if (user_type.compareTo("Funcionario")==0 || user_type.compareTo("Estudante")==0 || user_type.compareTo("Professor")==0) {
            this.user_type = user_type; 
            return true;
        } else return false;
    }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }
    public void setCc_number(String cc_number) { this.cc_number = cc_number; }
    public void setCc_shelflife(String cc_shelflife) { this.cc_shelflife = cc_shelflife; }
    public void setCollege(String college) { this.college = college; }
    public void setDepartment(String department) { this.department = department; }
    
    @Override
    public String toString() {
        return "Faculdade: "+this.getCollege()+     "\tDepartamento: "+this.getDepartment()+"\t"    +this.user_type+   "\tNome: "+this.name;
    }
         
}
