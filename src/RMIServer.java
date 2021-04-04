//  Default
import java.io.*;
//  RMI
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

//  CUSTOM 
import classes.User;
import classes.Election;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I, Runnable {   
        private static final long serialVersionUID = 1L;

        //  CONNECTION DATA
        private static RMIServer server=null;
        public static String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
        private static int port=1099;
        private RMIServer_I pinger=null;
        private boolean main_server= false;
        private String my_rmi_ip="", remoted_server_ip="";

        public ArrayList<RMIClient_I> clients_list= new ArrayList<>(), admins_list= new ArrayList<>();
        public ArrayList<String> associated_deps_list= new ArrayList<>();

        //  THREADS
        private static IsClientAlive client_checker;
        private static ElectionsState election_state;
        private static ServersManagement servers_manager;

        //  STORAGE DATA
        private FilesManagement file_manage= new FilesManagement();
        private static Inputs input_manage= new Inputs();
        
        //  DATA TO STORE
        private ArrayList<College> colleges= new ArrayList<>();
        public ArrayList<Election> unstarted_elections= new ArrayList<>(), running_elections= new ArrayList<>(), finished_elections= new ArrayList<>();

        public RMIServer() throws RemoteException { super(); }
        public void run() { }

        public static void main(String[] args) throws Exception {
            System.getProperties().put("java.security.policy","AdminConsole.policy");
            if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
            
            try { LocateRegistry.createRegistry(port); } 
            catch (Exception e) { }

            String ip="";
            try(final DatagramSocket socket = new DatagramSocket()){
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip=socket.getLocalAddress().getHostAddress();
            } catch (Exception e3) { throw new Exception("Nao foi possivel obter o endereco IP do Servidor"); }


            if (!initServers(ip)) System.exit(0);
            servers_manager= new ServersManagement("servers_manager", server);
            client_checker= new IsClientAlive("clients_checker", server);
            election_state= new ElectionsState("election_state", server);

            server.readAllFiles();

            try {
                client_checker.thread.join();
                System.out.println("Verificacao de ativacao dos Clientes: Desativa");
                election_state.thread.join();
                System.out.println("Verificacao de estado de Eleicoes: Desativa");
                servers_manager.manager_thread.join();
                System.out.println("Verificacao de estado dos Servidores: Desativa");
            } catch (InterruptedException e) { System.out.println("Interrupted"); }
        }
    
        //  ===========================================================================================================
        //  CONNECTIONS AND CONFIGURATIONS
        //  ===========================================================================================================
        private void readAllFiles() throws RemoteException {
            if (isMainServer()) {
                server.file_manage.loadElectionsFile(server.unstarted_elections, "unstarted");
                server.file_manage.loadElectionsFile(server.running_elections, "running");
                server.file_manage.loadElectionsFile(server.finished_elections, "finished");
                server.file_manage.loadCollegesFile(server.colleges);
            } else if (!isMainServer() && getPinger()!=null) {
                setColleges(server.getPinger().getColleges());
                setUnstarted_elections(server.getPinger().getUnstartedElections());
                setRunning_elections(server.getPinger().getRunningElections());
                setFinished_elections(server.getPinger().getFinishedElections());
            }
        }

        private static boolean initServers(String my_ip) {
            Scanner keyboard= new Scanner(System.in);
            String server1_ip= input_manage.askVariable(keyboard, "Insira o IP do Servidor Principal, se este for o Servidor Principal clique Enter: ", 5);
            keyboard.close();
            if (server1_ip.isBlank()) {
                //  TURN ON SERVER 1
                try { 
                    server= new RMIServer();
                    server.my_rmi_ip= getRegistryFromIP(my_ip, rmiregistry1);
                    Naming.rebind(server.my_rmi_ip, server);
                    System.out.println("Servidor 1 a correr no IP "+ server.my_rmi_ip);
                    server.main_server=true;
                    return true;
                } catch (Exception e2) { return false;}
            } else {
                //  TURN ON SERVER 2
                try { 
                    server= new RMIServer();
                    server.my_rmi_ip= getRegistryFromIP(my_ip, rmiregistry2);
                    server.remoted_server_ip= getRegistryFromIP(server1_ip, rmiregistry1);
                    Naming.rebind(server.my_rmi_ip, server);
                    server.pinger = (RMIServer_I) Naming.lookup(server.remoted_server_ip);
                    if (server.pinger.subscribeNewServer(server.my_rmi_ip)) System.out.println("200: Servidor associado com Sucesso!");
                    else { System.out.println("500: O servidor ja tem um Servidor Secundario Associado!"); return false; }
                    System.out.println("Servidor 2 a correr no IP "+ server.my_rmi_ip);
                    server.main_server=false;
                    return true;
                } catch (Exception e1) { System.out.println("Inseriu um IP incorreto!"); return false; }
            }
        }

        synchronized public String subscribeNewClient(RMIClient_I new_client, String depart_name) throws RemoteException {
            if (depart_name==null) { 
                server.admins_list.add(new_client);
                server.getPinger().setClientsList(server.admins_list, true);
                System.out.println("Novo Administrador Conectado ["+(server.admins_list.size()-1)+"]"); 
            } else { 
                Department selected_depart= getUniqueDepartment(depart_name);
                if (!selected_depart.getVoteTable()) return "400: "+depart_name+" nao tem Mesa de Voto!\n";
                if (selected_depart.getActivatedVoteTable()) return "400: Ja existe uma Mesa de Voto associada ao "+depart_name+"!\n";
                selected_depart.turnOnVoteTable();
                associated_deps_list.add(depart_name);
                server.clients_list.add(new_client); 
                server.getPinger().setClientsList(server.clients_list, false);
                System.out.println("Mesa de Voto do "+depart_name+" Conectado ["+(server.clients_list.size()-1)+"]");
            } return "200: Adicionado ao Servidor com Sucesso";
        }
        synchronized public boolean subscribeNewServer(String new_server_ip) throws RemoteException {
            if (server.isMainServer() && server.getPinger()==null) { 
                try {
                    remoted_server_ip= new_server_ip;
                    server.setPinger((RMIServer_I) Naming.lookup(remoted_server_ip));
                    System.out.println("Novo Servidor Secundario Adicionado!");
                    for (RMIClient_I admin : server.admins_list) { if (admin!=null) admin.setNewServer(new_server_ip); }
                    for (RMIClient_I client : server.clients_list) { if (client!=null) client.setNewServer(new_server_ip); }
                    return true; 
                } catch (Exception e) { return false; }
            } else return false;
        }

        synchronized private static String getRegistryFromIP(String ip, String registryName) { return "rmi://"+ip+":"+port+"/"+registryName; }
        synchronized public String ping() throws RemoteException { return "ACK"; }


        //  ===========================================================================================================
        //  SERVER ATTRIBUTES
        //  ===========================================================================================================
        synchronized public boolean isMainServer() throws RemoteException { return main_server; }
        synchronized public String getMy_rmi_ip() throws RemoteException { return my_rmi_ip; }
        synchronized public String getRemoted_server_ip() throws RemoteException { return remoted_server_ip; }
        synchronized public RMIServer_I getPinger() throws RemoteException { return pinger; }
        synchronized public void setMy_rmi_ip(String full_ip) throws RemoteException { this.my_rmi_ip= full_ip; }
        synchronized public void setRemoted_server_ip(String full_ip) throws RemoteException { this.remoted_server_ip= full_ip; }
        synchronized public void changeServerPriority() throws RemoteException { 
            server.main_server=!server.main_server; 
            if (!server.main_server) System.out.println("Este Servidor passou a ser Secundario!"); 
            else { System.out.println("Este Servidor passou a ser Primario!"); server.pinger=null; }
        }
        synchronized public void setPinger(RMIServer_I pinger) { 
            this.pinger= pinger; 
            if (this.pinger==null) {
                this.remoted_server_ip="";
                System.out.println("O Servidor Secundario foi desassociado!");
            }
        }


        //  ===========================================================================================================
        //  COMUNICATIONS WITH ADMIN CONSOLE
        //  ===========================================================================================================

        /**
        * @param new_college Name of the new college, registed with the new user data
        * @param new_department Name of the new department, registed with the new user data
        * @param new_user New User data
        * @return returns a 200 code if sucess during registation, 400 otherwise
        */
        synchronized public String registUser(String new_college, String new_department, User new_user) throws RemoteException {
            if (verifyUserExistence(new_user)) return "400: Uma pessoa com esse Numero de CC ja foi registada!\n";
            int college_index = IntStream.range(0, server.colleges.size()).filter(i -> server.colleges.get(i).getName().equals(new_college)).findFirst().orElse(-1);
            if (college_index==-1) {
                server.colleges.add(new College(new_college, new Department(new_department, new_college, new_user)));
            } else {
                int department_index = IntStream.range(0, server.colleges.get(college_index).getDepartments().size()).filter(i -> server.colleges.get(college_index).getDepartments().get(i).getName().equals(new_department)).findFirst().orElse(-1);
                if (department_index==-1) server.colleges.get(college_index).getDepartments().add(new Department(new_department, new_college, new_user));
                else server.colleges.get(college_index).getDepartments().get(department_index).getUsersWithType(new_user.getUser_type()).add(new_user);
            }
            
            System.out.println("Nova Pessoa Registada pela Consola de Administrador");
            file_manage.saveCollegesFile(colleges);
            if (server.getPinger()!=null) server.getPinger().setColleges(colleges);
            return "200: Pessoa registada com sucesso!\n";
        }
        synchronized public boolean verifyUserExistence(User comparing_user) {
            for (College college : server.colleges) {
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
            for (Election election : server.unstarted_elections) 
                if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
            for (Election election : server.running_elections)
                if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
            for (Election election : server.finished_elections)
                if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";

            server.unstarted_elections.add(new_election);
            server.file_manage.saveElectionsFile(server.unstarted_elections, "unstarted");
            if (server.getPinger()!=null) server.getPinger().setUnstarted_elections(server.unstarted_elections);
            System.out.println("Nova Eleicao Registada pela Consola de Administrador");
            return "200: Eleicao registada com sucesso!";
        }

        synchronized public ArrayList<College> getColleges() throws RemoteException { return this.colleges; }
        synchronized public ArrayList<String> getDepartmentsNames() throws RemoteException { 
            ArrayList<String> names= new ArrayList<>();
            for (College college : colleges)
                for (Department department : college.getDepartments()) names.add(department.getName());
            return names;
        }
        synchronized public ArrayList<String> getCollegesNames() throws RemoteException { 
            ArrayList<String> names= new ArrayList<>();
            for (College college : colleges) names.add(college.getName());
            return names;
        }
        synchronized public College getUniqueCollege(String college_name) throws RemoteException { 
            for (College college : server.getColleges()) 
                if (college.getName().compareTo(college_name)==0) return college;
            return null;
        }
        synchronized public Department getUniqueDepartment(String department_name) throws RemoteException { 
            for (College college : server.getColleges())
                for (Department department : college.getDepartments())
                    if (department.getName().compareTo(department_name)==0) return department;
            return null;
        }
        
        synchronized public ArrayList<Election> getUnstartedElections() throws RemoteException { return unstarted_elections; }
        synchronized public ArrayList<Election> getRunningElections() throws RemoteException { return running_elections; }
        synchronized public ArrayList<Election> getFinishedElections() throws RemoteException { return finished_elections; }
        synchronized public ArrayList<String> getElectionNames(String election_state) throws RemoteException { 
            ArrayList<String> names= new ArrayList<>();
            if (election_state=="running") for (Election election : running_elections) names.add(election.getTitle());
            else if (election_state=="finished") for (Election election : finished_elections) names.add(election.getTitle());
            else if (election_state=="unstarted") for (Election election : unstarted_elections) names.add(election.getTitle());
            return names;
        }
        synchronized public Election getUniqueElection(String election_name, String election_state) throws RemoteException { 
            if (election_state=="unstarted") {
                for (Election election : server.getUnstartedElections())
                    if (election.getTitle().compareTo(election_name)==0) return election;
            } else if (election_state=="running") {
                for (Election election : server.getRunningElections())
                    if (election.getTitle().compareTo(election_name)==0) return election;
            } else if (election_state=="finished") {
                for (Election election : server.getFinishedElections())
                    if (election.getTitle().compareTo(election_name)==0) return election;
            } return null;
        }

        synchronized public String setUpdatedElection(Election updated_election, boolean is_candidature) throws RemoteException { 
            for (Election election : unstarted_elections)
                if (election.getTitle().compareTo(updated_election.getTitle())==0) {
                    unstarted_elections.set(unstarted_elections.indexOf(election), updated_election);
                    server.file_manage.saveElectionsFile(unstarted_elections, "unstarted");
                    if (server.getPinger()!=null) server.getPinger().setUnstarted_elections(unstarted_elections);
                    if (is_candidature) { System.out.println("Nova Candidatura Submetida pela Consola de Administrador"); return "200: Candidatura Submetida com Sucesso"; }
                    else { System.out.println("Eleicao "+election.getTitle()+" alterada pela Consola de Administrador"); return "200: Eleicao Editada com Sucesso"; }
                }
            return "400: Essa Eleicao nao foi encontrada";
        }
        synchronized public String setUpdatedDepartment(Department updated_department, boolean new_vote_table) throws RemoteException { 
            for (College college : colleges) {
                ArrayList<Department> colleg_deps= college.getDepartments();
                int dep_index = IntStream.range(0, colleg_deps.size()).filter(i -> colleg_deps.get(i).getName().equals(updated_department.getName())).findFirst().orElse(-1);
                if (dep_index==-1) continue;
                college.getDepartments().set(dep_index, updated_department);
                server.file_manage.saveCollegesFile(colleges);
                if (server.getPinger()!=null) server.getPinger().setColleges(colleges);
                if (new_vote_table) { 
                    System.out.println("Mesa de Voto Registada no Departamento "+updated_department.getName()+", "+updated_department.getCollege()+" com "+updated_department.getMCServerDatas()+" terminais de voto"); 
                    return "200: Mesa de Voto Registada com Sucesso"; 
                } else {
                    System.out.println("Mesa de Voto Eliminada no Departamento "+updated_department.getName()+", "+updated_department.getCollege()); 
                    return "200: Mesa de Voto Eliminada com Sucesso"; 
                }
            } return "400: Departamento nao encontrado";
        }

        synchronized public ArrayList<Department> getDepartmentsWithOrNotVoteTable(boolean with) throws RemoteException {
            ArrayList<Department> available_departments= new ArrayList<>();
            for (College college : colleges) {
                for (Department department : college.getDepartments()) {
                    if (!with && !department.getVoteTable()) available_departments.add(department);
                    if (with && department.getVoteTable()) available_departments.add(department);
                }
            } return available_departments;
        }

        synchronized public ArrayList<Election> getElectionToVoteTable(String depart_name) throws RemoteException {
            ArrayList<Election> available_elections= new ArrayList<>();
            for (Election running_election : server.running_elections) {
                if (running_election.getCollege_restrictions().isEmpty() && running_election.getDepartment_restrictions().isEmpty()) {
                    available_elections.add(running_election); continue;
                }
                for (String college_name : running_election.getCollege_restrictions()) {
                    College college= getUniqueCollege(college_name);
                    for (Department department : college.getDepartments())
                        if (department.getName().compareTo(depart_name)==0) available_elections.add(running_election);
                } 
                for (String department_name : running_election.getDepartment_restrictions()) 
                    if (department_name.compareTo(depart_name)==0) available_elections.add(running_election);
            } 
            return available_elections;
        }

        //  ===========================================================================================================
        //  COMUNICATIONS WITH MULTICAST SERVERS
        //  ===========================================================================================================

        synchronized public boolean authorizeUser(String cc_number) throws RemoteException {
            for (College college : colleges) {
                for (Department department : college.getDepartments()) {
                    for (User student : department.getStudents())
                        if (student.getCc_number().compareTo(cc_number)==0) return true;
                    for (User teacher : department.getTeachers())
                        if (teacher.getCc_number().compareTo(cc_number)==0) return true;
                    for (User staff : department.getStaff())
                        if (staff.getCc_number().compareTo(cc_number)==0) return true;
                }
            } return false;
        }
        synchronized public boolean authenticateUser(String username, String password) throws RemoteException {
            for (College college : colleges) {
                for (Department department : college.getDepartments()) {
                    for (User student : department.getStudents())
                        if (student.getName().compareTo(username)==0 && student.getPassword().compareTo(password)==0) return true;
                    for (User teacher : department.getTeachers())
                        if (teacher.getName().compareTo(username)==0 && teacher.getPassword().compareTo(password)==0) return true;
                    for (User staff : department.getStaff())
                        if (staff.getName().compareTo(username)==0 && staff.getPassword().compareTo(password)==0) return true;
                }
            } return false;
        }

        synchronized public void setColleges(ArrayList<College> colleges) throws RemoteException { 
            if (!isMainServer()) System.out.println("Informacao dos Eleitores recebida do Servidor Primario!");
            else System.out.println("Informacao dos Eleitores recebida do Servidor Secundario!");
            this.colleges = colleges; 
            file_manage.saveCollegesFile(this.colleges);
        }
        synchronized public void setUnstarted_elections(ArrayList<Election> unstarted_elections) throws RemoteException { 
            if (!isMainServer()) System.out.println("Informacao das Eleicoes Nao Comecadas recebida do Servidor Primario!");
            else System.out.println("Informacao das Eleicoes Nao Comecadas recebida do Servidor Secundario!");
            this.unstarted_elections = unstarted_elections; 
            file_manage.saveElectionsFile(running_elections, "unstarted");
        }
        synchronized public void setRunning_elections(ArrayList<Election> running_elections) throws RemoteException { 
            if (!isMainServer()) System.out.println("Informacao das Eleicoes Atuais recebida do Servidor Primario!");
            else System.out.println("Informacao das Eleicoes Atuais recebida do Servidor Scundario!");
            this.running_elections = running_elections; 
            file_manage.saveElectionsFile(running_elections, "running");
        }
        synchronized public void setFinished_elections(ArrayList<Election> finished_elections) throws RemoteException { 
            if (!isMainServer()) System.out.println("Informacao das Eleicoes Atuais recebida do Servidor Primario!");
            else System.out.println("Informacao das Eleicoes Acabadas recebida do Servidor Secundario!");
            this.finished_elections = finished_elections;
            file_manage.saveElectionsFile(running_elections, "finished");
        }
        synchronized public void setClientsList(ArrayList<RMIClient_I> clients_list, boolean admins) throws RemoteException { 
            if (!isMainServer()) System.out.println("Clients Ativos recebidos do Servidor Primario!");
            else System.out.println("Clients Ativos recebidos do Servidor Secundario!");
            if (admins) this.admins_list = clients_list;
            else this.clients_list= clients_list;
        }

}

/**
 * FilesManagement takes care of all data storage
*/
class FilesManagement {
    public FilesManagement() { }
    /**
     * Writes colleges in a object file called "database_colleges.data"
     * @param colleges ArrayList of Colleges, considering each one has a Department and each Department has 3 ArrayList (students, teachers and staff)
     * @return true if the file was save successfully, false otherwise
     */
    public boolean saveCollegesFile(ArrayList<College> colleges) {
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
    public Thread manager_thread;
    public RMIServer server; 
    public int error_response;

    ServersManagement(String threadname, RMIServer server) {
        this.server = server;
        manager_thread = new Thread(this, threadname);
        System.out.println("Verificacao de estado dos Servidores: Ativa");
        error_response= 0;
        manager_thread.start();
    }
    
    public void run() {
        while (true) {
            try { Thread.sleep(10); }
            catch (Exception e) { }
            try {
                if (server.getPinger()!=null) {
                    try { 
                        server.getPinger().ping();
                        error_response=0;
                    } catch(RemoteException e) { 
                        if (!server.isMainServer()) System.out.println("Ping "+(++error_response)+" ao Servidor Principal: Falhado");  
                        else System.out.println("Ping "+(++error_response)+" ao Servidor Secundario: Falhado");  
                        try { 
                            server.setPinger((RMIServer_I) Naming.lookup(server.getRemoted_server_ip())); 
                            if (!server.isMainServer()) System.out.println("O Servidor Primario voltou!");
                            else System.out.println("O Servidor Secundario voltou!");
                            error_response=0;
                        } catch (Exception e2) { }
                    }
                    
                    if(error_response==5 && !server.isMainServer()) {
                        try { 
                            error_response=0;
                            server.changeServerPriority();
                        } catch(Exception e){ e.printStackTrace(); }   
                    } 
                    else if (error_response==5 && server.isMainServer()) { server.setPinger(null); error_response=0; }
                }
            } catch (RemoteException e0) { }
        }
    }
    
}


/**
 * IsClientAlive is a Thread that iterates all the Clients (includind Admins) and checks if they're alive
 */
class IsClientAlive implements Runnable {
    public Thread thread;
    private RMIServer server;
    
    /**
     * @param threadname Name of the Thread
     * @param clients ArrayList of subscribed and running Clients 
     * @param admins ArrayList of subscribed and running Admin Consoles
     */
    public IsClientAlive(String threadname, RMIServer server) {
        this.server= server;
        thread = new Thread(this, threadname);
        System.out.println("Verificacao de ativacao dos Clientes: Ativa");
        thread.start();
    }
    
    
    public void run() {
        int client_id=0, admin_id=0;
        while (true) {
            try { Thread.sleep(10); }
            catch (Exception e) { }
            
            //  PING CLIENTS
            if (!server.clients_list.isEmpty()) {
                //  RESET ARRAY
                if (client_id<0 || client_id>=server.clients_list.size()) client_id=0;
                try { 
                    if (server.clients_list.get(client_id)!=null) { server.clients_list.get(client_id).ping(); client_id++; } 
                    else client_id++;
                } catch (Exception e1) {
                    try {
                        server.getUniqueDepartment(server.associated_deps_list.get(client_id)).turnOffVoteTable();
                        server.clients_list.set(client_id, null);
                        server.getPinger().setClientsList(server.clients_list, false);
                        System.out.println("O Cliente ["+(client_id++)+"] desconectou-se!");
                    } catch (Exception e2) { }
                }
            }
            //  PING ADMINS
            if (!server.admins_list.isEmpty()) {
                //  RESET ARRAY
                if (admin_id<0 || admin_id>=server.admins_list.size()) admin_id=0;
                try { 
                    if (server.admins_list.get(admin_id)!=null) { server.admins_list.get(admin_id).ping(); admin_id++; } 
                    else admin_id++;
                } catch (Exception e) {
                    server.admins_list.set(admin_id, null);
                    try { server.getPinger().setClientsList(server.admins_list, true); }
                    catch (Exception e2) { }
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
    RMIServer server;
    FilesManagement file_manage= new FilesManagement();

    
    ElectionsState(String threadname, RMIServer server) {
        this.server = server;
        thread = new Thread(this, threadname);
        System.out.println("Verificacao de estado das Eleicoes: Ativa");
        thread.start();
    }
    public void run() {
        int unstarted_id=0, running_id=0;
        Election temp_elec;
        while (true) {
            try { Thread.sleep(10); }
            catch (Exception e) { }
            LocalDateTime now= LocalDateTime.now();
            //  CHECK UNSTARTED ELECTIONS
            if (!server.unstarted_elections.isEmpty()) {
                //  RESET ARRAY
                if (unstarted_id<0 || unstarted_id>=server.unstarted_elections.size()) unstarted_id=0;
                temp_elec= server.unstarted_elections.get(unstarted_id);
                
                try { 
                    if (temp_elec.getStarting().compareTo(now)<=0) {
                        server.running_elections.add(temp_elec);
                        server.unstarted_elections.remove(unstarted_id);
                        file_manage.saveElectionsFile(server.unstarted_elections, "unstarted");
                        file_manage.saveElectionsFile(server.running_elections, "running");
                        if (server.getPinger()!=null && server.isMainServer()) {
                            server.getPinger().setUnstarted_elections(server.unstarted_elections);
                            server.getPinger().setRunning_elections(server.running_elections); 
                        }
                        System.out.println("A Eleicao "+temp_elec.getTitle()+" comecou!"); 
                    } else unstarted_id++;
                } catch (Exception e) { }
            }
            //  CHECK RUNNING ELECTIONS
            if (!server.running_elections.isEmpty()) {
                //  RESET ARRAY
                if (running_id<0 || running_id>=server.running_elections.size()) running_id=0;
                temp_elec= server.running_elections.get(running_id);
                
                try { 
                    if (temp_elec.getEnding().compareTo(now)<0) { 
                        server.finished_elections.add(temp_elec);
                        server.running_elections.remove(running_id); 
                        file_manage.saveElectionsFile(server.running_elections, "running");
                        file_manage.saveElectionsFile(server.finished_elections, "finished");
                        if (server.getPinger()!=null && server.isMainServer()) {
                            server.getPinger().setRunning_elections(server.running_elections);
                            server.getPinger().setFinished_elections(server.finished_elections);
                        }
                        System.out.println("A Eleicao "+temp_elec.getTitle()+" acabou!"); 
                    } else running_id++;
                } catch (Exception e) { }
            }
        }
    }
}
