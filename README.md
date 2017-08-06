# ChatClient

A client for the ChatServer program that is capable of sending and receiving messages, authenticate with the server and send server commands.

The ChatServer program is also available to try/download. See: https://github.com/maxwey/ChatServer

## What this is, and what this isn't

Firstly, this is a work in progress; it has not been thoroughly tested by any means, and may not always work.

In addition, the communication protocol used in this program is not a standardized protocol; and the functions of the protocol may not be the same as other more standard protocols. See the ChatServer README for more information about these protocols.

This program started as a side project, and is no longer my main focus, but I will surely be returning to this every so often to tweak and change things as I learn more.

Lastly, I built this as a place to learn and explore something new. If you have any comments, ideas, questions, or suggestions on anything,  I would be extremely interested in knowing what you think!

## How to use the client

The client can be run from the command line (execute `java Client` after compilation the source files). It does not take any launch arguments.

The UI is self explanatory.

When connecting to a server:
- the username must be 1-10 characters long and contain any characters 'A'-'Z' upper/lower case or '\_'. Numbers and special characters are not allowed.
- the password is not always required, but can be any character string.
- IP address is specified in IPv4 format
- the port number defaults to 58755 (current ChatServer default setting), but can be any number 0-65536


** For more technical detail on how this implementation works, see the ChatServer readme text **

## Future Work

- Multimedia/sending files
- greater security with server communication (no cleartext communications)
