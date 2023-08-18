@echo off

call protoc --java_out=../java/src/main/java common.proto

if not %errorlevel% == 0 goto end

call protoc --java_out=../java/src/main/java bank_transactions.proto

if not %errorlevel% == 0 goto end

echo --------------------------------
echo Successfully created artifacts
echo --------------------------------

:end
