FROM mcr.microsoft.com/mssql/server:2017-latest

USER root

COPY init-sql/entrypoint.sh /entrypoint.sh
COPY init-sql/init.sql /init-sql/init.sql

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/bin/bash", "/entrypoint.sh"]