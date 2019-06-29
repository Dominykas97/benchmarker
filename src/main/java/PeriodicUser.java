import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class PeriodicUser {

    public static void main(String[] args) throws Exception {
        Config config = Config.getInstance();
        ServerSocket server = new ServerSocket(config.portNumber);
        System.out.println("Control server has started");
        Socket socket = server.accept();
        System.out.println("Connection established");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        for (int i = 0; i < config.numMessages; i++) {
            System.out.print("Sending " + (i+1) + "/" + config.numMessages + " message");
            out.println(".");
            if (i < config.numMessages - 1)
                TimeUnit.MILLISECONDS.sleep(config.interMessageTime);
        }
        System.out.println();
        out.close();
        socket.close();
        server.close();
    }
}
