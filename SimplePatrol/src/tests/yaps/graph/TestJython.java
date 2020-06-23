package tests.yaps.graph;

import java.util.LinkedList;
import java.util.List;

import org.python.core.PyList;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class TestJython {
    
    public static void main(String[] args) {
//    	PythonInterpreter interpreter = new PythonInterpreter();
//    	interpreter.exec("import sys\nsys.path.append('\\Users\\Pablo\\Documents\\Projetos\\Projetos de Patrulha\\SimplePatrol')\nimport mwmatching");
//    	
//    	// execute a function that takes a string and returns a string
//    	PyObject someFunc = interpreter.get("mwmatching.funct");
//      System.out.println(someFunc);
//    	PyObject result = someFunc.__call__(new PyString("Test"));
//    	String realResult = (String) result.__tojava__(String.class);
    	
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        
        interpreter.exec("aux1 = sys.path[0]");
        interpreter.exec("directory = aux1[0 : aux1.rfind('\\\\')]"); //directory should point to "lib" directory
        interpreter.exec("sys.path.append(directory)"); //("sys.path.append('C:\\Users\\Pablo\\Documents\\Projetos\\Projetos de Patrulha\\SimplePatrol')");
        //interpreter.exec("print sys.path");
        
        interpreter.exec("import mwmatching");

        interpreter.set("paramet", new PyString("ddd"));
        interpreter.exec("print mwmatching.funct(paramet)");
        
        List<int[]> listTuples = new LinkedList<int[]>();
        listTuples.add(new int[]{1, 2, 110});
        listTuples.add(new int[]{0, 1, 20});
        listTuples.add(new int[]{2, 3, 23});
        interpreter.set("aux1", new PyList(listTuples));
        interpreter.exec("print aux1");
        
        interpreter.exec("mate = mwmatching.maxWeightMatching(aux1, False)");
        interpreter.exec("print mate");
        interpreter.exec("mate = mwmatching.maxWeightMatching(aux1, True)");
        interpreter.exec("print mate");
        
        PyList m = (PyList) interpreter.get("mate");
        System.out.println("mate[0]: " + m.get(0));
        System.out.println(m.get(0).getClass());
        System.out.println(m);
    	
    	interpreter.close();
	}
    
}
