//  Default
import java.io.*;

//  RMI
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

import classes.Candidature;
//  CUSTOM 
import classes.Election;
import classes.User;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I, Runnable {   
    private static final long serialVersionUID = 1L;

    //  Connection Data
	private int port=1099;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
    private static RMIServer server1, server2; 
    private ArrayList<RMIClient_I> clients_list= new ArrayList<>();
    private ArrayList<RMIClient_I> admins_list= new ArrayList<>();
    private Registry regist = LocateRegistry.createRegistry(port);

    //  Threads
    private static IsClientAlive client_checker;

    //  Storage Data
    private FilesManagement file_manage= new FilesManagement();
    
    //  Data to Store
    private ArrayList<College> colleges= new ArrayList<>();
    private ArrayList<Election> unstarted_elections= new ArrayList<>(), running_elections= new ArrayList<>(), finished_elections= new ArrayList<>();

    public RMIServer() throws RemoteException { super(); }

    public static void main(String[] args) throws Exception {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
        
        initServers();

        server1.file_manage.loadElectionsFile(server1.unstarted_elections, "unstarted");
        server1.file_manage.loadElectionsFile(server1.running_elections, "running");
        server1.file_manage.loadElectionsFile(server1.finished_elections, "finished");
        server1.file_manage.loadCollegesFile(server1.colleges);
        client_checker= new IsClientAlive("clients_checker", server1.clients_list, server1.admins_list);
        
        try {
			client_checker.thread.join();
            System.out.println("Verificacao de ativacao dos clientes: Desativa");
		} catch (InterruptedException e) { System.out.println("Interrupted"); }

        if (!server1.running_elections.isEmpty()) {
            for (String  name: server1.getElectionNames("unstarted")) System.out.println(name);
            for (String  name: server1.getElectionNames("running")) System.out.println(name);
        }
        if (!server1.colleges.isEmpty()) for (String  name: server1.getCollegesNames()) System.out.println(name);
    }

    public void run() { 
        initServers();
    }
    
    private static boolean initServers() {
        try {
            server1 = new RMIServer();  // this would firstly be the main rmi server
            server1.regist.rebind(server1.rmiregistry1, server1);
            System.out.println("Servidor Primario Ativo");
            return true;
        } catch (Exception e1) { 
            try {
                heartbeatServer(server1, server2);
            } catch (Exception e2) { System.out.println("Erro a ligar os servers no porto "+server1.port);} 
            return false; 
        }
    }

    synchronized public String subscribeNewClient(RMIClient_I new_client) throws RemoteException {
        if (new_client.getIsAdmin()) { admins_list.add(new_client); System.out.println("Novo Administrador Conectado ["+(admins_list.size()-1)+"]"); }
		else { clients_list.add(new_client); System.out.println("Novo Cliente Conectado ["+(clients_list.size()-1)+"]"); }
        return "200: Adicionado ao Servidor com Sucesso";
	}

    public static void heartbeatServer(RMIServer active_server, RMIServer secundary_server) throws RemoteException, NotBoundException, InterruptedException{
        int error_response = 0;
                          
        while(error_response!=5){
            try { active_server.ping(); System.out.println("Ping "+error_response+": Recebido"); }
            catch(Exception e){ error_response++; System.out.println("Ping "+error_response+": Falhado"); }
            
            if(error_response==5) {
                try { 
                    secundary_server= new RMIServer();
                    secundary_server.regist.rebind(secundary_server.rmiregistry2, secundary_server); 
                    System.out.println("Servidor Activo Alterado para o Secundario"); 
                } 
                catch(Exception e){ e.printStackTrace(); }   
            }
        }
    }

    //  ===========================================================================================================

    /**
     * @param new_college Name of the new college, registed with the new user data
     * @param new_department Name of the new department, registed with the new user data
     * @param new_user New User data
     * @return returns a 200 code if sucess during registation, 400 otherwise
     */
    synchronized public String registUser(String new_college, String new_department, User new_user) throws RemoteException {
        if (verifyUserExistence(new_user)) return "400: Uma pessoa com esse Numero de CC ja foi registada!\n";
        int college_index = IntStream.range(0, colleges.size()).filter(i -> colleges.get(i).getName().equals(new_college)).findFirst().orElse(-1);
        if (college_index==-1) {
            colleges.add(new College(new_college, new Department(new_department, new_user)));
        } else {
            int department_index = IntStream.range(0, colleges.get(college_index).getDepartments().size()).filter(i -> colleges.get(college_index).getDepartments().get(i).getName().equals(new_department)).findFirst().orElse(-1);
            if (department_index==-1) colleges.get(college_index).getDepartments().add(new Department(new_department, new_user));
            else colleges.get(college_index).getDepartments().get(department_index).getUsersWithType(new_user.getUser_type()).add(new_user);
        }
        
        System.out.println("Nova Pessoa Registada pela Consola de Administrador");
        file_manage.saveCollegesFile(colleges);
        return "200: Pessoa registada com sucesso!\n";
	}
    public boolean verifyUserExistence(User comparing_user) {
        for (College college : colleges) {
            for (Department department : college.getDepartments()) {
                for (User user : department.getStudents()) {
                    if (comparing_user.getCc_number().compareTo(user.getCc_number())==0) return true;
                }
                for (User user : department.getTeachers()) {
                    if (comparing_user.getCc_number().compareTo(user.getCc_number())==0) return true;
                }
                for (User user : department.getStaff()) {
                    if (comparing_user.getCc_number().compareTo(user.getCc_number())==0) return true;
                }
            }
        } return false;
    }
   
    synchronized public String registElection(Election new_election) throws RemoteException {
        for (Election election : unstarted_elections) if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
        for (Election election : running_elections) if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
        for (Election election : finished_elections) if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";

        unstarted_elections.add(new_election);
        file_manage.saveElectionsFile(unstarted_elections, "unstarted");
        System.out.println("Nova Eleicao Registada pela Consola de Administrador");
        return "200: Eleicao registada com sucesso!";
	}

    synchronized public ArrayList<College> getColleges() throws RemoteException { return this.colleges; }
    synchronized public ArrayList<String> getDepartmentsNames() throws RemoteException { 
        ArrayList<String> names= new ArrayList<>();
        for (College college : colleges) {
            for (Department department : college.getDepartments()) names.add(department.getName());
        } return names;
    }
    synchronized public ArrayList<String> getCollegesNames() throws RemoteException { 
        ArrayList<String> names= new ArrayList<>();
        for (College college : colleges) names.add(college.getName());
        return names;
    }
    synchronized public College getUniqueCollege(String college_name) throws RemoteException { 
        try {
            for (College college : server1.getColleges()) {
                if (college.getName().compareTo(college_name)==0) return college;
            } return null;
        } catch (Exception e1) {
            try {
                for (College college : server2.getColleges()) {
                    if (college.getName().compareTo(college_name)==0) return college;
                } return null;
            } catch (Exception e2) { System.out.println("500: Nao ha servers!"); return null; }
        }
    }
    synchronized public Department getUniqueDepartment(String department_name) throws RemoteException { 
        try {
            for (College college : server1.getColleges()) {
                for (Department department : college.getDepartments()) {
                    if (department.getName().compareTo(department_name)==0) return department;
                }
            } return null;
        } catch (Exception e1) {
            try {
                for (College college : server2.getColleges()) {
                    for (Department department : college.getDepartments()) {
                        if (department.getName().compareTo(department_name)==0) return department;
                    }
                } return null;
            } catch (Exception e2) { System.out.println("500: Nao ha servers!"); return null; }
        }
    }
    
    synchronized public ArrayList<Election> getUnstartedElections() throws RemoteException { return this.unstarted_elections; }
    synchronized public ArrayList<Election> getRunningElections() throws RemoteException { return this.running_elections; }
    synchronized public ArrayList<Election> getFinishedElections() throws RemoteException { return this.finished_elections; }
    synchronized public ArrayList<String> getElectionNames(String election_state) throws RemoteException { 
        ArrayList<String> names= new ArrayList<>();
        if (election_state=="running") for (Election election : running_elections) names.add(election.getTitle());
        else if (election_state=="finished") for (Election election : finished_elections) names.add(election.getTitle());
        else if (election_state=="unstarted") for (Election election : unstarted_elections) names.add(election.getTitle());
        return names;
    }
    
    synchronized public String setUpdatedElection(Election updated_election, boolean is_candidature) throws RemoteException { 
        for (Election election : unstarted_elections) {
            if (election.getTitle().compareTo(updated_election.getTitle())==0) {
                unstarted_elections.set(unstarted_elections.indexOf(election), updated_election);
                if (is_candidature) { System.out.println("Nova Candidatura Submetida pela Consola de Administrador"); return "200: Candidatura Submetida com Sucesso"; }
                else { System.out.println("Eleicao "+election.getTitle()+" alterada pela Consola de Administrador"); return "200: Eleicao Editada com Sucesso"; }
            }
        } return "400: Essa Eleicao nao foi encontrada";
    }

    synchronized public String ping() throws RemoteException { return "ACK"; }
}


