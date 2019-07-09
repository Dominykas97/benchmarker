import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class ControlServer {

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

    public static void main(String[] args) throws Exception {
        Config config = Config.getInstance();

        // Send messages to the Flink app
        try (
                ServerSocket server = new ServerSocket(config.controlPort);
                Socket socket = server.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            for (int i = 0; i < config.numMessages; i++) {
                System.out.println("Sending " + (i + 1) + "/" + config.numMessages + " message");
                out.println(".");
                if (i < config.numMessages - 1)
                    TimeUnit.MILLISECONDS.sleep(config.interMessageTime);
            }
        }

        // Re-open the server and read the job's runtime
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

        // Since Prometheus uses HTTPS without a valid certificate, we need to disable some stuff
        trustAllCertificates();
        disableHostnameVerification();

        System.out.println("Recording " + config.metrics.size() + " metrics");
        for (int i = 0; i < config.metrics.size(); i++) {
            // Get the JSON performance data
            URL prometheus = new URL("https://" + config.prometheusHostname +
                    "/api/v1/query?query=" + config.metrics.get(i).query + "[" + runtime + "m]");
            System.out.println("Connecting to " + prometheus);
            HttpsURLConnection connection = (HttpsURLConnection) prometheus.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String data = br.readLine();
            br.close();

            // Write it to a file
            String filename = "data/" + config.metrics.get(i).filename + ".json";

            System.out.println("Writing this data to " + filename + ":");
            System.out.println(data);

            File file = new File(filename);
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        System.out.println("Control server is terminating");
    }
}
