package org.koenighotze.org.koenighotze.echoserver

import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicBoolean

class EchoRequest {
    val shouldShutdown = AtomicBoolean(false)

    fun pipeInputToOutput(connection: Socket) {
        connection.getInputStream().bufferedReader().use { input ->
            connection.getOutputStream().bufferedWriter().use { out ->
                while (!connection.isClosed && !shouldShutdown.get()) {
                    try {
                        val data = input.readLine() ?: break
                        println("$this Read $data")
                        out.write("$data\n")
                        out.flush()
                    } catch (e: SocketTimeoutException) {
                        println("$this Connection timeout for ${connection.remoteSocketAddress} with ${e.message}")
                        out.write("Are you alive?\n")
                        out.flush()
                    }
                }
            }
        }
    }

    fun echo(connection: Socket) {
        println("Handling incoming connection from ${connection.remoteSocketAddress}")
        try {
            connection.soTimeout = 5000
            pipeInputToOutput(connection)
        } finally {
            println("$this Closing connection to ${connection.remoteSocketAddress}")
            connection.close()
        }
    }

    fun shutdown() {
        println("$this  Should shut down!")
        shouldShutdown.set(true)
    }
}
