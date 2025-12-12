# Agent Instructions

This file provides AI coding assistant guidance when working with this RFC 862 Echo Protocol implementation.

## Project Philosophy

This is a minimal-dependency implementation focusing on core Java/Kotlin networking APIs. Avoid adding external libraries unless absolutely necessary. Prefer standard library solutions.

## Code Standards

### Kotlin Style
- Use idiomatic Kotlin: `use` for resource management, extension functions where appropriate
- Prefer immutability: use `val` over `var` when possible
- Use property syntax over getter/setter methods
- Leverage Kotlin's null safety features

### Package Structure Issue
**IMPORTANT**: There is a package naming duplication issue in the codebase:
- Main code: `org.koenighotze.org.koenighotze.echoserver` (has duplicate)
- Tests: `org.koenighotze.setupverification.org.koenighotze.echoserver`

When refactoring or creating new files, use the clean package structure:
- Main code: `org.koenighotze.echoserver`
- Tests: `org.koenighotze.echoserver`

Do not perpetuate the duplication in new code.

## Architecture Patterns

### Concurrency
- Use `ExecutorService` for managing connection handler threads
- Use `AtomicBoolean` for shutdown signaling
- Use `Collections.synchronizedCollection()` for thread-safe collections
- Each client connection runs in its own task submitted to the thread pool

### Resource Management
- Always use Kotlin's `use` blocks for streams and sockets
- Ensure proper cleanup in `finally` blocks where `use` cannot be applied
- Set socket timeouts to prevent hanging connections

### Shutdown Pattern
The server implements graceful shutdown in this order:
1. Close server socket (reject new connections)
2. Signal all active requests to stop
3. Wait for executor termination (up to 10 seconds)
4. Force shutdown remaining threads

When modifying shutdown logic, maintain this ordering.

## Testing Guidelines

### Framework
- Use JUnit 5 (`@Test`, `@Nested`, `@DisplayName`)
- Use Mockito for mocking sockets and streams
- Use Kotlin test assertions (`kotlin.test.*`)

### Test Structure
- Group related tests using `@Nested` inner classes
- Use descriptive `@DisplayName` annotations for test context
- Mock external dependencies (sockets, streams) rather than using real network connections

### Test Patterns for This Codebase
When testing socket operations:
```kotlin
fun createMockSocket(input: String, outputStream: OutputStream?): Socket {
    val mockSocket = mock(Socket::class.java)
    `when`(mockSocket.getInputStream()).thenReturn(input.byteInputStream())
    `when`(mockSocket.getOutputStream()).thenReturn(outputStream)
    `when`(mockSocket.remoteSocketAddress).thenReturn(InetSocketAddress("127.0.0.1", 8080))
    return mockSocket
}
```

### TODO Tests
Several tests are marked with `TODO()`. When implementing:
- Socket timeout tests should verify keep-alive messages are sent
- Verify the connection continues processing after timeout
- Ensure proper exception handling

## Common Modifications

### Adding New Connection Handling Logic
Modify `EchoRequest.pipeInputToOutput()`:
- Check `shouldShutdown.get()` and `connection.isClosed` before processing
- Handle exceptions appropriately (timeout vs fatal errors)
- Always flush output after writing

### Changing Server Behavior
Modify `EchoServer.listen()`:
- Maintain the accept loop with proper exception handling
- Track new request types in the `requests` collection
- Ensure executor submissions are properly handled

### Adding Configuration
Currently hardcoded values:
- Port: 7 (in `Main.kt`)
- Socket timeout: 5000ms (in `EchoRequest.kt`)
- Shutdown wait: 10 seconds (in `EchoServer.kt`)

When making these configurable, use constructor parameters rather than global state.

## Implementation Roadmap

Per README.md, the remaining steps are:
- **Step 4**: Add TCP/UDP command line flag
- **Step 5**: Refine clean shutdown (mostly complete)

When implementing Step 4:
- UDP requires `DatagramSocket` instead of `ServerSocket`
- UDP is connectionless - different paradigm than current TCP implementation
- Consider separate classes: `TcpEchoServer` and `UdpEchoServer`

## Error Handling

- Log errors with context (connection address, error message)
- Catch `SocketException` when server socket is closed during shutdown
- Catch `SocketTimeoutException` for keep-alive logic
- Don't swallow unexpected exceptions - let them propagate or log them

## Debugging

The codebase uses `println()` for logging. When debugging:
- Log connection lifecycle events (accept, close)
- Log in-flight request count
- Log shutdown events
- Include `this` reference for `EchoRequest` to track individual connections

## Running Manual Tests

```bash
# Terminal 1: Start server
./gradlew run

# Terminal 2: Test with netcat
nc localhost 7
# Type text and verify it echoes back
# Type 'Q' in server terminal to test shutdown

# Alternative: use telnet
telnet localhost 7
```

## Performance Considerations

- Cached thread pool scales to handle many concurrent connections
- Each connection gets its own thread - reasonable for moderate connection counts
- For high-scale scenarios (1000+ concurrent connections), consider async I/O (NIO)
- Current implementation is optimized for correctness, not maximum throughput
