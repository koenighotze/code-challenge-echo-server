package org.koenighotze.org.koenighotze.echoserver

import java.net.ServerSocket

fun main() {
    val server = EchoServer(ServerSocket(7))
    server.listen()
}