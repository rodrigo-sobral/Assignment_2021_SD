
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;


public class MCClient implements Runnable{

    private MCServerData vote_terminal;
    private String message;
    private boolean Connected;
    private boolean login_sucessed;
    public Thread thread;
    private Inputs inpu;
    private Scanner scanner;
    boolean exit=false;

    
    private String cc;

    //threads
    private static SecMultGClient cliente2;
    private static MCClient cliente;
    private static Eleitor_Connected thread_eleitor;
    //thread para contar o tempo em que o terminal esta inativo


    //construtor
    public MCClient(String threadname,Eleitor_Connected thread_eleitor,SecMultGClient cliente2,Inputs inpu, Scanner scanner){
        this.vote_terminal = new MCServerData();
        this.message = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
        MCClient.cliente2 = cliente2;
        MCClient.thread_eleitor = thread_eleitor;
        this.login_sucessed=false;
        this.cc = "";
        this.inpu = inpu;
        this.scanner = scanner;
    }

    //getter
    public String getMessage() { return message; }
    public MCServerData getVote_terminal() { return vote_terminal; }
    public Boolean getConnected() { return Connected; }    
    public Boolean getLogin_sucessed() { return login_sucessed; }
    public String getCc() { return cc; }
    public Inputs getInpu() { return inpu; }
    public Scanner getScanner() { return scanner; }
    
    //setter
    public void setMessage(String message) { this.message = message; }
    public void setConnected(Boolean connected) { Connected = connected; }
    public void setLogin_sucessed(Boolean login_sucessed) { this.login_sucessed = login_sucessed; }
    public void setCc(String cc) { this.cc = cc; }
    
    public static void main(String[] args) {
        String depar="";
        Inputs input = new Inputs();
        Scanner scanner = new Scanner(System.in);

        cliente = new MCClient("cliente",thread_eleitor,cliente2,input,scanner);
        cliente2 = new SecMultGClient("cliente2");
        thread_eleitor = new Eleitor_Connected("thread_eleitor", cliente2, cliente,input,scanner);
        while(true){
            depar = input.askVariable(scanner,"Insira o deparmento a que o terminal de voto pertence: " , 0);
            cliente2.getMCServerData().setDepar(depar);
            ReadWrite.read_ip_port("MCServerData.txt", cliente2.getMCServerData().getDeparNome(), cliente);
            if (cliente.vote_terminal.getDeparNome().compareTo("")==0){
                System.out.println("Nao existe nenhuma mesa aberta nesse departamento");
            }
            else break;
        }
        //SERVER->CLIENT
        
        //gerar numero aleatorio para atribuir um id ao terminal
        //para o cliente2 (envia as mensagens do server) ter o id associado
        cliente2.getMCServerData().setN_terminal_vote(Integer.parseInt(Gerar_Numeros.gerar_port(100, 1)));
        //para o client1 (recebe as mensagens do server) ter o id associado
        cliente.getVote_terminal().setN_terminal_vote(cliente2.getMCServerData().getN_terminal_vote());
        System.out.println("ID TERMINAL: "+cliente.getVote_terminal().getN_terminal_vote());
        //enviar a mensagem para o servidor com o id (quer dizer que esta livre)
        cliente2.setMessage("type|envia_id;id|"+cliente2.getMCServerData().getN_terminal_vote());
        cliente2.thread.start();
        cliente.thread.start();
        synchronized(cliente2.thread){
            try{
                System.out.println("wait na main");
                cliente2.thread.wait();
                System.out.println("Saiu da main");
                cliente.stop();
                cliente2.stop();
            }catch (Exception e) { e.printStackTrace(); }
        }
        thread_eleitor.stop();
        System.exit(0);
    }

    //recebe mensagens do server
    public void run() {
        MulticastSocket socket = null;
        String messag_lida;
        String []aux;

        while(true){
            try {Thread.sleep(1000);} catch (InterruptedException e){}
            ReadWrite.read_ip_port("MCServerData.txt", cliente2.getMCServerData().getDeparNome(), cliente);
            
            if(cliente.vote_terminal.getDeparNome().compareTo("")!=0){
                try {
                    socket = new MulticastSocket(Integer.parseInt(getVote_terminal().getPort()));  // create socket and bind it
                    InetAddress group = InetAddress.getByName(getVote_terminal().getIp());
                    socket.joinGroup(group);
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    
                    while (!exit) {
                        try {Thread.sleep(1000);} catch (InterruptedException e){}

                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        cliente.setMessage(message);
                        messag_lida = Handler_Message.typeMessage_Client(message, getVote_terminal().getN_terminal_vote());
                        aux = messag_lida.split("\\|");

                        if (aux[0].compareTo("choose")==0){
                            //setConnected(true);
                            cliente.setCc(aux[1]);
                            //Eleitor_Connected thread_connected = new Eleitor_Connected(secgm_terminal_voto, terminal_voto);
                            thread_eleitor.thread.start();
                        } else {
                            if(messag_lida.compareTo("sucessed")==0){
                                cliente2.setMessage("");
                                cliente.setLogin_sucessed(true);
                                synchronized (thread_eleitor.thread) {
                                    System.out.println("Parte de notify");
                                    thread_eleitor.thread.notify();
                                    System.out.println("notificou");
                                }
                            }
                            else if(messag_lida.compareTo("unsucessed")==0){
                                cliente2.setMessage("");
                                cliente.setLogin_sucessed(false);
                                synchronized (thread_eleitor.thread) {
                                    System.out.println("Parte de notify");
                                    thread_eleitor.thread.notify();
                                    System.out.println("notificou");
                                }
                            } else if(messag_lida.compareTo("false")==0){
                                //do something
                                continue;
                            }
                            //feito em baixo
                            else if(messag_lida.compareTo("connected_server")==0)cliente2.setMessage("");    
                            else if(messag_lida.compareTo("")==0) System.out.println("Ignorar mensagem");
                            else {
                                cliente2.setMessage("");
                                Eleitor_Connected.ListaCandidaturas(cliente2, message, cliente.getInpu(), cliente.getScanner());
                            }
                        }
                    }
                } 
                catch(SocketTimeoutException se) { System.out.println("Aviso: Excedeu o tempo limite, o Terminal de Voto ira encerrar!"); }
                catch (IOException e) { e.printStackTrace(); } 
                finally { socket.close(); }
            }
            
        }
        
    }
    public void stop() { exit = true; }
        
}


