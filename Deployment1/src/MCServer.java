import java.util.ArrayList;

import classes.Candidature;
import classes.Election;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;
import java.lang.Thread;


public class MCServer extends UnicastRemoteObject implements Runnable {
    private static final long serialVersionUID = 1L;
    private MCServerData desk;
    private String message_envia;
    private Boolean votes_aux;
    private String message_recebe;
    private ArrayList<String> array_candidature;
    private ArrayList<String> eleitor_cc;
    private Boolean ready;
    //threads
    private static MCServer mesa_voto;
    private static SecMultServer mesa_voto2;
    private static RMIClient rmi_connection;
    public Thread thread;
    Election selected_election;
    

    public Boolean getVotes_aux() {
        return votes_aux;
    }
    public Boolean getReady() {
        return ready;
    }
    public String getMessage_envia() {
        return message_envia;
    }
    public String getMessage_recebe() {
        return message_recebe;
    }

    public Election getSelected_election() {
        return selected_election;
    }
    public void setMessage_envia(String message_envia) {
        this.message_envia = message_envia;
    }

    public void setVotes_aux(Boolean votes_aux) {
        this.votes_aux = votes_aux;
    }
    public void setMessage_recebe(String message_recebe) {
        this.message_recebe = message_recebe;
    }
    public void setReady(Boolean ready) {
        this.ready = ready;
    }
    
    public MCServer(Election selected_election,int n_max_terminais,SecMultServer mesa_voto2,String threadname,String ip, String port,String depar) throws RemoteException {
        this.message_envia = "";
        this.message_recebe = "";
        this.desk = new MCServerData(ip,port,depar);
        MCServer.mesa_voto2 = mesa_voto2;
        thread = new Thread(this,threadname);
        this.array_candidature = new ArrayList<>();
        this.eleitor_cc = new ArrayList<>();
        this.selected_election = selected_election;
        this.ready = false;
        this.votes_aux = false;
    }

    public ArrayList<String> getEleitor_cc() { return eleitor_cc;}
    public ArrayList<String> getArray_candidature() { return array_candidature; }
    public Thread getThread() { return thread; }
    public MCServerData getDesk() { return desk; }

    
    public static void main(String[] args) throws RemoteException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String depar="";
        Inputs input = new Inputs();
        int cont = 0, ind;
        Random alea = new Random();
        int n_max_terminais=2 ;
        String ip,port;
        
        rmi_connection = new RMIClient();
        while (!rmi_connection.connect2Servers(scanner));

        ArrayList<Election> available_elections=null;
        int election_option=0;
        while (available_elections==null) {
            while (true) {
                depar = input.askDepartment(rmi_connection, scanner, new ArrayList<>(), true);
                if (!depar.isEmpty()) break;
            }
            try { if (rmi_connection.getServer1()!=null) available_elections= rmi_connection.getServer1().getElectionToVoteTable(depar); }
            catch (Exception e1) {
                try { if (rmi_connection.getServer2()!=null) available_elections= rmi_connection.getServer2().getElectionToVoteTable(depar); }
                catch (Exception e2) { }
            }
            if (available_elections==null) { System.out.println("400: Nao existe uma Eleicao para esse Departamento!"); continue; }
            
            //  ASK ELECTION TO THE VOTE TABLE
            System.out.println("----------------------------------------");
            System.out.println("Eleicoes Disponiveis [0 para Voltar]");
            System.out.println("----------------------------------------");
            for (int i = 0; i < available_elections.size(); i++)  {
                Election temp_election= available_elections.get(i);
                System.out.println(i+1+": "+temp_election.getElectionState()+"\t"+temp_election.getTitle()+"\t"+temp_election.getStartingDateString()+"\t"+temp_election.getEndingDateString()+"\t"+temp_election.getDescription());
            }
            System.out.println("----------------------------------------");
            election_option= input.checkIntegerOption(scanner, "Opcao: ", 0, available_elections.size())-1;
            
            if (election_option==-1) { available_elections=null; continue; }
        }

        while (!rmi_connection.subscribe2Servers(rmi_connection, depar));
        Election selected_election= available_elections.get(election_option);

        mesa_voto = new MCServer(selected_election,n_max_terminais,mesa_voto2,"mesa_voto","","",depar);
        mesa_voto2 = new SecMultServer(mesa_voto2,mesa_voto.getEleitor_cc(),selected_election,rmi_connection,mesa_voto,"mesa_voto2","", "", depar);

