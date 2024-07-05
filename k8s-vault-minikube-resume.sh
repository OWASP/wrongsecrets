#!/bin/bash

kubectl port-forward vault-0 -n vault 8200:8200 &
kubectl port-forward \
  $(kubectl get pod -l app=secret-challenge -o jsonpath="{.items[0].metadata.name}") \
  8080:8080 \
  ;
