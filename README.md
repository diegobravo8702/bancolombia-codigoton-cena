# Bancolombia-codigoton-cena
# Comenzamos pues !


# Preparación del entorno

La base de datos se monto con docker. Uso una imagen de mariaDB.

las credenciales se ingresan en el archivo variables.env

Se corre el script bd.sql que se encuentra en la carpeta especificacion 

para levantar la BD basta con un 

```sh
$ docker-compose up -d
```


# definicion de arquitectura

La aplicacion se podria exponer en cloud como api.

GCloud
SpringBoot
OpenJDK 8
Maven
MariaDB

# Ejecución
Se debe correr el proyecto con maven. 
Datos de conexión a la bd: modificarlos en el archivo 
#### src/main/resources/application.properties
# URL Api 
la api se encuentra versionada. Buena practica en caso de sufrir cambios futuros
POST
```sh
http://{{HOST}}:{{PORT}}/codigoton/api/v1/organizacion
```

# Entities
el mapeo de valores de dinero lo realizo contra Objetos BigDecimal en java, usar Double podria traer errores de presicion dado que los calculos se realizan sobre datos binarios.

#### TOFIX:
crear mappers para no trabajar con entities

# Archivos
Los archivos subidos no se cargan pero su contenido quedara registrado en el log de la aplicación.

# estrategia de procesamiento de archivos
Se lee cada linea y una validación con expresiones regular indicará si es una etiqueta de mesa o un filtro



# Logica de negocio
Los filtros TC y UG los ejecuta la BD.
Los filtros de RI y RF los ejecuta la app.

### Proceso por mesa
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
4. Ordenamientos
- Se ordena los candidatos, primero por codigo.
- Se ordena los candidatos, despues por monto. quedando este como el orden principal.
5. Seleccion por sexo:
-- Si aun hay lugar para alguien de su sexo.
-- Se confirma al candidato como invitado.
-- Se excluye de la lista de candidatos.
-- Se excluye a todos los demas candidatos que pertenezcan a su misma empresa.

6. al finalizar la mesa se evalua la cantidad de hombres y mujeres, en caso de haber diferencias, se elimina la cantidad excedente del genero correspondiente.

# MIS DUDAS O TEMAS PENDIENTES
## [ACLARADA] El ordenamiento por monto de balance y cuenta. ¿las dos son de mayor a menor?
Yo puse que los dos casos son de mayor a menor
####Aclaración: Solicité aclaracion por correo a la organización y me dicen que el ordenamiento de mayor a menor aplica solamente para la suma del monto, el orden de codigo se realizara de menor a mayor. Esto impacta el orden de los invitados de la mesa 5. Ya he actualizado mis resultados.

## ¿Los valores de rangos eran inclusivos o exclusivos?
puse que fueran exclusivos
## Si una persona ya fue invitada a una mesa, ¿no debe ser tenida en cuenta para otras mesas?
lo manejé excluyendo a los candidatos que ya fueron confirmados en mesas anteriores.



### MIS RESULTADOS

``` 
<General>
C10039,C10129,C10105,C10126,C10091,C10051
<Mesa 1>
C10078,C10086,C10025,C10089,C10190,C10191,C10043,C10104
<Mesa 2>
C10144,C10070,C10076,C10134,C10151,C10090
<Mesa 3>
CANCELADA
<Mesa 4>
CANCELADA
<Mesa 5>
C10201,C10202,C10203,C10204,C10205,C10206,C10207,C10208
<Mesa 6>
C10186,C10116,C10088,C10169,C10209,C10178
```

Muchas cosas por mejorar.
### Que buena experiencia.
Saludos.
