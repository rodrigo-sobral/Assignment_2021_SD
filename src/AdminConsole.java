//	Default
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import classes.Candidature;
import classes.Election;
import classes.User;


public class AdminConsole extends RMIClient {
    private static final long serialVersionUID = 1L;
    private static Inputs input_manage= new Inputs();
    private static AdminConsole admin;

    public AdminConsole() throws RemoteException { super(); }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
                
		admin = new AdminConsole();
        admin.connect2Servers();
		admin.adminMenu();
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
            System.out.print("| Opcao: ");
            option= input_manage.checkIntegerOption(keyboard.nextLine(), option, 4);
            
            if (option==1) {
                managePeople(keyboard, option);
            } else if (option==2) {
                manageElections(keyboard, option);
            } else if (option==3) {
                manageCandidatures(keyboard, option);
            } else if (option==4) {
                manageVoteTables(keyboard, option);
            } else {
                System.out.println("A encerrar...");
                try { TimeUnit.SECONDS.sleep(3); }
                catch (Exception e1) { }
                keyboard.close();
                return;
            }
        }
    }

    private void managePeople(Scanner keyboard, int option) {
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|           Gerir Pessoas            |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Pessoa                |");
            System.out.println("|  2: Eliminar Pessoa                |");
            System.out.println("|  3: Editar Pessoa                  |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            System.out.print("| Opcao: ");
            String str_option = keyboard.nextLine();
            option= input_manage.checkIntegerOption(str_option, option, 3);
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
    private boolean manageElections(Scanner keyboard, int option) {
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|           Gerir Eleicoes           |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Eleicao               |");
            System.out.println("|  2: Eliminar Eleicao               |");
            System.out.println("|  3: Editar Eleicao                 |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            System.out.print("| Opcao: ");
            String str_option = keyboard.nextLine();
            option= input_manage.checkIntegerOption(str_option, option, 3);
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
            keyboard.close();
            return false;
        }
    }
    private boolean manageCandidatures(Scanner keyboard, int option) {
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|         Gerir Candidaturas         |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Candidatura           |");
            System.out.println("|  2: Eliminar Candidatura           |");
            System.out.println("|  3: Editar Candidatura             |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            System.out.print("| Opcao: ");
            String str_option = keyboard.nextLine();
            option= input_manage.checkIntegerOption(str_option, option, 3);
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
            keyboard.close();
            return false;
        }
    }
    private boolean manageVoteTables(Scanner keyboard, int option) {
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|        Gerir Mesas de Voto         |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Mesa de Voto          |");
            System.out.println("|  2: Eliminar Mesa de Voto          |");
            System.out.println("|  3: Editar Mesa de Voto            |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            System.out.print("| Opcao: ");
            String str_option = keyboard.nextLine();
            option= input_manage.checkIntegerOption(str_option, option, 3);
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
            keyboard.close();
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
        try { 
            System.out.println(admin.getServer1().registUser(new_college, new_department, new_user)); 
        } 
        catch (Exception e1) {
            try { 
                System.out.println(admin.getServer2().registUser(new_college, new_department, new_user));
            } 
            catch (Exception e2) { System.out.println("Nao ha servers.\n"+e2); }
        }
    }
    private void addElection(Scanner keyboard) {
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
            System.out.println("400: A Data de Fim tem de ser posterior ao dia "+temp_date1.toString()+"!");
            temp_date2= input_manage.askDate(keyboard, "Data de Fim da Eleicao [dd/mm/aaaa]: ");
        } 
        LocalTime temp_hour2= input_manage.askHour(keyboard, "Hora de Fim da Eleicao [hh:mm]: ");        
        new_election.setEnding(LocalDateTime.of(temp_date2, temp_hour2));

        try { 
            System.out.println(admin.getServer1().registElection(new_election));
        } 
        catch (Exception e1) {
            try { 
                System.out.println(admin.getServer2().registElection(new_election));
            } 
            catch (Exception e2) { System.out.println("Nao ha servers.\n"+e2); }
        }
    }
    private void addCandidature(Scanner keyboard) {

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

    public int checkIntegerOption(String str_option, int option, int option_limit) {
        try {
            option= Integer.parseInt(str_option);
            if (option>=0 && option<=option_limit) return option;
            else throw new Exception("Error! Invalid Option");
        } catch (Exception e) {
            System.out.println(e);
            try { TimeUnit.SECONDS.sleep(3); }
            catch (Exception e1) { }
            return -1;
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