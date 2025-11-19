# Echo Server

Implementation of RFC 862 - Echo Protocol using Kotlin and minimal dependencies.

https://substack.com/inbox/post/177665315

## TO DO

### Step 1

In this step your goal is to build a simple server that will start-up, bind to all the local IP addresses, listen on port 7, and accept a TCP connection. To complete this step simply have the server print out a log message to show a connection has been accepted and then have it shutdown. Refer to the documentation for you programming language to find out how to write network programs using it.

You can test your server using two terminals, in one run the server and in the other use netcat or telnet to connect to it.

### Step 2

In this step your goal is to extend your server to accept multiple concurrent connections. This will require you to keep the main ‘thread’ of execution running and listening for incoming connections as well as spawning a new ‘thread’ of execution to handle each client.

Note I’m putting ‘thread’ in quotes here because I’m referring to something that is running concurrently alongside something else. That could be multiple operating system threads or it could be multiple async tasks. Your choice!

You can test your server using two terminals, in one run the server and in the other use netcat or telnet to connect to it.

### Step 3

In this step your goal is to read data from the client and write that data back to the client. That should continue until the client terminates the connection.

### Step 4 

In this step your goal is to add a command line flag so your echo server can be started up using either TCP or UDP on port 7.

### Step 5

In this step your goal is to shutdown cleanly. Ensuring all connections are closed and any inflight echo messages are sent before shutdown.