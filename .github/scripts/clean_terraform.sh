#!/bin/bash
# Usage: source this script with source ./clean_terraform.sh and run clean_terraform
function clean_terraform {
  echo "Finding and removing terraform artifacts and cache..."
  for filename in $(
    find . -name '.terraform*' \
      ! -path "./prod/*" ! -path "./test/*" \
      ! -path "./dev/*" ! -path "./uat/*"
  ); do
    echo "removing: " $filename"..."
    rm -rf $filename
  done
  echo "Done!"
}

# Uncomment if you want to run this script directly instead of sourcing
# clean_terraform
