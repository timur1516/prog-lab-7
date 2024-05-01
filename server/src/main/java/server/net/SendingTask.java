package server.net;

import common.net.requests.ServerResponse;

import java.net.SocketAddress;

public record SendingTask(ServerResponse response, SocketAddress address) {
}
