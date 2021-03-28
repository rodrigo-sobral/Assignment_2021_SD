
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;


public class MCClient implements Runnable{

    private VoteTerminal vote_terminal;
    private String messag;
    private Boolean Connected;
    public Thread thread;

    //threads
    private static SecMultGClient cliente2;
    private static MCClient cliente;
    //private static Eleitor_Connected thread_eleitor;


    //construtor
    public MCClient(String threadname){
        this.vote_terminal = new VoteTerminal();
        this.messag = "";
        this.Connected = false;
        thread = new Thread(this,threadname);
    }

    //getter
    public String getMessag() { return messag; }
    public VoteTerminal getVote_terminal() { return vote_terminal; }
    public Boolean getConnected() { return Connected; }    
    
    //setter
    public void setMessag(String messag) { this.messag = messag; }
    public void setConnected(Boolean connected) { Connected = connected; }

    public static void main(String[] args) {
        cliente = new MCClient("cliente");
        cliente2 = new SecMultGClient("cliente2");
        Inputs inpu = new Inputs();
        
        Scanner scanner = new Scanner(System.in);
        String depar;
        depar = inpu.askVariable(scanner,"Insert the department to which the desk vote belongs: " , 0);
        cliente2.getMcclient().getVote_terminal().setNome_depar(depar);

        //SERVER->CLIENT
        ReadWrite.read_ip_port("VoteDesk.txt", depar, cliente);
        if (cliente.vote_terminal.getNome_depar().compareTo("")==0){
            System.out.println("There is no desk vote open in that department");
        }
        cliente2.getMcclient().getVote_terminal().setId_terminal(Integer.parseInt(Gerar_Numeros.gerar_port(100, 1)));
        cliente.getVote_terminal().setId_terminal(cliente2.getMcclient().getVote_terminal().getId_terminal());
        cliente2.getMcclient().setMessag("type|envia_id;id|"+cliente2.getMcclient().getVote_terminal().getId_terminal());
        
        scanner.close();
        //terminal_voto.start();
        cliente2.start();
        //MulticastUser user = new MulticastUser();
        //user.start();
    }


    //recebe mensagens do server
    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(Integer.parseInt(getVote_terminal().getPort()));  // create socket and bind it
            InetAddress group = InetAddress.getByName(getVote_terminal().getIp());
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.print("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                if (Handler_Message.typeMessage_Client(message, getVote_terminal().getId_terminal()).compareTo("Choose")==0){
                    System.out.println("É este o terminal de voto escolhido");
                    setConnected(true);
                    //Eleitor_Connected thread_connected = new Eleitor_Connected(secgm_terminal_voto, terminal_voto);
                    //thread_connected.start();
                }
                else if(Handler_Message.typeMessage_Client(message, getVote_terminal().getId_terminal()).compareTo("LoginSucess")==0){
                    cliente.notify();
                }
            }
        } catch (IOException e) { e.printStackTrace(); } 
        finally { socket.close(); }
    }
}

class SecMultGClient extends Thread{
    
    //atributos
    private MCClient mcclient;

  
    public SecMultGClient(String threadname) {
        this.mcclient = new MCClient(threadname);
   
    }
    
    public MCClient getMcclient() { return mcclient; }

    public void run(){
        getMcclient().getVote_terminal().setIp(Gerar_Numeros.gerar_ip());
        getMcclient().getVote_terminal().setPort(Gerar_Numeros.gerar_port(1000,100));
        ReadWrite.Write("TerminalVote.txt", getMcclient().getVote_terminal().getNome_depar(), getMcclient().getVote_terminal().getIp(), getMcclient().getVote_terminal().getPort());
        //gerar numero aleatorio para o id do terminal de voto
        while (true){
            MulticastSocket socket = null;
            if (getMcclient().getMessag().compareTo("")!=0){
                try {
                    socket = new MulticastSocket();  // create socket without binding it (only for sending)
                    byte[] buffer = getMcclient().getMessag().getBytes();
                    InetAddress group = InetAddress.getByName(getMcclient().getVote_terminal().getIp());
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(getMcclient().getVote_terminal().getPort()));
                    socket.send(packet);
                    getMcclient().setMessag("");
                    
                    try { sleep((long) (Math.random() * 1000)); } catch (InterruptedException e) { }        
                } catch (IOException e) { e.printStackTrace(); } 
                finally { socket.close(); }
            }
        }
    }
}

class Eleitor_Connected implements Runnable {
    private static SecMultGClient secgm_terminal_voto;
    //private static MCClient terminal_voto ;

    public Eleitor_Connected(SecMultGClient secgm_terminal_voto,MCClient terminal_voto){
        Eleitor_Connected.secgm_terminal_voto= secgm_terminal_voto;
        //Eleitor_Connected.terminal_voto = terminal_voto;
    }
    
    public void run(){
        Scanner scanner = new Scanner(System.in);
        Inputs inpu = new Inputs();
        String username,password;
        username = inpu.askVariable(scanner,"Username: " , 0);
        password = inpu.askVariable(scanner,"Password: " , 2);
        secgm_terminal_voto.getMcclient().setMessag("type|login;username|"+username+";password|"+password);
        try { secgm_terminal_voto.wait(); }
        catch (Exception e) { }
    
    }
   
    public static void ListaEleicoes(String message){
    }
}

