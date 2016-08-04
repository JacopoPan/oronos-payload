#!/bin/bash

cd ./apache-ant-1.9.7/bin/
./ant jar

mv ./dist/USBtinController.jar ../../

cd ../../

python ./test-counterpart.py &
java -jar ./USBtinController.jar
