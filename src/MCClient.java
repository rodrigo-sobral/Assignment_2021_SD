
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;
import classes.User;
import javax.swing.text.StyledEditorKit.BoldAction;

/**
 * The MulticastClient class joins a multicast group and loops receiving
 * messages from that group. The client also runs a MulticastUser thread that
 * loops reading a string from the keyboard and multicasting it to the group.
 * <p>
 * The example IPv4 address chosen may require you to use a VM option to
 * prefer IPv4 (if your operating system uses IPv6 sockets by default).
 * <p>
 * Usage: java -Djava.net.preferIPv4Stack=true MulticastClient
 *
 * @author Raul Barbosa
 * @version 1.0
 */
public class MCClient extends Thread{
    private VoteTerminal vote_terminal;
    private String messag;
    private Boolean Connected;
    private static SecMultGClient secgm_terminal_voto;
    private static MCClient terminal_voto ;
    //construtor
    public MCClient(){
        this.vote_terminal = new VoteTerminal();
        this.messag = "";
        this.Connected = false;
    }

    //getter
    public String getMessag() { return messag; }
    public VoteTerminal getVote_terminal() { return vote_terminal; }
    public Boolean getConnected() { return Connected; }    
    
    //setter
    public void setMessag(String messag) { this.messag = messag; }
    public void setConnected(Boolean connected) { Connected = connected; }

    public static void main(String[] args) {
        terminal_voto = new MCClient();
        Inputs inpu = new Inputs();
        secgm_terminal_voto = new SecMultGClient();
        Scanner scanner = new Scanner(System.in);
        String depar;
        depar = inpu.askVariable(scanner,"Insert the department to which the desk vote belongs: " , 0);
        secgm_terminal_voto.getMcclient().getVote_terminal().setNome_depar(depar);

        //SERVER->CLIENT
        ReadWrite.read_ip_port("VoteDesk.txt", depar, terminal_voto);
        if (terminal_voto.vote_terminal.getNome_depar().compareTo("")==0){
            System.out.println("There is no desk vote open in that department");
        }
        secgm_terminal_voto.getMcclient().getVote_terminal().setId_terminal(Integer.parseInt(Gerar_Numeros.gerar_port(100, 1)));
        terminal_voto.getVote_terminal().setId_terminal(secgm_terminal_voto.getMcclient().getVote_terminal().getId_terminal());
        secgm_terminal_voto.getMcclient().setMessag("type|envia_id;id|"+secgm_terminal_voto.getMcclient().getVote_terminal().getId_terminal());
        
        //scanner.close();
        terminal_voto.start();
        secgm_terminal_voto.start();
        //MulticastUser user = new MulticastUser();
        //user.start();*/
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
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                if (Handler_Message.typeMessage_Client(message, getVote_terminal().getId_terminal()).compareTo("Choose")==0){
                    System.out.println("É este o terminal de voto escolhido");
                    setConnected(true);
                    Eleitor_Connected.eleitor_acoes(message,secgm_terminal_voto.getMcclient());
                }
                //else if(Handler_Message.typeMessage_Client(message, id))

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}





class SecMultGClient extends Thread{
    private MCClient mcclient;


    //construtor
    public SecMultGClient(){
        this.mcclient = new MCClient();
        
    }
    

    //getters
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
                    
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }

        }
        
        }

        

    }

}



class Eleitor_Connected{
    public static void eleitor_acoes(String mensagem, MCClient sec_mult){
        Scanner scanner = new Scanner(System.in);
        Inputs inpu = new Inputs();
        String username,password;
        username = inpu.askVariable(scanner,"Username: " , 0);
        password = inpu.askVariable(scanner,"Password: " , 2);
        sec_mult.setMessag("type|login;username|"+username+";password|"+password);
    }

    public static void ListaEleicoes(String message){

    }
}



/*
class MulticastUser extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public MulticastUser() {
        super("User " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
                String readKeyboard = keyboardScanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
  
}*/