package informante.connector;

import informante.dto.CurrencyServiceResponse;

public class CurrenciesConnector {

    private String currenciesConnectorHost;

    public CurrenciesConnector(String currenciesConnectorHost) {
        this.currenciesConnectorHost = currenciesConnectorHost;
    }

    public CurrencyServiceResponse getCurrencies(){
        return new CurrencyServiceResponse();
    }
}
