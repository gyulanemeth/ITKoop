#!/bin/bash
function setup(){
	echo "Let me do the setup part for you ..."
	echo "Deleting /tmp/badassdeps "
	rm -rf /tmp/badassdeps
	echo "Downloading jwebsocket to /tmp/"
	wget http://jwebsocket.googlecode.com/files/jWebSocketServer-1.0-nb20105.zip -O /tmp/jwebsocket_server.zip
	echo "Creating /tmp/badassdeps/ "
	mkdir /tmp/badassdeps
	echo "Unpacking our libs"
	unzip /tmp/jwebsocket_server.zip -d /tmp/badassdeps/
	echo "Cleaning up ..."
	rm /tmp/jwebsocket_server.zip
	echo "Done"
}

function build(){
	echo "Let's build our bad-ass server!"
	export JWEBSOCKET_HOME="/tmp/badassdeps/jwebsocket-1.0"
	echo "Deleting old bins"
	rm -rf CoopServer/console_build
	echo "Creating console_build directory"
	mkdir -p CoopServer/console_build
	echo "running javac"
	javac -classpath /tmp/badassdeps/jWebSocket-1.0/libs/jWebSocketServer-1.0.jar:CoopServer/src -d CoopServer/console_build CoopServer/src/server/cooproject/itk/hu/*.java
	echo "done"
}

function run(){
	echo "Starting server"
	export JWEBSOCKET_HOME="/tmp/badassdeps/jWebSocket-1.0"
	java -classpath CoopServer/console_build:/tmp/badassdeps/jWebSocket-1.0/libs/jWebSocketServer-1.0.jar org.jwebsocket.console.JWebSocketServer -config `pwd`/config/serverConfig.xml
}

if [[ $1 == "setup" ]];
then
	setup
elif [[ $1 == "build" ]];
then
	build
elif [[ $1 == "run" ]];
then
	run
else
	echo "missing argument setup/build/run"
fi

