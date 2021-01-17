#!/bin/sh

chmod +x init-letsencrypt.sh && ./init-letsencrypt.sh
./init-letsencrypt.sh
sed -i 's/#//' docker-compose.yml
docker-compose up -d
