//  Default
import java.io.*;

//  RMI
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;

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

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {   
    private static final long serialVersionUID = 1L;

    private static FilesManagement file_management= new FilesManagement();
	private static int port=1099;
	private static String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
    private static RMIServer server1, server2; 

    private static ArrayList<User> pessoas= new ArrayList<>();
    //private static ArrayList<User> eleicoes= new ArrayList<>();
    //private static ArrayList<User> candidaturas= new ArrayList<>();

    public RMIServer() throws RemoteException { super(); }

    public static void main(String[] args) throws Exception {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
        
        pessoas= file_management.loadUsersFile(pessoas);
        
        for (User pessoa : pessoas) System.out.println(pessoa);

        boolean result;
        do result= initServers(); while (!result);
    }

    private static boolean initServers() {
        try {
            Registry regist = LocateRegistry.createRegistry(port);
            server1 = new RMIServer();  // this would firstly be the main rmi server
            server2 = new RMIServer();  // this would firstly be the secundary rmi server
            regist.rebind(rmiregistry1, server1);
            System.out.println("Hello Server1 ready.");
            regist.rebind(rmiregistry2, server2);
            System.out.println("Hello Server2 ready.");
            return true;
        } catch (Exception e2) { System.out.println("Error turning servers on port "+port); return false; }
    }

    
    public String registUser(User new_user) throws RemoteException {
        pessoas.add(new_user);
        file_management.saveUsersFile(pessoas);
        return "CODE 200: Pessoa registada com sucesso!";
	}
    
    public String printOnServer(String message) throws RemoteException {
		System.out.println(message);
        return "ACK";
	}
}


