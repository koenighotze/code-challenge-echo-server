package org.koenighotze.org.koenighotze.echoserver

import java.net.ServerSocket
import java.net.Socket

class EchoServer {
    val serverSocket: ServerSocket

    constructor(socket: ServerSocket) {
        this.serverSocket = socket
    }

    fun listen() {
        println("Echo server listening on port ${serverSocket.localPort}")
        val connection = serverSocket.accept()
        handleConnection(connection)
    }

    fun handleConnection(connection: Socket) {
        println("Handling incoming connection from ${connection.remoteSocketAddress}")
        val input = connection.getInputStream().bufferedReader()
        val data = input.readLine() ?: "n/a"
        println("Read $data")
    }

    fun shutdown() {
        println("Closing connection")
        serverSocket.close()
    }
}
