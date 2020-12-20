#!/bin/bash

# https://sparkbyexamples.com/spark/spark-setup-on-hadoop-yarn/
# https://computingforgeeks.com/how-to-install-apache-spark-on-ubuntu-debian/
# https://supergloo.com/spark-scala/apache-spark-cluster-run-standalone/
# https://spark.apache.org/docs/latest/spark-standalone.html
# https://spark.apache.org/docs/latest/monitoring.html
# https://stackoverflow.com/questions/34284565/cannot-start-spark-history-server

# Update the system
sudo apt update
sudo apt full-upgrade --yes

# Install Java
sudo apt install default-jdk

# Download and install spark
cd || exit
curl -O https://apache.mirrors.benatherton.com/spark/spark-3.0.1/spark-3.0.1-bin-hadoop3.2.tgz
tar xvf spark-3.0.1-bin-hadoop3.2.tgz
rm spark-3.0.1-bin-hadoop3.2.tgz

# Apache Spark log config for history server
cd spark-3.0.1-bin-hadoop3.2 || exit
mkdir ./logs
mkdir ./logs/history-server
cp ./conf/spark-defaults.conf.template ./conf/spark-defaults.conf
echo "
spark.eventLog.enabled           true
spark.eventLog.dir               $HOME/spark-3.0.1-bin-hadoop3.2/logs/history-server
spark.history.fs.logDirectory    $HOME/spark-3.0.1-bin-hadoop3.2/logs/history-server" >> ./conf/spark-defaults.conf

# Apache Spark console log level
cp ./conf/log4j.properties.template ./conf/log4j.properties
sed -i 's/log4j.rootCategory=INFO/log4j.rootCategory=WARN/g' ./conf/log4j.properties

# Create alias to start/stop the master, the slave, the history server and to open the web UI
echo "
# Apache Spark
alias spark-start=\"~/spark-3.0.1-bin-hadoop3.2/sbin/start-master.sh && ~/spark-3.0.1-bin-hadoop3.2/sbin/start-slave.sh spark://parrot:7077 && ~/spark-3.0.1-bin-hadoop3.2/sbin/start-history-server.sh && xdg-open http://localhost:8080\"
alias spark-stop=\"~/spark-3.0.1-bin-hadoop3.2/sbin/stop-history-server.sh && ~/spark-3.0.1-bin-hadoop3.2/sbin/stop-slave.sh && ~/spark-3.0.1-bin-hadoop3.2/sbin/stop-master.sh\"" >> ~/.bashrc
source ~/.bashrc
