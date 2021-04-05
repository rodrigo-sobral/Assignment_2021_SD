import java.io.Serializable;
import java.util.ArrayList;


public class College implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private ArrayList<Department> departments= new ArrayList<>();
    
    public String getName() { return name; }
    public ArrayList<Department> getDepartments() { return departments; }
        
    public College(String name, Department department) {
        this.name = name;
        this.departments.add(department);
    }

}
