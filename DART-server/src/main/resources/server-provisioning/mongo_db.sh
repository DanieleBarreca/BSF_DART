#!/usr/bin/env bash

echo "db.dropDatabase();db.createUser( { user: '${dart.mongo.user}', pwd: '${dart.mongo.password}', roles: ['readWrite'] } ) " | mongo -u "${dart.mongo.admin.user}" -p "${dart.mongo.admin.password}" --authenticationDatabase "${dart.mongo.admin.db}" ${dart.mongo.server}/${dart.mongo.db} 

echo "MONGO DB USER ${dart.mongo.user} CREATED"