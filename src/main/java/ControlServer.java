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
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
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
        // Send messages to the Flink app
        Config config = Config.getInstance();
        ServerSocket server = new ServerSocket(config.controlPort);
        System.out.println("Control server has started");
        Socket socket = server.accept();
        System.out.println("Connection established");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        for (int i = 0; i < config.numMessages; i++) {
            System.out.println("Sending " + (i+1) + "/" + config.numMessages + " message");
            out.println(".");
            if (i < config.numMessages - 1)
                TimeUnit.MILLISECONDS.sleep(config.interMessageTime);
        }
        out.close();
        socket.close();
        server.close();

        // Since Prometheus uses HTTPS without a valid certificate, we need to disable some stuff
        trustAllCertificates();
        disableHostnameVerification();

        // Get the JSON performance data
        String timeInterval = "1h";

        String metric = "flink_taskmanager_job_task_operator_componentThroughput";
        URL prometheus = new URL("https://" + config.prometheusHostname +
                "/api/v1/query?query=" + metric + "[" + timeInterval + "]");
        HttpsURLConnection connection = (HttpsURLConnection) prometheus.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String data = reader.readLine();
        reader.close();

        // Write it to a file
        System.out.println(data);
        BufferedWriter writer = new BufferedWriter(new FileWriter("data/output.json"));
        writer.write(data);
        writer.close();
    }
}
