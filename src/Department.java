import java.util.ArrayList;

import classes.User;

public class Department {
    private String name;
    private MCServer vote_table= new MCServer();
    private ArrayList<User> students= new ArrayList<>();
    private ArrayList<User> teachers= new ArrayList<>();
    private ArrayList<User> staff= new ArrayList<>();
    
    public String getName() { return name; }
    public MCServer getVote_table() { return vote_table; }
    public ArrayList<User> getStudents() { return students; }
    public ArrayList<User> getTeachers() { return teachers; }
    public ArrayList<User> getStaff() { return staff; }
    
    public Department(String name, User new_user) {
        this.name = name;
        if (new_user.getUser_type().compareTo("Estudante")==0) this.getStudents().add(new_user);
        else if (new_user.getUser_type().compareTo("Professor")==0) this.getTeachers().add(new_user);
        else if (new_user.getUser_type().compareTo("Funcionario")==0) this.getStaff().add(new_user);
    }
    
    public void setName(String name) { this.name = name; }
    public void setVote_table(MCServer vote_table) { this.vote_table = vote_table; }
    public void setStudents(ArrayList<User> students) { this.students = students; }
    public void setTeachers(ArrayList<User> teachers) { this.teachers = teachers; }
    public void setStaff(ArrayList<User> staff) { this.staff = staff; }

    public boolean verifyUserExistence(User comparing_user) {
        for (User user : this.getStudents()) {
            if (comparing_user.getCc_number().compareTo(user.getCc_number())==0) return true;
        }
        for (User user : this.getTeachers()) {
            if (comparing_user.getCc_number().compareTo(user.getCc_number())==0) return true;
        }
        for (User user : this.getStaff()) {
            if (comparing_user.getCc_number().compareTo(user.getCc_number())==0) return true;
        }
        return false;
    }

    public String toString(String user_type) {
        String final_string="";
        if (user_type=="all" || user_type.compareTo("Professor")==0) {
            for (User user : this.teachers) final_string+="Departamento: "+this.name+"\t"+user+"\n";
        } if (user_type=="all" || user_type.compareTo("Estudante")==0) {
            for (User user : this.students) final_string+="Departamento: "+this.name+"\t"+user+"\n";
        } if (user_type=="all" || user_type.compareTo("Funcionario")==0) {
            for (User user : this.staff) final_string+="Departamento: "+this.name+"\t"+user+"\n";
        } 
        return final_string;
    }
}
