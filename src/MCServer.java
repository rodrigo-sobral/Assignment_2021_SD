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

    private MCServerData desk;
    private String mensagens;
    private ArrayList<String> array_candidature;

    //threads
    private static MCServer mesa_voto;
    private static SecMultServer mesa_voto2;
    private static RMIClient rmi_connection;
    public Thread thread;
    
    public MCServer(SecMultServer mesa_voto2,String threadname,String ip, String port,String depar) throws RemoteException {
        this.mensagens = "";
        this.desk = new MCServerData(ip,port,depar);
        MCServer.mesa_voto2 = mesa_voto2;
        thread = new Thread(this,threadname);
        this.array_candidature = new ArrayList<>();
    }

    public ArrayList<String> getArray_candidature() { return array_candidature; }
    public Thread getThread() { return thread; }
    public MCServerData getDesk() { return desk; }
    public String getMensagens() { return mensagens; }
    public void setMensagens(String mensagens) { this.mensagens = mensagens; }


    public void printar_array_id_conectados(){
        System.out.println("Printar Array ID Conectado");
        for (int i = 0; i < mesa_voto.getDesk().getArray_id().size(); i++) {
            System.out.println(mesa_voto.getDesk().getArray_id().get(i)+" ");
        }
    }
    public static void main(String[] args) throws RemoteException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String cart, depar="";
        Inputs input = new Inputs();
        String messag = "";
        int cont = 0, ind;
        Random alea = new Random();
        
        rmi_connection = new RMIClient();
        while (!rmi_connection.connect2Servers(scanner));

        ArrayList<Election> available_elections=null;
        int election_option=0;
        while (available_elections==null) {
            while (true) {
                depar = input.askDepartment(rmi_connection, scanner, new ArrayList<>(), true);
                if (!depar.isEmpty()) break;
            }
            try { available_elections= rmi_connection.getServer1().getElectionToVoteTable(rmi_connection, depar); }
            catch (Exception e1) {
                try { available_elections= rmi_connection.getServer2().getElectionToVoteTable(rmi_connection, depar); }
                catch (Exception e2) { }
            }
            if (available_elections==null) { System.out.println("400: Nao existe uma Eleicao para esse Departamento!"); continue; }
            
            //  ASK ELECTION TO THE CANDIDATURE 
            System.out.println("----------------------------------------");
            System.out.println("Eleicoes Disponiveis [0 para Voltar]");
            System.out.println("----------------------------------------");
            for (int i = 0; i < available_elections.size(); i++) 
                System.out.println(i+1+": "+available_elections.get(i).getTitle()+"\t"+available_elections.get(i).getStartingDateString()+"\t"+available_elections.get(i).getEndingDateString()+"\t"+available_elections.get(i).getDescription());
            System.out.println("----------------------------------------");
            election_option= input.checkIntegerOption(scanner, "Opcao: ", 0, available_elections.size())-1;
            
            if (election_option==-1) { available_elections=null; continue; }
        }

        //  ASK TO CHANGE A VARIABLE
        Election selected_election= available_elections.get(election_option);

        mesa_voto = new MCServer(mesa_voto2,"mesa_voto",Gerar_Numeros.gerar_ip(),Gerar_Numeros.gerar_port(1000,10),depar);
        mesa_voto2 = new SecMultServer(rmi_connection,mesa_voto,"mesa_voto2","", "", depar);

        //selected_election.registVote(voter_choice, voter_cc);
        for (Candidature string : selected_election.getCandidatures_to_election()) {
            mesa_voto.getArray_candidature().add(string.getCandidature_name());
        }
        
        ReadWrite.Write("MCServerData.txt", mesa_voto.desk.getDeparNome(), mesa_voto.desk.getIp(),mesa_voto.desk.getPort());
        System.out.println("--------Mesa de Voto do Departamento "+mesa_voto.desk.getDeparNome()+"--------");
        while(true) {
            try {Thread.sleep(1000);} catch (InterruptedException e) { }
            cart  = input.askVariable(scanner, "Insira o Numero do seu CC: ", 2);
            try {
                if (!rmi_connection.getServer1().authorizeUser(cart)) { System.out.println("404: Inseriu um Numero de CC invalido!"); continue; }
            } catch (RemoteException e) {
                try {
                    if (!rmi_connection.getServer2().authorizeUser(cart)) { System.out.println("404: Inseriu um Numero de CC invalido!"); continue; }
                } catch (RemoteException e1) { }
            }
            mesa_voto.thread.start();
            mesa_voto2.thread.start();
            
            synchronized (mesa_voto.thread) {
                try {
                    System.out.println("WAIT");
                    mesa_voto.thread.wait();
                    System.out.println("SAIU DO WAIT");
                    System.out.println("---A adquirir um terminal para o eleitor---");
                    System.out.println("...Esperar que mais terminais de voto se conectem...");
                    try {Thread.sleep(10000);} catch (InterruptedException e) { }
                    if(mesa_voto.getDesk().getArray_id().size() !=0){
                        if (mesa_voto.getDesk().getArray_id().size()==1) ind = 0;
                        else ind = alea.nextInt((mesa_voto.getDesk().getArray_id().size()) + 1);
                        for (int i = 0; i < mesa_voto.getDesk().getArray_id().size(); i++) 
                            System.out.println("Elemento "+i+" "+mesa_voto.getDesk().getArray_id().get(i));
                        System.out.println("Terminal de voto com id"+mesa_voto.getDesk().getArray_id().get(ind)+"selecionado");
                        mesa_voto.setMensagens("type|connected;cc|"+cart+";id|"+mesa_voto.getDesk().getArray_id().get(ind));
                        mesa_voto.getDesk().getArray_id().remove(mesa_voto.getDesk().getArray_id().get(ind));   
                        mesa_voto.printar_array_id_conectados();
                    }

                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
        
    public void run () {
        String[] conection;
        String[] aux;
        Scanner keyboardScanner= new Scanner(System.in);
        InetAddress group;
        DatagramPacket packet;
        System.out.println("entrei server->client");
        while(true){
            MulticastSocket socket = null;
            try {
                socket = new MulticastSocket();  // create socket without binding it (only for sending)                
                while (true) {
                    try {Thread.sleep(1000);} catch (InterruptedException e){}
                    if (mesa_voto.getMensagens().compareTo("")!=0){
                        System.out.println("mesa voto envia para o client: "+mesa_voto.getMensagens());
                        byte[] buffer = mesa_voto.getMensagens().getBytes();
                        group = InetAddress.getByName(getDesk().getIp());
                        packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getDesk().getPort()));
                        socket.send(packet);
                        conection = mesa_voto.getMensagens().split(";");
                        //aux = conection[0].split("\\|");
                        if(conection[1].compareTo("received")==0){
                            synchronized (mesa_voto.thread) {
                                System.out.println("Parte de notify");
                                mesa_voto.thread.notify();
                                System.out.println("notificou");
                            }
                            
                        }
                        mesa_voto.setMensagens("");
                    }
                }
            } catch (IOException e) { e.printStackTrace(); } 
            finally { socket.close(); keyboardScanner.close(); }
        }  
    }  
}

