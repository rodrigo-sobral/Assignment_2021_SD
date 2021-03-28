import java.io.*;
import javax.swing.JOptionPane;

public class ReadWrite implements Serializable{
    
    private static final long serialVersionUID = 1L;

    //escrever no ficheiro txt os ips do departamento
    public static void Write (String filename,String depar,String ip,String port){
        try {
            FileWriter fp = new FileWriter(filename,true);
            fp.write(depar+" "+ip+" "+port+"\n");
            fp.close();
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    //buscar info para o client
    public static void read_ip_port(String filename,String depar,MCClient client){
        //se for a primeira utilização, le o ficheiro que contem as pessoas e coloca as no array pessoas que exsite no centro de investigação
        File f= new File(filename);
        if(f.exists() && f.isFile()){
            try{
                FileReader fr = new FileReader(f);
                BufferedReader br= new BufferedReader(fr);
                String line;
                while((line=br.readLine()) !=null){
                    String lista[] = line.split(" ");
                    if(lista[0].compareTo(depar)==0){
                        //linha correta
                        client.getVote_terminal().setNome_depar(depar);
                        client.getVote_terminal().setPort(lista[2]);
                        client.getVote_terminal().setIp(lista[1]);
                    }
                }
                br.close();
                fr.close();
            }catch(FileNotFoundException ex){
                JOptionPane.showMessageDialog(null, "[pessoas.txt]: Erro ao abrir o ficheiro de texto", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, " [pessoas.txt]: Erro ao ler o ficheiro de texto.", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "[pessoas.txt]: Erro, o ficheiro não existe", "ERRO!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void read_ip_port_terminal(String filename,String depar,MCServer server){
        //se for a primeira utilização, le o ficheiro que contem as pessoas e coloca as no array pessoas que exsite no centro de investigação
        File f= new File(filename);
        if(f.exists() && f.isFile()){
            try{
                FileReader fr = new FileReader(f);
                BufferedReader br= new BufferedReader(fr);
                String line;
                while((line=br.readLine()) !=null){
                    String lista[] = line.split(" ");
                    if(lista[0].compareTo(depar)==0){
                        //linha correta
                        server.getDesk().setDepar(depar);
                        server.getDesk().setPort(lista[2]);
                        server.getDesk().setIp(lista[1]);
                    }
                }
                br.close();
                fr.close();
            }catch(FileNotFoundException ex){
                JOptionPane.showMessageDialog(null, "[pessoas.txt]: Erro ao abrir o ficheiro de texto", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, " [pessoas.txt]: Erro ao ler o ficheiro de texto.", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "[pessoas.txt]: Erro, o ficheiro não existe", "ERRO!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
