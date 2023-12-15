#!/bin/bash

/startpulse.sh &
ln -s -r /var/tmp/wrongsecrets /config/Desktop/wrongsecrets
ln -s /var/tmp/wrongsecrets/welcome.md /config/Desktop/welcome.md
sudo chown abc /config/.config/pulse
/usr/bin/startxfce4 > /dev/null 2>&1
