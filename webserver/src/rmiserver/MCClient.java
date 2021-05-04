import org.javaguy.coolframework.MyClass;
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
    private String aux_received;
    
    private String cc;

    //threads
    private static SecMultGClient cliente2;
    private static Cliente_Received cliente2_received;
    private static MCClient cliente;
    private static Eleitor_Connected thread_eleitor;
    
    //thread para contar o tempo em que o terminal esta inativo

    //inicialmente fala com uma thread, depois passa para a outra
    //construtor
    public MCClient(String threadname,Eleitor_Connected thread_eleitor,SecMultGClient cliente2,Inputs inpu, Scanner scanner){
        this.vote_terminal = new MCServerData("","","");
        this.message = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
        MCClient.cliente2 = cliente2;
        MCClient.thread_eleitor = thread_eleitor;
        this.login_sucessed=false;
        this.cc = "";
        this.inpu = inpu;
        this.scanner = scanner;
        this.aux_received = "00";
    }

    //getter
    public String getMessage() { return message; }
    public MCServerData getVote_terminal() { return vote_terminal; }
    public Boolean getConnected() { return Connected; }    
    public Boolean getLogin_sucessed() { return login_sucessed; }
    public String getCc() { return cc; }
    public Inputs getInpu() { return inpu; }
    public Scanner getScanner() { return scanner; }
    public String getAux_received() {
        return aux_received;
    }
    //setter
    public void setMessage(String message) { this.message = message; }
    public void setConnected(Boolean connected) { Connected = connected; }
    public void setLogin_sucessed(Boolean login_sucessed) { this.login_sucessed = login_sucessed; }
    public void setCc(String cc) { this.cc = cc; }
    public void setAux_received(String aux_received) {
        this.aux_received = aux_received;
    }
    public static void main(String[] args) {
        Inputs input = new Inputs();
        Scanner scanner = new Scanner(System.in);

        cliente = new MCClient("cliente",thread_eleitor,cliente2,input,scanner);
        thread_eleitor = new Eleitor_Connected("thread_eleitor",cliente2,cliente,input,scanner);

        cliente2 = new SecMultGClient(cliente,"cliente2",cliente2_received);
        cliente2_received = new Cliente_Received(cliente,"cliente2_received",cliente2,thread_eleitor);
        System.out.print("Insira o ip(224.0.0.0 a 239.255.255.255) do grupo da mesa de voto que se quer conectar: ");
        cliente2.getMCServerData().setIp(scanner.nextLine());
        System.out.print("Insira a porta do grupo da mesa de voto que se quer conectar: ");
        cliente2.getMCServerData().setPort(scanner.nextLine());
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
        cliente2_received.thread.start();
        cliente.thread.start();
        synchronized(cliente.thread){
            try{
                cliente.thread.wait();
                cliente2.stop();
              
            }catch (Exception e) { e.printStackTrace(); }
        }
        cliente.stop();
        thread_eleitor.stop();
        System.exit(0);
    }


    public void run() {
        
        String messag_lida;
        String []aux;
        String[] string1;
        String string;
        
        while(true){
            MulticastSocket socket = null;
            try {
                while(true){
                    InetAddress group = InetAddress.getByName(getVote_terminal().getIp());
                    socket = new MulticastSocket();
                    try {Thread.sleep(1000);} catch (InterruptedException e){}
                    if(getMessage().compareTo("")!=0){
                        aux = getMessage().split(";");
                        string1 = aux[0].split("\\|");
                        byte[] buf = getMessage().getBytes();
                        DatagramPacket mesgOut = new DatagramPacket(buf, buf.length, group, Integer.parseInt(getVote_terminal().getPort()));
                        
                        socket.send(mesgOut);
                        aux = getMessage().split(";");
                        string1 = aux[0].split("\\|");
                        if (string1[1].compareTo("resultado")==0){
            
                            synchronized (Thread.currentThread()) { Thread.currentThread().notify(); } 
                        }
                        break;
                        
                        
                        
                    }
                    socket.close();
                }
            
        
                if (getVote_terminal().getIp().compareTo("")!=0){
                    
                    InetAddress group = InetAddress.getByName(getVote_terminal().getIp());
                    socket = new MulticastSocket(Integer.parseInt(getVote_terminal().getPort()));
                    socket.joinGroup(group);
                    byte[] inBuf = new byte[8*1024];
                    DatagramPacket msgIn = new DatagramPacket(inBuf, inBuf.length);
                    socket.receive(msgIn);
                    string = new String(inBuf, 0,msgIn.getLength());

                    messag_lida = Handler_Message.typeMessage_Client(string, cliente.vote_terminal.getN_terminal_vote(), cliente);
                    aux = messag_lida.split("\\|");
                    if(messag_lida.compareTo("sucessed")==0){
                        cliente.setMessage("");
                        cliente.setLogin_sucessed(true);
                        synchronized (thread_eleitor.thread) {
                            thread_eleitor.thread.notify();
                        }
                    }
                    else if(messag_lida.compareTo("unsucessed")==0){
                        cliente.setMessage("");
                        cliente.setLogin_sucessed(false);
                        synchronized (thread_eleitor.thread) {
                            thread_eleitor.thread.notify();
                        }
                    }else if(messag_lida.compareTo("")==0){

                    }else if(messag_lida.compareTo("sair")==0){
                        
                    }
                    else{
                        Eleitor_Connected.ListaCandidaturas(cliente,string,inpu,scanner);

                    }
                    
                }
            }catch (Exception e) { e.printStackTrace();
                }finally {
                    socket.close();
                }   
            
            }
        }
    
    public void stop() { exit = true; }
        


}
class Cliente_Received implements Runnable{
    private String message;
    boolean exit=false;