        for (Candidature string : selected_election.getCandidatures_to_election()) mesa_voto.getArray_candidature().add(string.getCandidature_name());
        
        
        System.out.print("Insira o ip(224.0.0.0 a 239.255.255.255) do grupo da mesa de voto que se quer conectar: ");
        mesa_voto.getDesk().setIp(scanner.nextLine());
        
        System.out.print("Insira a porta do grupo da mesa de voto que se quer conectar: ");
        mesa_voto.getDesk().setPort(scanner.nextLine());
        System.out.println("--------Mesa de Voto do Departamento "+mesa_voto.desk.getDeparNome()+"--------");
        ip=Gerar_Numeros.gerar_ip();
        port=Gerar_Numeros.gerar_port(100, 1);
        mesa_voto2.getDesk().setIp(ip);
        mesa_voto2.getDesk().setPort(port);
        mesa_voto2.setMensagens("");
        while(true) {
            try {Thread.sleep(100);} catch (InterruptedException e) { }
            System.out.print("\033[H\033[2J");  
            System.out.flush(); 
            String inputed_cc= input.askVariable(scanner, "Insira o Numero do seu CC: ", 2);
            mesa_voto.getEleitor_cc().add(inputed_cc); 
            try {
                if (rmi_connection.getServer1()!=null && !rmi_connection.getServer1().authorizeUser(inputed_cc, selected_election)) { 
                    input.messageToWait("403: Nao esta autorizado a votar nesta Eleicao!"); 
                    continue; 
                }
            } catch (RemoteException e) {
                try {
                    if (rmi_connection.getServer2()!=null && !rmi_connection.getServer2().authorizeUser(inputed_cc, selected_election)) {
                        input.messageToWait("403: Nao esta autorizado a votar nesta Eleicao!"); 
                        continue; 
                    }
                } catch (RemoteException e1) { }
            }
            input.messageToWait("Sera reencaminhado para um Terminal de Voto...");

            if(cont==0) {
                mesa_voto.thread.start();
                mesa_voto2.thread.start();
                cont++;
            } 
            while(true){
                try {Thread.sleep(2000);} catch (InterruptedException eInterruptedException) { }
                if (mesa_voto.getReady() == true){
                    try {Thread.sleep(1000);} catch (InterruptedException eInterruptedException) { }
                    try {Thread.sleep(2000);} catch (InterruptedException eInterruptedException) { }
                    if (mesa_voto.getDesk().getArray_id().size()==1) ind = 0;
                    else ind = alea.nextInt((mesa_voto.getDesk().getArray_id().size()-1) + 1);
                    mesa_voto.setMessage_envia("type|connected;cc|"+mesa_voto.getEleitor_cc().get(mesa_voto.getEleitor_cc().size()-1)+";id|"+mesa_voto.getDesk().getArray_id().get(ind));
                    mesa_voto.getDesk().getArray_id().remove(mesa_voto.getDesk().getArray_id().get(ind));   
                    mesa_voto.setReady(false);
                    try {Thread.sleep(2000);} catch (InterruptedException eInterruptedException) { }
                    break;
                }else{
                    continue;
                }
            }
            
        }   
    }

    //encontrar terminais de voto
    public void run () {
        String string;
        int aux_ = 0;
        String mensagem="";
        MulticastSocket socket = null;
        String []lista;
        String []sublista;
        mensagem="type|mult;"+mesa_voto2.getDesk().getIp()+";"+mesa_voto2.getDesk().getPort()+";"+mesa_voto.getDesk().getDeparNome(); 
        while(true){
            try {
                if (aux_==0){
                    InetAddress group = InetAddress.getByName(getDesk().getIp());
                    socket = new MulticastSocket(Integer.parseInt(getDesk().getPort()));
                    socket.joinGroup(group);
                    byte[] inBuf = new byte[8*1024];
                    DatagramPacket msgIn = new DatagramPacket(inBuf, inBuf.length);
                    socket.receive(msgIn);
                    string = new String(inBuf, 0,msgIn.getLength());
                    Handler_Message.typeMessage(mensagem,selected_election, string, mesa_voto, rmi_connection, mesa_voto2);
           
                }
                
                while(true){
                    InetAddress group = InetAddress.getByName(getDesk().getIp());
                    try {Thread.sleep(1000);} catch (InterruptedException e){}
                    if(getMessage_envia().compareTo("")!=0){
                        try {Thread.sleep(3000);} catch (InterruptedException e) { }
                        byte[] buf = getMessage_envia().getBytes();
                        DatagramPacket mesgOut = new DatagramPacket(buf, buf.length, group, Integer.parseInt(getDesk().getPort()));
                        socket.send(mesgOut);
                        lista = getMessage_envia().split(";");
                        sublista = lista[0].split("\\|");
                        if(sublista[1].compareTo("mult")==0){
                            mesa_voto.setReady(true);
                            aux_ = 1;
                        }else if(sublista[1].compareTo("connected")==0){
                            aux_ = 0;
                        }
                        setMessage_envia("");
                        break;
                    }
                    
                }
                
            }catch (Exception e) { e.printStackTrace(); }
        }          
      
        
    }
}
        