//recebe as mensagens do cliente
class SecMultServer implements Runnable {

    private MCServerData desk;
    private String mensagens;

    public Thread thread;
    private static MCServer mesa_voto;
    private static RMIClient rmi_connection;


    public SecMultServer(RMIClient rmi_connection,MCServer mesa_voto,String threadname,String ip, String port,String depar) throws RemoteException {
        this.desk = new MCServerData(ip, port, depar);
        SecMultServer.mesa_voto = mesa_voto;
        thread = new Thread(this,threadname);
        SecMultServer.rmi_connection = rmi_connection;
    }
    
    public MCServerData getDesk() { return desk; }
    public String getMensagens() { return mensagens; }

   
    public void run() {
        
        File f= new File("TerminalVote.txt");
        System.out.println("entrei client->server");
        while(true){
            if (f.exists() && f.isFile()){
                ReadWrite.read_ip_port_terminal("TerminalVote.txt", getDesk());
                if (getDesk().getIp().compareTo("")!=0){
                    MulticastSocket socket = null;
                    try {
                        socket = new MulticastSocket(Integer.parseInt(getDesk().getPort()));  // create socket and bind it
                        InetAddress group = InetAddress.getByName(getDesk().getIp());
                        System.out.println(Integer.parseInt(getDesk().getPort())+getDesk().getIp());
                        socket.joinGroup(group);
                        while (true) {
                            byte[] buffer = new byte[256];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);                            
                            socket.receive(packet);
                            System.out.println("(Server)Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                            String message = new String(packet.getData(), 0, packet.getLength());
                            System.out.println(message);
                            Handler_Message.typeMessage(message, mesa_voto,rmi_connection);
                            

                        }
                    } catch (IOException e) { e.printStackTrace();
                    } finally { socket.close(); }
                }
            }
        }
       
    }    
}

class Handler_Message{
    private static boolean authenticateUser;


