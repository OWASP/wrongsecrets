#!/bin/bash
export DOTNET_ROOT=/etc/dotnet
export DOTNET_INSTALL_DIR="/etc/dotnet"
export PATH="$PATH:$DOTNET_ROOT:$DOTNET_ROOT/tools"
export PATH="$PATH:/config/.dotnet/tools"

# Default settings
if [ ! -d "${HOME}"/.config/xfce4/xfconf/xfce-perchannel-xml ]; then
  mkdir -p "${HOME}"/.config/xfce4/xfconf/xfce-perchannel-xml
  cp /defaults/xfce/* "${HOME}"/.config/xfce4/xfconf/xfce-perchannel-xml/
fi

sudo ln -s -r /var/tmp/wrongsecrets /config/Desktop/wrongsecrets
sudo ln -s /var/tmp/wrongsecrets/welcome.md /config/Desktop/welcome.md

# Start DE
exec dbus-launch --exit-with-session /usr/bin/xfce4-session > /dev/null 2>&1
