package server.net;

import common.net.requests.ClientRequest;

import java.net.SocketAddress;

/**
 * Record to store and transfer request from client with client address
 * @param clientRequest
 * @param address
 */
public record HandlingTask(ClientRequest clientRequest, SocketAddress address) {
}
