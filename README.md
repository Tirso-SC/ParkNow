# 🚗 ParkNow

**ParkNow** es una aplicación de escritorio diseñada para facilitar la búsqueda y gestión de estacionamiento en Madrid. Permite a los usuarios autenticarse, consultar en tiempo real las plazas libres en el mapa, validar si una ubicación corresponde a una zona de estacionamiento regulado (SER) y registrar la entrada o salida de su vehículo.

---

## ✨ Funcionalidades principales

* **Autenticación de usuarios:** Sistema de inicio de sesión y registro de cuentas.
* **Mapa interactivo en tiempo real:** Visualización cartográfica (OpenStreetMap) con pines diferenciados por colores:
  * 🟢 **Libre:** Plaza disponible para estacionar.
  * 🔴 **Ocupada:** Plaza utilizada por otro conductor.
  * 🔵 **La mía:** Ubicación activa del vehículo del usuario actual.
* **Validación de zona regulada (SER):** Control de restricciones que impide aparcar fuera de los límites autorizados (si seleccionas un punto no habilitado, la app te avisa con el mensaje: *"Ahí no se puede aparcar. Elige un punto sobre una calle con plazas de aparcamiento (zona SER)"*).
* **Gestión de estancia:**
  * Selección de plaza manual por clic o mediante coordenadas (latitud/longitud).
  * Confirmación de aparcamiento con el botón **"He aparcado aquí"**.
  * Indicador del estado actual con registro de hora exacta (*"Aparcado desde las HH:mm [Activo]"*).
  * Finalización de la estancia mediante el botón **"Ya me he marchado"**.
* **Navegación rápida:** Botón para centrar la vista en el mapa de Madrid y filtro para consultar plazas por zona/barrio.

---

## 🛠️ Tecnologías utilizadas

* **Lenguaje & GUI:** Java (JavaFX / Swing)
* **Gestor de dependencias:** Apache Maven
* **Mapas:** Integración OpenStreetMap
* **CI/CD & Calidad:** GitHub Actions & Qodana


---