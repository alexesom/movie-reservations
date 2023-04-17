Set-Variable -Name "INTERFACE" -Value "localhost"
Set-Variable -Name "PORT" -Value "8000"
Set-Variable -Name "PG_HOST" -Value "postgres"
Set-Variable -Name "PG_DB" -Value "cinema"
Set-Variable -Name "PG_USER" -Value "postgres"
Set-Variable -Name "PG_PASSWORD" -Value "admin"
Set-Variable -Name "PG_PORT" -Value "5432"

$env:INTERFACE = $INTERFACE
$env:PORT = $PORT
$env:PG_HOST = $PG_HOST
$env:PG_DB = $PG_DB
$env:PG_USER = $PG_USER
$env:PG_PASSWORD = $PG_PASSWORD
$env:PG_PORT = $PG_PORT

docker-compose.exe build
docker-compose.exe up