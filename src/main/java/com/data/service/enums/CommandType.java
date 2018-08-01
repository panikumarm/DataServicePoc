package com.data.service.enums;

import java.util.HashMap;
import java.util.Map;

import static com.data.service.constants.DataServiceConstants.COMMAND_TERMINATE;

public enum CommandType {

    TERMINATE(COMMAND_TERMINATE);
    private final String commandType;

    CommandType(String commandType) {
        this.commandType = commandType;
    }

    private static Map<String, CommandType> lookupMap = new HashMap<>();

    static {
        for (CommandType commandType : CommandType.values()) {
            lookupMap.put(commandType.getCommandType(), commandType);
        }
    }

    public String getCommandType() {
        return this.commandType;
    }

    public static CommandType getCommandTypeByValue(String commandType) {
        return lookupMap.get(commandType);
    }
}
