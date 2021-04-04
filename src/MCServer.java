import java.util.ArrayList;

import classes.Candidature;
import classes.Election;

import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;
import java.io.*;
import java.lang.Thread;


public class MCServer extends UnicastRemoteObject implements Runnable {
    private static final long serialVersionUID = 1L;
    private int n_max_terminais;
    private MCServerData desk;
    private String message_envia;
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
        this.n_max_terminais = n_max_terminais;
        this.selected_election = selected_election;
        this.ready = false;
    }

    public ArrayList<String> getEleitor_cc() { return eleitor_cc;}
    public ArrayList<String> getArray_candidature() { return array_candidature; }
    public Thread getThread() { return thread; }
    public MCServerData getDesk() { return desk; }


    public void printar_array_id_conectados(){
        System.out.println("Printar Array ID Conectado");
        for (int i = 0; i < mesa_voto.getDesk().getArray_id().size(); i++) {
            System.out.println(mesa_voto.getDesk().getArray_id().get(i)+" ");
        }
    }
    public static void main(String[] args) throws RemoteException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String depar="";
        Inputs input = new Inputs();
        int cont = 0, ind;
        Random alea = new Random();
        String cart;
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
            try { available_elections= rmi_connection.getServer1().getElectionToVoteTable(depar); }
            catch (Exception e1) {
                try { available_elections= rmi_connection.getServer2().getElectionToVoteTable(depar); }
                catch (Exception e2) { }
            }
            if (available_elections==null) { System.out.println("400: Nao existe uma Eleicao para esse Departamento!"); continue; }
            
            //  ASK ELECTION TO THE CANDIDATURE 
            System.out.println("----------------------------------------");
            System.out.println("Eleicoes Disponiveis [0 para Voltar]");
            System.out.println("----------------------------------------");
            for (int i = 0; i < available_elections.size(); i++)  {
                Election temp_election= available_elections.get(i);
                System.out.println(i+1+": "+temp_election.getElection_type()+"\t"+temp_election.getTitle()+"\t"+temp_election.getStartingDateString()+"\t"+temp_election.getEndingDateString()+"\t"+temp_election.getDescription());
            }
            System.out.println("----------------------------------------");
            election_option= input.checkIntegerOption(scanner, "Opcao: ", 0, available_elections.size())-1;
            
            if (election_option==-1) { available_elections=null; continue; }
        }

        rmi_connection.subscribe2Servers(rmi_connection, depar);
        Election selected_election= available_elections.get(election_option);

        mesa_voto = new MCServer(selected_election,n_max_terminais,mesa_voto2,"mesa_voto",Gerar_Numeros.gerar_ip(),Gerar_Numeros.gerar_port(1000,10),depar);
        mesa_voto2 = new SecMultServer(mesa_voto2,mesa_voto.getEleitor_cc(),selected_election,rmi_connection,mesa_voto,"mesa_voto2","", "", depar);

        for (Candidature string : selected_election.getCandidatures_to_election()) mesa_voto.getArray_candidature().add(string.getCandidature_name());
        
        
        System.out.println("Insira o ip(224.0.0.0 a 239.255.255.255.) do grupo da mesa de voto que se quer conectar: ");
        mesa_voto.getDesk().setIp(scanner.nextLine());
        
        System.out.println("Insira a porta do grupo da mesa de voto que se quer conectar: ");
        mesa_voto.getDesk().setPort(scanner.nextLine());
        System.out.println("--------Mesa de Voto do Departamento "+mesa_voto.desk.getDeparNome()+"--------");
        ip=Gerar_Numeros.gerar_ip();
        port=Gerar_Numeros.gerar_port(100, 1);
        mesa_voto2.getDesk().setIp(ip);
        mesa_voto2.getDesk().setPort(port);
        mesa_voto2.setMensagens("");
        while(true) {
            System.out.println("entrou no while");
            try {Thread.sleep(1000);} catch (InterruptedException e) { }
            //System.out.print("\033[H\033[2J");  
            //System.out.flush(); 
            mesa_voto.getEleitor_cc().add(input.askVariable(scanner, "-------Insira o Numero do seu CC: ---------", 2));
            //System.out.println(cart);
            //mesa_voto.getEleitor_cc().add(input.askVariable(scanner, "Insira o Numero do seu CC: ", 2)); 
            /*try {
                if (!rmi_connection.getServer1().authorizeUser(mesa_voto.getEleitor_cc().get(mesa_voto.getEleitor_cc().size()-1))) { System.out.println("404: Inseriu um Numero de CC invalido!"); continue; }
            } catch (RemoteException e) {
                try {
                    if (!rmi_connection.getServer2().authorizeUser(mesa_voto.getEleitor_cc().get(mesa_voto.getEleitor_cc().size()-1))) { System.out.println("404: Inseriu um Numero de CC invalido!"); continue; }
                } catch (RemoteException e1) { }
            }*/
            if (cont ==0){    
                mesa_voto.thread.start();
                mesa_voto2.thread.start();
                cont++;
            } 
            System.out.println("aqui");
            while(true){
                System.out.println("entrou aqui");
                try {Thread.sleep(2000);} catch (InterruptedException eInterruptedException) { }
                if (mesa_voto.getReady() == true){
                    System.out.println("ENTROU PARA ESCOLHER");
                    mesa_voto.printar_array_id_conectados();
                    try {Thread.sleep(2000);} catch (InterruptedException eInterruptedException) { }
                    mesa_voto.printar_array_id_conectados();
                    if (mesa_voto.getDesk().getArray_id().size()==1) ind = 0;
                    else ind = alea.nextInt((mesa_voto.getDesk().getArray_id().size()-1) + 1);
                    mesa_voto.setMessage_envia("type|connected;cc|"+mesa_voto.getEleitor_cc().get(mesa_voto.getEleitor_cc().size()-1)+";id|"+mesa_voto.getDesk().getArray_id().get(ind));
                    mesa_voto.getDesk().getArray_id().remove(mesa_voto.getDesk().getArray_id().get(ind));   
                    mesa_voto.printar_array_id_conectados();
                    try {Thread.sleep(2000);} catch (InterruptedException eInterruptedException) { }
                    break;
                }else{
                    continue;
                }/*
                try {
                    
                    if (mesa_voto.getDesk().getArray_id().size() ==0){
                        System.out.println("WAIT");
                        mesa_voto.thread.wait();
                        System.out.println("SAIU WAIT");
                        mesa_voto.printar_array_id_conectados();
                        if (mesa_voto.getDesk().getArray_id().size()==1) ind = 0;
                        else ind = alea.nextInt((mesa_voto.getDesk().getArray_id().size()-1) + 1);
                        mesa_voto.setMessage_envia("type|connected;cc|"+mesa_voto.getEleitor_cc().get(mesa_voto.getEleitor_cc().size()-1)+";id|"+mesa_voto.getDesk().getArray_id().get(ind));
                        mesa_voto.getDesk().getArray_id().remove(mesa_voto.getDesk().getArray_id().get(ind));   
                        mesa_voto.printar_array_id_conectados();
                    }
                    else{
                        mesa_voto.printar_array_id_conectados();
                        if (mesa_voto.getDesk().getArray_id().size()==1) ind = 0;
                        else ind = alea.nextInt((mesa_voto.getDesk().getArray_id().size()-1) + 1);
                        mesa_voto.setMessage_envia("type|connected;cc|"+mesa_voto.getEleitor_cc().get(mesa_voto.getEleitor_cc().size()-1)+";id|"+mesa_voto.getDesk().getArray_id().get(ind));
                        mesa_voto.getDesk().getArray_id().remove(mesa_voto.getDesk().getArray_id().get(ind));   
                        mesa_voto.printar_array_id_conectados();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }*/
            }
            System.out.println("saiu do while");
            
        }   
    }

    //encontrar terminais de voto
    public void run () {
        //synchronized(Thread.currentThread()){
            System.out.println("thread->multicast find");
            String string;
            String mensagem="";
            MulticastSocket socket = null;
            String []lista;
            String []sublista;
            System.out.println("IP:"+mesa_voto2.getDesk().getIp());
            System.out.println("PORT:"+mesa_voto2.getDesk().getPort());
            mensagem="type|mult;"+mesa_voto2.getDesk().getIp()+";"+mesa_voto2.getDesk().getPort()+";"+mesa_voto.getDesk().getDeparNome(); 
            while(true){
                try {
                    InetAddress group = InetAddress.getByName(getDesk().getIp());
                    socket = new MulticastSocket(Integer.parseInt(getDesk().getPort()));
                    socket.joinGroup(group);
                    byte[] inBuf = new byte[8*1024];
                    DatagramPacket msgIn = new DatagramPacket(inBuf, inBuf.length);
                    socket.receive(msgIn);
                    string = new String(inBuf, 0,msgIn.getLength());
                    System.out.println("Mult1 TERMINAIS Received:" + string);
                    System.out.println(mensagem);
                    Handler_Message.typeMessage(mensagem,selected_election, string, mesa_voto, rmi_connection, mesa_voto2);
                    while(true){
                        try {Thread.sleep(3000);} catch (InterruptedException e){}
                        System.out.println("MENSAGEM"+getMessage_envia());
                        if(getMessage_envia().compareTo("")!=0){
                            try {Thread.sleep(3000);} catch (InterruptedException e) { }
                            byte[] buf = getMessage_envia().getBytes();
                            System.out.println("Mult1 TERMINAIS send:" + getMessage_envia());
                            DatagramPacket mesgOut = new DatagramPacket(buf, buf.length, group, Integer.parseInt(getDesk().getPort()));
                            socket.send(mesgOut);
                            lista = getMessage_envia().split(";");
                            sublista = lista[0].split("\\|");
                            

                            setMessage_envia("");
                            break;
                        }
                        
                    }
                    
                    //setMessage_envia("");
                }catch (Exception e) { e.printStackTrace(); }
            }          
        //}
        
    }
}
        

        /*
    public void run () {
        synchronized(Thread.currentThread()){
            String[] conection;
            Scanner keyboardScanner= new Scanner(System.in);
            InetAddress group;
            DatagramPacket packet;
            while(true){
                MulticastSocket socket = null;
                try {
                    socket = new MulticastSocket();  // create socket without binding it (only for sending)                
                    while (true) {
                        try {Thread.sleep(1000);} catch (InterruptedException e) { }
                        if (mesa_voto.getMensagens().compareTo("")!=0) {
                            byte[] buffer = mesa_voto.getMensagens().getBytes();
                            group = InetAddress.getByName(getDesk().getIp());
                            packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getDesk().getPort()));
                            socket.send(packet);
                            conection = mesa_voto.getMensagens().split(";");
                            if (conection[1].compareTo("received")==0) {
                                if (mesa_voto.getDesk().getArray_id().size()==mesa_voto.n_max_terminais)
                                    synchronized (mesa_voto.thread) { mesa_voto.thread.notify(); }
                            }
                            mesa_voto.setMensagens("");
                        }
                    }
                }catch (IOException e) { e.printStackTrace(); } 
                finally { socket.close(); keyboardScanner.close(); }
            }  
        }
    }*/  


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
        //synchronized(Thread.currentThread()){
            System.out.println("entrei client->server");
            String string;
            String aux;
            String []sublista;
            String []aux1;
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
                        DatagramPacket msgIn =
                        new DatagramPacket(inBuf, inBuf.length);
                        socket.receive(msgIn);
                        string = new String(inBuf, 0,msgIn.getLength());
                        System.out.println("VOTES Received:" + string);
                        //socket.leaveGroup(group);
                        aux = Handler_Message.typeMessage("", selected_election, string, mesa_voto, rmi_connection, mesa_voto2);
                        sublista = aux.split(";");
                        if(sublista[0].compareTo("sucessed")==0){
                            id = sublista[1];
                            setMensagens("type|login;status|sucessed;id|"+id);
                        }else if(sublista[0].compareTo("unsucessed")==0){
                            id = sublista[1];
                            setMensagens("type|login;status|unsucessed;id|"+id);
                        }else if(sublista[0].compareTo("listacandidaturas")==0){
                            setMensagens("type|"+aux);
                            
                        }
                        while(true){
                            try {Thread.sleep(100);} catch (InterruptedException e){}
                            if (getMensagens().compareTo("")!=0){
                                System.out.println("VOTES Mensagem a enviar:"+getMensagens());
                                byte[] buf = getMensagens().getBytes();
                                DatagramPacket mesgOut = new DatagramPacket(buf, buf.length, group, Integer.parseInt(getDesk().getPort()));
                                socket.send(mesgOut);
                                //setMensagens("");
                                break;
                            }
                            
                        }
                        
                    ////
                        
                    //} 
                    }catch (Exception e) { e.printStackTrace();
                    }finally {
                        socket.close();
                    }
                    setMensagens("");    
            
            }
        //}
        
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
            mesa_voto.getDesk().getArray_id().add(Integer.parseInt(sublista[1]));
            System.out.println(aux);
            mesa_voto.setMessage_envia(aux);
            mesa_voto.printar_array_id_conectados();
        } 
        else if(sublista[1].compareTo("wait")==0){
            mesa_voto.setReady(true);
        }

        ///////////////////////////verrrrrrrrrrrrrrr
        /*else if(sublista[1].compareTo("ask")==0) {
            sublista = lista[1].split("\\|");
            message_client = "type|listacandidaturas;";
            for (int i = 0; i < mesa_voto.getArray_candidature().size(); i++) message_client+=mesa_voto.getArray_candidature().get(i)+";";
            
            message_client+="id|"+sublista[1];
            mesa_voto.setMensagens(message_client);
        }*/
        else if(sublista[1].compareTo("login")==0){
            System.out.println("AQUI");
            for (int i = 1; i < lista.length-1; i++) {
                sublista = lista[i].split("\\|");
                add_mensagens.add(sublista[1]);
            }
            
            sublista = lista[lista.length -1].split("\\|");
            id = sublista[1];
            try {    
                if (rmi_connection.getServer1().authenticateUser(add_mensagens.get(0), add_mensagens.get(1)) == true){
                    //mesa_voto2.setMensagens("type|login;status|sucessed;id|"+id);
                    return "sucessed;"+id;
                }
                    
                else {
                    return "unsucessed;"+id;
                }//mesa_voto2.setMensagens("type|login;status|unsucessed;id|"+id);
            } catch (RemoteException e) { } 

            add_mensagens.clear();
           // return true;
        } else if(sublista[1].compareTo("resultado")==0) {
            sublista = lista[1].split("\\|");
            
            if (sublista.length ==1) voto = "";
            else voto = sublista[1];

            sublista = lista[2].split("\\|");
            cc = sublista[1];
            selected_election.registVote(voto, cc,mesa_voto.getDesk().getDeparNome());
           
        }else if(sublista[1].compareTo("ask")==0) {
            sublista = lista[1].split("\\|");
            message_client = "listacandidaturas;";
            for (int i = 0; i < mesa_voto.getArray_candidature().size(); i++) message_client+=mesa_voto.getArray_candidature().get(i)+";";
            
            message_client+="id|"+sublista[1];
            return message_client;
            //mesa_voto.setMessage_envia(message_client);
        
        }/*else if(sublista[1].compareTo("wait")==0){
            System.out.println("ENTROU AUQI");
            synchronized (mesa_voto.thread) { mesa_voto.thread.notify(); }
        }*/
        return "";
    }

    //trata das mensagens que o cliente recebe
    public static String typeMessage_Client(String mensagem, int id,MCClient cliente){
        System.out.println("chegou esta mensagem"+mensagem);
        String message="";
        String[] lista = mensagem.split(";");
        String [] sublista= lista[lista.length-1].split("\\|");
        String [] sublista1= lista[0].split("\\|");
        if (sublista1[1].compareTo("mult")==0){
            cliente.getVote_terminal().setIp(lista[1]);
            cliente.getVote_terminal().setPort(lista[2]);
            cliente.getVote_terminal().setDepar(lista[3]);
            return "mult";
        }
        //  verificar se e o terminal que queremos
        if (id == Integer.parseInt(sublista[1])) {
            sublista = lista[0].split("\\|");
            if (sublista[1].compareTo("login")==0){
                //  verificar status
                sublista = lista[1].split("\\|");
                if (sublista[1].compareTo("sucessed")==0) return "sucessed";    
                else return "unsucessed";
            }
            else if(lista[1].compareTo("received")==0) return "connected_server";
            else if(sublista[1].compareTo("connected")==0){
                sublista = lista[1].split("\\|");
                message = "choose|"+sublista[1];
                return message;
            } else if(sublista[1].compareTo("listacandidaturas")==0){
                message=mensagem;
                /*for (int i = 1; i < lista.length-1; i++) {
                    if (i==lista.length-1) message+=lista[i];
                    message+=lista[i]+";";
                }*/
                System.out.println("PRINT "+message);
                return message;
           
            }else return "";
        } else return "";
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