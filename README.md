# informante-ejercicio-meli

El servicio puede responder a 4 consultas.

1- Obtener información del país según una IP

GET /informante/ip/<IP-A-Consultar>
Modelo de respuesta:

```text
{
	ip: "5.6.7.8",
	currentDate: "Mon Apr 20 00:06:55 ART 2020",
	country: "Germany",
	isoCodes: ["de"],
	languages: ["German"],
	currencies: ["EUR"],
	currenciesRatesInUSD: {
		EUR: 0.9208103131
	},
	timeZones: ["UTC+01:00"],
	datesWithTimeZone: [{UTC+01:00: "20/04/20 02:25:33"}],
	estimatedDistanceFromReferencePointInKM: 11566.193747789961,
	coordinates: [[-34.6131516, -58.3772316],[51, 9]]
}
```

2- Obtener consulta de IP a país mas cercano a Buenos Aires

GET /informante/invocations/closest
Modelo de respuesta:

```text
{
    countryName: "Germany",
    distance: 11566.193747789961,
    invocations: 1
}
```
3- Obtener consulta de IP a país mas cercano a Buenos Aires

GET /informante/invocations/farthest
Modelo de respuesta:

```text
{
    countryName: "Japan",
    distance: 18522.768377733297,
    invocations: 1
}
```
4- Obtener un promedio de la distancia de las consultas hechas hasta el momento

GET /informante/invocations/average-distance
Modelo de respuesta:

```text
{
averageDistance: 11566.193747789961
}
```

Faltantes:

-Persistencia:
    
   No llegue a hacer que se persistan los datos, pero de haber llegado estaba pensando en utilizar una base de datos relacional,
   dado los requerimientos pienso que con una base SQL voy a poder cumplir con los requisitos pedidos. Según entendí,
   la pegada que más tráfico iba a recibir es la de la consulta del promedio de la distancia, por ende quiero asegurarme 
   poder asegurar Availability, pero perdiendo consistencia. 
    
   La estrategia que elegí para retornar ese valor fue mantener en memoria una variable que me representa el último 
   promedio calculado, ese valor es actualizado cada cierto tiempo por un proceso que corre cada cierto tiempo configurado. 
   Si bien no soy consistente, puedo asegurar poder responder a los pedidos.
   
   La tabla que iba a persistir consistía en tres campos: 
   
   _Nombre de país,
   
   _Distancia de ese a un punto de referencia (Buenos Aires )
   en este caso, 
   
   _Y un acumulador que representa a las pegadas hechas hasta el momento
    
   Si bien estoy persistiendo valores que se pueden calcular, es por una razón. El calculo de la distancia es un proceso
   complejo, que no varia si lo calculo por un mismo país, por ende, prefiero calcular ese valor una vez y consultarlo cada
   vez que necesite retornar esa distancia y después consultar el valor persistido.
   
   El acumulador lo hice para evitar tener un registro por cada pegada que me hacen, haciendo así que el calculo del total
   por país pueda tornarse más pesado, ya que tendría que traerme todos los registros de un país solo para acumular los
   registros que existen. Teniendo un acumulador solamente me traigo un registro por cada consulta, el del país que necesito.
   
   Por otro lado había pensado tener Snapshots de algunas pegadas a los servicios externos, ya que son datos que es difícil
   que cambien minuto a minuto, el código de un país o la información de su lengua o moneda es difícil que varíe. Si bien
   también hice un pseudo snapshot con el valor de las monedas, la idea seria actualizar esos valores cada cierto tiempo,
   una hora por ejemplo. No llegue a hacer una implementación de un Snapshot pero seria bueno tenerlo para evitar hacer
   muchas consultas contra servicios externos, evitando así ser 100% dependientes del estado de los mismos.
   
-Testing
    No realice los tests unitarios debido a que no me alcanzo el tiempo, es una parte vital del desarrollo pero dado el tiempo
    que tenia, preferí cumplir con los puntos pedidos

Hay varios puntos que realice suposiciones y desarrolle en base a ellas, como por ejemplo la cantidad de pegadas que podría
llegar a recibir el resto de los servicios. Por ende pensé que el core del servicio que doy son las consultas y no tanto
las pegadas que se hacen, si hubiese sido el core la persistencia de datos hubiese utilizado un Cassandra para persistir por ejemplo.
   



