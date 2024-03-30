package common.net.requests;

import java.io.Serializable;

/**
 * Record for client request
 * @param type Request type
 * @param data Any serializable data
 */
public record ClientRequest(ClientRequestType type, Serializable data) implements Serializable {}
