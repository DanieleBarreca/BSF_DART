#!/usr/bin/env bash

echo "Add/update admin user ..."

sed -i '$ d' $JBOSS_HOME/standalone/configuration/mgmt-users.properties 
$JBOSS_HOME/bin/add-user.sh "${DART_WILDFLY_ADMIN_USER}" "${DART_WILDFLY_ADMIN_PWD}" --silent

until mysql -h $(echo ${DART_SQL_SERVER}|cut -d: -f1) -P $(echo ${DART_SQL_SERVER}|cut -d: -f2) -u $DART_SQL_USER -p$DART_SQL_PWD $DART_SQL_DB -e '\q';do 
	sleep 1
done

wait-for-it.sh ${DART_MONGO_SERVER} -t 0

echo "Start server ..."
$JBOSS_HOME/bin/standalone.sh -c standalone.xml -b 0.0.0.0 -bmanagement 0.0.0.0
