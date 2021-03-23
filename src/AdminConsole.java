//	Default
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import classes.Candidature;
//import classes.Candidature;
import classes.Election;
import classes.User;


public class AdminConsole extends RMIClient {
    private static final long serialVersionUID = 1L;
    private static Inputs input_manage= new Inputs();
    private static AdminConsole admin;

    public AdminConsole() throws RemoteException { 
        super(); 
        isAdmin=true; 
    }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
                
		admin = new AdminConsole();
        admin.connect2Servers(admin);
		admin.adminMenu();
        try {
            System.out.println(admin.getServer1().unsubscribeNewClient(admin));
        } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().unsubscribeNewClient(admin)); }
            catch (Exception e2) { System.out.println("500: Nao ha servers.\n");}
        }
        System.exit(0);
    }

    private void adminMenu() {
        int option=0;
        Scanner keyboard= new Scanner(System.in);
        while (true) {
            try { Runtime.getRuntime().exec("cls"); }
            catch (Exception e) { }
            System.out.println("|====================================|");
            System.out.println("|             Menu Admin             |");
            System.out.println("|====================================|");
            System.out.println("|  1: Gerir Pessoas                  |");
            System.out.println("|  2: Gerir Eleicoes                 |");
            System.out.println("|  3: Gerir Candidaturas             |");
            System.out.println("|  4: Gerir Mesas de Voto            |");
            System.out.println("|  0: Sair                           |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 4);
            
            if (option==1) {
                managePeople(keyboard);
            } else if (option==2) {
                manageElections(keyboard);
            } else if (option==3) {
                manageCandidatures(keyboard);
            } else if (option==4) {
                manageVoteTables(keyboard);
            } else {
                System.out.println("A encerrar...");
                try { TimeUnit.SECONDS.sleep(3); }
                catch (Exception e1) { }
                keyboard.close();
                return;
            }
        }
    }

    private void managePeople(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|           Gerir Pessoas            |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Pessoa                |");
            System.out.println("|  2: Eliminar Pessoa                |");
            System.out.println("|  3: Editar Pessoa                  |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 3);
            if (option!=-1) break;
        }
        if (option==1) {
            addPerson(keyboard);
        } else if (option==2) {
        } else if (option==3){
        } else {
            System.out.println("Voltando para o Menu Admin...");
            try { TimeUnit.SECONDS.sleep(3); }
            catch (Exception e1) {
            }
        }
    }
    private boolean manageElections(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|           Gerir Eleicoes           |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Eleicao               |");
            System.out.println("|  2: Eliminar Eleicao               |");
            System.out.println("|  3: Editar Eleicao                 |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 3);
            if (option!=-1) break;
        }
        if (option==1) {
            addElection(keyboard);
            return true;
        } else if (option==2) {
            return true;
        } else if (option==3){
            return true;
        } else {
            System.out.println("Voltando para o Menu Admin...");
            try { TimeUnit.SECONDS.sleep(3); }
            catch (Exception e1) { }
            return false;
        }
    }
    private boolean manageCandidatures(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|         Gerir Candidaturas         |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Candidatura           |");
            System.out.println("|  2: Eliminar Candidatura           |");
            System.out.println("|  3: Editar Candidatura             |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 3);
            if (option!=-1) break;
        }
        if (option==1) {
            addCandidature(keyboard);
            return true;
        } else if (option==2) {
            return true;
        } else if (option==3){
            return true;
        } else {
            System.out.println("Voltando para o Menu Admin...");
            try { TimeUnit.SECONDS.sleep(3); }
            catch (Exception e1) { }
            return false;
        }
    }
    private boolean manageVoteTables(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|        Gerir Mesas de Voto         |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Mesa de Voto          |");
            System.out.println("|  2: Eliminar Mesa de Voto          |");
            System.out.println("|  3: Editar Mesa de Voto            |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 3);
            if (option!=-1) break;
        }
        if (option==1) {
            return true;
        } else if (option==2) {
            return true;
        } else if (option==3){
            return true;
        } else {
            System.out.println("Voltando para o Menu Admin...");
            try { TimeUnit.SECONDS.sleep(3); }
            catch (Exception e1) { }
            return false;
        }
    }

    private void addPerson(Scanner keyboard) {
        String new_college, new_department;
        User new_user= new User();
        while(!new_user.setUser_type(input_manage.askVariable(keyboard, "Funcao [Funcionario/Professor/Estudante]: ", 0)));
        new_user.setName(input_manage.askVariable(keyboard, "Nome: ", 0));
        new_user.setPassword(input_manage.askVariable(keyboard, "Password: ", 1));
        new_user.setAddress(input_manage.askVariable(keyboard, "Morada: ", 0));
        new_user.setPhone_number(input_manage.askVariable(keyboard, "Contacto Telefonico: ", 4));
        new_college= input_manage.askVariable(keyboard, "Faculdade: ",0);
        new_department= input_manage.askVariable(keyboard, "Departamento: ", 0);
        new_user.setCc_number(input_manage.askVariable(keyboard, "Numero Cartao Cidadao: ", 2));
        new_user.setCc_shelflife(input_manage.askVariable(keyboard, "Validade: ", 3));

        new_user.setCollege(new_college);
        new_user.setDepartment(new_department);
        try { 
            System.out.println(admin.getServer1().registUser(new_college, new_department, new_user)); 
        } 
        catch (Exception e1) {
            try { 
                System.out.println(admin.getServer2().registUser(new_college, new_department, new_user));
            } 
            catch (Exception e2) { System.out.println("500: Nao ha servers.\n"); }
        }
    }
    private void addElection(Scanner keyboard) {
        int option;
        Election new_election= new Election();

        while(!new_election.setElection_type(input_manage.askVariable(keyboard, "Tipo Eleicao [Funcionario/Professor/Estudante]: ", 0)));

        new_election.setTitle(input_manage.askVariable(keyboard, "Titulo: ", 0));
        new_election.setDescription(input_manage.askVariable(keyboard, "Descricao: ", 5));
        
        LocalDate temp_date1= input_manage.askDate(keyboard, "Data de Inicio da Eleicao [dd/mm/aaaa]: ");
        while (LocalDate.now().compareTo(temp_date1)>=0) {
            System.out.println("400: A Data de Inicio tem de ser posterior ao dia de hoje!");
            temp_date1= input_manage.askDate(keyboard, "Data de Inicio da Eleicao [dd/mm/aaaa]: ");
        }
        LocalTime temp_hour1= input_manage.askHour(keyboard, "Hora de Inicio da Eleicao [hh:mm]: ");        
        
        new_election.setStarting(LocalDateTime.of(temp_date1, temp_hour1));

        LocalDate temp_date2= input_manage.askDate(keyboard, "Data de Fim da Eleicao [dd/mm/aaaa]: ");
        while (temp_date1.compareTo(temp_date2)>=0) {
            System.out.println("400: A Data de Fim tem de ser posterior ao dia "+new_election.getStartingString()+"!");
            temp_date2= input_manage.askDate(keyboard, "Data de Fim da Eleicao [dd/mm/aaaa]: ");
        } 
        LocalTime temp_hour2= input_manage.askHour(keyboard, "Hora de Fim da Eleicao [hh:mm]: ");        

        new_election.setEnding(LocalDateTime.of(temp_date2, temp_hour2));
        
        while (true) {
            option = input_manage.checkIntegerOption(keyboard, "Pretende restringir a eleicao?\n[0-Nao | 1-Sim]: ", 0, 1);
            if (option==-1) { System.out.println("Erro: Insira uma opcao valida!"); continue; }
            else if (option==0) break;
            else if (option==1) {
                while (true) {
                    option = input_manage.checkIntegerOption(keyboard, "Pretende aplicar a restricao a Faculdades ou Departamentos?\n[0-Voltar | 1-Faculdades | 2-Departamentos]: ", 0, 2);
                    if (option==-1) { System.out.println("Erro: Insira uma opcao valida!"); continue; }
                    else if (option==1) new_election.getCollege_restrictions().add(input_manage.askCollege(admin, keyboard, new_election.getCollege_restrictions()));
                    else if (option==2) new_election.getDepartment_restrictions().add(input_manage.askDepartment(admin, keyboard, new_election.getDepartment_restrictions()));
                    break;
                }
            }
        }

        try { System.out.println(admin.getServer1().registElection(new_election)); } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().registElection(new_election)); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers\n"+e2); }
        }
    }
    private void addCandidature(Scanner keyboard) {
        ArrayList<Election> available_elections= new ArrayList<>();
        int option;
        Candidature new_candidature= new Candidature(input_manage.askVariable(keyboard, "Titulo: ", 0));

        //  Get Election to the new Candidature
        try { available_elections= admin.getServer1().getElections(); } 
        catch (Exception e1) {
            try { available_elections= admin.getServer2().getElections(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); }
        }
        if (available_elections.isEmpty()) { System.out.println("Erro: Ainda nao ha Eleicoes registadas!"); return; }
        System.out.println("----------------------------------------");
        System.out.println("Eleicoes Disponiveis");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_elections.size(); i++) 
            System.out.println(i+1+": "+available_elections.get(i).getTitle()+"\t"+available_elections.get(i).getStartingString()+"\t"+available_elections.get(i).getEndingString()+"\t"+available_elections.get(i).getDescription());
        System.out.println("----------------------------------------");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 1, available_elections.size())-1;

        //  Get Users who can Candidate to this Election
        ArrayList<String> collegs_restricts= available_elections.get(option).getCollege_restrictions();
        ArrayList<String> deps_restricts= available_elections.get(option).getDepartment_restrictions();
        String user_type_restrics= available_elections.get(option).getElection_type();

        ArrayList<User> users_restricts= new ArrayList<>();

        //  Get Users available from specific Colleges
        for (String college : collegs_restricts) {
            try { 
                for (Department department : admin.getServer1().getUniqueCollege(college).getDepartments()) 
                    users_restricts.addAll(department.getUsersWithType(user_type_restrics));
            } catch (Exception e1) {
                try {
                    for (Department department : admin.getServer2().getUniqueCollege(college).getDepartments()) 
                    users_restricts.addAll(department.getUsersWithType(user_type_restrics));
                } catch (Exception e2) { System.out.println("500: Nao ha servers"); }
            }
        }
        //  Get Users available from specific Departments
        for (String department : deps_restricts) {
            try { users_restricts.addAll(admin.getServer1().getUniqueDepartment(department).getUsersWithType(user_type_restrics)); } 
            catch (Exception e1) {
                try { users_restricts.addAll(admin.getServer2().getUniqueDepartment(department).getUsersWithType(user_type_restrics)); } 
                catch (Exception e2) { System.out.println("500: Nao ha servers"); }
            }
        }

        if (users_restricts.isEmpty()) { System.out.println("Erro: Ainda nao ha Candidatos abrangidos pelas restricoes aplicadas!"); return; }
        //  ASK CANDIDATES TO ADMIN
        System.out.println("----------------------------------------");
        System.out.println("Candidatos Disponiveis");
        System.out.println("----------------------------------------");
        for (User user : users_restricts) System.out.println(user);
        System.out.println("----------------------------------------");
        ArrayList<Integer> options= input_manage.checkSeveralIntegerOptions(keyboard, "Opcao [n1,n2,n3]: ", 1, users_restricts.size());
        for (Integer integer : options) new_candidature.getCandidates().add(users_restricts.get(integer-1));
        
        //  SEND ELECTION SELECTED UPDATED TO RMI SERVER
        available_elections.get(option).getCandidatures_to_election().add(new_candidature);
        try { System.out.println(admin.getServer1().setUpdatedElection(available_elections.get(option))); } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer1().setUpdatedElection(available_elections.get(option))); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); }
        }
    }
}


