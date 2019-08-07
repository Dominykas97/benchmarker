import com.fasterxml.jackson.annotation.JsonProperty;

/* An ENum that defines whether to simulate I/O and if so, whether to do it during initialisation or with every
   received message. This class is initialised as part of the Config class, according to the global.yaml file. */
public enum IOMode {
    @JsonProperty("off")
    OFF,
    @JsonProperty("startup")
    STARTUP,
    @JsonProperty("regular")
    REGULAR;
}
