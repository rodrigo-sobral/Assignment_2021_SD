package rmiserver;
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
import rmiserver.classes.User;
import rmiserver.classes.Vote;
import rmiserver.classes.Election;
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

        //  CLIENTS DATA
        public ArrayList<RMIClient_I> clients_list= new ArrayList<>(), admins_list= new ArrayList<>();
        public ArrayList<String> associated_deps_list= new ArrayList<>();

        //  THREADS
        private static IsClientAlive client_checker;
        private static ElectionsState election_state;
        private static ServersManagement servers_manager;

        //  STORAGE DATA
        private ObjectFilesManagement file_manage= new ObjectFilesManagement();
        private static Inputs input_manage= new Inputs();
        
        //  DATA TO STORE
        private ArrayList<College> colleges= new ArrayList<>();
        public ArrayList<Election> unstarted_elections= new ArrayList<>(), running_elections= new ArrayList<>(), finished_elections= new ArrayList<>();

        public RMIServer() throws RemoteException { super(); }
        public void run() { }

        public static void main(String[] args) throws Exception {
            //System.getProperties().put("java.security.policy","AdminConsole.policy");
            //if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
            
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

        /**
         * reads files if it's the main server, otherwise gets data from main server
         */
        private void readAllFiles() throws RemoteException {
            if (isMainServer()) {
                file_manage.loadElectionsFile(server.getUnstartedElections(), "unstarted");
                file_manage.loadElectionsFile(server.getRunningElections(), "running");
                file_manage.loadElectionsFile(server.getFinishedElections(), "finished");
                file_manage.loadCollegesFile(server.getColleges());
            } else if (!isMainServer() && getPinger()!=null) {
                setColleges(server.getPinger().getColleges());
                setUnstarted_elections(server.getPinger().getUnstartedElections());
                setRunning_elections(server.getPinger().getRunningElections());
                setFinished_elections(server.getPinger().getFinishedElections());
            }
        }

        /**
         * @param my_ip machine's ip
         * @return true if it actually inited server 
         */
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

        /**
         * @param new_client client that will be subscribed to the server
         * @param depart_name name of the department where the vote table is located, null if is just an administrator console
         * @return 200 message if there was no problems subscribing new client, 400 message otherwise
         */
        synchronized public String subscribeNewClient(RMIClient_I new_client, String depart_name) throws RemoteException {
            if (depart_name==null) { 
                server.admins_list.add(new_client);
                if (server.getPinger()!=null) server.getPinger().setClientsList(server.admins_list, true);
                System.out.println("Novo Administrador Conectado ["+(server.admins_list.size()-1)+"]"); 
            } else { 
                Department selected_depart= getUniqueDepartment(depart_name);
                if (!selected_depart.getVoteTable()) return "403: "+depart_name+" nao tem Mesa de Voto!\n";
                if (selected_depart.getActivatedVoteTable()) return "403: Ja existe uma Mesa de Voto associada ao "+depart_name+"!\n";
                selected_depart.turnOnVoteTable();
                associated_deps_list.add(depart_name);
                server.clients_list.add(new_client); 
                if (server.getPinger()!=null) server.getPinger().setClientsList(server.clients_list, false);
                System.out.println("Mesa de Voto do "+depart_name+" Conectado ["+(server.clients_list.size()-1)+"]");
            } return "200: Adicionado ao Servidor com Sucesso";
        }
        /**
         * @param new_server_ip ip address from the secundary server 
         * @return true if there was no problems subscribing new server, false otherwise
         */
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

        /**
         * 
         * @param ip machine's ipv4 address
         * @param registryName registry name (rmiregistry1 or rmiregistry2)
         * @return rmi full ip address
         */
        synchronized private static String getRegistryFromIP(String ip, String registryName) { return "rmi://"+ip+":"+port+"/"+registryName; }
        synchronized public String ping() throws RemoteException { return "ACK"; }


        //  ===========================================================================================================
        synchronized public boolean isMainServer() throws RemoteException { return main_server; }
        synchronized public String getMy_rmi_ip() throws RemoteException { return my_rmi_ip; }
        synchronized public String getRemoted_server_ip() throws RemoteException { return remoted_server_ip; }
        synchronized public RMIServer_I getPinger() throws RemoteException { return pinger; }
        synchronized public void setMy_rmi_ip(String full_ip) throws RemoteException { this.my_rmi_ip= full_ip; }
        synchronized public void setRemoted_server_ip(String full_ip) throws RemoteException { this.remoted_server_ip= full_ip; }
        synchronized public void changeServerPriority() throws RemoteException { 
            this.main_server=!this.main_server; 
            if (!this.main_server) System.out.println("Este Servidor passou a ser Secundario!"); 
            else { System.out.println("Este Servidor passou a ser Primario!"); this.setPinger(null); }
        }
        synchronized public void setPinger(RMIServer_I pinger) { 
            this.pinger= pinger; 
            if (this.pinger==null) { this.remoted_server_ip=""; System.out.println("O Servidor Secundario foi desassociado!"); }
        }


        //  ===========================================================================================================
        //  COMUNICATIONS WITH ADMIN CONSOLE
        //  ===========================================================================================================

        /**
         * it regists a new user and possibly a new department and college 
        * @param new_college Name of the new college, registed with the new user data
        * @param new_department Name of the new department, registed with the new user data
        * @param new_user New User data
        * @return returns a 200 code if sucess during registation, 400 otherwise
        */
        synchronized public String registUser(String new_college, String new_department, User new_user) throws RemoteException {
            if (verifyUserExistence(new_user)) return "403: Ja foi registado um Eleitor com esse Numero de CC!\n";
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
    
        /**
         * checks if a user is registed in the whole system, using cc number as a reference
         * @param comparing_user potential repeated user
         * @return true if is already registed, false if isn't
         */
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
    
        /**
         * regists a new election, according to its state
         * @return returns a 200 code if sucess during registation, 400 otherwise
         */
        synchronized public String registElection(Election new_election) throws RemoteException {
            for (Election election : getUnstartedElections()) 
                if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
            for (Election election : getRunningElections())
                if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";
            for (Election election : getFinishedElections())
                if (election.getTitle()==new_election.getTitle()) return "400: Uma Eleicao com esse Titulo ja foi registada!";

            getUnstartedElections().add(new_election);
            file_manage.saveElectionsFile(getUnstartedElections(), "unstarted");
            if (pinger!=null) pinger.setUnstarted_elections(getUnstartedElections());
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
        
        /**
         * Searches for a College with the same name as college_name
         * @return found college, null if wasn't found
         */
        synchronized public College getUniqueCollege(String college_name) throws RemoteException { 
            for (College college : getColleges()) 
                if (college.getName().compareTo(college_name)==0) return college;
            return null;
        }
        
        /**
         * Searches for a Department with the same name as department_name
         * @return found department, null if wasn't found
         */
        synchronized public Department getUniqueDepartment(String department_name) throws RemoteException { 
            for (College college : getColleges())
                for (Department department : college.getDepartments())
                    if (department.getName().compareTo(department_name)==0) return department;
            return null;
        }
        
        synchronized public ArrayList<Election> getUnstartedElections() throws RemoteException { return unstarted_elections; }
        synchronized public ArrayList<Election> getRunningElections() throws RemoteException { return running_elections; }
        synchronized public ArrayList<Election> getFinishedElections() throws RemoteException { return finished_elections; }
        
        /**
         * @return a list of all elections with a given state
         */
        synchronized public ArrayList<String> getElectionNames(String election_state) throws RemoteException { 
            ArrayList<String> names= new ArrayList<>();
            if (election_state.compareTo("running")==0) for (Election election : running_elections) names.add(election.getTitle());
            else if (election_state.compareTo("finished")==0) for (Election election : finished_elections) names.add(election.getTitle());
            else if (election_state.compareTo("unstarted")==0) for (Election election : unstarted_elections) names.add(election.getTitle());
            return names;
        }
        
        /**
         * Searches for a Election with the same name as election_name and same state as election_state
         * @return found election, null if wasn't found
         */
        synchronized public Election getUniqueElection(String election_name, String election_state) throws RemoteException { 
            if (election_state.compareTo("unstarted")==0) {
                for (Election election : getUnstartedElections())
                    if (election.getTitle().compareTo(election_name)==0) return election;
            } else if (election_state.compareTo("running")==0) {
                for (Election election : getRunningElections())
                    if (election.getTitle().compareTo(election_name)==0) return election;
            } else if (election_state.compareTo("finished")==0) {
                for (Election election : getFinishedElections())
                    if (election.getTitle().compareTo(election_name)==0) return election;
            } return null;
        }

        synchronized public String setUpdatedElection(Election updated_election, boolean is_candidature) throws RemoteException { 
            for (Election election : unstarted_elections) {
                if (election.getTitle().compareTo(updated_election.getTitle())==0) {
                    unstarted_elections.set(unstarted_elections.indexOf(election), updated_election);
                    this.file_manage.saveElectionsFile(unstarted_elections, "unstarted");
                    if (this.getPinger()!=null) this.getPinger().setUnstarted_elections(unstarted_elections);
                    if (is_candidature) { System.out.println("Nova Candidatura Submetida pela Consola de Administrador"); return "200: Candidatura Submetida com Sucesso"; }
                    else { System.out.println("Eleicao "+election.getTitle()+" alterada pela Consola de Administrador"); return "200: Eleicao Editada com Sucesso"; }
                }
            } return "400: Essa Eleicao nao foi encontrada";
        }

        

        synchronized public String setUpdatedDepartment(Department updated_department, boolean new_vote_table) throws RemoteException { 
            for (College college : colleges) {
                ArrayList<Department> colleg_deps= college.getDepartments();
                int dep_index = IntStream.range(0, colleg_deps.size()).filter(i -> colleg_deps.get(i).getName().equals(updated_department.getName())).findFirst().orElse(-1);
                if (dep_index==-1) continue;
                college.getDepartments().set(dep_index, updated_department);
                this.file_manage.saveCollegesFile(colleges);
                if (pinger!=null) pinger.setColleges(colleges);
                if (new_vote_table) { 
                    System.out.println("Mesa de Voto Registada no Departamento "+updated_department.getName()+", "+updated_department.getCollege()+" com "+updated_department.getVoteTerminals()+" terminais de voto"); 
                    return "200: Mesa de Voto Registada com Sucesso"; 
                } else {
                    System.out.println("Mesa de Voto Eliminada no Departamento "+updated_department.getName()+", "+updated_department.getCollege()); 
                    return "200: Mesa de Voto Eliminada com Sucesso"; 
                }
            } return "400: Departamento nao encontrado";
        }

        synchronized public boolean setUpdatedUser(User user_update) throws RemoteException{

            ArrayList<User> lista_users = new ArrayList<>();
            System.out.println("ENTROU AQUI\n");
            for (College college : colleges) {
                for (Department department : college.getDepartments()) {
                    lista_users.addAll(department.getStudents());
                    lista_users.addAll(department.getTeachers());
                    lista_users.addAll(department.getStaff());
                }
            }
            for (User user : lista_users) {
                if (user.getCc_number().compareTo(user_update.getCc_number())==0){
                   user.setNome_id(user_update.getNome_id());
                   user.setId_fb(user_update.getId_fb());
                   return true;
                }
            }
            return false;

            

            
        }

        /**
         * @return a list of departments with (or not, depending on the given argument) vote table
         */
        synchronized public ArrayList<Department> getDepartmentsWithOrNotVoteTable(boolean with) throws RemoteException {
            ArrayList<Department> available_departments= new ArrayList<>();
            for (College college : colleges) {
                for (Department department : college.getDepartments()) {
                    if (!with && !department.getVoteTable()) available_departments.add(department);
                    if (with && department.getVoteTable()) available_departments.add(department);
                }
            } return available_departments;
        }

        /**
         * @return a list of elections running in a given depart_name
         */
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

        synchronized public String receiveVote(Election updated_election) throws RemoteException {
            for (Election election : running_elections) {
                if (election.getTitle().compareTo(updated_election.getTitle())==0) {
                    running_elections.set(running_elections.indexOf(election), updated_election);
                    this.file_manage.saveElectionsFile(running_elections, "running");
                    if (this.getPinger()!=null) this.getPinger().setRunning_elections(running_elections);
                    System.out.println("Voto submetido na Eleicao "+updated_election.getTitle()); 
                    return "200: Voto Submetido com Sucesso";
                }
            } return "400: ";
        }

        //  ===========================================================================================================
        //  COMUNICATIONS WITH MULTICAST SERVERS
        //  ===========================================================================================================

        synchronized public boolean authorizeUser(String cc_number, Election voting_election) throws RemoteException {
            for (Vote voter : voting_election.getVoters())
                if (voter.getVoter_cc().compareTo(cc_number)==0) return false;
            for (College college : colleges) {
                if (voting_election.getCollege_restrictions().isEmpty() || voting_election.getCollege_restrictions().contains(college.getName())) {
                    for (Department department : college.getDepartments()) {
                        if (voting_election.getDepartment_restrictions().isEmpty() || voting_election.getDepartment_restrictions().contains(department.getName())) {
                            for (User student : department.getStudents())
                                if (student.getCc_number().compareTo(cc_number)==0) return true;
                            for (User teacher : department.getTeachers())
                                if (teacher.getCc_number().compareTo(cc_number)==0) return true;
                            for (User staff : department.getStaff())
                                if (staff.getCc_number().compareTo(cc_number)==0) return true;
                        }
                    }
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
            this.colleges = colleges; 
            file_manage.saveCollegesFile(this.colleges);
            if (!isMainServer()) System.out.println("Informacao dos Eleitores recebida do Servidor Primario!");
            else System.out.println("Informacao dos Eleitores recebida do Servidor Secundario!");
        }
        synchronized public void setUnstarted_elections(ArrayList<Election> unstarted_elections) throws RemoteException { 
            this.unstarted_elections = unstarted_elections; 
            file_manage.saveElectionsFile(running_elections, "unstarted");
            if (!isMainServer()) System.out.println("Informacao das Eleicoes Nao Comecadas recebida do Servidor Primario!");
            else System.out.println("Informacao das Eleicoes Nao Comecadas recebida do Servidor Secundario!");
        }
        synchronized public void setRunning_elections(ArrayList<Election> running_elections) throws RemoteException { 
            this.running_elections = running_elections; 
            file_manage.saveElectionsFile(running_elections, "running");
            if (!isMainServer()) System.out.println("Informacao das Eleicoes Atuais recebida do Servidor Primario!");
            else System.out.println("Informacao das Eleicoes Atuais recebida do Servidor Scundario!");
        }
        synchronized public void setFinished_elections(ArrayList<Election> finished_elections) throws RemoteException { 
            this.finished_elections = finished_elections;
            file_manage.saveElectionsFile(running_elections, "finished");
            if (!isMainServer()) System.out.println("Informacao das Eleicoes Acabadas recebida do Servidor Primario!");
            else System.out.println("Informacao das Eleicoes Acabadas recebida do Servidor Secundario!");
        }
        synchronized public void setClientsList(ArrayList<RMIClient_I> clients_list, boolean admins) throws RemoteException { 
            if (admins) this.admins_list = clients_list;
            else this.clients_list= clients_list;
            if (!isMainServer()) System.out.println("Clients Ativos recebidos do Servidor Primario!");
            else System.out.println("Clients Ativos recebidos do Servidor Secundario!");
        }

}

/**
 * ObjectFilesManagement takes care of all data storage and reading relative to object files
*/
class ObjectFilesManagement {  
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
     * @param colleges ArrayList of Colleges to be filled, considering each one has a Department and each Department has 3 ArrayList (students, teachers and staff)
     * @return list of all the colleges
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
    
    /**
     * Writes elections in diferent object files, depending on the elections' state (unstarted, running or finished)
     * @param elections ArrayList of Elections
     * @param election_state type of all election present in elections argument 
     * @return true if the file was save successfully, false otherwise
     */
    public boolean saveElectionsFile(ArrayList<Election> elections, String election_state) {
        String filePath = "database_elections_"+election_state+".dat";
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
    
    /**
     * Reads and loads all elections from diferent object files, depending on the elections' state (unstarted, running or finished)
     * @param elections ArrayList of Elections to be filled
     * @param election_state type of all election present in elections argument 
     * @return list of all the elections wich state was referenced in the arguments
     */
    public ArrayList<Election> loadElectionsFile(ArrayList<Election> elections, String election_state) {
        String filePath = "database_elections_"+election_state+".dat";
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

/**
 * ServersManagement is a Thread that has the responsability to response to failovers between main and secundary server
 */
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
    ObjectFilesManagement file_manage= new ObjectFilesManagement();

    
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
