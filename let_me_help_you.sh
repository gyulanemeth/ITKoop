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

function run(){
	echo "Let's run our bad-ass server!"
}

if [[ $1 == "setup" ]];
then
	setup
elif [[ $1 == "run" ]];
then
	run
else
	echo "missing argument run/setup"
fi

