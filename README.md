
# BaseServer

Este proyecto es la base de otros microservicios REAKTOR, como [FirebaseServer](https://github.com/IESJandula/Reaktor_FirebaseServer), [PrintersServer](https://github.com/IESJandula/Reaktor_PrintersServer) y [PrintersClient](https://github.com/IESJandula/Reaktor_PrintersClient). Contiene utilidades genéricas que se utilizan en los proyectos anteriores.

## Descripción de los Servicios

El `BaseServer` incluye varios servicios clave que proporcionan funcionalidades comunes para otros microservicios:

### AuthorizationService
Este servicio se encarga de la autenticación y autorización de usuarios en el sistema. Gestiona los tokens JWT de acceso y las verificaciones de permisos de usuario basándose en los roles definidos (PROFESOR, DIRECCIÓN, ADMINISTRADOR). También maneja las validaciones y la renovación de dichos tokens JWT, asegurando que solo los usuarios autorizados accedan a los recursos protegidos.

### SessionStorageService
`SessionStorageService` gestiona el almacenamiento temporal de las sesiones de usuario en el servidor. Permite guardar y recuperar información relevante para la sesión de un usuario mientras está conectado, como identificadores de sesión, tiempo de expiración, y datos específicos de usuario.

### JarUpdateService
`JarUpdateService` es responsable de gestionar la actualización automática de los archivos JAR de los microservicios. Se encargará de estar pendiente de si el fat-jar se ha actualizado. En caso afirmativo, parará el microservicio en cuestión, para que el sistema operativo, a través de servicios y timers lo relance de nuevo.

## Variables de Configuración

Las variables anotadas con `@Value` en este proyecto (`BaseServer`) vendrán definidas en el archivo de configuración del microservicio específico que esté utilizando este proyecto base. Cada microservicio proporcionará sus propias configuraciones personalizadas para ajustarse a sus requisitos particulares.

## Roles

El sistema utiliza diferentes roles para gestionar permisos y accesos de usuario. Los roles disponibles son:

- **PROFESOR**
- **DIRECCIÓN**
- **ADMINISTRADOR**

El rol `CLIENTE_IMPRESORA` es específico del proyecto `PrintersClient`.

Asegúrate de configurar estos roles correctamente en la base de datos de Firebase NoSQL.

## Creación de Elementos en la Colección de Firebase

Para que el sistema funcione correctamente, es necesario crear una colección en Firebase llamada `usuarios` donde se almacenarán los datos de los usuarios. Sigue los siguientes pasos para crear elementos en esta colección:

1. **Accede a la consola de Firebase**:
   Ve a [Firebase Console](https://console.firebase.google.com/) e inicia sesión con tu cuenta de Google.

2. **Selecciona tu proyecto**:
   Haz clic en tu proyecto para abrir el panel de control.

3. **Ve a Firestore Database**:
   En el menú de la izquierda, selecciona **Firestore Database** y haz clic en **Crear base de datos** si aún no lo has hecho. Asegúrate de seleccionar el modo de producción.

4. **Crear la colección `usuarios`**:
   - Haz clic en **Iniciar colección** y escribe `usuarios` como nombre de la colección.
   - Haz clic en **Siguiente** para añadir el primer documento.

5. **Añadir un documento**:
   - Define el **ID del documento**: Este será el UID del usuario, que puedes obtener desde la pestaña de Firebase Authentication.
   - Añade los siguientes campos al documento:
     - **email** (tipo: `string`): El correo electrónico del usuario.
     - **nombre** (tipo: `string`): El nombre del usuario.
     - **apellidos** (tipo: `string`): Los apellidos del usuario.
     - **roles** (tipo: `array`): Lista de roles asignados al usuario, por ejemplo: `["PROFESOR", "DIRECCIÓN"]`.

6. **Guardar el documento**:
   Haz clic en **Guardar** para crear el documento en la colección `usuarios`.

Repite estos pasos para cada usuario que necesites agregar al sistema.