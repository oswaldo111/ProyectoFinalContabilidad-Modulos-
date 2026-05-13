# Sistema Contable Integral Multi-Empresa 🚀

Sistema de gestión contable robusto diseñado bajo una arquitectura modular, permitiendo la administración independiente de múltiples empresas en una sola plataforma.

## 🛠️ Stack Tecnológico
* [cite_start]**Lenguaje:** Java 17 (LTS)[cite: 4].
* [cite_start]**Interfaz Gráfica:** Java Swing[cite: 7].
* [cite_start]**Persistencia de Datos:** Hibernate / JDBC[cite: 6].
* **Base de Datos:** Supabase (PostgreSQL).

## 🏗️ Arquitectura y Patrones
[cite_start]El proyecto utiliza un enfoque de **Monolito Modular** para facilitar el trabajo simultáneo de 18 colaboradores, minimizando conflictos en Git[cite: 5, 8, 10].

### Patrones de Diseño Aplicados:
* [cite_start]**MVC (Modelo-Vista-Controlador):** Separación estricta entre la interfaz, la lógica y los datos[cite: 14].
* [cite_start]**DAO (Data Access Object):** Centralización de la persistencia para facilitar el mantenimiento[cite: 15].
* [cite_start]**Singleton:** Aplicado en el núcleo (`core`) para gestionar la conexión a la base de datos y la sesión del usuario de forma global[cite: 16].

## 📂 Estructura del Proyecto
[cite_start]El código se organiza por módulos funcionales (Package by Feature)[cite: 5]. [cite_start]Cada grupo es dueño de su lógica dentro de su respectivo paquete[cite: 12].

```text
src/main/java/com/sistema/contable/
[cite_start]├── core/                # Núcleo: Conexión DB, Sesión y Seguridad [cite: 25]
[cite_start]├── modules/             # Funcionalidades divididas por Grupos [cite: 25]
[cite_start]│   ├── compras/         # Grupo 1 [cite: 27]
[cite_start]│   ├── ventas/          # Grupo 2 [cite: 29]
[cite_start]│   ├── inventarios/     # Grupo 3 [cite: 31]
[cite_start]│   ├── bancos/          # Grupo 4 [cite: 33]
[cite_start]│   ├── rrhh/            # Grupo 5 [cite: 35]
[cite_start]│   ├── fiscal/          # Grupo 6 [cite: 37]
[cite_start]│   └── contabilidad/    # Grupo 7 [cite: 42]
[cite_start]├── ui/                  # Componentes de UI comunes y Dashboard [cite: 43]
[cite_start]└── Main.java            # Punto de entrada de la aplicación [cite: 44]
