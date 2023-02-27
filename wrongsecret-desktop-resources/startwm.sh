#!/bin/bash

/startpulse.sh &
cp -r /var/tmp/wrongsecrets /config/Desktop/wrongsecrets
cp -r /var/tmp/wrongsecrets/welcome.md /config/Desktop
sudo chown abc /config/.config/pulse
/usr/bin/startxfce4 > /dev/null 2>&1
