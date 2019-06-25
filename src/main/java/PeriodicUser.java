import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class PeriodicUser {

    public static void main(String[] args) throws Exception {
        Configuration config = Configuration.getInstance();
        ServerSocket server = new ServerSocket(config.portNumber);
        Socket socket = server.accept();
        System.out.println("Connection established");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        for (int i = 0; i < config.numMessages; i++) {
            out.println("a");
            System.out.print("\rMessages sent: " + (i+1) + "/" + config.numMessages);
            if (i < config.numMessages - 1)
                TimeUnit.MILLISECONDS.sleep(config.interMessageTime);
        }
        System.out.println();
        out.close();
        socket.close();
        server.close();
    }
}
