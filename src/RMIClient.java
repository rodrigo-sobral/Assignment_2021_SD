import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RMIClient extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I server1, server2;
	private int port=1099;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
    private String my_rmi_ip, remoted_rmi_ip;

    RMIClient() throws RemoteException { 
        super();
        System.getProperties().put("java.security.policy","AdminConsole.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
    }

    protected boolean connect2Servers(Scanner keyboard) {
        try { LocateRegistry.createRegistry(port); } 
        catch (Exception e) { }
        String server1_ip= new Inputs().askVariable(keyboard, "Insira o IP dum Servidor Ativo: ", 5);
		try { 
            String full_ip= getRegistryFromIP(server1_ip, rmiregistry1);
            server1 = (RMIServer_I) Naming.lookup(full_ip);
            server1.setMy_rmi_ip(full_ip);
            if (!server1.isMainServer()) {
                server2= (RMIServer_I) Naming.lookup(server1.getRemoted_server_ip());
                server2.setRemoted_server_ip(full_ip);
                server1.setRemoted_server_ip(server1.getRemoted_server_ip());
                server2.setMy_rmi_ip(server1.getRemoted_server_ip());
            } return true;
        } catch (Exception e1) { 
            try {
                //String full_ip= "rmi://192.168.1.37:1099/rmiconnection2";
                String full_ip= getRegistryFromIP(server1_ip, rmiregistry2);
                server1 = (RMIServer_I) Naming.lookup(full_ip);
                server1.setMy_rmi_ip(full_ip);
                if (!server1.isMainServer()) {
                    server2= (RMIServer_I) Naming.lookup(server1.getRemoted_server_ip());
                    server2.setRemoted_server_ip(full_ip);
                    server1.setRemoted_server_ip(server1.getRemoted_server_ip());
                    server2.setMy_rmi_ip(server1.getRemoted_server_ip());
                } return true;
            } catch (Exception e) { System.out.println("Inseriu um IP errado!"); return false;}
        }
	}
    protected boolean subscribe2Servers(RMIClient client, String depart_name) {
        String result=null;
        try {
            result = server1.subscribeNewClient(client, depart_name);
            System.out.println(result); 
        } catch (Exception e1) {
            System.out.println("Servidor 1 Fechado");
			try { 
                result = server2.subscribeNewClient(client, depart_name);
                System.out.println(result);
            } catch (Exception e2) { System.out.println("Servidor 2 Fechado."); return false; }
		}
        if (depart_name!=null && result!=null) {
            String[] http_code= result.split(":");
            if (http_code[0].compareTo("200")==0) return true;
            else return false;
        } return false;
    }

	public RMIServer_I getServer1() { return server1; }
	public RMIServer_I getServer2() { return server2; }
    private String getRegistryFromIP(String ip, String registryName) { return "rmi://"+ip+":"+port+"/"+registryName; }

    synchronized public String ping() throws RemoteException { return "ACK"; }

    public String getMyRmiIp() throws RemoteException { return my_rmi_ip; }
    public String getRemotedRmiIp() throws RemoteException { return remoted_rmi_ip; }

    public void setMyRmiIp(String my_rmi_ip) throws RemoteException { this.my_rmi_ip = my_rmi_ip; }
    public void setRemotedRmiIp(String remoted_rmi_ip) throws RemoteException { this.remoted_rmi_ip = remoted_rmi_ip; }

}
