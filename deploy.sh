#!/bin/bash


ps aux | grep src/demo.sh | grep -v grep |  awk '{print $2}' | xargs -I% kill -9 %

rm -rf ~/app/demo
mkdir -p ~/app/demo
cp demo.tar.gz ~/app/demo

cd ~/app/demo
tar -zxvf demo.tar.gz
nohup bash src/demo.sh > log 2>&1 &