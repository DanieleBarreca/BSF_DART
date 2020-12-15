#!/usr/bin/env bash

script_dir=$(dirname $0)
cd ${script_dir}

if [ -z $(docker images -q ${dart.docker.name}) ];then
	echo "Loading DART image"
	docker load --input DART_image_${dart.docker.version}.tar
fi

if [ -z $(docker images -q mongo:3.6) ];then
	echo "Loading MONGO image"
	docker load --input mongodb_image_3.6.tar
fi

if [ -z $(docker images -q mysql:5.7) ];then
	echo "Loading MYSQL image"
	docker load --input mysql_image_5.7.tar
fi

source default.env

source ./stop.sh

docker-compose -f docker-compose.yml up -d dart-server