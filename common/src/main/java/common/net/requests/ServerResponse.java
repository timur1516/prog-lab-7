package common.net.requests;

import java.io.Serializable;

/**
 * Record for responses of any commands
 * @param state Result state
 * @param data Serializable data with result
 */
public record ServerResponse(ResultState state, Serializable data) implements Serializable {}
