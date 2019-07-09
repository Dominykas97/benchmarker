#!/bin/sh

flink run -m jobmanager:8081 benchmarker-0.1.jar &
java -cp benchmarker-0.1.jar ControlServer
