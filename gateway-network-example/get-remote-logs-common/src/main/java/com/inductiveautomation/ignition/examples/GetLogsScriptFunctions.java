package com.inductiveautomation.ignition.examples;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.inductiveautomation.ignition.common.script.builtin.KeywordArgs;
import com.inductiveautomation.ignition.common.script.builtin.PyArgumentMap;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunctionDocProvider;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONException;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

/**
 * Created by mattgross on 9/15/2016. When the script functions are called from the Gateway, client, or designer, the
 * actual code is executed here. Classes in the Gateway and client extend this class within their scopes and implement
 * any abstract functions in their own way.
 */
public abstract class GetLogsScriptFunctions {

    @NoHint
    public abstract HashMap<String, List<LoggingEvent>> getLogEntriesInternal(List<String> remoteServers, Date startDate, Date endDate);

    @KeywordArgs(names = {"remoteServers", "startDate", "endDate"}, types = {List.class, Date.class, Date.class})
    public PyDictionary getRemoteLogEntries(PyObject[] pyArgs, String[] keywords) throws JSONException {
        PyArgumentMap args = PyArgumentMap.interpretPyArgs(pyArgs, keywords, GetLogsScriptFunctions.class, "getRemoteLogEntries");
        List<String> remoteServers = (List<String>) args.getArg("remoteServers");

        if (remoteServers == null || remoteServers.size() == 0) {
            throw Py.ValueError("Missing required argument remoteServers");
        }

        Date startDate = args.getDateArg("startDate");
        Date endDate = args.getDateArg("endDate");

        HashMap<String, List<LoggingEvent>> logsMap = getLogEntriesInternal(remoteServers, startDate, endDate);
        PyDictionary dict = new PyDictionary();
        for(String key: logsMap.keySet()){

            DatasetBuilder dataBuilder = new DatasetBuilder()
                    .colNames("level","name","timestamp","message")
                    .colTypes(String.class,String.class,Date.class,String.class);

            List<LoggingEvent> events = logsMap.get(key);

            // Convert the logging events into dataset rows
            for(LoggingEvent event: events){
                dataBuilder.addRow(event.getLevel().toString(), event.getLoggerName(), new Date(event.getTimeStamp()), event.getMessage().toString());
            }

            dict.put(key, dataBuilder.build());
        }

        return dict;
    }

    /**
     * The text in this class appears on the script method info popup in places like the Script Console.
     */
    public static class Documentation implements ScriptFunctionDocProvider {

        private static final Map<String, String> getLogsMap = new LinkedHashMap<String, String>();


        static {
            getLogsMap.put("List<String> remoteServers","A list of remote servers");
            getLogsMap.put("Date startDate", "Returns all logs after this date. Set to None to not use a start date.");
            getLogsMap.put("Date endDate", "Returns all logs before this date. Set to None to not use an end date");
        }

        @Override
        public String getMethodDescription(String path, Method m) {
            if (m.getName().equals("getRemoteLogEntries")) {
                return "Returns logs from all selected servers as a map.";
            }

            return null;
        }

        @Override
        public Map<String, String> getParameterDescriptions(String path, Method m) {
            if (m.getName().equals("getRemoteLogEntries")) {
                return getLogsMap;
            }

            return null;
        }

        @Override
        public String getReturnValueDescription(String path, Method m) {
            if (m.getName().equals("getRemoteLogEntries")) {
                return "A map of servers. Each server's logging events is a Dataset in the map.";
            }
            return null;
        }

    }
}
