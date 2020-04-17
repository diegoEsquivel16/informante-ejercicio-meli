package informante.connector;

import informante.exception.RestClientHttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestClient {

    private HttpClient cliente;
    private String host;

    public RestClient(String host){
        this.cliente = HttpClients.createDefault();
        this.host = host;
    }

    private HttpResponse get(String path){
        String url = host + path;
        HttpGet httpGetRequest = new HttpGet(url);
        try{
            return cliente.execute(httpGetRequest);
        }catch (IOException exc){
            throw new RestClientHttpException(path);
        }
    }

    public String getAsString(String path){
        HttpResponse response = this.get(path);
        if (resultWithErrors(response)) throw new RestClientHttpException(path);
        try{
            return EntityUtils.toString(response.getEntity());
        }catch (IOException exc){
            throw new RestClientHttpException(path);
        }
    }

    private Boolean resultWithErrors(HttpResponse response){
        int statusCode = response.getStatusLine().getStatusCode();
        return 400 <= statusCode && statusCode <= 599;
    }

}