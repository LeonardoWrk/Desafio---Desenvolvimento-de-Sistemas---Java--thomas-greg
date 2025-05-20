#!/bin/bash

/opt/mssql/bin/sqlservr &

echo "Aguardando o SQL Server iniciar..."
for i in {1..30}; do
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'SenhaThomas123!' -Q "SELECT 1" &> /dev/null
    if [ $? -eq 0 ]; then
        echo "SQL Server está pronto!"
        break
    fi
    sleep 2
done

if [ -f /init-sql/init.sql ]; then
    echo "Executando init.sql..."
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'SenhaThomas123!' -i /init-sql/init.sql
else
    echo "init.sql não encontrado."
fi

wait