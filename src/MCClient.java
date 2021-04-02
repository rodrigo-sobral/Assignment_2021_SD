
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;


public class MCClient implements Runnable{

    private VoteTerminal vote_terminal;
    private String message;
    private Boolean Connected;
    private Boolean login_sucessed;
    public Thread thread;
    boolean exit=false;
    
    private String cc;

    //threads
    private static SecMultGClient cliente2;
    private static MCClient cliente;
    private static Eleitor_Connected thread_eleitor;
    //thread para contar o tempo em que o terminal esta inativo


    //construtor
    public MCClient(String threadname,Eleitor_Connected thread_eleitor,SecMultGClient cliente2){
        this.vote_terminal = new VoteTerminal();
        this.message = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
        MCClient.cliente2 = cliente2;
        MCClient.thread_eleitor = thread_eleitor;
        this.login_sucessed=false;
        this.cc = "";
    }

    //getter
    public String getMessage() { return message; }
    public VoteTerminal getVote_terminal() { return vote_terminal; }
    public Boolean getConnected() { return Connected; }    
    public Boolean getLogin_sucessed() { return login_sucessed; }
    public String getCc() { return cc; }
    
    
    //setter
    public void setMessage(String message) { this.message = message; }
    public void setConnected(Boolean connected) { Connected = connected; }
    public void setLogin_sucessed(Boolean login_sucessed) { this.login_sucessed = login_sucessed; }
    public void setCc(String cc) { this.cc = cc; }
    
    public static void main(String[] args) {
        String depar="";
        Inputs input = new Inputs();
        Scanner scanner = new Scanner(System.in);

        cliente = new MCClient("cliente",thread_eleitor,cliente2);
        cliente2 = new SecMultGClient("cliente2",cliente,thread_eleitor);
        thread_eleitor = new Eleitor_Connected("thread_eleitor", cliente2, cliente,input,scanner);
        while(true){
            depar = input.askVariable(scanner,"Insira o deparmento a que o terminal de voto pertence: " , 0);
            cliente2.getVoteTerminal().setNome_depar(depar);
            ReadWrite.read_ip_port("MCServerData.txt", cliente2.getVoteTerminal().getNome_depar(), cliente);
            if (cliente.vote_terminal.getNome_depar().compareTo("")==0){
                System.out.println("Nao existe nenhuma mesa aberta nesse departamento");
            }
            else break;
        }
        //SERVER->CLIENT
        
        //gerar numero aleatorio para atribuir um id ao terminal
        //para o cliente2 (envia as mensagens do server) ter o id associado
        cliente2.getVoteTerminal().setId_terminal(Integer.parseInt(Gerar_Numeros.gerar_port(100, 1)));
        //para o client1 (recebe as mensagens do server) ter o id associado
        cliente.getVote_terminal().setId_terminal(cliente2.getVoteTerminal().getId_terminal());
        System.out.println("ID TERMINAL: "+cliente.getVote_terminal().getId_terminal());
        //enviar a mensagem para o servidor com o id (quer dizer que esta livre)
        cliente2.setMessage("type|envia_id;id|"+cliente2.getVoteTerminal().getId_terminal());
        cliente2.thread.start();
        cliente.thread.start();
        synchronized(cliente2.thread){
            try{
                System.out.println("wait na main");
                cliente2.thread.wait();
                System.out.println("Saiu da main");
            }catch (Exception e) { e.printStackTrace(); }
        }
    }



