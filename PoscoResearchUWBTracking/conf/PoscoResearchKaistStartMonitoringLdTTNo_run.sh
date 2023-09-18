#! /bin/sh

CUCBE_HOME=/usr/local/posco/uwbpos
JAVA_HOME=/usr/local/java/jdk1.8.0_321

CLASS_PATH=$CUCBE_HOME/mysql-connector-java-5.1.30-bin.jar
CLASS_PATH=$CLASS_PATH:.:
export CLASS_PATH

echo "A new Posco Research Kaist Start Monitoring Backup Start........"
$JAVA_HOME/bin/java -Xms125m -Xmx256m -cp $CLASS_PATH PoscoResearchKaistStartMonitoringLdTTNo