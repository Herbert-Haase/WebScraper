#!/bin/bash

# 1. Build (Docker will now use cache for 'sbt update')
sudo docker build -t webreport:v1 .

# 2. Setup X11
xhost +local:root
sudo docker rm -f webreport-instance 2>/dev/null

# 3. Run with PERSISTENT VOLUMES
# We map your local SBT and Coursier caches so the container doesn't re-download.
# We also map the 'target' folder so it doesn't re-compile everything.
sudo docker run -it \
  --env="DISPLAY" \
  --env="QT_X11_NO_MITSHM=1" \
  --volume="/tmp/.X11-unix:/tmp/.X11-unix:rw" \
  --volume="$HOME/.sbt:/root/.sbt" \
  --volume="$HOME/.cache/coursier:/root/.cache/coursier" \
  --volume="$(pwd)/target:/app/target" \
  --name webreport-instance \
  webreport:v1
