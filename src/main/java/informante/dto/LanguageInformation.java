package informante.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LanguageInformation {

    @JsonProperty("iso639_1")
    private String iso639One;
    @JsonProperty("iso639_2")
    private String iso639Two;
    private String name;

    public String getIso639One() {
        return iso639One;
    }

    public void setIso639One(String iso639One) {
        this.iso639One = iso639One;
    }

    public String getIso639Two() {
        return iso639Two;
    }

    public void setIso639Two(String iso639Two) {
        this.iso639Two = iso639Two;
    }

    private String nativeName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }
}
