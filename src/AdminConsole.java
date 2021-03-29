//	Default
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import classes.Candidature;
import classes.Election;
import classes.User;


public class AdminConsole extends RMIClient {
    private static final long serialVersionUID = 1L;
    private static Inputs input_manage= new Inputs();
    private static AdminConsole admin;

    public AdminConsole() throws RemoteException { super(); }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy","AdminConsole.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
                
		admin = new AdminConsole();
        admin.connect2Servers();
        admin.subscribe2Servers(admin, null);
		admin.adminMenu();
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
            
            if (option==1) managePeople(keyboard);
            else if (option==2) manageElections(keyboard);
            else if (option==3) manageCandidatures(keyboard);
            else if (option==4) manageVoteTables(keyboard);
            else {
                input_manage.messageToWait("A encerrar...");
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
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 1);
            if (option!=-1) break;
        }
        if (option==1) addPerson(keyboard);
        else { input_manage.messageToWait("Voltando para o Menu Admin..."); return ; }
    }
    private void manageElections(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|           Gerir Eleicoes           |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Eleicao               |");
            System.out.println("|  2: Editar Eleicao                 |");
            System.out.println("|  3: Consultar Eleicoes Passadas    |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 3);
            if (option!=-1) break;
        }
        if (option==1) addElection(keyboard);
        else if (option==2) editElection(keyboard);
        else if (option==3) consultPassedElections(keyboard);
        else { input_manage.messageToWait("Voltando para o Menu Admin..."); return ; }
    }
    private void manageCandidatures(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|         Gerir Candidaturas         |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Candidatura           |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 1);
            if (option!=-1) break;
        }
        if (option==1) addCandidature(keyboard);
        else { input_manage.messageToWait("Voltando para o Menu Admin..."); return ; }
    }
    private void manageVoteTables(Scanner keyboard) {
        int option;
        while (true) {
            System.out.println("|====================================|");
            System.out.println("|        Gerir Mesas de Voto         |");
            System.out.println("|====================================|");
            System.out.println("|  1: Registar Mesa de Voto          |");
            System.out.println("|  2: Eliminar Mesa de Voto          |");
            System.out.println("|  3: Consultar Mesas de Voto        |");
            System.out.println("|  0: Voltar                         |");
            System.out.println("|====================================|");
            option= input_manage.checkIntegerOption(keyboard, "| Opcao: ", 0, 3);
            if (option!=-1) break;
        }
        if (option==1) addVoteTable(keyboard);
        else if (option==2) deleteVoteTable(keyboard);
        else if (option==3) consultVoteTables(keyboard);
        else { input_manage.messageToWait("Voltando para o Menu Admin..."); return ; }
    }

    private void addPerson(Scanner keyboard) {
        String new_college, new_department;
        User new_user= new User();

        //  GET DEFAULT USER DATA
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

        //  SEND NEW USER TO RMI SERVER
        try { System.out.println(admin.getServer1().registUser(new_college, new_department, new_user)); } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().registUser(new_college, new_department, new_user)); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers.\n"); }
        }
    }
    private void addElection(Scanner keyboard) {
        int option;
        Election new_election= new Election();


        //  GET DEFAULT ELECTION DATA
        while(!new_election.setElection_type(input_manage.askVariable(keyboard, "Tipo Eleicao [Funcionario/Professor/Estudante]: ", 0)));

        new_election.setTitle(input_manage.askVariable(keyboard, "Titulo: ", 5));
        new_election.setDescription(input_manage.askVariable(keyboard, "Descricao: ", 5));
        
        LocalDate temp_date1= input_manage.askDate(keyboard, "Data de Inicio da Eleicao [dd/mm/aaaa]: ");
        while (LocalDate.now().compareTo(temp_date1)>0) {
            System.out.println("Erro: A Data de Inicio tem de ser posterior ao dia de hoje!");
            temp_date1= input_manage.askDate(keyboard, "Data de Inicio da Eleicao [dd/mm/aaaa]: ");
        }
        LocalTime temp_hour1= input_manage.askHour(keyboard, "Hora de Inicio da Eleicao [hh:mm]: ");        
        while (LocalDate.now().compareTo(temp_date1)==0 && LocalTime.now().compareTo(temp_hour1)>0) {
            System.out.println("Erro: A Hora de Inicio tem de ser posterior a Hora atual!");
            temp_hour1= input_manage.askHour(keyboard, "Hora de Inicio da Eleicao [hh:mm]: ");        
        }
        
        new_election.setStarting(LocalDateTime.of(temp_date1, temp_hour1));

        LocalDate temp_date2= input_manage.askDate(keyboard, "Data de Fim da Eleicao [dd/mm/aaaa]: ");
        while (temp_date1.compareTo(temp_date2)>0) {
            System.out.println("Erro: A Data de Fim tem de ser posterior ao dia "+new_election.getStartingDateString()+"!");
            temp_date2= input_manage.askDate(keyboard, "Data de Fim da Eleicao [dd/mm/aaaa]: ");
        } 
        LocalTime temp_hour2= input_manage.askHour(keyboard, "Hora de Fim da Eleicao [hh:mm]: ");        
        while (temp_date1.compareTo(temp_date2)==0 && temp_hour1.compareTo(temp_hour2)>0) {
            System.out.println("Erro: A Hora de Fim tem de ser posterior a Hora "+new_election.getStartingHourString()+"!");
            temp_hour2= input_manage.askHour(keyboard, "Hora de Fim da Eleicao [hh:mm]: ");
        }

        new_election.setEnding(LocalDateTime.of(temp_date2, temp_hour2));
    
        //  --------------------------------------------------------------------------------------------------------
        //  RESTRICT ELECTION
        while (true) {
            option = input_manage.checkIntegerOption(keyboard, "Pretende restringir a eleicao?\n[0-Nao | 1-Sim]: ", 0, 1);
            if (option==-1) { System.out.println("Erro: Insira uma opcao valida!"); continue; }
            else if (option==0) break;
            else if (option==1) {
                while (true) {
                    option = input_manage.checkIntegerOption(keyboard, "Pretende aplicar a restricao a Faculdades ou Departamentos?\n[0-Voltar | 1-Faculdades | 2-Departamentos]: ", 0, 2);
                    if (option==-1) { System.out.println("Erro: Insira uma opcao valida!"); continue; }
                    else if (option==1) {
                        String result=input_manage.askCollege(admin, keyboard, new_election.getCollege_restrictions());
                        if (!result.isEmpty()) {
                            new_election.getCollege_restrictions().add(result);
                            System.out.println("Restricao aplicada com Sucesso!\n");
                        }
                    } else if (option==2) {
                        String result= input_manage.askDepartment(admin, keyboard, new_election.getDepartment_restrictions(), false);
                        if (!result.isEmpty()) {
                            new_election.getDepartment_restrictions().add(result);
                            System.out.println("Restricao aplicada com Sucesso!\n");
                        }
                    } 
                    break;
                }
            }
        }
        //  SEND NEW ELECTION TO RMI SERVER
        try { System.out.println(admin.getServer1().registElection(new_election)); } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().registElection(new_election)); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers\n"); }
        }
    }
    private void addCandidature(Scanner keyboard) {
        ArrayList<Election> available_elections= new ArrayList<>();
        Candidature new_candidature= new Candidature();
        int option;

        //  GET ELECTION TO THE NEW CANDIDATURE
        try { available_elections = admin.getServer1().getUnstartedElections(); } 
        catch (Exception e1) {
            try { available_elections = admin.getServer2().getUnstartedElections(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); }
        }
        if (available_elections.isEmpty()) { input_manage.messageToWait("Erro: Nao ha Eleicoes registadas!"); return; }
        
        for (Election election : available_elections) {
            for (Candidature cand : election.getCandidatures_to_election()) {
                System.out.println(cand.getCandidature_name());
                for (User us : cand.getCandidates()) System.out.println(us.getCc_number());
            }
        }

        //  ASK ELECTION TO THE CANDIDATURE 
        System.out.println("----------------------------------------");
        System.out.println("Eleicoes Disponiveis [0 Para Voltar]");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_elections.size(); i++) {
            Election aux= available_elections.get(i);
            System.out.println(i+1+": "+aux.getTitle()+"\t"+aux.getStartingDateString()+"\t"+aux.getEndingDateString()+aux.getCandidatures_to_election().size()+"\t"+aux.getDescription());
        }
        System.out.println("----------------------------------------");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 0, available_elections.size())-1;
        if (option==-1) { input_manage.messageToWait("Voltando para o Menu Admin..."); return; }
        
        //  GET SELECTED ELECTION AND ITS RESTRICTIONS
        Election selected_election= available_elections.get(option);
        ArrayList<String> collegs_restricts= selected_election.getCollege_restrictions();
        ArrayList<String> deps_restricts= selected_election.getDepartment_restrictions();
        String user_type_restrics= selected_election.getElection_type();
        ArrayList<User> users_restricts= new ArrayList<>();

        //  ASK CANDIDATURE NAME
        new_candidature.setCandidature_name(input_manage.askVariable(keyboard, "Titulo: ", 5));

        //  CHECK IF THAT CANDIDATURE NAME ALREADY EXIST
        for (Candidature candidature : selected_election.getCandidatures_to_election()) {
            System.out.println(candidature.getCandidature_name());
            if (candidature.getCandidature_name().equals(new_candidature.getCandidature_name())) { input_manage.messageToWait("Erro: Ja existe uma Eleicao com o nome "+candidature.getCandidature_name()+"!"); return; }
            
        }

        //  GET USERS AVAILABLE FROM SPECIFIC COLLEGES
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

        //  GET USERS AVAILABLE FROM SPECIFIC DEPARTMENTS
        for (String department : deps_restricts) {
            try { users_restricts.addAll(admin.getServer1().getUniqueDepartment(department).getUsersWithType(user_type_restrics)); } 
            catch (Exception e1) {
                try { users_restricts.addAll(admin.getServer2().getUniqueDepartment(department).getUsersWithType(user_type_restrics)); } 
                catch (Exception e2) { System.out.println("500: Nao ha servers"); }
            }
        }

        //  TAKE OFF USERS ALREADY CANDIDATED
        for (Candidature candidature : selected_election.getCandidatures_to_election()) {
            for (User user : candidature.getCandidates()) {
                int user_index = IntStream.range(0, users_restricts.size()).filter(i -> users_restricts.get(i).getCc_number().equals(user.getCc_number())).findFirst().orElse(-1);
                if (user_index!=-1) users_restricts.remove(user_index);
            }
        }
    
        //  CHECK IF THERE'S USERS TO THE ELECTION
        if (users_restricts.isEmpty()) { 
            input_manage.messageToWait("Erro: Nao ha Candidatos Disponiveis");
            return; 
        }

        //  ASK CANDIDATES
        System.out.println("----------------------------------------");
        System.out.println("Candidatos Disponiveis");
        System.out.println("----------------------------------------");
        for (int i = 0; i < users_restricts.size(); i++) System.out.println(i+1+":\t"+users_restricts.get(i));
        System.out.println("----------------------------------------");
        ArrayList<Integer> options= input_manage.checkSeveralIntegerOptions(keyboard, "Opcao [n1,n2,n3]: ", 1, users_restricts.size());
        for (Integer integer : options) new_candidature.getCandidates().add(users_restricts.get(integer-1));
        
        //  SEND SELECTED UPDATED ELECTION TO RMI SERVER
        selected_election.getCandidatures_to_election().add(new_candidature);
        try { System.out.println(admin.getServer1().setUpdatedElection(selected_election, true)); } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer1().setUpdatedElection(selected_election, true)); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); }
        }
    }
    private void addVoteTable(Scanner keyboard) {
        ArrayList<Department> available_deps=null;
        int option;

        try { available_deps= admin.getServer1().getDepartmentsWithOrNotVoteTable(false); } 
        catch (Exception e1) {
            try { available_deps= admin.getServer2().getDepartmentsWithOrNotVoteTable(false); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers!\n"); }
        }
        if (available_deps.isEmpty()) { input_manage.messageToWait("Erro: Nao existem Departamentos Disponiveis!"); return; };

        //  ASK DEPARTMENT FROM THE ONES WHICH HAVE NOT VOTE TABLE 
        System.out.println("----------------------------------------");
        System.out.println("Departamentos Disponiveis [0 Para Voltar]");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_deps.size(); i++) {
            Department aux= available_deps.get(i);
            System.out.println(i+1+": "+aux.getCollege()+"\t"+aux.getName());
        }
        System.out.println("----------------------------------------");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 0, available_deps.size())-1;
        if (option==-1) { input_manage.messageToWait("Voltando para o Menu Admin..."); return; }
        Department selected_dep= available_deps.get(option);

        selected_dep.createVoteTable(input_manage.checkIntegerOption(keyboard, "Quantos terminais tera a Mesa de Voto do "+selected_dep.getName()+" [Max=100]: ", 1, 100));

        try { System.out.println(admin.getServer1().setUpdatedDepartment(selected_dep, true)); }
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().setUpdatedDepartment(selected_dep, true)); } 
            catch (Exception e) { System.out.println("500: Nao ha servers!\n"); }
        }
    }
    private void deleteVoteTable(Scanner keyboard) {
        ArrayList<Department> available_deps=null;
        int option;

        try { available_deps= admin.getServer1().getDepartmentsWithOrNotVoteTable(true); } 
        catch (Exception e1) {
            try { available_deps= admin.getServer2().getDepartmentsWithOrNotVoteTable(true); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers!\n"); }
        }
        if (available_deps.isEmpty()) { input_manage.messageToWait("Erro: Nao existem Mesas de Voto Disponiveis!"); return; };

        //  ASK DEPARTMENT FROM THE ONES WHICH HAVE NOT VOTE TABLE 
        System.out.println("----------------------------------------");
        System.out.println("Mesas de Voto Disponiveis [0 Para Voltar]");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_deps.size(); i++) {
            Department aux= available_deps.get(i);
            if (aux.getActivatedVoteTable()) System.out.println(i+1+": "+aux.getCollege()+"\t"+aux.getName()+"\tAtiva\t"+aux.getVoteTerminals());
            else System.out.println(i+1+": "+aux.getCollege()+"\t"+aux.getName()+"\tInativa\t"+aux.getVoteTerminals());
        }
        System.out.println("----------------------------------------");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 0, available_deps.size())-1;
        if (option==-1) { input_manage.messageToWait("Voltando para o Menu Admin..."); return; }
        Department selected_dep= available_deps.get(option);
        selected_dep.deleteVoteTable();

        try { System.out.println(admin.getServer1().setUpdatedDepartment(selected_dep, false)); }
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().setUpdatedDepartment(selected_dep, false)); } 
            catch (Exception e) { System.out.println("500: Nao ha servers!\n"); }
        }
    }

    private void editElection(Scanner keyboard) { 
        ArrayList<Election> available_elections= new ArrayList<>();
        int option;

        //  GET ELECTION TO THE NEW CANDIDATURE
        try { available_elections = admin.getServer1().getUnstartedElections(); 
        } catch (Exception e1) {
            try { available_elections = admin.getServer2().getUnstartedElections(); 
            } catch (Exception e2) { System.out.println("500: Nao ha servers"); }
        }
        if (available_elections.isEmpty()) { System.out.println("Erro: Nao ha Eleicoes registadas!"); return; }

        //  ASK ELECTION TO THE CANDIDATURE 
        System.out.println("----------------------------------------");
        System.out.println("Eleicoes Disponiveis");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_elections.size(); i++) 
            System.out.println(i+1+": "+available_elections.get(i).getTitle()+"\t"+available_elections.get(i).getStartingDateString()+"\t"+available_elections.get(i).getEndingDateString()+"\t"+available_elections.get(i).getDescription());
        System.out.println("----------------------------------------");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 1, available_elections.size())-1;
        
        //  ASK TO CHANGE A VARIABLE
        Election updated_election= available_elections.get(option);
        System.out.println("\nQue atributo pretende Alterar?");
        System.out.println("----------------------------------------");
        System.out.println("1: Titulo: "+updated_election.getTitle());
        System.out.println("2: Descricao: "+updated_election.getDescription());
        System.out.println("3: Data e Hora de Inicio: "+updated_election.getStartingDateString());
        System.out.println("4: Data e Hora de Fim: "+updated_election.getEndingDateString());
        System.out.println("0: Voltar");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 0, 4);
        
        System.out.println("----------------------------------------");
        if (option==0) { input_manage.messageToWait("Voltando para o Menu Amin..."); return ; } 
        else if (option==1) updated_election.setTitle(input_manage.askVariable(keyboard, "Novo Titulo: ", 0));
        else if (option==2) updated_election.setDescription(input_manage.askVariable(keyboard, "Nova Descricao: ", 5));
        else if (option==3) {
            LocalDate temp_date1= input_manage.askDate(keyboard, "Nova Data de Inicio da Eleicao [dd/mm/aaaa]: ");
            while (LocalDate.now().compareTo(temp_date1)>=0) {
                System.out.println("Erro: A Data de Inicio tem de ser posterior ao dia de hoje!");
                temp_date1= input_manage.askDate(keyboard, "Nova Data de Inicio da Eleicao [dd/mm/aaaa]: ");
            }
            LocalTime temp_hour1= input_manage.askHour(keyboard, "Nova Hora de Inicio da Eleicao [hh:mm]: ");        
            updated_election.setStarting(LocalDateTime.of(temp_date1, temp_hour1));
        } else if (option==4) {
            LocalDate temp_date2= input_manage.askDate(keyboard, "Nova Data de Fim da Eleicao [dd/mm/aaaa]: ");
            LocalTime temp_hour2= input_manage.askHour(keyboard, "Nova Hora de Fim da Eleicao [hh:mm]: ");        
            if (updated_election.getStarting().compareTo(LocalDateTime.of(temp_date2, temp_hour2))>=0) {
                System.out.println("Erro: A Nova Data de Fim tem de ser posterior ao dia "+updated_election.getStartingDateString()+"!");
                input_manage.messageToWait("Voltando para o Menu Admin..."); return ;
            } else { updated_election.setEnding(LocalDateTime.of(temp_date2, temp_hour2)); }
        } 

        //  SEND NEW ELECTION TO RMI SERVER
        try { System.out.println(admin.getServer1().setUpdatedElection(updated_election, false)); } 
        catch (Exception e1) {
            try { System.out.println(admin.getServer2().setUpdatedElection(updated_election, false)); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers\n"); }
        }
    }
    
    private void consultPassedElections(Scanner keyboard) {
        ArrayList<Election> available_elections= new ArrayList<>();
        int option;

        //  GET ELECTION TO THE NEW CANDIDATURE
        try { available_elections = admin.getServer1().getFinishedElections(); } 
        catch (Exception e1) {
            try { available_elections = admin.getServer2().getFinishedElections(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); }
        }
        if (available_elections.isEmpty()) { input_manage.messageToWait("Erro: Nao ha Eleicoes registadas!"); return; }
        
        //  ASK ELECTION 
        System.out.println("----------------------------------------");
        System.out.println("Eleicoes Finalizadas [0 Para Voltar]");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_elections.size(); i++) {
            Election aux= available_elections.get(i);
            System.out.println(i+1+": "+aux.getTitle()+"\t"+aux.getStartingDateString()+"\t"+aux.getEndingDateString()+aux.getCandidatures_to_election().size()+"\t"+aux.getDescription());
        } System.out.println("----------------------------------------");
        option= input_manage.checkIntegerOption(keyboard, "Opcao: ", 0, available_elections.size())-1;
        if (option==-1) { input_manage.messageToWait("Voltando para o Menu Admin..."); return; }

        Election selected_election= available_elections.get(option);
        
        System.out.println("----------------------------------------");
        System.out.println("Titulo: "+selected_election.getTitle());
        System.out.println("Descricao: "+selected_election.getDescription());
        System.out.println("Data Inicio: "+selected_election.getStartingDateString()+"  "+selected_election.getStartingHourString());
        System.out.println("Data Fim: "+selected_election.getEndingDateString()+"  "+selected_election.getEndingHourString());
        System.out.println("Eleitores/Candidatos: "+selected_election.getElection_type());
        if (!selected_election.getCollege_restrictions().isEmpty()) {
            System.out.println("Faculdades:");
            for (String college : selected_election.getCollege_restrictions()) System.out.print(" "+college);
            System.out.println("");
        } if (!selected_election.getDepartment_restrictions().isEmpty()) {
            System.out.println("Departamentos:");
            for (String department : selected_election.getDepartment_restrictions()) System.out.print(" "+department);
            System.out.println("");
        } if (!selected_election.getCandidatures_to_election().isEmpty()) {
            System.out.println("Candidatos: ");
            for (Candidature candidature: selected_election.getCandidatures_to_election()) 
                System.out.printf("\t %s\tVotos: %.2f%% (%d)\n", candidature.getCandidature_name(), (candidature.getCandidature_votes()/selected_election.getTotalVotes())*100, candidature.getCandidature_votes());
            System.out.printf("Votos Nulos: %.2f%% (%d)\n", (selected_election.getNullVotes()/selected_election.getTotalVotes())*100, selected_election.getNullVotes());
            System.out.printf("Votos em Branco: %.2f%% (%d)\n", (selected_election.getBlankVotes()/selected_election.getTotalVotes())*100, selected_election.getBlankVotes());
        }
        System.out.println("Total de Votos: "+ selected_election.getTotalVotes());
        System.out.println("----------------------------------------");
    }

    private void consultVoteTables(Scanner keyboard) {
        ArrayList<Department> available_deps=null;
        GetEnterKey pressed_enter= new GetEnterKey("pressed_enter", keyboard, input_manage);

        while (true) {
            //  CLEAR CONSOLE
            try { Thread.sleep(100); }
            catch (Exception e) { }
            System.out.print("\033[H\033[2J");  
            System.out.flush(); 

            try { available_deps= admin.getServer1().getDepartmentsWithOrNotVoteTable(true); } 
            catch (Exception e1) {
                try { available_deps= admin.getServer2().getDepartmentsWithOrNotVoteTable(true); } 
                catch (Exception e2) { System.out.println("500: Nao ha servers!\n"); }
            }
            if (available_deps.isEmpty()) { input_manage.messageToWait("Erro: Nao existem Mesas de Voto Disponiveis!"); return; };

            //  ASK DEPARTMENT FROM THE ONES WHICH HAVE NOT VOTE TABLE 
            System.out.println("----------------------------------------");
            System.out.println("Mesas de Voto Disponiveis");
            System.out.println("----------------------------------------");
            for (int i = 0; i < available_deps.size(); i++) {
                Department aux= available_deps.get(i);
                if (aux.getActivatedVoteTable()) System.out.println(i+1+": "+aux.getCollege()+"\t"+aux.getName()+"\tAtiva\t"+aux.getVoteTerminals());
                else System.out.println(i+1+": "+aux.getCollege()+"\t"+aux.getName()+"\tInativa\t"+aux.getVoteTerminals());
            }
            System.out.println("----------------------------------------");
            System.out.println("Pressione Enter para Voltar...");

            if (!pressed_enter.enter_thread.isAlive()) break;
            try { pressed_enter.enter_thread.join(1000); } 
            catch (InterruptedException e) { break; }
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
        ArrayList<String> available_colleges= new ArrayList<>();
        int option;
        
        //  Get College Names from RMI Server
        try { available_colleges = admin.getServer1().getCollegesNames(); } 
        catch (Exception e1) {
            try { available_colleges = admin.getServer2().getCollegesNames(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); return "";}
        }
        
        //  There's no registed Colleges
        if (available_colleges.isEmpty()) { System.out.println("Erro: Nao existem Faculdades registadas!\n"); return ""; }
        
        //  Removing Colleges already registed
        for (String temp_college : college_restrictions) available_colleges.remove(temp_college);
        
        System.out.println("Insira a Faculdade onde ocorrera a Eleicao");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_colleges.size(); i++) System.out.println(i+1+": "+available_colleges.get(i));
        System.out.println("----------------------------------------");
        option= checkIntegerOption(keyboard, "Opcao: ", 1, available_colleges.size())-1;
        return available_colleges.get(option);
    }
    public String askDepartment(RMIClient client, Scanner keyboard, ArrayList<String> department_restrictions, boolean voting_table) {
        ArrayList<String> available_departments= new ArrayList<>();
        int option;

        //  Get Department Names from RMI Server
        try { available_departments = client.getServer1().getDepartmentsNames(); } 
        catch (Exception e1) {
            try { available_departments = client.getServer2().getDepartmentsNames(); } 
            catch (Exception e2) { System.out.println("500: Nao ha servers"); return "";}
        }

        //  There's no registed Departments
        if (available_departments.isEmpty()) { messageToWait("Erro: Nao existem Departamentos registados!\n"); return ""; }
        
        //  Removing Departments already registed
        if (!voting_table) for (String temp_depart : department_restrictions) available_departments.remove(temp_depart);
        
        System.out.println("Insira o Departamento onde ocorrera a Eleicao");
        System.out.println("----------------------------------------");
        for (int i = 0; i < available_departments.size(); i++) System.out.println(i+1+": "+available_departments.get(i));
        System.out.println("----------------------------------------");
        option= checkIntegerOption(keyboard, "Opcao: ", 1, available_departments.size())-1;

        //  VERIFY IF THE SELECTED DEPARTMENT ALREADY HAS A VOTE TABLE ASSOCIATED
        //  IF TRUE RETURNS ERROR, OTHERWISE, SETS A VOTE TABLE
        String selected_dep=available_departments.get(option);
        boolean result=false;
        if (voting_table) {
            try { result= client.subscribe2Servers(client, selected_dep); } 
            catch (Exception e1) { return ""; }
            if (!result) return "";
        } return selected_dep;
    }
    public String askCCNumber(RMIClient client, Scanner keyboard) {
        boolean result=false;
        while (true) {
            String cc_number= askVariable(keyboard, "Insira o seu Numero de CC: ", 2);
            try { result= client.getServer1().authorizeUser(cc_number); } 
            catch(Exception e1) {
                try { result= client.getServer2().authorizeUser(cc_number); }
                catch (Exception e2) {System.out.println("500: Nao ha servers!");}
            }
            if (result) { messageToWait("Sera reencaminhado para um Terminal de Voto..."); return cc_number; }
        }
    }

    public int checkIntegerOption(Scanner keyboard, String message, int min_limit, int max_limit) {
        while (true) {
            System.out.print(message);
            try {
                int option= Integer.parseInt(keyboard.nextLine());
                if (option>=min_limit && option<=max_limit) return option;
                else throw new Exception();
            } catch (Exception e) { messageToWait("Erro: Opcao Invalida!"); }
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
            } catch (Exception e) { messageToWait("Erro: Opcao Invalida!"); }
        }
    }
    public boolean checkString(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkPassword(String s) {
        if (s.isBlank()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isSpaceChar(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkPhoneNumber(String s) {
        if (s.isBlank() || s.length()!=9) return false;
        if (Character.compare(s.charAt(0), '9')!=0 || (Character.compare(s.charAt(1), '1')!=0 && Character.compare(s.charAt(1), '2')!=0 && Character.compare(s.charAt(1), '3')!=0 && Character.compare(s.charAt(1), '6')!=0)) 
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkStringInteger(String s) {
        if (s.isBlank()) return false;
        if (!Character.isJavaIdentifierStart(s.charAt(0)) && !Character.isDigit(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        } return true;
    }
    public boolean checkValidity(String s) {
        if (s.isBlank() || s.length()!=5) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i==2 && Character.compare(s.charAt(2),'-')!=0) return false;
            else if (i!=2 && !Character.isDigit(s.charAt(i))) return false;
        } 
        int aux= Integer.parseInt(s.split("-")[0]);
        if (aux>0 && aux<=12) return true;
        else return false;
    }

    public void messageToWait(String message) {
        System.out.println(message);
        try { TimeUnit.SECONDS.sleep(2); }
        catch (Exception e1) { }
    }
}


/**
 * GetEnterKey Wait until an Enter key be pressed
 */
class GetEnterKey implements Runnable {
    Scanner keyboard;
    Inputs input;
    Thread enter_thread;

    public GetEnterKey(String threadname, Scanner keyboard, Inputs input) {
        this.keyboard= keyboard;
        this.input= input;
        enter_thread= new Thread(this, threadname);
        enter_thread.start();
    }

    public void run() {
        while (true) {
            //try { Thread.sleep(100); }
            //catch (Exception e) { }
            try {
                String enter= keyboard.nextLine();
                if (enter.isEmpty()) return;
            } catch (Exception e) { }
        }
    }
}

