public class VoteTerminal {
    private int id_terminal;
    private String nome_depar;
    private String ip;
    private String port;

    public VoteTerminal(){
        this.nome_depar = "";
        this.port = "";
        this.ip ="";
        this.id_terminal = 0;
    }
    
    //getters
    public int getId_terminal() { return id_terminal; }
    public String getNome_depar() { return nome_depar; }
    public String getIp() {
        return ip;
    }
    public String getPort() {
        return port;
    }

    //setters
    public void setId_terminal(int id_terminal) { this.id_terminal = id_terminal; }
    public void setNome_depar(String nome_depar) { this.nome_depar = nome_depar; }                                                                                                                                                                                                                                                                                                                                                                         
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(String port) {
        this.port = port;
    }
}