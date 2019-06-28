#!/bin/sh

java -cp benchmarker-0.1.jar PeriodicUser &
flink run -m jobmanager:8081 benchmarker-0.1.jar
