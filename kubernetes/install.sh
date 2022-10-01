#!/bin/bash

kubectl apply -f namespace.yml
kubectl apply -f service-account.yml

kubectl apply -f mongo.yml
kubectl apply -f redis.yml

kubectl apply -f message-service.yml
kubectl apply -f hello-service.yml
kubectl apply -f ui-interface.yml
