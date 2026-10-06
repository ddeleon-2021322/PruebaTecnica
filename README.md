# Documentación del Proyecto

### Arquitectura de Persistencia y Acceso a Datos

#### 1. Capa de Dominio / Entidades (entity)
El paquete `entity` contiene los modelos de dominio (Plain Old Java Objects - POJOs) que mapean de manera bidireccional con las tablas de la base de datos relacional.

* Modelos Principales:
    * Usuario: Gestiona las credenciales de acceso y la información del usuario. Implementa UserDetails para su integración fluida con la seguridad de Spring. Define el estado del usuario (ACTIVO, SANCIONADO)[cite: 1] y su nivel de acceso a través de los roles del sistema (ADMIN, BIBLIOTECARIO, LECTOR)[cite: 1].
    * Libro: Representa el catálogo físico de la biblioteca. Incorpora mecanismos de Bloqueo Optimista (Optimistic Locking) mediante la anotación @Version. Esto garantiza la integridad transaccional bajo condiciones de estrés extremo y alta concurrencia, previniendo condiciones de carrera (Race Conditions) al actualizar el stock disponible de un ejemplar.
    * Prestamo: Entidad transaccional que relaciona a los usuarios con los libros. Controla el ciclo de vida del flujo de préstamos mediante los estados definidos (ACTIVO, DEVUELTO, ATRASADO)[cite: 1].

* Enumeraciones (Enums):
    * Se utilizan enums (EstadoUsuario, RolUsuario, EstadoPrestamo) para tipar fuertemente las reglas de negocio en la base de datos y evitar inconsistencias en la captura de estados.

#### 2. Capa de Repositorios (repository)
El paquete `repository` actúa como la capa de acceso a datos (Data Access Layer). Utiliza las interfaces de Spring Data JPA (JpaRepository) para abstraer las operaciones CRUD y evitar la inyección manual de consultas SQL (Boilerplate).

* Abstracción y Consultas Derivadas:
    * Además de los métodos estándar de persistencia, se implementaron Derived Query Methods (creación de consultas a partir de los nombres de los métodos) altamente optimizados para resolver las reglas de negocio complejas sin sobrecargar la memoria de la aplicación.

* Validación de Reglas de Negocio en BD:
    * PrestamoRepository incluye sentencias específicas para validar automáticamente que un usuario con rol LECTOR no exceda el límite máximo de 3 préstamos activos simultáneamente[cite: 1].
    * Incluye métodos de consulta por fechas para detectar en tiempo real si un préstamo no se devuelve en la fecha pactada[cite: 1].
    * Permite la actualización automática del infractor al estado SANCIONADO al intentar un nuevo préstamo[cite: 1].
    * UsuarioRepository expone métodos de búsqueda por email (findByEmail) esenciales para la resolución del filtro de autenticación Stateless basado en tokens JWT[cite: 1].

#### 3. Aplication.properties (Conexion a base de datos) 
El apartado de aplication properties funciona como puente para conectar la aplicacion con un base de datos PostgreSQL
    * Se agregaron las credenciales y URL de conexión en application.properties. 
    * Se configuró Hibernate (ddl-auto=update) para la creación automática de tablas.
    * Se definió el dialecto de PostgreSQL para la persistencia de datos.

### Capa de Servicios y Lógica de Negocio (service)

El paquete `service` constituye el núcleo central (el "cerebro") de la aplicación. Aquí se implementan estrictamente todas las reglas operativas, se coordina el acceso a los repositorios y se garantiza la integridad de los datos a través del manejo de transacciones.

#### Componentes Principales:
* **AuthService:** Encargado de la lógica de registro y acceso. Gestiona el registro de usuarios asignando el rol predeterminado LECTOR[cite: 1], asegura el manejo adecuado de contraseñas con PasswordEncoder (BCrypt)[cite: 1] y orquesta la generación de tokens JWT tras validar las credenciales[cite: 1].
* **LibroService:** Administra el catálogo de la biblioteca, encapsulando las operaciones CRUD y controlando las modificaciones sobre el inventario físico (stock total y disponible).
* **PrestamoService:** Concentra las operaciones más críticas del sistema. Implementa un estricto control transaccional (@Transactional) en el flujo de préstamo y devolución[cite: 1] para garantizar que los datos no se corrompan bajo estrés extremo o alta concurrencia.

#### Reglas de Negocio Implementadas:
Esta capa valida en tiempo real las restricciones de la biblioteca antes de persistir cualquier cambio en la base de datos:
* Valida que un libro no pueda prestarse si su número de ejemplares disponibles es 0[cite: 1].
* Garantiza que un usuario con rol LECTOR no pueda tener más de 3 préstamos activos simultáneamente[cite: 1].
* Calcula automáticamente el plazo de entrega de 14 días para cada préstamo[cite: 1].
* Verifica el historial del usuario y, si no devuelve un libro en la fecha pactada, lo pasa a estado SANCIONADO automáticamente al intentar un nuevo préstamo[cite: 1].

#### Manejo de Excepciones:
Los servicios están diseñados para interrumpir su ejecución de manera limpia y segura si se rompe alguna regla. En lugar de retornar respuestas HTTP directamente, lanzan excepciones personalizadas (ej. ResourceNotFoundException, BusinessRuleException)[cite: 1], delegando la responsabilidad de formatear el error al `ControllerAdvice`.