//envia mensagens para o server
class SecMultGClient implements Runnable{
    //atributos
    private String message;
    private Boolean Connected;
    private MCServerData MCServerData;
    boolean exit=false;

    //threads
    public Thread thread;
    
    public SecMultGClient(String threadname) {
        this.MCServerData = new MCServerData();
        this.message = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
    }
    
    public MCServerData getMCServerData() { return MCServerData; }
    public String getMessage() { return message; }
    public Boolean getConnected() { return Connected; }

    public void setMessage(String message) { this.message = message; }

    public void run() {
        String ip_port= ReadWrite.check_client_connect("TerminalVote.txt",getMCServerData().getDeparNome());
        String [] val;  
        String []aux;
        System.out.println(getMCServerData().getDeparNome());
        if (ip_port.compareTo("")!=0){
            val = ip_port.split(" ");
            getMCServerData().setIp(val[0]);
            getMCServerData().setPort(val[1]);
        } else {
            //gerar numero aleatorio para o id do terminal de voto
            getMCServerData().setIp(Gerar_Numeros.gerar_ip());
            getMCServerData().setPort(Gerar_Numeros.gerar_port(1000,100));
            ReadWrite.Write("TerminalVote.txt", getMCServerData().getDeparNome(), getMCServerData().getIp(), getMCServerData().getPort(),true);
        }
        while (!exit){
            try {Thread.sleep(3000);} catch (InterruptedException e){}
            MulticastSocket socket = null;
            if (getMessage().compareTo("")!=0){
                try {
                    socket = new MulticastSocket();  // create socket without binding it (only for sending)
                    byte[] buffer = getMessage().getBytes();
                    InetAddress group = InetAddress.getByName(getMCServerData().getIp());
                    System.out.println("mensagem a enviar: "+getMessage());
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getMCServerData().getPort()));
                    socket.send(packet);
                    val = getMessage().split(";");
                    aux = val[0].split("\\|");

                    if (aux[1].compareTo("login")==0) getMessage().compareTo("");
                    else if (aux[1].compareTo("resultado")==0){
                        setMessage("");
                        synchronized (Thread.currentThread()) { Thread.currentThread().notify(); } 
                    }
                } catch (IOException e) { e.printStackTrace(); } 
                finally { socket.close(); }
            }
        }
    }
    public void stop() { exit = true; }
}

class Eleitor_Connected implements Runnable {
    private static SecMultGClient cliente2;
    private static MCClient cliente;
    public Thread thread;
    private Inputs input;
    private Scanner scanner;
    boolean exit=false;

    public Eleitor_Connected(String threadname,SecMultGClient cliente2,MCClient cliente,Inputs input,Scanner scanner){
        thread = new Thread(this,threadname);
        Eleitor_Connected.cliente2 = cliente2;
        Eleitor_Connected.cliente = cliente;
        this.input=input;
        this.scanner = scanner;
    }
    
    public Inputs getInput() { return input; }
    public Scanner getScanner() { return scanner; }
    
    public void run(){
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
        synchronized (Thread.currentThread()) {
            while(!exit){
                String username,password;
                username = getInput().askVariable(scanner,"Username: " , 0);
                password = input.askVariable(scanner,"Password: " , 1);
                cliente2.setMessage("type|login;username|"+username+";password|"+password+";id|"+cliente.getVote_terminal().getN_terminal_vote());
                try {
                    Thread.currentThread().wait(); 
                    if (cliente.getLogin_sucessed() == true) break;
                    else continue;
                } catch (InterruptedException e) { e.printStackTrace(); }
            }   
        } 
        cliente2.setMessage("type|ask;id|"+cliente.getVote_terminal().getN_terminal_vote());
    }
    
    public static void ListaCandidaturas(SecMultGClient cliente2,String message,Inputs input,Scanner scanner){
        String [] lista_candidaturas = message.split(";");
        String opcao;
        System.out.println("|=================================================|");
        System.out.println("|             Candidaturas disponiveis            |");
        System.out.println("|=================================================|");
        for (int i = 1; i < lista_candidaturas.length-1; i++) System.out.println((i)+ ": Candidatura " + lista_candidaturas[i]);
        System.out.println("Insira a opcao que pretende votar: ");
        opcao = scanner.nextLine();
        cliente2.setMessage("type|resultado;OpcaoVoto|"+opcao+";cc|"+cliente.getCc()+";id|" + cliente.getVote_terminal().getN_terminal_vote());
        ReadWrite.remove_line("TerminalVote.txt",cliente.getVote_terminal().getDeparNome());      
    }

    public void stop() { exit = true; }
}
