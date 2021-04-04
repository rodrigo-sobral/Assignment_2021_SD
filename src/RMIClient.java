import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RMIClient extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I server1=null, server2=null;
	private int port=1099;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";

    RMIClient() throws RemoteException { 
        super();
        System.getProperties().put("java.security.policy","AdminConsole.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
    }

    protected boolean connect2Servers(Scanner keyboard) {
        try { LocateRegistry.createRegistry(port); } 
        catch (Exception e) { }
        String server1_ip= new Inputs().askVariable(keyboard, "Insira o IP do Servidor Principal: ", 5);
        String full_ip1= getRegistryFromIP(server1_ip, rmiregistry1), full_ip2= getRegistryFromIP(server1_ip, rmiregistry2);

		try { 
            server1 = (RMIServer_I) Naming.lookup(full_ip1);
            return true;
        } catch (Exception e1) { 
            try {
                server1 = (RMIServer_I) Naming.lookup(full_ip2);
                return true;
            } catch (Exception e) { System.out.println("Inseriu um IP errado!\n"+e); return false; }
        }
	}
    
    protected boolean subscribe2Servers(RMIClient client, String depart_name) {
        String result=null;
        try {
            result = server1.subscribeNewClient(client, depart_name);
            System.out.println(result); 
        } catch (Exception e1) {
			try { 
                result = server2.subscribeNewClient(client, depart_name);
                System.out.println(result);
            } catch (Exception e2) { System.out.println("Servidor 1 e 2 Desligados."); return false; }
		}
       if (result.split(":")[0].compareTo("200")==0) return true;
       return false;
    }

	public RMIServer_I getServer1() { return server1; }
	public RMIServer_I getServer2() { return server2; }
	public boolean setNewServer(String new_server_ip) throws RemoteException { 
        if (server1==null) { 
            try { server1= (RMIServer_I) Naming.lookup(new_server_ip); return true; }
            catch (Exception e) { return false; }
        } else if (server2==null) { 
            try { server2= (RMIServer_I) Naming.lookup(new_server_ip); return true; }
            catch (Exception e) { return false; }
        } else return false;
    }

    private String getRegistryFromIP(String ip, String registryName) { return "rmi://"+ip+":"+port+"/"+registryName; }

    synchronized public String ping() throws RemoteException { return "ACK"; }

}
