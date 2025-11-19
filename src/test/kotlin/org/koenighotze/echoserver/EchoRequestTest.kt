package org.koenighotze.setupverification.org.koenighotze.echoserver

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koenighotze.org.koenighotze.echoserver.EchoRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.text.Charsets.UTF_8

class EchoRequestTest {
    fun createMockSocket(input: String, outputStream: OutputStream?): Socket {
        val mockSocket = mock(Socket::class.java)
        `when`(mockSocket.getInputStream()).thenReturn(input.byteInputStream())
        `when`(mockSocket.getOutputStream()).thenReturn(outputStream)
        `when`(mockSocket.remoteSocketAddress).thenReturn(InetSocketAddress("127.0.0.1", 8080))
        return mockSocket
    }


    @DisplayName("when echoing to a socket")
    @Nested
    inner class EchoTest {
        @Test
        fun the_socket_timeout_should_be_set_to_a_positive_number() {
            val mockSocket = createMockSocket("test\n", ByteArrayOutputStream())
            val request = EchoRequest()

            request.echo(mockSocket)

            verify(mockSocket).soTimeout = 5000
        }

        @Test
        fun the_socket_should_be_closed_if_an_exception_occurred() {
            val mockSocket = mock(Socket::class.java)
            `when`(mockSocket.getInputStream()).thenThrow(RuntimeException("Test exception"))
            `when`(mockSocket.remoteSocketAddress).thenReturn(InetSocketAddress("127.0.0.1", 8080))

            assertThrows<RuntimeException> {
                EchoRequest().echo(mockSocket)
            }

            verify(mockSocket).close()
        }

        @Test
        fun the_socket_should_be_closed_if_no_exception_occurred() {
            val mockSocket = createMockSocket("", ByteArrayOutputStream())

            EchoRequest().echo(mockSocket)

            verify(mockSocket).close()
        }
    }

    @Test
    fun initially_the_shutdown_latch_should_not_be_set() {
        val request = EchoRequest()

        assertFalse(request.shouldShutdown.get())
    }

    @Test
    fun when_shutting_down_shutdown_latch_should_be_set() {
        val request = EchoRequest()

        request.shutdown()

        assertTrue(request.shouldShutdown.get())
    }

    @DisplayName("when piping input to output")
    @Nested
    inner class PipingRequestTest {
        @Test
        fun it_should_read_and_echo_single_line() {
            val expected = "test\n"
            val outputStream = ByteArrayOutputStream()
            val mockSocket = createMockSocket(expected, outputStream)

            EchoRequest().echo(mockSocket)

            assertEquals(expected, outputStream.toString(UTF_8))
        }

        @Test
        fun it_should_handle_empty_input_stream() {
            val outputStream = ByteArrayOutputStream()
            val mockSocket = createMockSocket("", outputStream)

            EchoRequest().pipeInputToOutput(mockSocket)

            assertEquals("", outputStream.toString(UTF_8))
        }

        @Test
        fun it_should_read_and_echo_multiple_lines() {
            val input = "line1\nline2\nline3\n"
            val outputStream = ByteArrayOutputStream()
            val mockSocket = createMockSocket(input, outputStream)

            EchoRequest().pipeInputToOutput(mockSocket)

            assertEquals(input, outputStream.toString(UTF_8))
        }

        @Test
        fun it_should_stop_when_input_stream_ends() {
            val input = "line1\nline2"
            val outputStream = ByteArrayOutputStream()
            val mockSocket = createMockSocket(input, outputStream)

            EchoRequest().pipeInputToOutput(mockSocket)

            assertEquals("line1\nline2\n", outputStream.toString(UTF_8))
        }

        @Test
        fun it_should_stop_when_socket_is_closed() {
            val outputStream = ByteArrayOutputStream()
            val mockSocket = createMockSocket("test\n", outputStream)
            `when`(mockSocket.isClosed).thenReturn(true)

            EchoRequest().pipeInputToOutput(mockSocket)

            assertEquals("", outputStream.toString(UTF_8))
        }

        @Test
        fun it_should_stop_when_shutdown_is_requested() {
            val outputStream = ByteArrayOutputStream()
            val mockSocket = createMockSocket("test\n", outputStream)
            val request = EchoRequest()
            request.shutdown()

            request.pipeInputToOutput(mockSocket)

            assertEquals("", outputStream.toString(UTF_8))
        }

        @Test
        fun it_should_handle_socket_timeout_and_send_keepalive() {
            TODO()
        }

        @Test
        fun it_should_continue_after_socket_timeout() {
            TODO()
        }

        @Test
        fun it_should_flush_output_after_each_line() {
            val outputStream = mock(java.io.OutputStream::class.java)
            val mockSocket = createMockSocket("test\n", outputStream)

            EchoRequest().pipeInputToOutput(mockSocket)

            verify(outputStream, atLeastOnce()).flush()
        }

        @Test
        fun it_should_close_streams_automatically() {
            val mockInputStream = mock(InputStream::class.java)
            `when`(mockInputStream.read(any(ByteArray::class.java), anyInt(), anyInt())).thenReturn(-1)
            val mockOutputStream = mock(OutputStream::class.java)
            val mockSocket = mock(Socket::class.java)
            `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)
            `when`(mockSocket.getOutputStream()).thenReturn(mockOutputStream)

            EchoRequest().pipeInputToOutput(mockSocket)

            verify(mockInputStream).close()
            verify(mockOutputStream).close()
        }
    }
}