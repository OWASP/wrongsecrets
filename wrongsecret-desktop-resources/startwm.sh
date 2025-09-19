#!/bin/bash

export DOTNET_ROOT=/usr/share/dotnet
export DOTNET_INSTALL_DIR="/usr/share/dotnet"
export PATH="$PATH:$DOTNET_ROOT:/root/.dotnet/tools"
export PATH="$PATH:/config/.dotnet/tools"
/startpulse.sh &
ln -s -r /var/tmp/wrongsecrets /config/Desktop/wrongsecrets
ln -s /var/tmp/wrongsecrets/welcome.md /config/Desktop/welcome.md
sudo chown abc /config/.config/pulse
/usr/bin/startxfce4 > /dev/null 2>&1