    //recebe mensagens do server
    public void run() {
        MulticastSocket socket = null;
        String messag_lida;

        while(true){
            try {Thread.sleep(1000);} catch (InterruptedException e){}
            ReadWrite.read_ip_port("MCServerData.txt", cliente2.getVoteTerminal().getNome_depar(), cliente);
            if(cliente.vote_terminal.getNome_depar().compareTo("")!=0){
                try {
                
                    System.out.println("server->cliente");
                    socket = new MulticastSocket(Integer.parseInt(getVote_terminal().getPort()));  // create socket and bind it
                    InetAddress group = InetAddress.getByName(getVote_terminal().getIp());
                    socket.joinGroup(group);
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    while (true) {
                        try {Thread.sleep(1000);} catch (InterruptedException e){}
                        socket.receive(packet);
                        System.out.print("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("e a mensagem"+message);
                        cliente.setMessage(message);
                        
                        messag_lida = Handler_Message.typeMessage_Client(message, getVote_terminal().getId_terminal());
                        System.out.println("Mensagem lida: "+messag_lida);
                        if (messag_lida.compareTo("choose")==0){
                            System.out.println("Terminal Escolhido");
                            setConnected(true);
                            //Eleitor_Connected thread_connected = new Eleitor_Connected(secgm_terminal_voto, terminal_voto);
                            thread_eleitor.thread.start();
                        }
                        else if(messag_lida.compareTo("sucessed")==0){
                            cliente.setLogin_sucessed(true);
                            synchronized (cliente.thread) {
                                System.out.println("Parte de notify");
                                cliente.thread.notify();
                                System.out.println("notificou");
                            }
                        }
                        else if(messag_lida.compareTo("unsucessed")==0){
                            cliente.setLogin_sucessed(false);
                            synchronized (cliente.thread) {
                                System.out.println("Parte de notify");
                                cliente.thread.notify();
                                System.out.println("notificou");
                            }
                        }
                        else if(messag_lida.compareTo("false")==0){
                            //do something
                            continue;
                        }
                        else if(messag_lida.compareTo("connected_server")==0){
                            //feito em baixo
                            
                        }
                        //lista eleicoes
                        else{
                            System.out.println("AHHHHH");
                            cliente.setMessage(messag_lida);
                            Thread.currentThread().notify();
    
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
    private VoteTerminal voteTerminal;
    boolean exit=false;

    //threads
    private static Eleitor_Connected thread_eleitor;
    private static MCClient cliente;
    public Thread thread;
  

    
    public SecMultGClient(String threadname,MCClient cliente,Eleitor_Connected thread_eleitor) {
        SecMultGClient.cliente = cliente;
        SecMultGClient.thread_eleitor = thread_eleitor;
        this.voteTerminal = new VoteTerminal();
        this.message = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
    }
    
    public VoteTerminal getVoteTerminal() { return voteTerminal; }
    public String getMessage() { return message; }
    public Boolean getConnected() { return Connected; }

    public void setMessage(String message) { this.message = message; }

    public void run() {
        String ip_port= ReadWrite.check_client_connect("TerminalVote.txt",getVoteTerminal().getNome_depar());
        String [] val;  
        String []aux;
        System.out.println("cliente->server");
        System.out.println(getVoteTerminal().getNome_depar());
        if (ip_port.compareTo("")!=0){
            val = ip_port.split(" ");
            getVoteTerminal().setIp(val[0]);
            getVoteTerminal().setPort(val[1]);
        } else {
            getVoteTerminal().setIp(Gerar_Numeros.gerar_ip());
            getVoteTerminal().setPort(Gerar_Numeros.gerar_port(1000,100));
            ReadWrite.Write("TerminalVote.txt", getVoteTerminal().getNome_depar(), getVoteTerminal().getIp(), getVoteTerminal().getPort());
            //gerar numero aleatorio para o id do terminal de voto
        }
        while (true){
            try {Thread.sleep(2000);} catch (InterruptedException e){}
            MulticastSocket socket = null;
            if (getMessage().compareTo("")!=0){
                try {
                    socket = new MulticastSocket();  // create socket without binding it (only for sending)
                    byte[] buffer = getMessage().getBytes();
                    InetAddress group = InetAddress.getByName(getVoteTerminal().getIp());
                    System.out.println("mensagem a enviar: "+getMessage());
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getVoteTerminal().getPort()));
                    socket.send(packet);
                    val = getMessage().split(";");
                    aux = val[0].split("\\|");
                    if (aux[1].compareTo("login")==0){
                        getMessage().compareTo("");
                    }else if (aux[1].compareTo("resultado")==0){
                        //terminar sockets
                        System.out.println("....a fechar o terminal....");
                        synchronized (Thread.currentThread()) {
                            System.out.println("Parte de notify");
                            Thread.currentThread().notify();;
                            System.out.println("notificou");
                        }

                    }
                    /*if (aux[1].compareTo("envia_id")==0){
                        //fazer a conecao
                        System.out.println("__entrou aqui__");
                        if (cliente.getMessage().compareTo("")!=0){
                            aux = cliente.getMessage().split(";");
                            if (aux[1].compareTo("received")==0){
                                cliente.setConnected(true);

                                setMessage("");
                            }
                        }
                        
                    }*/
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
        synchronized (Thread.currentThread()) {
            while(true){
                String username,password;
                username = getInput().askVariable(scanner,"Username: " , 0);
                password = input.askVariable(scanner,"Password: " , 1);
                cliente2.setMessage("type|login;username|"+username+";password|"+password+";id|"+cliente.getVote_terminal().getId_terminal());
                try {
                    System.out.println("WAIT USER CHECK");
                    Thread.currentThread().wait(); 
                    System.out.println("SAIU USER CHECK");
                    if (cliente.getLogin_sucessed() == true) break;
                    else continue;
                    
                } catch (InterruptedException e) { e.printStackTrace(); }
            }   
        } 
        System.out.println("saiu do while (user e pass corret)");
        ListaCandidaturas(cliente2,cliente.getMessage(), input, scanner);
    }
    
    public static void ListaCandidaturas(SecMultGClient cliente2,String message,Inputs input,Scanner scanner){
        String [] lista_candidaturas = message.split(";");
        String opcao;
        System.out.println("|==========================================|");
        System.out.println("|             Listas para votar            |");
        System.out.println("|==========================================|");
        for (int i = 0; i < lista_candidaturas.length; i++) System.out.println((i+1)+ ": Lista " + lista_candidaturas[i]);
        System.out.println("Insira a opcao que pretende votar: ");
        opcao = scanner.nextLine();
        if (opcao.compareTo("")==0){
            //voto branco
            cliente2.setMessage("type|resultado;OpcaoVoto|Branco;cc|"+cliente.getCc()+";id|" + cliente.getVote_terminal().getId_terminal());
        }else if (input.checkStringInteger(opcao) ==true){
            if (Integer.parseInt(opcao)>=0 && Integer.parseInt(opcao)<lista_candidaturas.length){
                cliente2.setMessage("type|resultado;opcaovoto|" + lista_candidaturas[Integer.parseInt(opcao)] +";cc|"+cliente.getCc()+ ";id|" + cliente.getVote_terminal().getId_terminal());
            }
        }else{
            //voto nulo
            cliente2.setMessage("type|resultado;OpcaoVoto|Nulo;cc|"+cliente.getCc()+";id|" + cliente.getVote_terminal().getId_terminal());
        }       
       
    }
    public void stop() { exit = true; }
}
