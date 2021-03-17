//	Default
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class AdminConsole extends RMIClient {
    private static final long serialVersionUID = 1L;
    //private static AdminConsole admin;
	public AdminConsole() throws RemoteException { super(); }

    public static void main(String[] args) {
		//admin = new AdminConsole();
		adminMenu();
    }

    private static void adminMenu() {
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
            option= checkIntegerOption(keyboard.nextLine(), option, 4);
            if (option!=-1) break;
            
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
        keyboard.close();
    }

    private static boolean managePeople(Scanner keyboard, int option) {
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
            option= checkIntegerOption(str_option, option, 3);
            if (option!=-1) break;
        }
        if (option==1) {
            addPerson(keyboard);
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
    private static boolean manageElections(Scanner keyboard, int option) {
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
            option= checkIntegerOption(str_option, option, 3);
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
    private static boolean manageCandidatures(Scanner keyboard, int option) {
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
            option= checkIntegerOption(str_option, option, 3);
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
    private static boolean manageVoteTables(Scanner keyboard, int option) {
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
            option= checkIntegerOption(str_option, option, 3);
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

    private static void addPerson(Scanner keyboard) {
        System.out.print("Nome: ");
        //String str_option = keyboard.nextLine();
        
    }


    private static int checkIntegerOption(String str_option, int option, int option_limit) {
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
}
