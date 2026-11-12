# Cheap Travel - Detector de Vuelos Baratos y Alertas

**Cheap Travel** es una aplicación móvil nativa para Android desarrollada con **Kotlin**, **Jetpack Compose** y **Material Design 3**, diseñada para cazar las tarifas aéreas más económicas en aerolíneas mexicanas e internacionales (Volaris, Viva Aerobus, Aeroméxico y Mexicana).

Calcula el costo total real incluyendo la **TUA (Tarifa de Uso de Aeropuerto)** específica por cada aeropuerto de salida, cargos por equipaje y selección de asientos, con multiplicador de pasajeros y un sistema de alertas push automáticas en segundo plano.

---

## 🚀 Descarga y Release (APK Instalable)

Puedes instalar directamente la versión compilada para Android:
- **Archivo APK:** [`release/cheap-travel-v1.0.apk`](release/cheap-travel-v1.0.apk)
- **Versión:** 1.0 (Build release/debug listo para instalar)

---

## ✨ Características Principales

### 1. Radar de Vuelos Flash ($199 MXN)
- Detección de promociones flash de Volaris (desde $199 MXN) y Viva Aerobus (desde $249 MXN).
- Filtros rápidos por:
  - 🏖️ **Playas**: Cancún, Tulum, Puerto Vallarta, Los Cabos, Mazatlán, Huatulco, La Paz, Cozumel.
  - 🇲🇽 **Nacionales**: CDMX (AICM y AIFA), Guadalajara, Monterrey, Tijuana, Mérida, Oaxaca, Aguascalientes, etc.
  - 🌎 **Internacionales**: Los Ángeles, Miami, Chicago, Houston, Nueva York, Las Vegas, Orlando, Dallas, San Francisco, Bogotá, Madrid.
  - Filtro por aerolínea específica y control deslizante de tarifa base máxima.

### 2. Buscador Flexible Multi-Aeropuerto
- Permite seleccionar **múltiples aeropuertos de salida simultáneos** (ej. AICM + AIFA al mismo tiempo).
- Permite seleccionar **múltiples aeropuertos de llegada** simultáneos o explorar con *"Cualquier destino"*.

### 3. Calculadora Transparente de Tarifa Total (TUA, Equipaje y Asientos)
- **TUA por Aeropuerto de Salida**: Aplica la tarifa oficial del aeropuerto de salida (destacando el ahorro del AIFA de $309 MXN vs. los $570 MXN del AICM).
- **Selector de Pasajeros**: Adultos, niños (-10% en tarifa base) y bebés en brazos (exentos de TUA).
- **Paquetes de Equipaje**:
  - *Zero*: Solo objeto personal bajo el asiento ($0).
  - *Básica*: Objeto personal + maleta de mano de 10 kg en compartimento superior.
  - *Plus*: Objeto personal + maleta de mano 10 kg + maleta documentada de 25 kg.
- **Asientos**: Asignación aleatoria sin costo, asiento estándar o espacio extra / primera fila.
- **Desglose en tiempo real**: Suma línea por línea y costo final transparente en Pesos Mexicanos (MXN).

### 4. Calendario de Precios Bajos
- Matriz mensual de fechas futuras para cualquier ruta.
- Indicadores visuales de días con tarifas de $199 MXN en verde fluorescente para planear viajes en las fechas más baratas.

### 5. Alertas de Precio en Segundo Plano con Notificaciones Push
- Permite fijar un presupuesto máximo deseado por ruta (ej. *"Avisarme si CDMX a Cancún baja de $800 MXN"*).
- Almacenamiento local persistente con **Room Database**.
- Emisión de notificaciones push nativas en Android (`POST_NOTIFICATIONS`) cuando se detectan caídas de precio.
- Botón *"Probar Notificación"* para comprobar el funcionamiento inmediato en la barra de estado.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **UI Toolkit:** Jetpack Compose + Material Design 3 (M3)
- **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern + Kotlin Flow
- **Base de Datos Local:** Android Jetpack Room con KSP
- **Notificaciones:** Android NotificationManagerCompat con canales de notificación de alta prioridad
- **Build System:** Gradle (Kotlin DSL - `.gradle.kts`)
- **Min SDK:** 24 (Android 7.0+)
- **Target SDK:** 36 (Android 15+)

---

## 📲 Compilación desde Código Fuente

Para compilar el proyecto localmente con Android Studio:

```bash
# Clonar el repositorio
git clone https://github.com/cesar0012/app-vuelos.git
cd app-vuelos

# Compilar el APK de prueba
gradle assembleDebug

# El archivo generado estará en:
# app/build/outputs/apk/debug/app-debug.apk
```
