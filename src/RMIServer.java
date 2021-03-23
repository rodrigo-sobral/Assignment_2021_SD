//  Default
import java.io.*;

//  RMI
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

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
    //private static Thread connection;
    //private static Thread heartbeat_checking;

    //  Storage Data
    private FilesManagement file_manage= new FilesManagement();
    
    //  Data to Store
    private ArrayList<College> colleges= new ArrayList<>();
    private ArrayList<Election> elections= new ArrayList<>();
    //private ArrayList<Election> finished_elections= new ArrayList<>();

    public RMIServer() throws RemoteException { super(); }

    public static void main(String[] args) throws Exception {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
        
        //new Thread(this, name);
        //connection= new Thread(connection, getClientHost());
        initServers();
                

        server1.file_manage.loadElectionsFile(server1.elections);
        if (!server1.elections.isEmpty()) for (String  name: server1.getElectionNames()) System.out.println(name);
        if (!server1.colleges.isEmpty()) for (String  name: server1.getCollegesNames()) System.out.println(name);
    }

    public void run() { 
        
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
        if (new_client.getIsAdmin()) { admins_list.add(new_client); System.out.println("Novo Administrador Ligado"); }
		else { clients_list.add(new_client); System.out.println("Novo Cliente Ligado"); }
        return "200: Adicionado ao Servidor com Sucesso";
	}
    synchronized public String unsubscribeNewClient(RMIClient_I new_client) throws RemoteException {
        if (new_client.getIsAdmin()) { admins_list.remove(new_client); System.out.println("Administrador Desligado"); }
		else { clients_list.remove(new_client); System.out.println("Cliente Desligado"); }
        return "200: Removido ao Servidor com Sucesso";
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
        for (Election election : elections) {
            if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
        } 
        elections.add(new_election);
        file_manage.saveElectionsFile(elections);
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
    
    synchronized public ArrayList<Election> getElections() throws RemoteException { return this.elections; }
    synchronized public ArrayList<String> getElectionNames() throws RemoteException { 
        ArrayList<String> names= new ArrayList<>();
        for (Election election : elections) names.add(election.getTitle());
        return names;
    }
    synchronized public String setUpdatedElection(Election updated_election) throws RemoteException { 
        for (Election election : elections) {
            if (election.getTitle().compareTo(election.getTitle())==0) {
                election=updated_election;
                return "200: Candidatura Submetida com Sucesso";
            }
        } return "500: Essa Eleicao nao foi encontrada";
    }

    synchronized public String ping() throws RemoteException { return "ACK"; }
    
}


/**
 * FilesManagement
*/
class FilesManagement {

    public FilesManagement() { }

    public boolean saveCollegesFile(ArrayList<College> colleges) {
        if (colleges.size()==0) return false;
        String filePath = "database_users.dat";
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
    public ArrayList<College> loadCollegesFile(ArrayList<College> colleges) {
        String filePath = "database_users.dat";
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
        } catch (FileNotFoundException e) { System.out.println("404: File not found"); }
        catch (IOException e) { System.out.println("Error initializing stream"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        return colleges;
    }

    public boolean saveElectionsFile(ArrayList<Election> elections) {
        if (elections.size()==0) return false;
        String filePath = "database_elections.dat";
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
    public ArrayList<Election> loadElectionsFile(ArrayList<Election> elections) {
        String filePath = "database_elections.dat";
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
        } catch (FileNotFoundException e) { System.out.println("404: File not found"); }
        catch (IOException e) { System.out.println("Error initializing stream"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        return elections;
    }
    
}

class NewThread implements Runnable {
    String name;
    Thread t;
    NewThread(String threadname) {
        name = threadname;
        t = new Thread(this, name);
        System.out.println("New thread: " + t);
        t.start(); // Start the thread
    }

    public void run() {      // entry point
        try {
            for(int i = 5; i > 0; i--) {
                System.out.println(name + ": " + i);
                Thread.sleep(1000);
            }
        } 
        catch (InterruptedException e) {
            System.out.println(name + "Interrupted");
        }
        System.out.println(name + " exiting.");
    }
  }