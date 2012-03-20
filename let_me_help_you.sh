#!/bin/bash

JWSDIR="./CoopServer/lib"
LOGDIR="./CoopServer/logs"
LOGFIL="serverlog.txt"

function setup(){
	echo "Let me do the setup part for you ..."

	if [ ! -d ${JWSDIR}/jWebSocket-1.0 ]
	then
		echo "Deleting ${JWSDIR}/jWebSocket-1.0"
		rm -rf ${JWSDIR}/jWebSocket-1.0
		echo "Downloading jwebsocket to here"
		wget http://jwebsocket.googlecode.com/files/jWebSocketServer-1.0-nb20105.zip -O ./jwebsocket_server.zip
		echo "Creating ${JWSDIR} "
		mkdir ${JWSDIR} -p
		echo "Unpacking our libs"
		unzip ./jwebsocket_server.zip -d ${JWSDIR}
		echo "Cleaning up ..."
		rm ./jwebsocket_server.zip
		echo "Adding JWebSocketHome"
		export JWEBSOCKET_HOME="${JWSDIR}/jWebSocket-1.0"
	else
		echo "jWebSocket stuff already there (delete to reinstall)";
	fi

	if [ ! -f ${JWSDIR}/mongo-2.7.3.jar ]
	then
		wget https://github.com/downloads/mongodb/mongo-java-driver/mongo-2.7.3.jar -O ${JWSDIR}/mongo-2.7.3.jar
	else
		echo "MongoDB driver already there";
	fi

	echo "Done"
}

function build(){
	stop
	echo "Let's build our bad-ass server!"
	export JWEBSOCKET_HOME="${JWSDIR}/jWebSocket-1.0"
	echo "Deleting old bins"
	rm -rf CoopServer/console_build
	echo "Creating console_build directory"
	mkdir -p CoopServer/console_build
	echo "running javac"
	javac -classpath ${JWSDIR}/*.jar:${JWSDIR}/jWebSocket-1.0/libs/jWebSocketServer-1.0.jar:CoopServer/src -d CoopServer/console_build CoopServer/src/server/cooproject/itk/hu/*.java
	echo "done"
}

function run(){
	stop
	echo "Starting server"
	export JWEBSOCKET_HOME="${JWSDIR}/jWebSocket-1.0"
	mkdir -p ${LOGDIR}
	echo -e "$(date +"%Y-%m-%d %T") Server started \n" >> ${LOGDIR}/${LOGFIL}
	OUT="| tee -a"
	if [[ $1 == "silent" ]];
	then
		OUT=">>"
	fi
	eval java -classpath ${JWSDIR}/*.jar:CoopServer/console_build:${JWSDIR}/jWebSocket-1.0/libs/jWebSocketServer-1.0.jar org.jwebsocket.console.JWebSocketServer -config `pwd`/config/serverConfig.xml ${OUT} ${LOGDIR}/${LOGFIL} &
}

function update(){
	stop
	echo "Attempting to update from git origin, whatever that may be"
	git pull origin master
	echo "Done. run build to compile."
}

function stop(){
	echo "Stopping anything listening on 8787..."
	fuser -k 8787/tcp
	mkdir -p ${LOGDIR}
	echo -e "$(date +"%Y-%m-%d %T") Server stopped \n" >> ${LOGDIR}/${LOGFIL}
	echo "done"
}

function check_logs(){
	tail ${LOGDIR}/${LOGFIL}
}

function clear_logs(){
	echo "" > ${LOGDIR}/${LOGFIL}
}

if [[ $1 == "setup" ]];
then
	setup
elif [[ $1 == "build" ]];
then
	build
elif [[ $1 == "run" ]];
then
	run $2
elif [[ $1 == "stop" ]];
then
	stop
elif [[ $1 == "update" ]];
then
	update
elif [[ $1 == "check_logs" ]];
then
	check_logs
elif [[ $1 == "clear_logs" ]];
then
	clear_logs
else
	echo -e "missing argument. Valid arguments are:
  setup        install the dependencies.
  build        compile sources.
  run [silent] run the server [without console output]
  stop         stop any running servers
  update       get fresh source from the master
  check_logs   get the latest log entries. Try tail -f ${LOGDIR}/${LOGFIL} for full tailing
  clear_logs   clear all log entries."
fi

