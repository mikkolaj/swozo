#!/bin/bash

public_ip="$(dig +short myip.opendns.com @resolver1.opendns.com)"
echo "HOST=$public_ip" >> /home/swozo/.sozisel/.jitsi_env
