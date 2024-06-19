#!/bin/bash

# Turn on bash's job control
#set -m

# Start primary process (Javascript webserver) and put it in the background
# node server/server.js &

# Start the second process (ReactJS front screens) and put it in the background
# npm start &
# Bring the primary process back into the foreground and leave it there
# fg %1

npm start


