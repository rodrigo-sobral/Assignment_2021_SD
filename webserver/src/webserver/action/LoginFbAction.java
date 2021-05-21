package webserver.action;
import rmiserver.classes.User;

import java.rmi.RemoteException;
import java.util.Random;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import uc.sd.apis.FacebookApi2;

public class LoginFbAction extends Action{
    private static final String NETWORK_NAME = "Facebook";
    private static final Token EMPTY_TOKEN = null;
    public String autho_url;
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private String code;
	@Override
	public String execute() throws RemoteException{
        final String secretState = "secret" + new Random().nextInt(999_999);
		// Replace these with your own api key and secret
        String apiKey = "2894802620848226";
        String apiSecret = "fcbe57b400b0a254657053e308521c9e";
        
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi2.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback("http://localhost:8080/webserver/loginFb") // Do not change this.
                                      .state(secretState)
                                      .build();
    
        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();
    
        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        autho_url=service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize Scribe here:");
        System.out.println(autho_url);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        getSession().put("service", service);
        getSession().put("autho_url", autho_url);
        System.out.println("code"+code);
        return SUCCESS;
         
	}

    public String login_verify() throws RemoteException{
         
        Token EMPTY_TOKEN= null;
        System.out.println("Trading the Request Token for an Access Token...");
        OAuthService service = (OAuthService) getSession().get("service");
        Verifier verifier = new Verifier(code);
        System.out.println("verifier "+verifier);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println("ola "+response.getCode());
        System.out.println("adeus"+response.getBody());
        String body = response.getBody();

        JSONObject obj = (JSONObject)JSONValue.parse(body); 
        System.out.println("Valor do id: " + obj.get("id").toString());
        System.out.println("Valor do nome: " + obj.get("name").toString());
        //savefacedata(obj.get("name").toString(), obj.get("id").toString());
        System.out.println("tam"+getRMIConnection().getUsers().size());
        for(User user:getRMIConnection().getUsers()){
            System.out.println("numero "+user.getId_fb());
            if(user.getId_fb()!=null && user.getId_fb().compareTo(obj.get("id").toString())==0){
                //encontro o user e esta associado com a conta fb
                saveLoggedUser(user);
                System.out.println("LOGIN COM SUCESSO");
                return LOGIN;
            }
        }
        System.out.println("LOGIN SEM SUCESSO NECESSITA ASSOCIAR CONTA");
        return ERROR;
    }

    public String getAutho_url() {
        return autho_url;
    }

    public void setAutho_url(String autho_url) {
        this.autho_url = autho_url;
    }
	

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    
}
