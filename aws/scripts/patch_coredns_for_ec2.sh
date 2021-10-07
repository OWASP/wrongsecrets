#!/bin/bash
# This create script must return a json as an output. Therefore, we suspend the kubectl output and return a static json.
# See: https://registry.terraform.io/providers/scottwinkler/shell/latest/docs/resources/shell_script_resource
kubectl patch deployment coredns \
      --namespace kube-system \
      --type=merge \
      -p '{"spec": {"template": {"metadata": {"annotations": {"eks.amazonaws.com/compute-type": "ec2"}}}}}' \
      --kubeconfig <(echo $KUBECTL_CONFIG | base64 --decode) &>/dev/null
echo {\"result\": \"coredns compute type switched back to ec2\"}