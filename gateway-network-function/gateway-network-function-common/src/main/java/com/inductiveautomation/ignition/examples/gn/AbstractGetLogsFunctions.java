package com.inductiveautomation.ignition.examples.gn;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.rpc.proto.ProtoRpcSerializer;
import com.inductiveautomation.ignition.common.script.builtin.KeywordArgs;
import com.inductiveautomation.ignition.common.script.builtin.PyArgumentMap;
import com.inductiveautomation.ignition.common.script.hints.JythonElement;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.inductiveautomation.ignition.common.script.hints.ScriptArg;
import com.inductiveautomation.ignition.common.script.hints.SystemLibrary;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

/**
 * When the script functions are called from the Gateway, client, or designer, the
 * actual code is executed here. Classes in the Gateway and client extend this class within their scopes and implement
 * any abstract functions in their own way.
 */
@SystemLibrary(mountPath = "system.example.gn")
public abstract class AbstractGetLogsFunctions {
    static {
        /*
         This static block registers our properties bundle so that the PropertiesFileDocProvider is able
         to retrieve the documentation for our scripting functions
        */
        BundleUtil.get().addBundle(
            AbstractGetLogsFunctions.class.getSimpleName(),
            AbstractGetLogsFunctions.class.getClassLoader(),
            AbstractGetLogsFunctions.class.getName().replace('.', '/')
        );
    }

    /* This is used by the RPC system to serialize objects between the gateway and designer/client. This module's
     objects are relatively trivial, and do not require any specialized serialization. But if you needed to, the
     call would look something like this:
     <code>
     ProtoRpcSerializer.newBuilder()
        .addGsonAdapter(SomeObject.class, new SomeObjectSerializer())
        .build();

     public class SomeObjectSerializer implements JsonSerializer<SomeObject>, JsonDeserializer<SomeObject> {
     ...
     }
     </code>
     */
    protected static final ProtoRpcSerializer rpcSerializer = ProtoRpcSerializer.newBuilder().build();

    /**
     * Both the gateway and the client versions of the call go through here. This internally calls either the
     * gateway version or the client version of getLogEntriesInternal() to get the actual log entries.
     * @param pyArgs
     * @param keywords
     * @return a dictionary, where each key is the remote gateway and the values for each key are the log entries
     */
    @JythonElement(docBundlePrefix = "AbstractGetLogsFunctions")
    @KeywordArgs(names = {"remoteServers", "startDate", "endDate"}, types = {List.class, Date.class, Date.class})
    public PyDictionary getRemoteLogEntries(PyObject[] pyArgs, String[] keywords) throws JSONException {
        PyArgumentMap args =
            PyArgumentMap.interpretPyArgs(pyArgs, keywords, AbstractGetLogsFunctions.class, "getRemoteLogEntries");
        List<String> remoteServers = (List<String>) args.getArg("remoteServers");

        if (remoteServers == null || remoteServers.isEmpty()) {
            throw Py.ValueError("Missing required argument remoteServers");
        }

        Date startDate = args.getDateArg("startDate");
        Date endDate = args.getDateArg("endDate");

        Map<String, List<LogEvent>> logsMap = getLogEntriesInternal(remoteServers, startDate, endDate);
        PyDictionary dict = new PyDictionary();
        for(String key: logsMap.keySet()){

            DatasetBuilder dataBuilder = new DatasetBuilder()
                    .colNames("level","name","timestamp","message")
                    .colTypes(String.class,String.class,Date.class,String.class);

            List<LogEvent> events = logsMap.get(key);

            // Convert the logging events into dataset rows
            for(LogEvent event: events){
                dataBuilder.addRow(event.getLevel().toString(),
                    event.getLoggerName(),
                    new Date(event.getTimestamp()),
                    event.getMessage().toString());
            }

            dict.put(key, dataBuilder.build());
        }

        return dict;
    }

    @JythonElement(docBundlePrefix = "AbstractGetLogsFunctions")
    public byte[] getRemoteLogFile(@ScriptArg("remoteServer") String remoteServer) throws IOException {
        if (StringUtils.isBlank(remoteServer)) {
            throw Py.ValueError("Missing required argument remoteServer");
        }

        // Note that this returns the file path of the file on the *gateway* file system.
        return getLogFileInternal(remoteServer);
    }

    @JythonElement(docBundlePrefix = "AbstractGetLogsFunctions")
    public String pushRemoteLogFile(@ScriptArg("remoteServer") String remoteServer) throws IOException {
        if (StringUtils.isBlank(remoteServer)) {
            throw Py.ValueError("Missing required argument remoteServer");
        }

        return pushLogFileInternal(remoteServer);
    }

    /**
     * This is implemented by the gateway and the client implementations of AbstractGetLogsFunctions
     *
     */
    @NoHint
    public abstract Map<String, List<LogEvent>> getLogEntriesInternal(List<String> remoteServers,
                                                                          Date startDate,
                                                                          Date endDate);

    /**
     * This is implemented by the gateway and the client implementations of AbstractGetLogsFunctions
     *
     */
    @NoHint
    public abstract byte[] getLogFileInternal(String remoteServer) throws IOException;

    /**
     * This is implemented by the gateway and the client implementations of AbstractGetLogsFunctions
     *
     */
    @NoHint
    public abstract String pushLogFileInternal(String remoteServer) throws IOException;
}