//grupo multicast que trata das votacoes
class SecMultServer implements Runnable {
    private ArrayList<String> eleitor_cc;
    private MCServerData desk;
    private String mensagens;
    private Election selected_election;
    public Thread thread;
    private static MCServer mesa_voto;
    private static SecMultServer mesa_voto2;
    private static RMIClient rmi_connection;

    
    public SecMultServer(SecMultServer mesa_voto2,ArrayList <String> eleitor_cc,Election selected_election,RMIClient rmi_connection,MCServer mesa_voto,String threadname,String ip, String port,String depar) throws RemoteException {
        this.desk = new MCServerData(ip, port, depar);
        SecMultServer.mesa_voto = mesa_voto;
        thread = new Thread(this,threadname);
        SecMultServer.rmi_connection = rmi_connection;
        this.selected_election = selected_election;
        this.eleitor_cc = eleitor_cc;   
        SecMultServer.mesa_voto2 = mesa_voto2;
    }
    
    public ArrayList<String> getEleitor_cc() { return eleitor_cc; }
    public Election getSelected_election() { return selected_election; }
    public MCServerData getDesk() { return desk; }
    public String getMensagens() { return mensagens; }

   
    public void setMensagens(String mensagens) {
        this.mensagens = mensagens;
    }

    public void run() {
        synchronized(Thread.currentThread()){
            String string;
            String aux;
            String []sublista;
            String id;
            while(true){
                MulticastSocket socket = null;
                try {
                    
                    InetAddress group = InetAddress.getByName(getDesk().getIp());
                    socket = new MulticastSocket(Integer.parseInt(getDesk().getPort()));
                    socket.joinGroup(group);
                    //socket.setTimeToLive(1);
                    //if(getMensagens().compareTo("")!=0){
                    byte[] inBuf = new byte[8*1024];
                    DatagramPacket msgIn = new DatagramPacket(inBuf, inBuf.length);
                    socket.receive(msgIn);
                    string = new String(inBuf, 0,msgIn.getLength());
                    //socket.leaveGroup(group);
                    aux = Handler_Message.typeMessage("", selected_election, string, mesa_voto, rmi_connection, mesa_voto2);
                    sublista = aux.split(";");
                    sublista[0].split("\\|");
                    if(sublista[0].compareTo("sucessed")==0){
                        id = sublista[1];
                        setMensagens("type|login;status|sucessed;id|"+id);
                    }else if(sublista[0].compareTo("unsucessed")==0){
                        id = sublista[1];
                        setMensagens("type|login;status|unsucessed;id|"+id);
                    }else if(sublista[0].compareTo("listacandidaturas")==0){
                        setMensagens("type|"+aux);     
                    }else if(sublista[0].compareTo("resultado")==0){
                        mesa_voto.setVotes_aux(true);
                    }
                          
                        
                    
                    if (mesa_voto.getVotes_aux() == false){
                        while(true){
                            socket = new MulticastSocket();
                            try {Thread.sleep(100);} catch (InterruptedException e){}
                            if (getMensagens().compareTo("")!=0){
                                byte[] buf = getMensagens().getBytes();
                                DatagramPacket mesgOut = new DatagramPacket(buf, buf.length, group, Integer.parseInt(getDesk().getPort()));
                                socket.send(mesgOut);
                                //setMensagens("");
                                
                                break;
                            }
                            socket.close();
                        }
                        mesa_voto.setVotes_aux(false);
                        
                    }
                }catch (Exception e) { e.printStackTrace();
                }finally {
                    socket.close();
                }
            
            }
        }
        
    }
}


        
   


