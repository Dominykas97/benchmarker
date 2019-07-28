import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class FunctionalUser {

    public FunctionalUser(String function, double binWidth, double initialX, double finalX) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        // Evaluate the function at initial_x + binWidth / 2, initial_x + 3 * binWidth / 2, ..., < final_x
        for (double x = initialX + binWidth / 2; x < finalX; x += binWidth) {
            double y = (Double) engine.eval(function.replaceAll("x", Double.toString(x)));
            int numRequests = (int) Math.round(y * binWidth);
            System.out.println("y = " + y + ", numRequests = " + numRequests);
        }
    }

    public static void main(String[] args) throws Exception {
        new FunctionalUser("10+5*x", 2, 0, 10);
    }
}
