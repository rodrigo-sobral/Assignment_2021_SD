package classes;

public interface UserInterface {
    public String getUser_type();
    public String getName();
    public String getPassword();
    public String getAddress();
    public String getPhone_number();
    public String getDepartment();
    public String getCollege();
    public String getCc_number();
    public String getCc_shelflife();
    
    public boolean setUser_type(String user_type);
    public void setName(String name);
    public void setPassword(String password);
    public void setAddress(String address);
    public void setPhone_number(String phone_number);
    public void setDepartment(String department);
    public void setCollege(String college);
    public void setCc_number(String cc_number);
    public void setCc_shelflife(String cc_shelflife);
        
}
