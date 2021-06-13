import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaEmbedUtils;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.Set;

public class test1 {
    @Test
   public void t1() {
        ScriptingContainer container = new ScriptingContainer();
        container.runScriptlet("puts 'Hello World!'");
    }
    @Test
    public void t2() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("jruby");
        engine.eval("puts 'Hello World!'");
    }
    @Test
    public void t3(){
        String filename=test1.class.getClassLoader().getResource("ruby.rb").getPath();
        ScriptingContainer container = new ScriptingContainer();
        container.runScriptlet(PathType.ABSOLUTE, filename);


    }
    @Test
    public void t4(){
        String script =
                "puts \"Hello World.\"\n" +
                        "puts \"Error is here.\"";
        ScriptingContainer container = new ScriptingContainer();
        JavaEmbedUtils.EvalUnit unit = container.parse(script, 1);
        Object ret = unit.run();
    }
    @Test
    public void t5(){
        System.out.println("[" + getClass().getName() + "]");
        ScriptingContainer container = new ScriptingContainer();
        container.put("message", "local variable");
        container.put("@message", "instance variable");
        container.put("$message", "global variable");
        container.put("MESSAGE", "constant");
        String script =
                "puts message\n" +
                        "puts @message\n" +
                        "puts $message\n" +
                        "puts MESSAGE";
        container.runScriptlet(script);
    }
    @Test
    public void t6(){
        System.out.println("[" + getClass().getName() + "]");
        ScriptingContainer container = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
        Object ret = container.runScriptlet("x=144");
        Object ret2 = container.runScriptlet("Math.sqrt x");
        System.out.println("Square root of " + ret + " is " + ret2);

        String message = "hot Vanilla Latte at that cafe.";
        container.put("message", message);
        ret = container.runScriptlet("ret=\"You can enjoy #{message}\"");
        System.out.println(ret);

        String correction = "could have enjoyed";
        container.put("correction", correction);
        ret = container.runScriptlet("ret = ret.gsub(/can enjoy/, correction)");
        System.out.println(ret);

        Map m = container.getVarMap();
        Set<String> keys = container.getVarMap().keySet();
        for (String key : keys) {
            System.out.println(key + ", " + m.get(key));
        }
    }
    @Test
    public void t7(){
        System.out.println("[" + getClass().getName() + "]");
        ScriptingContainer container = new ScriptingContainer();
        String script =
                "# Radioactive decay\n" +
                        "def amount_after_years(q0, t)\n" +
                        "q0 * Math.exp(1.0 / $half_life * Math.log(1.0/2.0) * t)\n" +
                        "end\n" +
                        "def years_to_amount(q0, q)\n" +
                        "$half_life * (Math.log(q) - Math.log(q0)) / Math.log(1.0/2.0)\n" +
                        "end";
        Object receiver = container.runScriptlet(script);

        container.put("$half_life", 24100); // Plutonium
        String method = "amount_after_years"; // calculates the amount left after given years
        Object[] args = new Object[2];
        args[0] = 10.0;    // initial amount is 10.0g
        args[1] = 1000;    // suppose 1000 years have passed
        Object result = container.callMethod(receiver, method, args, Double.class);
        System.out.println(args[0] + "g Plutonium to decay to " + result + "g in " + args[1] + " years");

        method = "years_to_amount"; // calculates the years to decay to a given amount
        args[0] = 10.0;    // initial amount is 10.0g
        args[1] = 1.0;     // suppose 1.0g is still there
        result = container.callMethod(receiver, method, args, Double.class);
        System.out.println(args[0] + "g Plutonium to decay to " + args[1] + "g in " + result + " years");
    }
    @Test
    public void t8()throws Exception{
        String filename= test1.class.getClassLoader().getResource("tree_with_ivars.rb").getPath();
        System.out.println("[" + getClass().getName() + "]");
        ScriptingContainer container = new ScriptingContainer();

        Object receiver = container.runScriptlet(PathType.CLASSPATH, filename);
        container.put(receiver, "@name", "cherry blossom");
        container.put(receiver, "@shape", "oval");
        container.put(receiver, "@foliage", "deciduous");
        container.put(receiver, "@color", "pink");
        container.put(receiver, "@bloomtime", "March - April");
        container.callMethod(receiver, "update", Object.class);
        System.out.println(container.callMethod(receiver, "to_s", String.class));

        container.put(receiver, "@name", "cedar");
        container.put(receiver, "@shape", "pyramidal");
        container.put(receiver, "@foliage", "evergreen");
        container.put(receiver, "@color", "nondescript");
        container.put(receiver, "@bloomtime", "April - May");
        container.callMethod(receiver, "update", Object.class);
        System.out.println(container.callMethod(receiver, "to_s", String.class));
    }
    @Test
    public void t9(){

    }


}
