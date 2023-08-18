@echo off

call protoc --java_out=./src/main/java common.proto

call protoc --java_out=./src/main/java bank_transactions.proto