    public Thread thread;
    private static MCClient cliente;
    private static SecMultGClient cliente2;
    private static Eleitor_Connected thread_eleitor;
    
    public Cliente_Received(MCClient cliente,String threadname,SecMultGClient cliente2,Eleitor_Connected thread_eleitor) {
        this.message = "";
        thread = new Thread(this,threadname);
        Cliente_Received.cliente = cliente;
        Cliente_Received.cliente2 = cliente2;
        Cliente_Received.thread_eleitor = thread_eleitor;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


    public void run(){
        MulticastSocket socket = null;
        String messag_lida;
        String []aux;

        while(!exit){
            try {Thread.sleep(1000);} catch (InterruptedException e){}
            try {
                socket = new MulticastSocket(Integer.parseInt(cliente2.getMCServerData().getPort()));  // create socket and bind it
                InetAddress group = InetAddress.getByName(cliente2.getMCServerData().getIp());
                socket.joinGroup(group);
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(60*1000);
                while (!exit) {
                    if (cliente.getConnected()==true) stop();
                    try {Thread.sleep(1000);} catch (InterruptedException e){}
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    
                    messag_lida = Handler_Message.typeMessage_Client(message, cliente.getVote_terminal().getN_terminal_vote(),cliente);
                    aux = messag_lida.split("\\|");

                    if (aux[0].compareTo("choose")==0){
                        cliente.setCc(aux[1]);
                        thread_eleitor.thread.start();
                        cliente2.stop();
                        cliente.setConnected(true);
                        //multicast votos
                    } else {
                        if(messag_lida.compareTo("mult")==0){
                            System.out.println("......Terminal de Voto do Departamento "+cliente.getVote_terminal().getDeparNome()+"......");
                            cliente.setAux_received("11");
                        }

                        }
                    }
                } 
                catch(SocketTimeoutException se) { System.out.println("Aviso: Excedeu o tempo limite, o Terminal de Voto ira encerrar!"); System.exit(0); return; }
                catch (IOException e) { e.printStackTrace(); } 
                finally { socket.close(); }
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
    private static MCClient cliente;
    
    public SecMultGClient(MCClient cliente,String threadname,Cliente_Received cliente2_received) {
        this.MCServerData = new MCServerData();
        this.message = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
        SecMultGClient.cliente = cliente;
    }
    
    public MCServerData getMCServerData() { return MCServerData; }
    public String getMessage() { return message; }
    public Boolean getConnected() { return Connected; }

    public void setMessage(String message) { this.message = message; }

    public void run() {
        String [] val;  
        String []aux;
        String []aux1;
        while (!exit){
            try {Thread.sleep(2000);} catch (InterruptedException e){}
            MulticastSocket socket = null;
            
            if (getMessage().compareTo("")!=0){
                aux = getMessage().split(";");
                val = aux[0].split("\\|");
                aux1 = aux[aux.length-1].split("\\|");
                if (aux1[1].compareTo(Integer.toString(cliente.getVote_terminal().getN_terminal_vote()))==0){
                    if(val[1].compareTo("envia_id")==0 && cliente.getAux_received().compareTo("11")!=0 || val[1].compareTo("mult")==0 && cliente.getAux_received().compareTo("11")!=0||val[1].compareTo("ok")==0){
                        try {
                            socket = new MulticastSocket();  // create socket without binding it (only for sending)
                            byte[] buffer = getMessage().getBytes();
                            InetAddress group = InetAddress.getByName(getMCServerData().getIp());
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getMCServerData().getPort()));
                            socket.send(packet);
                        } catch (IOException e) { e.printStackTrace(); } 
                        finally { socket.close(); }
                    }
                }
                
            }
        }
    }
    public void stop() { exit = true; }
}

class Eleitor_Connected implements Runnable {
    private static MCClient cliente;
    public Thread thread;
    private Inputs input;
    private Scanner scanner;
    boolean exit=false;

    public Eleitor_Connected(String threadname,SecMultGClient cliente2,MCClient cliente,Inputs input,Scanner scanner){
        thread = new Thread(this,threadname);
        Eleitor_Connected.cliente = cliente;
        this.input=input;
        this.scanner = scanner;
    }
    

    public Inputs getInput() { return input; }
    public Scanner getScanner() { return scanner; }
    
    public void run(){
        while(!exit){
            String username,password;
            username = getInput().askVariable(scanner,"Username: " , 0);
            password = input.askVariable(scanner,"Password: " , 1);
            cliente.setMessage("type|login;username|"+username+";password|"+password+";id|"+cliente.getVote_terminal().getN_terminal_vote());
            synchronized(Thread.currentThread()){
                try {
                    Thread.currentThread().wait(); 
                    if (cliente.getLogin_sucessed() == true) break;
                    else continue;
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
            
        }  
        cliente.setMessage("type|ask;id|"+cliente.getVote_terminal().getN_terminal_vote());
        } 
        
        
        
    
    public static void ListaCandidaturas(MCClient cliente,String message,Inputs input,Scanner scanner){
        String [] lista_candidaturas = message.split(";");
        String opcao;
        System.out.println("|=================================================|");
        System.out.println("|             Candidaturas disponiveis            |");
        System.out.println("|=================================================|");
        for (int i = 1; i < lista_candidaturas.length-1; i++) System.out.println((i)+ ": Candidatura " + lista_candidaturas[i]);
        System.out.print("Insira a opcao que pretende votar: ");
        opcao = scanner.nextLine();
        cliente.setMessage("type|resultado;OpcaoVoto|"+opcao+";cc|"+cliente.getCc()+";id|" + cliente.getVote_terminal().getN_terminal_vote());
    }

    public void stop() { exit = true; }
}