/**
 * Inputs
 */
class Inputs {

    /**
     * 
     * @param keyboard
     * @param message
     * @param input_type 0 to strings with no numbers
     * 1 to strings passwords
     * 2 to numbers with no hifen
     * 3 to validity
     * 4 to phone numbers
     * 5 string with no rules
     * @return inputed string well formated
     */
    public String askVariable(Scanner keyboard, String message, int input_type) {
        String str;
        while (true) {
            System.out.print(message);
            str= keyboard.nextLine();
            if (input_type==0 && this.checkString(str)) return str;
            if (input_type==1 && this.checkPassword(str)) return str;
            if (input_type==2 && this.checkStringInteger(str)) return str;
            if (input_type==3 && this.checkValidity(str)) return str;
            if (input_type==4 && this.checkPhoneNumber(str)) return str;
            if (input_type==5) return str;
        } 
    }
    
    public LocalDate askDate(Scanner keyboard, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                System.out.print(message);
                LocalDate converted_date = LocalDate.parse(keyboard.nextLine(), formatter);
                return converted_date;
            } catch (Exception e) { 
                System.out.println("400: Data Invalida.");
            }
        }
    }
    public LocalTime askHour(Scanner keyboard, String message) {
        while (true) {
            try { 
                System.out.print(message);
                return LocalTime.parse(keyboard.nextLine()); } 
            catch (Exception e) { System.out.println("400: Hora Invalida."); }
        }
    }

    public String askCollege(AdminConsole admin, Scanner keyboard, ArrayList<String> college_restrictions) {
        ArrayList<String> available_colleges;
        int option;
        
        //  Get College Names from RMI Server
        try { available_colleges = admin.getServer1().getCollegesNames(); } 
        catch (Exception e1) {
            try { available_colleges = admin.getServer2().getCollegesNames(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); return "";}
        }
        
        //  There's no registed Colleges
        if (available_colleges.isEmpty()) { System.out.println("Erro: Ainda nao existem Faculdades registadas!\n"); return ""; }
        
        //  Removing Colleges already registed
        for (String temp_college : college_restrictions) available_colleges.remove(temp_college);
        
        System.out.println("Insira a Faculdade onde ocorrera a Eleicao");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_colleges.size(); i++) System.out.println(i+1+": "+available_colleges.get(i));
        System.out.println("----------------------------------------");
        option= checkIntegerOption(keyboard, "Opcao: ", 1, available_colleges.size());
        return available_colleges.get(option);
    }
    public String askDepartment(AdminConsole admin, Scanner keyboard, ArrayList<String> department_restrictions) {
        ArrayList<String> available_departments;
        int option;

        //  Get Department Names from RMI Server
        try { available_departments = admin.getServer1().getDepartmentsNames(); } 
        catch (Exception e1) {
            try { available_departments = admin.getServer2().getDepartmentsNames(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); return "";}
        }

        //  There's no registed Departments
        if (available_departments.isEmpty()) { System.out.println("Erro: Ainda nao existem Departamentos registados!\n"); return ""; }
        
        //  Removing Departments already registed
        for (String temp_depart : department_restrictions) available_departments.remove(temp_depart);
        
        System.out.println("Insira o Departamento onde ocorrera a Eleicao");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_departments.size(); i++) System.out.println(i+1+": "+available_departments.get(i));
        System.out.println("----------------------------------------");
        option= checkIntegerOption(keyboard, "Opcao: ", 1, available_departments.size());
        return available_departments.get(option);
    }

    public int checkIntegerOption(Scanner keyboard, String message, int min_limit, int max_limit) {
        while (true) {
            System.out.print(message);
            try {
                int option= Integer.parseInt(keyboard.nextLine());
                if (option>=min_limit && option<=max_limit) return option;
                else throw new Exception("Erro: Opcao Invalida!");
            } catch (Exception e) {
                System.out.println(e);
                try { TimeUnit.SECONDS.sleep(3); }
                catch (Exception e1) { }
            }
        }
    }
    public ArrayList<Integer> checkSeveralIntegerOptions(Scanner keyboard, String message, int min_limit, int max_limit) {
        ArrayList<Integer> options= new ArrayList<>();
        while (true) {
            System.out.print(message);
            try {
                String[] str_options= keyboard.nextLine().split(",");
                int option;
                for (String temp_option : str_options) {
                    option= Integer.parseInt(temp_option);
                    if (option>=min_limit && option<=max_limit && !options.contains(option)) options.add(option);
                    else throw new Exception();
                } return options;
            } catch (Exception e) {
                System.out.println("Erro: Opcao Invalida!");
                try { TimeUnit.SECONDS.sleep(3); }
                catch (Exception e1) { }
            }
        }
    }
    public boolean checkString(String s) {
        if (s.isEmpty()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkPassword(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isSpaceChar(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkPhoneNumber(String s) {
        if (s.isEmpty() || s.length()!=9) return false;
        if (Character.compare(s.charAt(0), '9')!=0 || (Character.compare(s.charAt(1), '1')!=0 && Character.compare(s.charAt(1), '2')!=0 && Character.compare(s.charAt(1), '3')!=0 && Character.compare(s.charAt(1), '6')!=0)) 
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkStringInteger(String s) {
        if (s.isEmpty()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0)) && !Character.isDigit(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkValidity(String s) {
        if (s.isEmpty() || s.length()!=5) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i==2 && Character.compare(s.charAt(2),'-')!=0) return false;
            else if (i!=2 && !Character.isDigit(s.charAt(i))) return false;
        } 
        String aux= s.split("-")[0];
        if (aux.compareTo("01")>0 || aux.compareTo("12")<0) return true;
        else return false;
    }
    
}