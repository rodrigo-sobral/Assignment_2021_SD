package ws;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import rmiserver.Department;
import rmiserver.classes.Election;
import webserver.model.RMIConnection;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;


@ServerEndpoint(value = "/ws")
public class WebSocketServer  {
    private String username;
    private Session session;
    private static final Set<WebSocketServer> logged_users = new CopyOnWriteArraySet<>();

    public WebSocketServer() { }

    @OnOpen
    public void openSocket(Session session) {
        this.session = session;
        logged_users.add(this);
    }

    @OnClose
    public void closeSocket() {
        logged_users.remove(this);
        sendMessage(mountString(getLoggedUsernames()));
    }

    @OnMessage
    public void getMessage(String message) throws RemoteException {
        if (message.compareTo("unstarted")==0 || message.compareTo("running")==0 || message.compareTo("finished")==0) {
            String ask_elections= "";
            for (Election election : new RMIConnection().getElectionsByState(message)) ask_elections += election.toString(message, true);
            sendMessage(ask_elections);
        }
        else if (message.compareTo("vote_tables")==0) {
            String ask_vote_tables= "";
            for (Department department: new RMIConnection().getDepartmentsWithOrNotVoteTable(true)) ask_vote_tables += department;
            sendMessage(ask_vote_tables);
        } else {
            this.username = message;
            //  THIS MUST ALWAYS BE DONE TO INFORM ALL ADMINS WE STILL ALIVE IN THE SYSTEM
            ArrayList<String> logged_usernames = getLoggedUsernames();
            if (message.isEmpty()) {
                for (String user : logged_usernames) {
                    if (this.username.compareTo("admin")!=0 && this.username.compareTo(user)==0) logged_usernames.remove(user);
                }
            } 
            sendMessage(mountString(logged_usernames));
        }
    }

    @OnError
    public void handleError(Throwable e) { e.printStackTrace(); }

    //  ====================================================================================================
    
    //  SEND ACTIVE USERS TO THE ONES WHO ARE IN THE ADMIN MENU
    //  SEND UPDATED ELECTIONS TO THE ONES WHO ARE CONSULTING ELECTIONS
    //  SEND UPDATED VOTE TABLES TO THE ONES WHO ARE CONSULTING VOTE TABLES
    private void sendMessage(String text) {
        try {
            Iterator <WebSocketServer> iterator= logged_users.iterator();
            while(iterator.hasNext()) iterator.next().session.getBasicRemote().sendText(text);
        } catch (IOException e1) {
            try { this.session.close(); } 
            catch (IOException e2) { e2.printStackTrace(); }
        }
    }
    
    private String mountString(ArrayList<String> logged_usernames) {
        int admin_count=0, user_count=0;
        for (String user : logged_usernames) {
            if (user.compareTo("admin")==0) admin_count+=1;
            else user_count+=1;
        }
        String response= "Administradores Ativos: "+admin_count+"\nUtilizadores Ativos: "+user_count;
        for (String user : logged_usernames) if (user.compareTo("admin")!=0) response +=  "\n"+user;
        return response;
    }

    private ArrayList<String> getLoggedUsernames(){
        ArrayList<String> logged_usernames = new ArrayList<String>();
        Iterator <WebSocketServer> iterator= logged_users.iterator();

        while(iterator.hasNext()) logged_usernames.add(iterator.next().getUsername());
        return logged_usernames;
    }
    private String getUsername() { return this.username; }
}