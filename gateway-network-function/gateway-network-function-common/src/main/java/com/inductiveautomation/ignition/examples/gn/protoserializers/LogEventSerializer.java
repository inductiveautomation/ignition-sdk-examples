package com.inductiveautomation.ignition.examples.gn.protoserializers;

import com.google.protobuf.Message;
import com.inductiveautomation.ignition.common.logging.Level;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.model.exception.ProtobufDeserializationException;
import com.inductiveautomation.ignition.common.model.exception.ProtobufSerializationException;
import com.inductiveautomation.ignition.examples.gn.protocolbuffers.GetLogsProto.LogEventPB;
import com.inductiveautomation.metro.api.ProtobufSerializable;

/**
 * This class forms the glue between the .proto file and the gateway network. It converts a LogEvent object into
 * a Protobuf message, and vice versa. This object gets registered in GatewayHook#setup().
 */
public class LogEventSerializer implements ProtobufSerializable<LogEvent, LogEventPB> {
    @Override
    public LogEventPB getProtoMsgInstance() {
        // This is required for deserialization
        return LogEventPB.getDefaultInstance();
    }

    @Override
    public LogEventPB toProtobufMessage(LogEvent logEvent) throws ProtobufSerializationException {
        LogEventPB.Builder builder = LogEventPB.newBuilder();
        builder.setTimestamp(logEvent.getTimestamp());
        builder.setLoggerName(logEvent.getLoggerName());
        builder.setMessage(logEvent.getMessage());

        // With Protobuf, it's a lot easier to get the ordinal integer of a Java enum and serialize that.
        // When deserializing, we just use the ordinal value to look up the correct enum value from the list of
        // possible enum values.
        builder.setLevelValue(logEvent.getLevel().ordinal());

        if (logEvent.getMarker() != null) {
            builder.setMarker(logEvent.getMarker());
        }

        if (logEvent.getException() != null) {
            logEvent.setException(logEvent.getException());
        }

        return builder.build();
    }

    @Override
    public LogEvent fromProtobufMessage(Message message) throws ProtobufDeserializationException {
        LogEventPB pb = (LogEventPB) message;

        long timestamp = pb.getTimestamp();
        String loggerName = pb.getLoggerName();
        String logMessage = pb.getMessage();

        // With Protobuf, it's a lot easier to get the ordinal integer of a Java enum and serialize that.
        // When deserializing, we just use the ordinal value to look up the correct enum value from the list of
        // possible enum values.
        Level level = Level.values()[pb.getLevelValue()];

        // Some loggers don't use a marker, so the marker object will be unavailable in the Protobuf message.
        String marker = pb.hasMarker() ? pb.getMarker() : null;

        // Some loggers don't have an exception stacktrace. In that case, the stacktrace will be an empty array.
        String[] stacktrace = pb.getExceptionList().toArray(new String[0]);

        LogEvent evt = new LogEvent();
        evt.setTimestamp(timestamp);
        evt.setLoggerName(loggerName);
        evt.setMessage(logMessage);
        evt.setLevel(level);
        evt.setMarker(marker);
        evt.setException(stacktrace);
        return evt;
    }
}
