package org.koenighotze.org.koenighotze.echoserver

import java.net.ServerSocket
import java.net.SocketException
import java.util.Collections
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class EchoServer {
    private val serverSocket: ServerSocket
    private val executor: ExecutorService = newCachedThreadPool()
    private val requests = Collections.synchronizedCollection(ArrayList<EchoRequest>())

    constructor(socket: ServerSocket) {
        this.serverSocket = socket
    }

    fun listen() {
        while (!serverSocket.isClosed) {
            println("Echo server listening on port ${serverSocket.localPort}")

            try {
                val connection = serverSocket.accept()
                executor.submit {
                    val request = EchoRequest()
                    requests.add(request)

                    println("Inflight requests: ${requests.size}")
                    try {
                        request.echo(connection)
                    } finally {
                        requests.remove(request)
                        println("Inflight requests: ${requests.size}")
                    }
                }
            }
            catch (e: SocketException) {
                println("Error: ${e.message}")
            }
        }
    }

    fun shutdown() {
        println("Shutting down...")
        serverSocket.close()
        println("...waiting for threads to terminate...")
        requests.forEach { it.shutdown() }
        executor.awaitTermination(10, TimeUnit.SECONDS)
        println("Shutting down now!")
        executor.shutdownNow()
    }


}
