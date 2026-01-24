#!/bin/bash

# Build
sudo docker build -t webreport:v1 .

# Setup X11
xhost +local:root
sudo docker rm -f webreport-instance 2>/dev/null

# Run with persistent volumes
sudo docker run -it \
  --env="DISPLAY" \
  --env="QT_X11_NO_MITSHM=1" \
  --volume="/tmp/.X11-unix:/tmp/.X11-unix:rw" \
  --volume="$HOME/.sbt:/root/.sbt" \
  --volume="$HOME/.cache/coursier:/root/.cache/coursier" \
  --volume="$(pwd)/target:/app/target" \
  --name webreport-instance \
  webreport:v1
