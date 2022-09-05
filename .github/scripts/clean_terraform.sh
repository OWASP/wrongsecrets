#!/bin/bash
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
clean_terraform
