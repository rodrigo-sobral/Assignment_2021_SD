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
    import classes.Election;
    import classes.User;

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
            client_checker= new IsClientAlive("clients_checker", server.clients_list, server.admins_list, server.colleges, server.associated_deps_list);
            election_state= new ElectionsState("election_state", server);

            server.readAllFiles(server);

            if (!server.running_elections.isEmpty()) {
                for (String  name: server.getElectionNames("unstarted")) System.out.println(name);
                for (String  name: server.getElectionNames("running")) System.out.println(name);
            }
            if (!server.colleges.isEmpty()) for (String  name: server.getCollegesNames()) System.out.println(name);

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
        private void readAllFiles(RMIServer active_server) throws RemoteException {
            if (isMainServer()) {
                active_server.file_manage.loadElectionsFile(active_server.unstarted_elections, "unstarted");
                active_server.file_manage.loadElectionsFile(active_server.running_elections, "running");
                active_server.file_manage.loadElectionsFile(active_server.finished_elections, "finished");
                active_server.file_manage.loadCollegesFile(active_server.colleges);
            } else if (!isMainServer() && getPinger()!=null) {
                setColleges(active_server.getPinger().getColleges());
                setUnstarted_elections(active_server.getPinger().getUnstartedElections());
                setRunning_elections(active_server.getPinger().getRunningElections());
                setFinished_elections(active_server.getPinger().getFinishedElections());
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
                } catch (Exception e2) { System.out.println(e2); return false;}
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
                admins_list.add(new_client);
                //  TODO: corrigir bug do administrador se desconectar
                System.out.println("Novo Administrador Conectado ["+(admins_list.size()-1)+"]"); 
                }
            else { 
                Department selected_depart= getUniqueDepartment(depart_name);
                if (!selected_depart.getVoteTable()) return "400: "+depart_name+" nao tem Mesa de Voto!\n";
                if (selected_depart.getActivatedVoteTable()) return "400: Ja existe uma Mesa de Voto associada ao "+depart_name+"!\n";
                selected_depart.turnOnVoteTable();
                associated_deps_list.add(depart_name);
                clients_list.add(new_client); 
                System.out.println("Mesa de Voto do "+depart_name+" Conectado ["+(clients_list.size()-1)+"]");
            } return "200: Adicionado ao Servidor com Sucesso";
        }
        synchronized public boolean subscribeNewServer(String new_server_ip) throws RemoteException {
            if (pinger==null || getRemoted_server_ip().isEmpty() || getRemoted_server_ip().compareTo(new_server_ip)==0) { 
                try {
                    remoted_server_ip= new_server_ip;
                    setPinger((RMIServer_I) Naming.lookup(remoted_server_ip));
                    System.out.println("Novo Servidor Secundario Adicionado!");
                    return true; 
                } catch (Exception e) { System.out.println(e); return false; }
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
        synchronized public void setMy_rmi_ip(String full_ip) throws RemoteException { this.my_rmi_ip= full_ip; }
        synchronized public void setRemoted_server_ip(String full_ip) throws RemoteException { this.remoted_server_ip= full_ip; }
        synchronized public RMIServer_I getPinger() throws RemoteException { return pinger; }
        synchronized public void changeServerPriority() throws RemoteException { 
            main_server=!main_server; 
            if (!main_server) System.out.println("Este Servidor passou a ser Secundario!"); 
            else { System.out.println("Este Servidor passou a ser Primario!"); pinger=null; }
        }
        synchronized public void setPinger(RMIServer_I pinger) { 
            this.pinger= pinger; 
            if (this.pinger==null) System.out.println("O Servidor Secundario foi desassociado!");
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
            int college_index = IntStream.range(0, colleges.size()).filter(i -> colleges.get(i).getName().equals(new_college)).findFirst().orElse(-1);
            if (college_index==-1) {
                colleges.add(new College(new_college, new Department(new_department, new_college, new_user)));
            } else {
                int department_index = IntStream.range(0, colleges.get(college_index).getDepartments().size()).filter(i -> colleges.get(college_index).getDepartments().get(i).getName().equals(new_department)).findFirst().orElse(-1);
                if (department_index==-1) colleges.get(college_index).getDepartments().add(new Department(new_department, new_college, new_user));
                else colleges.get(college_index).getDepartments().get(department_index).getUsersWithType(new_user.getUser_type()).add(new_user);
            }
            
            System.out.println("Nova Pessoa Registada pela Consola de Administrador");
            file_manage.saveCollegesFile(colleges);
            if (server.getPinger()!=null) server.getPinger().setColleges(colleges);
            return "200: Pessoa registada com sucesso!\n";
        }
        synchronized public boolean verifyUserExistence(User comparing_user) {
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
            if (server.getPinger()!=null) server.getPinger().setUnstarted_elections(unstarted_elections);
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
        
        synchronized public String setUpdatedElection(Election updated_election, boolean is_candidature) throws RemoteException { 
            for (Election election : unstarted_elections)
                if (election.getTitle().compareTo(updated_election.getTitle())==0) {
                    unstarted_elections.set(unstarted_elections.indexOf(election), updated_election);
                    file_manage.saveCollegesFile(colleges);
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
                file_manage.saveCollegesFile(colleges);
                if (server.getPinger()!=null) server.getPinger().setColleges(colleges);
                if (new_vote_table) { 
                    System.out.println("Mesa de Voto Registada no Departamento "+updated_department.getName()+", "+updated_department.getCollege()+" com "+updated_department.getVoteTerminals()+" terminais de voto"); 
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
            System.out.println("Informacao dos Eleitores recebida do Servidor Primario!");
            this.colleges = colleges; 
        }
        synchronized public void setUnstarted_elections(ArrayList<Election> unstarted_elections) throws RemoteException { 
            System.out.println("Informacao das Eleicoes Nao Comecadas recebida do Servidor Primario!");
            this.unstarted_elections = unstarted_elections; 
        }
        synchronized public void setRunning_elections(ArrayList<Election> running_elections) throws RemoteException { 
            System.out.println("Informacao das Eleicoes Atuais recebida do Servidor Primario!");
            this.running_elections = running_elections; 
        }
        synchronized public void setFinished_elections(ArrayList<Election> finished_elections) throws RemoteException { 
            System.out.println("Informacao das Eleicoes Acabadas recebida do Servidor Primario!");
            this.finished_elections = finished_elections;
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
        public Thread manager_thread;
        public RMIServer server; 
        public boolean im_the_main_now=false;
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
                                im_the_main_now=!im_the_main_now;
                            } catch(Exception e){ e.printStackTrace(); }   
                        } 
                        else if (error_response==5 && server.isMainServer()) { server.setPinger(null); error_response=0; }

                        if (im_the_main_now) {
                            try {
                                try { 
                                    server.setPinger((RMIServer_I) Naming.lookup(server.getRemoted_server_ip())); 
                                    server.getPinger().changeServerPriority();
                                    im_the_main_now=!im_the_main_now; 
                                } catch (Exception e2) { }
                            } catch (Exception e1) { }
                        }
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
        private ArrayList<RMIClient_I> clients, admins;
        private ArrayList<College> colleges;
        private ArrayList<String> associated_deps_list= new ArrayList<>();

        /**
         * @param threadname Name of the Thread
         * @param clients ArrayList of subscribed and running Clients 
         * @param admins ArrayList of subscribed and running Admin Consoles
         */
        public IsClientAlive(String threadname, ArrayList<RMIClient_I> clients, ArrayList<RMIClient_I> admins, ArrayList<College> colleges, ArrayList<String> associated_deps_list) {
            this.clients = clients;
            this.admins = admins;
            this.colleges = colleges;
            this.associated_deps_list = associated_deps_list;
            thread = new Thread(this, threadname);
            System.out.println("Verificacao de ativacao dos Clientes: Ativa");
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
                    if (client_id<0 || client_id>=clients.size()) client_id=0;
                    try { 
                        if (clients.get(client_id)!=null) { clients.get(client_id).ping(); client_id++; } 
                        else client_id++;
                    } catch (Exception e1) {
                        try { turnOffVoteTable(associated_deps_list.get(client_id)); }
                        catch (Exception e2) { }
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
                        try {
                            //Naming.lookup(admins.get(admin_id))    
                        } catch (Exception e1) {
                        
                        }
                        admins.set(admin_id, null);
                        System.out.println("O Administrador ["+(admin_id++)+"] desconectou-se!");
                    }
                }
            }
        }
        synchronized public Department turnOffVoteTable(String department_name) throws RemoteException { 
            for (College college : colleges) {
                for (Department department : college.getDepartments()) {
                    if (department.getName().compareTo(department_name)==0) department.turnOffVoteTable();
                }
            } return null;
        }
        
    }


    /**
     * ElectionsState is a Thread that iterates all the Elections (including Unstarted, Running and Finished ones) and manage them according to their defined Starting and Ending Dates
    */
    class ElectionsState implements Runnable {
        public Thread thread;
        RMIServer server;

        /**
         * @param threadname Name of the Thread
         * @param unstarted_elections ArrayList of Unstarted Elections
         * @param running_elections ArrayList of Running Elections
         * @param finished_elections ArrayList of Finished Elections
        */
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
                try { Thread.sleep(100); }
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
                            if (server.getPinger()!=null) {
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
                            if (server.getPinger()!=null) {
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

