#!/usr/bin/env bash

script_dir=$(dirname $0)
cd ${script_dir}

for container in dart-server dart-mongo dart-mysql;do
	if [[ ! -z $(docker container ps |grep ${container}) ]];then
		 if [[ ${container} == 'dart-server' ]];then
    		echo "SHUTTING DOWN SERVER"
      		docker exec -it ${container} './bin/jboss-cli.sh' '--connect' 'command=:shutdown'
      	fi
	
		echo "STOPPING CONTAINER ${container}"
		docker container stop ${container}
	fi
	
	if [[ ! -z $(docker container ps -a |grep ${container}) ]];then
		echo "REMOVING CONTAINER ${container}"
		docker container rm ${container}
	fi
done