import java.io.PrintWriter;

/* A workload defines how the control server sends messages to the Flink application. It is initialised as part of
   the Config class. */
public abstract class Workload {
    public abstract void execute(PrintWriter out) throws Exception;
}
