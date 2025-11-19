package org.koenighotze.echoserver

import org.koenighotze.org.koenighotze.echoserver.EchoServer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import kotlin.concurrent.thread

fun withConsoleWatcher(f: () -> Unit) {
    thread(start = true, isDaemon = true, name = "console-watcher") {
        println("Type Q to exit")
        val reader = BufferedReader(InputStreamReader(System.`in`))
        while (true) {
            val line = try {
                reader.readLine()
            } catch (_: Exception) {
                null
            } ?: break
            if (line.trim().equals("q", ignoreCase = true)) {
                f()
                break
            }
        }
    }
}

fun main() {
    val server = EchoServer(ServerSocket(7))

    System.console()?.let {
        withConsoleWatcher { server.shutdown() }
    } ?: println("No interactive console detected. Use SIGINT (Ctrl+C) or stop the JVM to shut down the server.")

    server.listen()
}