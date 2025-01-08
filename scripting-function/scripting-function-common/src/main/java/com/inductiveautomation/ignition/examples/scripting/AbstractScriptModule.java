package com.inductiveautomation.ignition.examples.scripting;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.script.PyArgParser;
import com.inductiveautomation.ignition.common.script.builtin.KeywordArgs;
import com.inductiveautomation.ignition.common.script.hints.JythonElement;
import com.inductiveautomation.ignition.common.script.hints.JythonThrows;
import com.inductiveautomation.ignition.common.script.hints.ScriptArg;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;

public abstract class AbstractScriptModule {
    static {
        /*
         This static block registers our properties bundle so that the PropertiesFileDocProvider is able
         to retrieve the documentation for our scripting functions
        */
        BundleUtil.get().addBundle(
            AbstractScriptModule.class.getSimpleName(),
            AbstractScriptModule.class.getClassLoader(),
            AbstractScriptModule.class.getName().replace('.', '/')
        );
    }

    /**
     * The JythonElement annotation can be added to fields or methods
     */
    @JythonElement(docBundlePrefix = "AbstractScriptModule")
    public static final double EULERS_CONSTANT = 2.7182;

    /**
     * Oops, we had a typo in a constant; we'll have to keep publishing it to not break end user's scripts
     * But we can deprecate it with the deprecated annotation in Java, _and_ deprecate it for scripting users via the
     * properties bundle.
     *
     * @see com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider
     */
    @JythonElement(docBundlePrefix = "AbstractScriptModule")
    @Deprecated(since = "8.3.0")
    public static final double OILERS_CONSTANT = 2.7182;

    /**
     * {@link JythonThrows} is used by the doc provider to know about possible Jython exceptions, since those cannot be
     * part of the Java method signature.
     */
    @JythonElement(docBundlePrefix = "AbstractScriptModule")
    @JythonThrows("ZeroDivisionError")
    public int divide(@ScriptArg("a") int a,
                      @ScriptArg("b") int b) {
        try {
            return a / b;
        } catch (ArithmeticException e) {
            throw Py.ZeroDivisionError("division by zero");
        }
    }

    /**
     * In the abstract script module, we establish the public signature of our method(s)
     * If we can't implement them directly, we can defer to an abstract method for the actual implementation
     * In this case, we need RPC to retrieve this information from the gateway, but we don't want to deal with an
     * arbitrary dataset over RPC, so instead we use our custom Metadata record class and just adapt that into a DS
     * at the 'last mile', in the local execution scope
     */
    @JythonElement(docBundlePrefix = "AbstractScriptModule")
    public final Dataset getGatewayMetadata() {
        Metadata metadata = getArchImpl();
        return new DatasetBuilder()
                .colNames("name", "architecture", "version")
                .colTypes(String.class, String.class, String.class)
                .addRow(metadata.osArch(), metadata.osName(), metadata.osVersion())
                .build();
    }

    /**
     * Make sure any 'implementation' methods you add are protected <b>or</b> throw the Jython
     * {@link org.python.core.PyIgnoreMethodTag} exception, so that they're not implicitly exposed by Ignition's
     * scripting machinery to autocomplete.
     */
    protected abstract Metadata getArchImpl();

    /**
     * An example of a "complicated" method that accepts keyword arguments.
     */
    @KeywordArgs(
            names = {"first", "second", "third"},
            types = {String.class, int.class, PyFunction.class}
    )
    @JythonElement(docBundlePrefix = "AbstractScriptModule")
    public String doSomethingComplicated(PyObject[] args, String[] keywords) {
        PyArgParser argParser = PyArgParser.parseArgs(
                args,
                keywords,
                new String[]{"first", "second", "third"},
                new Class<?>[]{String.class, int.class, Object.class},
                "doSomethingComplicated"
        );
        String first = argParser.requireString("first");
        int second = argParser.getInteger("second").orElse(-1);

        String output = "First: %s, Second: %d".formatted(first, second);
        argParser.getPyObject("third").ifPresent(function -> function.__call__(Py.java2py(output)));
        return output;
    }
}
