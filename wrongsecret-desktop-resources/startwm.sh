#!/bin/bash

/startpulse.sh &
cp -r /var/tmp/wrongsecrets /config/Desktop
/usr/bin/startxfce4 > /dev/null 2>&1
