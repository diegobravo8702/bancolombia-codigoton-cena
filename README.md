# bancolombia-codigoton-cena
# comenzamos pues


# preparacion del entorno

La base de datos se monto con docker. Uso una imagen de mariaDB.

las credenciales se ingresan en el archivo variables.env

Se corre el script bd.sql que se encuentra en la carpeta especificacion 

para levantar el proyecto basta con un docker-compose up -d

# definicion de arquitectura

La aplicacion se expondra en cloud como api.

GCloud
SpringBoot
OpenJDK 8
Maven
MariaDB
Swagger

# URL Api 
la api se encuentra versionada. Buena practica en caso de sufrir cambios futuros
POST
http://{{HOST}}:{{PORT}}/codigoton/api/v1/organizacion



