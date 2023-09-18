#! /bin/sh

CUCBE_HOME=/usr/local/posco/uCube
JAVA_HOME=/usr/local/java/jdk1.8.0_321

CLASS_PATH=$CUCBE_HOME/mysql-connector-java-5.1.30-bin.jar
CLASS_PATH=$CLASS_PATH:.:
export CLASS_PATH

echo "A new Posco Research Crane Data Start........"
$JAVA_HOME/bin/java -Xms62m -Xmx256m -cp $CLASS_PATH PoscoResearchCraneDataSocketServer