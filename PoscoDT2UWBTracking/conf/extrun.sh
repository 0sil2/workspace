#! /bin/sh

#JAVA_HOME=/usr/local/java/jdk1.8.0_321

#CLASS_PATH=/usr/local/posco/uwbpos/uwbext.jar
#CLASS_PATH=/usr/local/posco/uwbpos/WebsocketClient-v2.2.1.a.jar
#CLASS_PATH=$CLASS_PATH:.:
#export CLASS_PATH

echo start....
/usr/local/java/jdk1.8.0_321/bin/java -cp "/usr/local/posco/uwbpos/uwbext.jar:/usr/local/posco/uwbpos/WebsocketClient-v2.2.1.a.jar" UWBExtendDataParsingClient
#$JAVA_HOME/bin/java -cp $CLASS_PATH UWBExtendDataParsingClient
