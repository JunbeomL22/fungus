#!/bin/bash

echo "Building project and copying dependencies..."
mvn clean package -q

echo "Running OrderId Hash Benchmark..."
java -cp "target/fungus-1.0-SNAPSHOT.jar:target/dependency/*" org.openjdk.jmh.Main com.junbeom.benchmark.order.OrderIdHashBenchmark