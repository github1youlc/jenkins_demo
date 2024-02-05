#!/bin/bash

rm -rf ~/app/demo
mkdir -p ~/app/demo
cp demo.tar.gz ~/app/demo

cd ~/app/demo
tar -zxvf demo.tar.gz
nohup bash src/demo.sh > log 2>&1 &