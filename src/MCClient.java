
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;


public class MCClient implements Runnable{

    private VoteTerminal vote_terminal;
    private String messag;
    private Boolean Connected;
    public Thread thread;

    //threads
    private static SecMultGClient cliente2;
    private static MCClient cliente;
    private static Eleitor_Connected thread_eleitor;
    //thread para contar o tempo em que o terminal esta inativo


    //construtor
    public MCClient(String threadname,Eleitor_Connected thread_eleitor,SecMultGClient cliente2){
        this.vote_terminal = new VoteTerminal();
        this.messag = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
        MCClient.cliente2 = cliente2;
        MCClient.thread_eleitor = thread_eleitor;
    }

    //getter
    public String getMessage() { return messag; }
    public VoteTerminal getVote_terminal() { return vote_terminal; }
    public Boolean getConnected() { return Connected; }    
    
    //setter
    public void setMessage(String messag) { this.messag = messag; }
    public void setConnected(Boolean connected) { Connected = connected; }

    public static void main(String[] args) {
        String depar;
        Inputs input = new Inputs();
        Scanner scanner = new Scanner(System.in);
        cliente = new MCClient("cliente",thread_eleitor,cliente2);
        cliente2 = new SecMultGClient("cliente2",cliente,thread_eleitor);
        thread_eleitor = new Eleitor_Connected("thread_eleitor", cliente2, cliente,input,scanner);
        depar = input.askVariable(scanner,"Insert the department to which the desk vote belongs: " , 0);
        cliente2.getVoteTerminal().setNome_depar(depar);

        //SERVER->CLIENT
        ReadWrite.read_ip_port("MCServerData.txt", depar, cliente);
        if (cliente.vote_terminal.getNome_depar().compareTo("")==0){
            System.out.println("There is no desk vote open in that department");
        }
        //gerar numero aleatorio para atribuir um id ao terminal
        //para o cliente2 (envia as mensagens do server) ter o id associado
        cliente2.getVoteTerminal().setId_terminal(Integer.parseInt(Gerar_Numeros.gerar_port(100, 1)));
        //para o client1 (recebe as mensagens do server) ter o id associado
        cliente.getVote_terminal().setId_terminal(cliente2.getVoteTerminal().getId_terminal());
        System.out.println("ID DO TERMINAL: "+cliente.getVote_terminal().getId_terminal()+" "+cliente2.getVoteTerminal().getId_terminal());
        //enviar a mensagem para o servidor com o id (quer dizer que esta livre)
        cliente2.setMessage("type|envia_id;id|"+cliente2.getVoteTerminal().getId_terminal());
        //terminal_voto.start();
        cliente2.thread.start();
        cliente.thread.start();
    }



    //recebe mensagens do server
    public void run() {
        MulticastSocket socket = null;
        String messag_lida;
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
                System.out.println(message);
                cliente.setMessage(messag);
                messag_lida = Handler_Message.typeMessage_Client(message, getVote_terminal().getId_terminal());
                System.out.println("Mensagem lida: "+messag_lida);
                if (messag_lida.compareTo("choose")==0){
                    System.out.println("Terminal Escolhido");
                    setConnected(true);
                    //Eleitor_Connected thread_connected = new Eleitor_Connected(secgm_terminal_voto, terminal_voto);
                    thread_eleitor.thread.start();
                }
                else if(messag_lida.compareTo("sucessed")==0){
                    //do something
                }
                else if(messag_lida.compareTo("unsucessed")==0){
                    //do something
                }
                else if(messag_lida.isEmpty()){
                    //do something
                    continue;
                }
                //lista eleicoes
                else{
                    cliente.setMessage(messag_lida);
                    Thread.currentThread().notify();

                }
            }
        } catch (IOException e) { e.printStackTrace(); } 
        finally { socket.close(); }
    }
}


//envia mensagens para o server
class SecMultGClient implements Runnable{
    //atributos
    private String messag;
    private Boolean Connected;
    private VoteTerminal voteTerminal;

    //threads
    private static Eleitor_Connected thread_eleitor;
    private static MCClient cliente;

    public Thread thread;
  
    public SecMultGClient(String threadname,MCClient cliente,Eleitor_Connected thread_eleitor) {
        SecMultGClient.cliente = cliente;
        SecMultGClient.thread_eleitor = thread_eleitor;
        this.voteTerminal = new VoteTerminal();
        this.messag = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
    }
    
    public VoteTerminal getVoteTerminal() { return voteTerminal; }
    public String getMessage() { return messag; }
    public Boolean getConnected() { return Connected; }

    public void setMessage(String messag) { this.messag = messag; }

    public void run(){
        String ip_port= ReadWrite.check_client_connect("TerminalVote.txt",getVoteTerminal().getNome_depar());
        String [] val;  
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
            try {Thread.sleep(1000);} catch (InterruptedException e){}
            MulticastSocket socket = null;
            if (getMessage().compareTo("")!=0){
                try {
                    socket = new MulticastSocket();  // create socket without binding it (only for sending)
                    byte[] buffer = getMessage().getBytes();
                    InetAddress group = InetAddress.getByName(getVoteTerminal().getIp());
                    System.out.println("mensagem a enviar: "+getMessage());
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getVoteTerminal().getPort()));
                    socket.send(packet);
                    setMessage("");
                    
                } catch (IOException e) { e.printStackTrace(); } 
                finally { socket.close(); }
            }
        }
    }
}

class Eleitor_Connected implements Runnable {
    private static SecMultGClient cliente2;
    private static MCClient cliente;
    public Thread thread;
    private Inputs input;
    private Scanner scanner;

    public Eleitor_Connected(String threadname,SecMultGClient cliente2,MCClient cliente,Inputs input,Scanner scanner){
        thread = new Thread(this,threadname);
        Eleitor_Connected.cliente2 = cliente2;
        Eleitor_Connected.cliente = cliente;
        this.input=input;
        this.scanner = scanner;
    }
    
    public Inputs getInpu() { return input; }
    public Scanner getScanner() { return scanner; }
    
    public void run(){
        synchronized (Thread.currentThread()) {
            while(true){
                String username,password;
                username = getInpu().askVariable(scanner,"Username: " , 0);
                password = input.askVariable(scanner,"Password: " , 1);
                cliente2.setMessage("type|login;username|"+username+";password|"+password+";id|"+cliente.getVote_terminal().getId_terminal());
                try {
                    System.out.println("entrou");
                    Thread.currentThread().wait(); 
                    System.out.println("saiu");
                    break;
                } catch (InterruptedException e) { e.printStackTrace(); }
            }   
        } 
        ListaEleicoes(cliente.getMessage(), input, scanner);
    }
   
    public static void ListaEleicoes(String message,Inputs input,Scanner scanner){
        message+="Voto Nulo; Voto Branco";
        String [] lista_eleicoes = message.split(";");
        int opcao;
        System.out.println("|==========================================|");
        System.out.println("|             Listas para votar            |");
        System.out.println("|==========================================|");
        for (int i = 0; i < lista_eleicoes.length; i++) System.out.println((i+1)+ ": Lista " + lista_eleicoes[i]);

        opcao = input.checkIntegerOption(scanner,"Insira a opção da lista que pretende votar", 1, lista_eleicoes.length );
        cliente2.setMessage("type|resultado;OpcaoVoto|" + Integer.toString(opcao) + ";id|" + cliente.getVote_terminal().getId_terminal());
    }
}

