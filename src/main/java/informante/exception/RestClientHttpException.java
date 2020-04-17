package informante.exception;

public class RestClientHttpException extends RuntimeException {

    public RestClientHttpException(String url){
        super("Couldn't connect with the next url "+ url);
    }
}
