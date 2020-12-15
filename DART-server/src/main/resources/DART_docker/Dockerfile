FROM openjdk:8
USER root
LABEL version="${dart.docker.name}"
ENV JBOSS_HOME /opt/jboss/wildfly

RUN apt-get update && apt-get install -y default-mysql-client

# Build DART
COPY wildfly_custom_17 $JBOSS_HOME
RUN chmod +x $JBOSS_HOME/bin/dart_loader/loader.sh

COPY dart-entry-point.sh $JBOSS_HOME/.

COPY wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh

#####
# DEFAULT MONGO and MYSQL settings
# Can be overwritten by setting the environment variables
# in docker-compose
ENV DART_WILDFLY_ADMIN_USER ${dart.wildfly.admin.user}
ENV DART_WILDFLY_ADMIN_PWD ${dart.wildfly.admin.password}

ENV DART_MONGO_USER ${dart.mongo.user}
ENV DART_MONGO_PWD ${dart.mongo.password}
ENV DART_MONGO_SERVER ${dart.mongo.server}
ENV DART_MONGO_DB ${dart.mongo.db}
ENV DART_MONGO_QUERY_LIMIT ${dart.mongo.query_limit}

ENV DART_SQL_USER ${dart.sql.user}
ENV DART_SQL_PWD ${dart.sql.password}
ENV DART_SQL_SERVER ${dart.sql.server}
ENV DART_SQL_DB ${dart.sql.db}
ENV DART_SQL_PARAMS ${dart.sql.params}

ENV DART_LOADER_USER ${dart.application.user}
ENV DART_LOADER_PASSWORD ${dart.application.user.password}
ENV DART_LOADER_GROUP ${dart.application.user.group}

WORKDIR $JBOSS_HOME

ENTRYPOINT ["/bin/bash", "/opt/jboss/wildfly/dart-entry-point.sh"]