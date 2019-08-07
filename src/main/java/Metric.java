/* A description of a metric consists of its printing-friendly name, filename to be used for recording Prometheus
   data, and its formal name as understood by Prometheus (query). Multiple instances of this class are initialised
   together with the Config class. */
public class Metric {
    public String name;
    public String filename;
    public String query;
}
