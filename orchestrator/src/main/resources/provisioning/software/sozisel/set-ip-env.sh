#!/bin/bash

public_ip="$(curl ifconfig.co/)"
echo "PUBLIC_URL=https://$public_ip:8443" >> /home/swozo/.sozisel/.env
