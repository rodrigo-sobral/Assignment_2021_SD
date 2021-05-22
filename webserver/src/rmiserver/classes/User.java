package rmiserver.classes;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user_type;    //  Professor, Estudante ou Funcionario
    private String name, password, address, phone_number,nome_id,id_fb,acess_token;
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
        if (college!=null) setCollege(college);
        if (department!=null) setDepartment(department);
        nome_id="";
        id_fb ="";
        acess_token="";
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
    public String getNome_id() {return nome_id;}
    public String getId_fb() {return id_fb;}
    public String getAcess_token() {return acess_token;}
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
    public void setId_fb(String id_fb) {this.id_fb = id_fb;}
    public void setNome_id(String nome_id) {this.nome_id = nome_id;}
    public void setAcess_token(String acess_token) {this.acess_token = acess_token;}

    @Override
    public String toString() {
        return "Faculdade: "+college+"\nDepartamento: "+department+"\nNome: "+name+"\nMorada: "+address+"\nFuncao: "+user_type+"\nNOME_ID "+nome_id+"\nFB_ID "+id_fb+"\nAcessToken"+acess_token+" -----------------------------\n";
    }

}