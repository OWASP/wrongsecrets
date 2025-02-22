#!/bin/bash

#Upload the docker image for debugging
docker login
#Replace pastekitoo by your username to build a docker image
docker build -f Dockerfile.debug -t pastekitoo/debug-container:latest .
#Same to push the docker image
docker push pastekitoo/debug-container:latest
#This is already done you can use my docker image

#Same to launch a debug container
kubectl debug -it $(kubectl get pod -l app=secret-challenge -o jsonpath="{.items[0].metadata.name}") --image=pastekitoo/debug-container:latest   --target=secret-challenge   --profile=restricted   -- /bin/bash
#You can now find the pid of the process thanks to ps aux
ps aux | grep secret
#You can now use gdb on the process launched for challenge52
gdb -p <PID>
#Find the variable's memory address
p &secret
#Use this address to find the secret
x/s 0x5889bda7a040 #for example
