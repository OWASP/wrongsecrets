#!/bin/bash

# Detect the architecture
ARCH=$(uname -m)

# Check if the system is running on Linux ARM
if [[ "$ARCH" == "arm" || "$ARCH" == "aarch64" ]]; then
    echo "Running on Linux ARM architecture"
    # Start the ARM binary
    ./home/wrongsecrets/wrongsecrets-challenge52-c-linux-arm
else
    echo "Running on non-ARM architecture"
    # Start the non-ARM binary
    ./home/wrongsecrets/wrongsecrets-challenge52-c-linux
fi
