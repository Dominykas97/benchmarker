import com.fasterxml.jackson.annotation.JsonProperty;

/* Should we simulate I/O during the initialisation or with every received message? This class is initialised as part
   of the Config class, according to the global.yaml file. */
public enum IOMode {
    @JsonProperty("startup")
    STARTUP,
    @JsonProperty("regular")
    REGULAR;
}
