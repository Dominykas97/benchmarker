import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/* A server responsible for sending messages to the Flink application as well
   as gathering and recording Prometheus data */
public class ControlServer {
    private static Config config;

    /*
     * From https://stackoverflow.com/questions/1201048/allowing-java-to-use-an-untrusted-certificate-for-ssl-https-connection
     */
    private static void trustAllCertificates() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        System.out.println("Created a TrustManager");

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        System.out.println("Installed the TrustManager");
    }

    private static void disableHostnameVerification() {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    private static void sendMessages() throws Exception {
        try (
                ServerSocket server = new ServerSocket(config.controlPort);
                Socket socket = server.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            // Send messages to the Flink app
            config.workload.execute(out);
        }
    }

    /* Re-open the server and read the job's runtime */
    private static long receiveRuntime() throws Exception {
        long runtime;
        try (
                ServerSocket server = new ServerSocket(config.controlPort);
                Socket socket = server.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            long runtimeMs = Long.parseLong(reader.readLine());
            runtime = TimeUnit.MINUTES.convert(runtimeMs, TimeUnit.MILLISECONDS) + 1;
            System.out.println("The job took about " + runtime + " min (" + runtimeMs + " ms)");
        }
        return runtime;
    }

    private static InputStream getPrometheusInputStream(int metricIndex, long runtime) throws Exception {
        String protocol = config.prometheusUsesHttps? "https" : "http";
        String port = config.prometheusUsesHttps? "" : (":" + config.prometheusPort);
        URL prometheus = new URL(protocol + "://" + config.prometheusHostname + port +
                "/api/v1/query?query=" + config.metrics.get(metricIndex).query + "[" + runtime + "m]");
        System.out.println("Connecting to " + prometheus);
        if (config.prometheusUsesHttps) {
            HttpsURLConnection connection = (HttpsURLConnection) prometheus.openConnection();
            return connection.getInputStream();
        }
        HttpURLConnection connection = (HttpURLConnection) prometheus.openConnection();
        return connection.getInputStream();
    }

    private static void saveMetrics(long runtime, int index) throws Exception {
        System.out.println("Recording " + config.metrics.size() + " metrics");
        for (int i = 0; i < config.metrics.size(); i++) {
            // Get the JSON performance data
            InputStream stream = getPrometheusInputStream(i, runtime);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String data = br.readLine();
            br.close();

            // Write it to a file
            String filename = "data/" + config.metrics.get(i).filename + "_" + index + ".json";

            System.out.println("Writing this data to " + filename + ":");
            System.out.println(data);

            File file = new File(filename);
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        config = Config.getInstance();
        System.out.println("Control server is initialising");

        if (config.prometheusUsesHttps) {
            trustAllCertificates();
            disableHostnameVerification();
        }

        for (int i = 0; ; i++) {
            sendMessages();
            long runtime = receiveRuntime();
            saveMetrics(runtime, i);
        }
    }
}
