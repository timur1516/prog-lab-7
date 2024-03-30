package common.net.requests;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Record to transfer commands between client and server
 * @param commandName String name of command
 * @param arguments Serializable ArrayList of all command arguments
 */
public record PackedCommand(String commandName, ArrayList<Serializable> arguments) implements Serializable {}
