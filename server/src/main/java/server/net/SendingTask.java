package server.net;

import common.net.requests.ServerResponse;

import java.net.SocketAddress;

/**
 * Record to store and transfer response which should be sent to client and his address
 * @param response
 * @param address
 */
public record SendingTask(ServerResponse response, SocketAddress address) {
}
