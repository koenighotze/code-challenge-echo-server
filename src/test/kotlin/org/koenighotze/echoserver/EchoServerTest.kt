package org.koenighotze.setupverification.org.koenighotze.echoserver

import org.junit.jupiter.api.Test
import org.koenighotze.org.koenighotze.echoserver.EchoServer
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.test.assertContains


private fun captureStdOut(block: () -> Unit): String {
    val originalOut = System.out
    val out = ByteArrayOutputStream()
    System.setOut(PrintStream(out))
    try {
        block()
    } finally {
        System.setOut(originalOut)
    }
    return out.toString()
}

class EchoServerTest {
//    @Test
//    fun when_handling_connections_the_input_should_be_echoed() {
//        val mockSocket = Mockito.mock(ServerSocket::class.java)
//        val server = EchoServer(mockSocket)
//
//        val mockClientConnection = Mockito.mock(Socket::class.java)
//
//        val inputStream = ByteArrayInputStream("Hello World\n".toByteArray())
//        `when`(mockClientConnection.getInputStream()).thenReturn(inputStream)
//
//        val result = captureStdOut {
//            server.handleConnection(mockClientConnection)
//        }
//
//        assertContains(result, "Hello World")
//    }


    @Test
    fun when_listening_connections_should_be_accepted() {
    }

    @Test
    fun when_shutting_down_the_socket_should_be_closed() {
    }

    @Test
    fun when_listening_and_a_connections_is_opened_an_exception_should_be_thrown() {
    }
}