package astparser;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Binding{
    int lineNumber;
    String typeOfStatement;
    String value;

    public Binding(int lineNumber, String typeOfStatement, String value) {
        this.lineNumber = lineNumber;
        this.typeOfStatement = typeOfStatement;
        this.value = value;
    }

    @Override
    public String toString() {
        if(!typeOfStatement.equals("MethodInvocation"))
            return
                    "LineNumber=" + lineNumber +
                            "\nType      ='" + typeOfStatement + '\'' +
                            "\nValue     ='" + value + "\'" +
                            "\n--";
        else
            return
                    "LineNumber=" + lineNumber +
                            "\nType      ='" + "MethodInvocation\'" +
                            "\n--";
    }
}

public class TemplateClass {
    static StringBuilder executionTrace;
    //    static
    static HashMap<String, List<Binding>> bindingTable = new HashMap<>();

    static {
        executionTrace = new StringBuilder();
//        bindingTrace = new StringBuilder();
    }
    public static void instrum(int lineNumber, String typeOfStatement, AP... args) {
        executionTrace.append("Line: "+lineNumber+" Type: "+typeOfStatement + "\n");
        for(AP arg: args){
            executionTrace.append(arg+"\n");
            if(arg.name.equals("")) continue;
            List<Binding> list = bindingTable.getOrDefault(arg.name, new ArrayList<>());
            if(!arg.type.equals("MethodInvocation"))
                //String value = (arg.value.length() > 0) ? arg.value : "''";
                list.add(new Binding(lineNumber, arg.type, arg.value));
            else
                list.add(new Binding(lineNumber, "MethodInvocation",""));
            bindingTable.put(arg.name, list);
        }
        executionTrace.append("==========\n");
//        if(args.length > 0){
//            List<Binding> list = bindingTable.getOrDefault(args[0].name, new ArrayList<>());
//            list.add(new Binding(lineNumber, args[0].value));
//            bindingTable.put(args[0].name, list);
//        }
    }

    public static void writeToFile(){
        System.out.println(executionTrace.toString());
        try (PrintWriter out = new PrintWriter("trace.txt")) {
            out.println(executionTrace.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder bindingTrace = new StringBuilder();
        for(Map.Entry<String, List<Binding>> entry: bindingTable.entrySet()){
            bindingTrace.append("------------------------"+ entry.getKey() +"------------------------\n");
            for(Binding binding: entry.getValue()){
                bindingTrace.append(binding+"\n");
            }
            bindingTrace.append("------------------------\n");
        }

        System.out.println(bindingTrace.toString());
        try (PrintWriter out = new PrintWriter("binding_trace.txt")) {
            out.println(bindingTrace.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
