package server.net;

import common.net.requests.ClientRequest;

import java.net.SocketAddress;

public record HandlingTask(ClientRequest clientRequest, SocketAddress address) {
}
