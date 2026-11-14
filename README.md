# Cheap Travel - Cazador y Planificador de Vuelos Baratos

**Cheap Travel** es una aplicación nativa de Android desarrollada con **Kotlin**, **Jetpack Compose** y **Material Design 3** (estética Dark Mode Neón), diseñada para encontrar vuelos económicos en aerolíneas mexicanas e internacionales (**Volaris**, **Viva Aerobus**, **Aeroméxico** y **Mexicana**) con cálculo real de **TUA**, calendario mensual interactivo de 90 días, geolocalización GPS, alertas persistentes en **Room** y enlaces verificados en vivo sin errores 404.

---

## Descarga y Release (APK Instalable)

- **Archivo APK:** [`release/cheap-travel-v1.1.apk`](release/cheap-travel-v1.1.apk)
- **Versión:** 1.1

---

## Características Principales

1. **Selección Multi-Aeropuerto y Chips Removibles:**
   - Selector superior para múltiples orígenes y múltiples destinos (o *"Cualquier destino"*).
   - Chips activos siempre visibles con botón de cierre rápido.
   - Detección automática del aeropuerto mexicano más cercano mediante `LocationManager` y cálculo de distancia **Haversine**.
2. **Ofertas en Vivo (Radar Flash):**
   - Sincronización directa de tarifas reales desde **Volaris.com** y **VivaAerobus.com** (`__NEXT_DATA__` / histogramas diarios de 30 días y tarifas promocionales).
   - Filtros rápidos (`Todas`, `En Vivo`, `Playas`, `Nacionales`, `Internacionales`, `Solo Volaris`, `Solo Viva Aerobus`), slider de tarifa base máxima y ordenamiento por **Precio** o **Fecha** (agrupando competencia directa cabeza a cabeza).
3. **Buscador Flexible Multi-Ruta:**
   - Búsqueda simultánea multi-origen / multi-destino por presupuesto total (Base + TUA), vuelos directos y aerolínea.
4. **Calendario Mensual y Plan de Viaje (Solo Ida / Viaje Redondo):**
   - Cuadrícula mensual interactiva (`LUN`–`DOM`) con tarifa mínima diaria, fechas en vivo destacadas, indicadores por aerolínea y flujo guiado en 2 pasos (`1. Salida` y `2. Regreso`), además de vista cronológica en lista e inversión rápida de ruta.
   - Ordenamiento inteligente (`Más Barato`, `Más Próximo`, `Recomendado (Balance)`) y filtros de horario, aerolínea y vuelos directos.
   - Tarjeta integrativa **TripPlanSummaryCard** con soporte **Smart Mix** (reserva directa por tramo o copiar plan al portapapeles).
5. **Alertas Locales (Room + Notificaciones Push):**
   - Creación, activación/desactivación y eliminación de alertas de precio guardadas en **Room Database** (`FlightAlert`, `FlightAlertDao`).
   - Escaneo de ofertas coincidentes y notificaciones locales de alta prioridad (`POST_NOTIFICATIONS` en Android 13+).
6. **Calculadora TUA y Enlaces Directos a Aerolínea (Sin 404):**
   - Desglose transparente de Adultos, Niños (-10% tarifa base), Bebés en brazos (exentos de TUA), paquetes de equipaje (`Zero`, `Básica 10kg`, `Plus 25kg`), asientos y ahorro de TUA (AIFA `NLU` $309 MXN vs AICM `MEX` $570 MXN).
   - Enlace principal directo a la página oficial de la aerolínea (ruta verificada en **Volaris.com** o buscador oficial de **Viva Aerobus**, **Aeroméxico** y **Mexicana**) con guía paso a paso Solo Ida y menú desplegable *"Ver otras opciones"* (**Kayak México**, **Google Flights** y copia de itinerario).
