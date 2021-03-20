//  Default
import java.io.*;

//  RMI
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;

import classes.Election;
import classes.User;

/**
 * FilesManagement
*/
class FilesManagement {

    public FilesManagement() { }

    public boolean saveUsersFile(ArrayList<User> pessoas) {
        if (pessoas.size()==0) return false;
        String filePath = "users.dat";
        try {
            FileOutputStream file = new FileOutputStream(new File(filePath));
            ObjectOutputStream writer = new ObjectOutputStream(file);
            
            writer.writeObject(pessoas);

            writer.close();
            file.close();
            return true;
        } catch (FileNotFoundException e) { System.out.println("ERROR 404: File not found"); } 
        catch (IOException e) { System.out.println("Error initializing stream\n"+e); } 
        return false;
    }
    public ArrayList<User> loadUsersFile(ArrayList<User> pessoas) {
        String filePath = "users.dat";
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            ObjectInputStream reader = new ObjectInputStream(file);

            Object obj = reader.readObject();
            if (obj instanceof ArrayList<?>) {
                ArrayList<?> al = (ArrayList<?>) obj;
                if (al.size() > 0) {
                    for (int i = 0; i < al.size(); i++) {
                        Object o = al.get(i);
                        if (o instanceof User) pessoas.add((User) o);
                    }
                }
              }
            
            reader.close();
            file.close();
            return pessoas;
        } catch (FileNotFoundException e) { System.out.println("File not found"); }
        catch (IOException e) { System.out.println("Error initializing stream"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        return pessoas;
    }
    
}

public class RMIServer extends UnicastRemoteObject implements RMIServer_I, Runnable {   
    private static final long serialVersionUID = 1L;

	private static int port=1099;
    private Registry regist = LocateRegistry.createRegistry(port);
	private static String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
    private static RMIServer server1, server2; 

    private ArrayList<College> colleges= new ArrayList<>();
    private ArrayList<Election> elections= new ArrayList<>();
    //private ArrayList<Election> finished_elections= new ArrayList<>();

    public RMIServer() throws RemoteException { super(); }

    public static void main(String[] args) throws Exception {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
        
        initServers();
        //server2.run();
    }

    public void run() { 
        
    }
    
    private static boolean initServers() {
        try {
            server1 = new RMIServer();  // this would firstly be the main rmi server
            server1.regist.rebind(rmiregistry1, server1);
            System.out.println("Hello Server1 ready.");
            server1.run();
            return true;
        } catch (Exception e1) { 
            try {
                server2 = new RMIServer();  // this would firstly be the secundary rmi server
                server2.regist.rebind(rmiregistry2, server2);
                System.out.println("Hello Server2 ready.");                
                server2.run();
            } catch (Exception e2) {
                System.out.println("Error turning servers on port "+port); 
            } return false; 
        }
    }

    
    public String registUser(String new_college, String new_department, User new_user) throws RemoteException {
        for (College college : colleges) {
            if (new_college.compareTo(college.getName())==0) {
                for (Department department : college.getDepartments()) {
                    if (new_department.compareTo(department.getName())==0 && !department.verifyUserExistence(new_user)) 
                        return "400: Uma pessoa com esse Numero de CC ja foi registada!";
                }
                college.getDepartments().add(new Department(new_department, new_user));
                System.out.println("Nova Pessoa Registada pela Consola de Administrador");
                return "200: Pessoa registada com sucesso!";
            }
        } 
        colleges.add(new College(new_college, new Department(new_department, new_user)));
        System.out.println("Nova Pessoa Registada pela Consola de Administrador");
        return "200: Pessoa registada com sucesso!";
	}
    public String registElection(Election new_election) throws RemoteException {
        for (Election election : elections) {
            if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
        } 
        elections.add(new_election);
        System.out.println("Nova Eleicao Registada pela Consola de Administrador");
        return "CODE 200: Eleicao registada com sucesso!";
	}

    public ArrayList<College> getColleges() { return this.colleges; }
    public ArrayList<Election> getElections() { return this.elections; }
    
    public String printOnServer(String message) throws RemoteException {
		System.out.println(message);
        return "ACK";
	}
}


