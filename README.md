# Sistema Contable Integral Multi-Empresa 🚀

Sistema de gestión contable robusto diseñado bajo una arquitectura modular, permitiendo la administración independiente de múltiples empresas en una sola plataforma.

## 🛠️ Stack Tecnológico
**Lenguaje:** Java 17 (LTS)
**Interfaz Gráfica:** Java Swing
**Persistencia de Datos:** Hibernate / JDBC
* **Base de Datos:** Supabase (PostgreSQL).

## 🏗️ Arquitectura y Patrones
El proyecto utiliza un enfoque de **Monolito Modular** para facilitar el trabajo simultáneo de 18 colaboradores, minimizando conflictos en Git

### Patrones de Diseño Aplicados:
**MVC (Modelo-Vista-Controlador):** Separación estricta entre la interfaz, la lógica y los datos
**DAO (Data Access Object):** Centralización de la persistencia para facilitar el mantenimiento
**Singleton:** Aplicado en el núcleo (`core`) para gestionar la conexión a la base de datos y la sesión del usuario de forma global

## 📂 Estructura del Proyecto
El código se organiza por módulos funcionales (Package by Feature). Cada grupo es dueño de su lógica dentro de su respectivo paquete

```text
src/main/java/com/sistema/contable/
├── core/                # Núcleo: Conexión DB, Sesión y Seguridad 
├── modules/             # Funcionalidades divididas por Grupos 
│   ├── compras/         # Grupo 1 
│   ├── ventas/          # Grupo 2 
│   ├── inventarios/     # Grupo 3 
│   ├── bancos/          # Grupo 4 
│   ├── rrhh/            # Grupo 5 
│   ├── fiscal/          # Grupo 6 
│   └── contabilidad/    # Grupo 7
├── ui/                  # Componentes de UI comunes y Dashboard 
└── Main.java            # Punto de entrada de la aplicación 
