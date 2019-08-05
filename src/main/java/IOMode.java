import com.fasterxml.jackson.annotation.JsonProperty;

public enum IOMode {
    @JsonProperty("off")
    OFF,
    @JsonProperty("startup")
    STARTUP,
    @JsonProperty("regular")
    REGULAR;
}