/**
 * FilesManagement take care of all data storage
*/
class FilesManagement {

    public FilesManagement() { }

    /**
     * Writes colleges in a object file called "database_colleges.data"
     * @param colleges ArrayList of Colleges, considering each one has a Department and each Department has 3 ArrayList (students, teachers and staff)
     * @return true if the file was save successfully, false otherwise
     */
    public boolean saveCollegesFile(ArrayList<College> colleges) {
        if (colleges.size()==0) return false;
        String filePath = "database_colleges.dat";
        try {
            FileOutputStream file = new FileOutputStream(new File(filePath));
            ObjectOutputStream writer = new ObjectOutputStream(file);
            
            writer.writeObject(colleges);

            writer.close();
            file.close();
            return true;
        } catch (FileNotFoundException e) { System.out.println("404: File not found"); } 
        catch (IOException e) { System.out.println("Error initializing stream\n"+e); } 
        return false;
    }
    
    /**
     * Reads and loads all colleges from a object file called "database_colleges.data"
     * @param colleges ArrayList of Colleges, considering each one has a Department and each Department has 3 ArrayList (students, teachers and staff)
     * @return true if the file was save successfully, false otherwise
     */
    public ArrayList<College> loadCollegesFile(ArrayList<College> colleges) {
        String filePath = "database_colleges.dat";
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            ObjectInputStream reader = new ObjectInputStream(file);

            Object file_obj = reader.readObject();
            ArrayList<?> coleg_list = (ArrayList<?>) file_obj;
            for (Object coleg : coleg_list) {
                College new_college= (College) coleg;
                colleges.add(new_college);
            }
            reader.close();
            file.close();
            return colleges;
        } catch (FileNotFoundException e) { System.out.println("404: File "+filePath+" not found"); }
        catch (IOException e) { System.out.println("Error initializing stream"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        return colleges;
    }


    public boolean saveElectionsFile(ArrayList<Election> elections, String election_type) {
        if (elections.size()==0) return false;
        String filePath = "database_elections_"+election_type+".dat";
        try {
            FileOutputStream file = new FileOutputStream(new File(filePath));
            ObjectOutputStream writer = new ObjectOutputStream(file);
            
            writer.writeObject(elections);

            writer.close();
            file.close();
            return true;
        } catch (FileNotFoundException e) { System.out.println("404: File not found"); } 
        catch (IOException e) { System.out.println("Error initializing stream\n"+e); } 
        return false;
    }
    public ArrayList<Election> loadElectionsFile(ArrayList<Election> elections, String election_type) {
        String filePath = "database_elections_"+election_type+".dat";
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            ObjectInputStream reader = new ObjectInputStream(file);

            Object obj = reader.readObject();
            if (obj instanceof ArrayList<?>) {
                ArrayList<?> al = (ArrayList<?>) obj;
                for (Object object : al) {
                    Election new_election= (Election) object;
                    elections.add(new_election);
                }
            }
            reader.close();
            file.close();
            return elections;
        } catch (FileNotFoundException e) { System.out.println("404: File "+filePath+" not found"); }
        catch (IOException e) { System.out.println("Error initializing stream"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        return elections;
    }
    
}

class ServersManagement implements Runnable {
    public RMIServer server1, server2; 
    public Thread manager_thread;

    ServersManagement(String threadname, RMIServer server1, RMIServer server2) {
        this.server1 = server1;
        this.server2 = server2;
        manager_thread = new Thread(this, threadname);
        manager_thread.start();
    }

    public void run() {
        while (true) {
            
        }
    }
}


/**
 * IsClientAlive is a Thread that iterates all the Clients (includind Admins) and checks if they're alive
 */
class IsClientAlive implements Runnable {
    public Thread thread;
    private ArrayList<RMIClient_I> clients, admins;

    /**
     * @param threadname Name of the Thread
     * @param clients ArrayList of Clients subscribed running
     * @param admins ArrayList of Admin Consoles subscribed running
     */
    public IsClientAlive(String threadname, ArrayList<RMIClient_I> clients, ArrayList<RMIClient_I> admins) {
        this.clients = clients;
        this.admins = admins;
        thread = new Thread(this, threadname);
        System.out.println("Verificacao de ativacao dos clientes: Ativa");
        thread.start();
    }

    public void run() {
        int client_id=0, admin_id=0;
        while (true) {
            try { Thread.sleep(100); }
            catch (Exception e) { }
            
            //  PING CLIENTS
            if (!clients.isEmpty()) {
                //  RESET ARRAY
                if (client_id<0 || client_id>=admins.size()) client_id=0;
                try { 
                    if (clients.get(client_id)!=null) { clients.get(client_id).ping(); client_id++; }
                    else client_id++;
                } catch (Exception e) {
                    clients.set(client_id, null);
                    System.out.println("O Cliente ["+(client_id++)+"] desconectou-se!");
                }
            }

            //  PING ADMINS
            if (!admins.isEmpty()) {
                //  RESET ARRAY
                if (admin_id<0 || admin_id>=admins.size()) admin_id=0;
                try { 
                    if (admins.get(admin_id)!=null) { admins.get(admin_id).ping(); admin_id++; } 
                    else admin_id++;
                } catch (Exception e) {
                    admins.set(admin_id, null);
                    System.out.println("O Administrador ["+(admin_id++)+"] desconectou-se!");
                }
            }
        }
    }
}



/**
 * ElectionsState is a Thread that iterates all the Elections (including Unstarted, Running and Finished ones) and manage them according to their defined Starting and Ending Dates
*/
class ElectionsState implements Runnable {
    public Thread thread;
    ArrayList<Election> unstarted_elections, running_elections, finished_elections;

    /**
     * @param threadname Name of the Thread
     * @param unstarted_elections ArrayList of Unstarted Elections
     * @param running_elections ArrayList of Running Elections
     * @param finished_elections ArrayList of Finished Elections
    */
    ElectionsState(String threadname, ArrayList<Election> unstarted_elections, ArrayList<Election> running_elections, ArrayList<Election> finished_elections) {
        this.unstarted_elections = unstarted_elections;
        this.running_elections = running_elections;
        this.finished_elections = finished_elections;
        thread = new Thread(this, threadname);
        thread.start();
    }

    public void run() {
        int unstarted_id=0, running_id=0;
        while (true) {
            try { Thread.sleep(100); }
            catch (Exception e) { }

            //  CHECK UNSTARTED ELECTIONS
            if (!unstarted_elections.isEmpty()) {
                //  RESET ARRAY
                if (unstarted_id<0 || unstarted_id>=unstarted_elections.size()) unstarted_id=0;
                try { 
                    if (unstarted_elections.get(unstarted_id)!=null) { //unstarted_elections.get(unstarted_id).ping(); unstarted_id++; 
                    }
                    else unstarted_id++;
                } catch (Exception e) {
                    unstarted_elections.set(unstarted_id, null);
                    System.out.println("O Cliente ["+(unstarted_id++)+"] desconectou-se!");
                }
            }
        }
    }
}
