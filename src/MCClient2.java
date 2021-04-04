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
                cliente.stop();
                cliente2.stop();
            }catch (Exception e) { e.printStackTrace(); }
        }
        thread_eleitor.stop();
        System.exit(0);
    }

    public void run() {
        MulticastSocket socket = null;
        String messag_lida;
        String []aux;
        while(true){
            try {Thread.sleep(1000);} catch (InterruptedException e){}
            if(cliente.vote_terminal.getNome_depar().compareTo("")!=0){
                try {
                
                    System.out.println("server->cliente");
                    socket = new MulticastSocket(Integer.parseInt(getVote_terminal().getPort()));  // create socket and bind it
                    InetAddress group = InetAddress.getByName(getVote_terminal().getIp());
                    socket.joinGroup(group);
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    while (!exit) {
                        try {Thread.sleep(1000);} catch (InterruptedException e){}
                        socket.receive(packet);
                        System.out.print("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("e a mensagem"+message);
                        cliente.setMessage(message);
                        
                        messag_lida = Handler_Message.typeMessage_Client(message, getVote_terminal().getId_terminal());
                        System.out.println("Mensagem lida: "+messag_lida);
                        aux = messag_lida.split("\\|");
                        if (aux[0].compareTo("choose")==0){
                            System.out.println("Terminal Escolhido");
                            //setConnected(true);
                            cliente.setCc(aux[1]);
                            //Eleitor_Connected thread_connected = new Eleitor_Connected(secgm_terminal_voto, terminal_voto);
                            thread_eleitor.thread.start();
                        }else{
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
                            }
                            else if(messag_lida.compareTo("false")==0){
                                //do something
                                continue;
                            }
                            else if(messag_lida.compareTo("connected_server")==0){
                                //feito em baixo
                                cliente2.setMessage("");
                                
                            }
                            else if(messag_lida.compareTo("")==0){
                                System.out.println("Ignorar mensagem");
                            }
                            //lista eleicoes
                            else{
                                System.out.println("AHHHHH");
                                cliente2.setMessage("");
                                Eleitor_Connected.ListaCandidaturas(cliente2, message, cliente.getInpu(), cliente.getScanner());
                                
                                //Thread.currentThread().notify();
        
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
