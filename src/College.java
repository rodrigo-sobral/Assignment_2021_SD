import java.util.ArrayList;


public class College {
    private String name;
    private ArrayList<Department> departments= new ArrayList<>();
    
    public String getName() { return name; }
    public ArrayList<Department> getDepartments() { return departments; }
    
    public void setName(String name) { this.name = name; }
    public void setDepartments(ArrayList<Department> departments) { this.departments = departments; }
    
    public College(String name, Department department) {
        this.name = name;
        this.departments.add(department);
    }
    
    public String toString(String users_type) {
        String final_string="";
        for (Department department : this.departments) final_string+=this.name+"\t"+department.toString(users_type);
        return final_string;
    }
}