class Handler_Message{
    //trata das mensagens que o server recebe
    public static String typeMessage(String aux ,Election selected_election,String mensagem,MCServer mesa_voto,RMIClient rmi_connection,SecMultServer mesa_voto2){
        String [] sublista;
        String[] lista = mensagem.split(";");
        String voto,cc;
        String message_client = "";
        ArrayList <String> add_mensagens = new ArrayList<>();
        String id;
        sublista= lista[0].split("\\|");

        if (sublista[1].compareTo("envia_id")==0) {
            sublista= lista[1].split("\\|");
            if(!mesa_voto.getDesk().getArray_id().contains(Integer.parseInt(sublista[1]))){
                mesa_voto.getDesk().getArray_id().add(Integer.parseInt(sublista[1]));
                
            }
            mesa_voto.setMessage_envia(aux+";id|"+sublista[1]);           
        } 
        else if(sublista[1].compareTo("login")==0){
            for (int i = 1; i < lista.length-1; i++) {
                sublista = lista[i].split("\\|");
                add_mensagens.add(sublista[1]);
            }
            
            sublista = lista[lista.length -1].split("\\|");
            id = sublista[1];
            try {    
                if (rmi_connection.getServer1().authenticateUser(add_mensagens.get(0), add_mensagens.get(1)) == true){
                    return "sucessed;"+id;
                }
                    
                else {
                    return "unsucessed;"+id;
                }
            } catch (RemoteException e) { } 

            add_mensagens.clear();
        } else if(sublista[1].compareTo("resultado")==0) {
            sublista = lista[1].split("\\|");
            
            if (sublista.length ==1) voto = "";
            else voto = sublista[1];

            sublista = lista[2].split("\\|");
            cc = sublista[1];
            
            selected_election.registVote(voto, cc,mesa_voto.getDesk().getDeparNome());
            boolean sended=false;
            System.out.println("A Enviar Eleicao Atualizada...");
            while (!sended) {
                try { 
                    if (rmi_connection.getServer1()!=null) {
                        new Inputs().messageToWait(rmi_connection.getServer1().setUpdatedElection(selected_election, false)); 
                        sended=true; 
                    } 
                } catch (Exception e1) {
                    try { 
                        if (rmi_connection.getServer2()!=null) {
                            new Inputs().messageToWait(rmi_connection.getServer2().setUpdatedElection(selected_election, false)); 
                            sended=true; 
                        } 
                    } catch (Exception e2) { }
                }
            } return "result";
        } else if(sublista[1].compareTo("ask")==0) {
            sublista = lista[1].split("\\|");
            message_client = "listacandidaturas;";
            for (int i = 0; i < mesa_voto.getArray_candidature().size(); i++) message_client+=mesa_voto.getArray_candidature().get(i)+";";
            
            message_client+="id|"+sublista[1];
            return message_client;
        
        }else if(sublista[1].compareTo("wait")==0){
            return "wait";
        }
        return "";
    }

    //trata das mensagens que o cliente recebe
    public static String typeMessage_Client(String mensagem, int id,MCClient cliente){
        String message="";
        String aux[];
        String[] lista = mensagem.split(";");
        String [] sublista= lista[lista.length-1].split("\\|");
        String [] sublista1= lista[0].split("\\|");
        //  verificar se e o terminal que queremos
        if (id == Integer.parseInt(sublista[1])) {
            sublista = lista[0].split("\\|");
            if (sublista[1].compareTo("login")==0){
                //  verificar status
                sublista = lista[1].split("\\|");
                if (sublista[1].compareTo("sucessed")==0) return "sucessed";    
                else return "unsucessed";
            }
            else if(sublista[1].compareTo("connected")==0){
                aux = lista[2].split("\\|");
                return "choose|"+aux[1];
            } else if(sublista[1].compareTo("listacandidaturas")==0){
                message=mensagem;
                return message;
           
            }else if(sublista1[1].compareTo("mult")==0){
                cliente.getVote_terminal().setIp(lista[1]);
                cliente.getVote_terminal().setPort(lista[2]);
                cliente.getVote_terminal().setDepar(lista[3]);
            
                return "mult";
            }else if (sublista[1].compareTo("wait")==0) return "wait";
            else return "";
        }else{
            if(sublista[1].compareTo("connected")==0){
                return "sair";
            }
            else return "";
        }
    }
}


class Gerar_Numeros {

    public static String gerar_ip(){
        int max = 239,min = 224;
        String ip="";
        for (int i = 0; i < 4; i++) {
            Random alea = new Random();
            int end = alea.nextInt((max - min) + 1) + min;
            if (i!=3) ip+=Integer.toString(end) +".";
            else ip+=Integer.toString(end);
            max = 255;
            min = 0;
        } return ip;
    }

    public static String gerar_port(int max, int min) { return Integer.toString(new Random().nextInt((max - min) + 1) + min); }
}
