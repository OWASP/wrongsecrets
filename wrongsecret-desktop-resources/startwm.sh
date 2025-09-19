#!/bin/bash

export DOTNET_ROOT=/etc/dotnet
export DOTNET_INSTALL_DIR="/etc/dotnet"
export PATH="$PATH:$DOTNET_ROOT:$DOTNET_ROOT/tools"
export PATH="$PATH:/config/.dotnet/tools"
/startpulse.sh &
ln -s -r /var/tmp/wrongsecrets /config/Desktop/wrongsecrets
ln -s /var/tmp/wrongsecrets/welcome.md /config/Desktop/welcome.md
sudo chown abc /config/.config/pulse
/usr/bin/startxfce4 > /dev/null 2>&1
