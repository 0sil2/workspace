#! /bin/sh

UWB_HOME=/usr/local/posco/uwbpos
JAVA_HOME=/usr/local/java/jdk1.8.0_321

CLASS_PATH=$UWB_HOME/uwbext.jar
CLASS_PATH=$CLASS_PATH:$UWB_HOME/json.jar
CLASS_PATH=$CLASS_PATH:$UWB_HOME/json-simple-1.1.1.jar
CLASS_PATH=$CLASS_PATH:$UWB_HOME/mysql-connector-java-5.1.30-bin.jar
CLASS_PATH=$CLASS_PATH:$UWB_HOME/WebsocketClient-v2.2.1.a.jar
CLASS_PATH=$CLASS_PATH:.:
export CLASS_PATH

echo "Start collecting UWB TAGS EXTENDER DATA........"
$JAVA_HOME/jre/bin/java -cp $CLASS_PATH UWBWebsocketExtendDataClient
#/usr/local/java/jdk1.8.0_321/jre/bin/java -cp "/usr/local/posco/uwbpos/uwbext.jar:/usr/local/posco/uwbpos/WebsocketClit-v2.2.1.a.jar" UWBWebsocketClient