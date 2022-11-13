#!/bin/bash

public_ip="$(curl ifconfig.co/)"
echo "HOST=$public_ip" >> /home/swozo/.sozisel/.jitsi_env
