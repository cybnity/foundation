#!/bin/bash

# Turn on bash's job control
set -m

# Start primary process and put it in the background
node server/server.js &

# Start the second process and put it in the background
npm start &

# Bring the primary process back into the foreground and leave it there
fg %1
