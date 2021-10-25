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

# Entities
el mapeo de valores de dinero lo realizo contra Objetos BigDecimal en java, usar Double podria traer errores de presicion dado que los calculos se realizan sobre datos binarios.

# Archivos
Los archivos subidos no se cargan pero su contenido quedara registrado en el log de la aplicacion.

# estrategia de procesamiento de archivos
Se lee cada linea y una validacion con expresiones regular indicará si es una etiqueta de mesa o un filtro



# Logica de negocio
Los filtros TC y UG los ejecuta la BD.
Los filtros de RI y RF los ejecuta la app.

# Proceso por mesa
1. obtencion de candidatos inicial:
- se obtienen los posibles candidatos es decir todos y a partir de aqui se excluyen segun corresponda.
- Se excluyen aquellos que ya han sido confirmados en otras mesas.
2. se aplica los filtros excluyentes:
- En caso de tener filtro de TC se excluye los que no cumplan este criterio.
- En caso de tener filtro de UG se excluye los que no cumplan este criterio.
- En caso de tener filtro de RI se excluye los que no cumplan este criterio.
- En caso de tener filtro de RF se excluye los que no cumplan este criterio.
3. descifrar codigos
- Si alguno de los candidatos tiene codigo cifrado, se descifra
