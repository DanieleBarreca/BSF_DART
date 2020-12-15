#!/usr/bin/env bash
echo "Creating mongo users..."

echo "db.dropDatabase();db.createUser( { user: '${DART_MONGO_USER}', pwd: '${DART_MONGO_PWD}', roles: ['readWrite'] } ) " | 
mongo --host localhost -u "${MONGO_INITDB_ROOT_USERNAME}" -p "${MONGO_INITDB_ROOT_PASSWORD}" --authenticationDatabase "${DART_MONGO_ADMIN_DB}" dart 

echo "MONGO DB USER ${DART_MONGO_USER} CREATED"
