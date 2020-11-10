#!/bin/sh
javac -cp compute.jar rmi/client/*.java 
#java -cp .;compute.jar -Djava.rmi.server.codebase=file:compute.jar -Djava.security.policy=./rmi/client/client.policy rmi.client.ComputePi localhost 45
java -cp .:compute.jar -Djava.rmi.server.codebase=file:compute.jar -Djava.security.policy=rmi/client/client.policy rmi.client.ComputePi localhost 45

