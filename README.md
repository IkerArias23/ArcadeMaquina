# 🎮 Máquina Arcade de Lógica

Una aplicación Java que implementa tres clásicos problemas de lógica y algoritmos en una interfaz arcade retro:

- **♟️ N Reinas**: Coloca N reinas en un tablero de ajedrez sin que se amenacen entre sí.
- **🐴 Recorrido del Caballo**: Recorre todo el tablero de ajedrez con un caballo, pasando una sola vez por cada casilla.
- **🗼 Torres de Hanoi**: Mueve una pila de discos de una torre a otra respetando las reglas del juego.



## ✨ Características

- 🖥️ Interfaz gráfica con estilo retro arcade implementada con JavaFX
- 🧠 Resolución automática de los problemas con algoritmos de backtracking
- 🚶‍♂️ Opción de resolución paso a paso para fines educativos
- 💾 Historial de partidas guardado en base de datos H2
- 🏗️ Diseño orientado a objetos con implementación de patrones de diseño

## 📋 Requisitos

- ☕ Java 23 o superior
- 🎨 JavaFX 23.0.2
- 🔨 Maven para gestión de dependencias

## 🚀 Instalación

1. Clona el repositorio:

git clone https://github.com/yourusername/MaquinaArcade.git

2. Navega al directorio del proyecto:

cd MaquinaArcade

3. Compila el proyecto con Maven:

mvn clean install

4. Ejecuta la aplicación:

mvn javafx

## 📁 Estructura del Proyecto

El proyecto sigue una arquitectura MVC (Modelo-Vista-Controlador):

- **📊 Modelo**: Implementación de los algoritmos de resolución de cada problema
- `com.arcade.model.game`: Lógica de juegos
- `com.arcade.model.entity`: Entidades para persistencia de datos
- `com.arcade.model.dao`: Acceso a la base de datos

- **🎮 Vista**: Interfaz gráfica implementada con JavaFX
- `com.arcade.view`: Componentes de la interfaz gráfica
- `resources/fxml`: Archivos FXML para definir las vistas
- `resources/css`: Estilos CSS para la interfaz gráfica

- **🔄 Controlador**: Manejo de eventos y comunicación entre vista y modelo
- `com.arcade.controller`: Controladores para las vistas
- `com.arcade.service`: Servicios para operaciones de negocio

## 🎯 Descripción de los Juegos

### ♟️ Problema de las N Reinas

Consiste en colocar N reinas en un tablero de ajedrez de NxN casillas, de forma que ninguna reina amenace a otra. Una reina amenaza a cualquier pieza que esté en su misma fila, columna o diagonal.

- 🧠 Implementación: Algoritmo de backtracking
- ⚙️ Características: Tamaño de tablero configurable de 4x4 a 12x12

### 🐴 Recorrido del Caballo

Consiste en encontrar una secuencia de movimientos de un caballo de ajedrez, de forma que visite cada casilla del tablero exactamente una vez. El caballo se mueve en forma de L (dos casillas en una dirección y luego una casilla en dirección perpendicular).

- 🧠 Implementación: Algoritmo de backtracking con la heurística de Warnsdorff
- ⚙️ Características: Tamaño de tablero configurable de 5x5 a 8x8, posición inicial seleccionable

### 🗼 Torres de Hanoi

Consiste en mover una pila de discos desde una torre origen a una torre destino, utilizando una torre auxiliar. Sólo se puede mover un disco a la vez y nunca se puede colocar un disco más grande sobre uno más pequeño.

- 🧠 Implementación: Solución recursiva
- ⚙️ Características: Número de discos configurable de 3 a 10

## 🏗️ Patrones de Diseño Implementados

- **🔒 Singleton**: Para clases como `HibernateConfig`, `AppConfig`, `GameRegistry`
- **🏭 Factory Method**: Para crear instancias de juegos con `GameFactory`
- **🧩 Strategy**: Diferentes algoritmos de resolución para cada juego
- **🏛️ MVC**: Separación de responsabilidades entre modelo, vista y controlador
- **💾 DAO**: Acceso a datos con `GameDAO` y `RecordRepository`
- **📚 Repository**: Para gestionar el acceso a los registros de juegos
- **🔍 Facade**: `GameService` proporciona una interfaz unificada para operaciones de juego
- **🎮 Command**: Para controlar los pasos de la resolución de juegos

## 🗃️ Base de Datos

La aplicación utiliza una base de datos H2 embebida para almacenar el historial de partidas:

- 📝 Registros de partidas con información sobre tiempo, pasos, completitud
- 🧩 Información específica para cada tipo de juego
- 📊 Consultas para estadísticas de juego

## 📜 Licencia

[MIT License](LICENSE)

## 📬 Contacto

Para cualquier pregunta o sugerencia, no dudes en abrir un issue en el repositorio.
