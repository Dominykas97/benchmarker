import java.io.PrintWriter;

public abstract class Workload {
    public abstract void execute(PrintWriter out) throws Exception;
}
