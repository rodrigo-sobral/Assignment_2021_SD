package webserver.action;
/*
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.OAuthRequest;
import org.apache.struts2.interceptor.SessionAware;*/

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import uc.sd.apis.*;
import java.util.Map;
import java.util.Random;
public class AssociateFbAction extends Action {
    
    private static final String NETWORK_NAME = "Facebook";
    private static final Token EMPTY_TOKEN = null;
    public String autho_url;
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private String code,state;
	@Override
	public String execute() throws Exception{
        final String secretState = "secret" + new Random().nextInt(999_999);
		// Replace these with your own api key and secret
        String apiKey = "1528703067521344";
        String apiSecret = "78b0377bee0088d8d8e5d4d8621cd1cd";
        
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi2.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback("http://localhost:8080/webserver/associaFb") // Do not change this.
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
        session.put("service", service);
        session.put("autho_url", autho_url);
        System.out.println("code"+code);
        return SUCCESS;
         
	}

    public String associar_face(){
        Token EMPTY_TOKEN= null;
        System.out.println("Trading the Request Token for an Access Token...");
        OAuthService service = (OAuthService) session.get("service");
        Verifier verifier = new Verifier(code);
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
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        return SUCCESS;
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
