import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class FunctionalUser {
    public String function;
    public double binWidth;
    public double initialX;
    public double finalX;

    public void execute() throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        // Evaluate the function at initial_x + binWidth / 2, initial_x + 3 * binWidth / 2, ..., < final_x
        for (double x = initialX + binWidth / 2; x < finalX; x += binWidth) {
            double y = (Double) engine.eval(function.replaceAll("x", Double.toString(x)));
            int numRequests = (int) Math.round(y * binWidth);
            System.out.println("y = " + y + ", numRequests = " + numRequests);
        }
    }
}
