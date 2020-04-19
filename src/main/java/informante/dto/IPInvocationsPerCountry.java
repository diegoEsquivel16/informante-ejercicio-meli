package informante.dto;

public class IPInvocationsPerCountry {

    private String countryName;
    private Double distance;
    private Integer invocations;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getInvocations() {
        return invocations;
    }

    public void setInvocations(Integer invocations) {
        this.invocations = invocations;
    }

    public void newInvocation(){
        this.invocations++;
    }
}
