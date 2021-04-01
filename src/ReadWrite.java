import java.io.*;
import java.util.ArrayList;

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
                JOptionPane.showMessageDialog(null, "[VoteDesk.txt]: Erro ao abrir o ficheiro de texto", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, " [VoteDesk.txt]: Erro ao ler o ficheiro de texto.", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "[VoteDesk.txt]: Erro, o ficheiro não existe", "ERRO!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void read_ip_port_terminal(String filename,MCServerData mesa){
        //se for a primeira utilização, le o ficheiro que contem as pessoas e coloca as no array pessoas que exsite no centro de investigação
        File f= new File(filename);
        if(f.exists() && f.isFile()){
            try{
                FileReader fr = new FileReader(f);
                BufferedReader br= new BufferedReader(fr);
                String line;
                while((line=br.readLine()) !=null){
                    String lista[] = line.split(" ");
                    if(lista[0].compareTo(mesa.getDeparNome())==0){
                        //linha correta
                        mesa.setDepar(mesa.getDeparNome());
                        mesa.setPort(lista[2]);
                        mesa.setIp(lista[1]);
                    }
                }
                br.close();
                fr.close();
            }catch(FileNotFoundException ex){
                JOptionPane.showMessageDialog(null, "[VoteTerminal.txt]: Erro ao abrir o ficheiro de texto", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, " [VoteTerminal.txt]: Erro ao ler o ficheiro de texto.", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "[VoteTerminal.txt]: Erro, o ficheiro não existe", "ERRO!", JOptionPane.ERROR_MESSAGE);
        }
    }

    //serve para quando uma mesa de voto/terminal de voto fechar
    //apagar a linha para n haver conflito quando 
    
    public static void remove_line(String filename,String depar){
        ArrayList <String> new_list = new ArrayList<>();
        File f= new File(filename);
        if(f.exists() && f.isFile()){
            try{
                FileReader fr = new FileReader(f);
                BufferedReader br= new BufferedReader(fr);
                String line;
                while((line=br.readLine()) !=null){
                    String lista[] = line.split(" ");
                    if(lista[0].compareTo(depar)!=0){
                        //linha correta
                        new_list.add(line);
                        
                    }
                }
                br.close();
                fr.close();
            }catch(FileNotFoundException ex){
                JOptionPane.showMessageDialog(null, "[VoteDesk.txt]: Erro ao abrir o ficheiro de texto", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, " [VoteDesk.txt]: Erro ao ler o ficheiro de texto.", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "[VoteDesk.txt]: Erro, o ficheiro não existe", "ERRO!", JOptionPane.ERROR_MESSAGE);
        }

        f.delete();
        for (int i = 0; i < new_list.size(); i++) {
            String lista[] = new_list.get(i).split(" ");
            ReadWrite.Write(filename, lista[0], lista[1], lista[2]);
        }
    }


    //verifica se esta algum cliente conectado na mesa de voto, se estiver vai buscar
    //no ficheiro txt
    public static String check_client_connect(String filename,String depar){
        String ip_port = "";
        File f= new File(filename);
        if(f.exists() && f.isFile()){
            try{
                FileReader fr = new FileReader(f);
                BufferedReader br= new BufferedReader(fr);
                String line;
                while((line=br.readLine())!=null){
                    String lista[] = line.split(" ");
                    if(lista[0].compareTo(depar)==0){
                        //linha correta
                        ip_port=lista[1]+" "+lista[2];
                        br.close();
                        fr.close();
                        return ip_port;
                    }
                }
                br.close();
                fr.close();
            }catch(FileNotFoundException ex){
                JOptionPane.showMessageDialog(null, "[TerminalVote.txt]: Erro ao abrir o ficheiro de texto", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, " [TerminalVote.txt]: Erro ao ler o ficheiro de texto.", "ERRO!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "[TerminalVote.txt]: Erro, o ficheiro não existe", "ERRO!", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }
}
