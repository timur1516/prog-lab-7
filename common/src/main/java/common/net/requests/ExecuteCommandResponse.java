package common.net.requests;

import java.io.Serializable;

/**
 * Record for responses of any commands
 * @param state Result state
 * @param data Serializable data with result
 */
public record ExecuteCommandResponse(ResultState state, Serializable data) implements Serializable {}
