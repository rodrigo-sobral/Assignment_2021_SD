import java.io.Serializable;
import java.util.ArrayList;

import classes.User;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name, college;
    private boolean vote_table;
    private boolean activated_vote_table;
    private int vote_terminals;
    private ArrayList<User> students= new ArrayList<>();
    private ArrayList<User> teachers= new ArrayList<>();
    private ArrayList<User> staff= new ArrayList<>();
    
    public Department(String name, String college, User new_user) {
        this.name = name;
        this.college= college;
        if (new_user.getUser_type().compareTo("Estudante")==0) this.getStudents().add(new_user);
        else if (new_user.getUser_type().compareTo("Professor")==0) this.getTeachers().add(new_user);
        else if (new_user.getUser_type().compareTo("Funcionario")==0) this.getStaff().add(new_user);
    }

    //  GETTERS
    public String getName() { return name; }
    public String getCollege() { return college; }
    public boolean getVoteTable() { return vote_table; }
    public boolean getActivatedVoteTable() { return activated_vote_table; }
    public int getVoteTerminals() { return vote_terminals; }
    public ArrayList<User> getStudents() { return students; }
    public ArrayList<User> getTeachers() { return teachers; }
    public ArrayList<User> getStaff() { return staff; }
    public ArrayList<User> getUsersWithType(String user_type) {
        if (user_type.compareTo("Professor")==0) return this.teachers;
        if (user_type.compareTo("Estudante")==0) return this.students;
        if (user_type.compareTo("Funcionario")==0) return this.staff;
        return null;
    }

    //  SETTERS
    public void setName(String name) { this.name = name; }
    public void setCollege(String college) { this.college = college; }
    public void setStudents(ArrayList<User> students) { this.students = students; }
    public void setTeachers(ArrayList<User> teachers) { this.teachers = teachers; }
    public void setStaff(ArrayList<User> staff) { this.staff = staff; }
    public void createVoteTable(int num_vote_terminals) { this.vote_table=true; this.activated_vote_table=false; this.vote_terminals=num_vote_terminals; }
    public void deleteVoteTable() { this.vote_table=false; this.activated_vote_table=false; this.vote_terminals=0; }
    
    public void turnOnVoteTable() { if (this.vote_table) this.activated_vote_table=true; }
    public void turnOffVoteTable() { if (this.vote_table) this.activated_vote_table=false; }


    public String toString(String user_type) {
        String final_string="";
        if (user_type=="all" || user_type.compareTo("Professor")==0) {
            for (User user : this.teachers) final_string+="Departamento: "+this.name+"\t"+user+"\n";
        } if (user_type=="all" || user_type.compareTo("Estudante")==0) {
            for (User user : this.students) final_string+="Departamento: "+this.name+"\t"+user+"\n";
        } if (user_type=="all" || user_type.compareTo("Funcionario")==0) {
            for (User user : this.staff) final_string+="Departamento: "+this.name+"\t"+user+"\n";
        } return final_string;
    }
    
}