    //trata das mensagens que o server recebe
    public static Boolean typeMessage(String mensagem,MCServer mesa_voto,RMIClient rmi_connection){
        String [] sublista;
        String [] aux;
        String[] lista = mensagem.split(";");
        Random alea = new Random();
        String message_client = "";
        ArrayList <String> add_mensagens = new ArrayList<>();
        int ind;
        String id;
        sublista= lista[0].split("\\|");
        if(sublista[1].compareTo("envia_id")==0){
            sublista= lista[1].split("\\|");
            mesa_voto.getDesk().getArray_id().add(Integer.parseInt(sublista[1]));
            mesa_voto.setMensagens("type|envia_id;received;id|"+sublista[1]);
            mesa_voto.printar_array_id_conectados();
        
            /*(mesa_voto.getDesk().getArray_id().size()==1){
                ind = 0;
            }
            else{
                ind = alea.nextInt((mesa_voto.getDesk().getArray_id().size()) + 1);
            }
            for (int i = 0; i < mesa_voto.getDesk().getArray_id().size(); i++) {
                System.out.println("Elemento "+i+" "+mesa_voto.getDesk().getArray_id().get(i));
            }
            //formar mensagem para enviar ao client
            System.out.println(mesa_voto.getDesk().getArray_id().get(ind));
            mesa_voto.setMensagens("type|connected;id|"+mesa_voto.getDesk().getArray_id().get(ind));
            System.out.println("Mensagem a enviar para o cliente: "+mesa_voto.getMensagens());
            mesa_voto.getDesk().getArray_id().remove(mesa_voto.getDesk().getArray_id().get(ind));
        */
        }
        else if(sublista[1].compareTo("ask")==0){
            sublista = lista[1].split("\\|");
            message_client = "type|listacandidaturas";
            for (int i = 0; i < mesa_voto.getArray_candidature().size(); i++) {
                message_client+=mesa_voto.getArray_candidature().get(i);
                message_client+=";";
            }
            message_client+="id|";
            message_client+=sublista[1];
            mesa_voto.setMensagens(message_client);
        }
        else if(sublista[1].compareTo("login")==0){
            for (int i = 1; i < lista.length-2; i++) {
                sublista = lista[i].split("\\|");
                add_mensagens.add(sublista[1]);
            }
            sublista = lista[lista.length -1].split("\\|");
            id = sublista[1];
            try {
                if (rmi_connection.getServer1().authenticateUser(add_mensagens.get(0), add_mensagens.get(1)) == true){
                    mesa_voto.setMensagens("type|login;status|sucessed;id|"+id);
                }else{
                    mesa_voto.setMensagens("type|login;status|unsucessed;id|"+id);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            

            add_mensagens.clear();
            return true;
        }

        return false;
    }


    //trata das mensagens que o cliente recebe
    public static String typeMessage_Client(String mensagem, int id){
        System.out.println("chegou esta mesangem"+mensagem);
        String [] sublista;
        String message="";
        String[] lista = mensagem.split(";");
        sublista= lista[lista.length-1].split("\\|");
        //verificar se e o terminal que queremos
        if (id == Integer.parseInt(sublista[1])){
            sublista = lista[0].split("\\|");
            if (sublista[1].compareTo("login")==0){
                //verificar status
                sublista = lista[1].split("\\|");
                if (sublista[1].compareTo("sucessed")==0) return "sucessed";    
                else return "unsucessed";
            }
            else if(lista[1].compareTo("received")==0) return "connected_server";
            else if(sublista[1].compareTo("connected")==0){
                sublista = lista[1].split("\\|");
                message = "choose|"+sublista[1];
                return message;
            }
            else if(sublista[1].compareTo("listaeleicoes")==0){
                for (int i = 1; i < lista.length-1; i++) {
                    message+=lista[i]+";";
                    if (i==lista.length-1) message+=lista[i];
                } return message;
            } else return "";
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
        }
        return ip;
    }

    public static String gerar_port(int max, int min) { return Integer.toString(new Random().nextInt((max - min) + 1) + min); }
}

/*
class Ask_Info_Eleitor implements Runnable{
    public Thread thread;
    private Scanner scanner;
    private Inputs input;
    private static MCServer mesa_voto;

    public Ask_Info_Eleitor(MCServer mesa_voto,String threadname,Scanner scanner,Inputs input){
        thread = new Thread(this,threadname);
        this.scanner = scanner;
        this.input = input;
        Ask_Info_Eleitor.mesa_voto=mesa_voto;
    }
    public static MCServer getMesa_voto() { return mesa_voto; }

    public void run(){
        String cart;
        System.out.println("entrou aqui\n");
        
    }

}*/