# Pruebas HTTP para el Sistema de Usuarios

Esta carpeta contiene archivos HTTP para probar los endpoints del sistema de usuarios una vez que el servidor esté corriendo.

## Requisitos Previos

1. **Servidor corriendo**: Asegúrate de que el servidor Spring Boot esté ejecutándose en `http://localhost:8080`
2. **Base de datos configurada**: La base de datos debe estar configurada y accesible
3. **Cliente HTTP**: Puedes usar:
   - IntelliJ IDEA (Run/Debug configurations)
   - VS Code con extensión REST Client
   - Postman
   - curl desde terminal

## Archivos Disponibles

### 1. `creacion-usuario.http`
Contiene 15 casos de prueba para el endpoint de creación de usuarios (`POST /api/usuarios`):

- ✅ **Caso exitoso**: Usuario válido
- ❌ **Email duplicado**: Error de conflicto
- ❌ **Validaciones Jakarta**: Email vacío, inválido, muy largo
- ❌ **Validaciones Jakarta**: Contraseña vacía, débil, sin requisitos
- ✅ **Normalización**: Email con espacios y mayúsculas
- ❌ **Múltiples errores**: Varias validaciones fallando
- ❌ **JSON malformado**: Formato incorrecto

### 2. `endpoints-usuario.http`
Contiene casos de prueba para todos los endpoints del controlador:

- `GET /api/usuarios` - Obtener todos los usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `GET /api/usuarios/email/{email}` - Obtener usuario por email
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario por ID
- `DELETE /api/usuarios/email/{email}` - Eliminar usuario por email
- Endpoints de verificación de existencia

## Cómo Usar

### Con IntelliJ IDEA:
1. Abre cualquier archivo `.http`
2. Haz clic en el botón "Run" (▶️) al lado de cada request
3. O usa `Ctrl+Shift+F10` para ejecutar el request seleccionado

### Con VS Code:
1. Instala la extensión "REST Client"
2. Abre cualquier archivo `.http`
3. Haz clic en "Send Request" que aparece sobre cada request

### Con curl (Terminal):
```bash
# Ejemplo de creación de usuario
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "contrasena": "MiPassword123!"
  }'
```

## Respuestas Esperadas

### Creación Exitosa (201):
```json
{
  "id": 1,
  "email": "usuario@ejemplo.com",
  "contrasena": "MiPassword123!",
  "rol": "USER"
}
```

### Error de Validación (400):
```json
{
  "mensaje": "Errores de validación",
  "errores": {
    "email": "El formato del email no es válido",
    "contrasena": "La contraseña debe tener al menos una mayúscula, un número, un carácter especial y mínimo 8 caracteres"
  },
  "status": 400
}
```

### Email Duplicado (409):
```json
"Ya existe un usuario con este email: usuario@ejemplo.com"
```

## Validaciones Jakarta Implementadas

### Email:
- ✅ **Obligatorio**: `@NotBlank`
- ✅ **Formato válido**: `@Email`
- ✅ **Longitud máxima**: `@Size(max = 150)`

### Contraseña:
- ✅ **Obligatoria**: `@NotBlank`
- ✅ **Patrón complejo**: `@Pattern` con regex que requiere:
  - Al menos una mayúscula
  - Al menos un número
  - Al menos un carácter especial
  - Mínimo 8 caracteres

## Notas Importantes

1. **IDs dinámicos**: Los IDs de usuario se generan automáticamente, ajusta los valores en las pruebas según sea necesario
2. **Base de datos**: Las pruebas pueden crear datos en la base de datos, considera usar un entorno de pruebas
3. **CORS**: El controlador tiene `@CrossOrigin(origins = "*")` configurado
4. **Rol por defecto**: Todos los usuarios se crean con rol `USER` automáticamente

## Solución de Problemas

### Error 500 - Error interno del servidor:
- Verifica que la base de datos esté configurada correctamente
- Revisa los logs del servidor para más detalles

### Error 404 - No encontrado:
- Asegúrate de que el servidor esté corriendo en el puerto correcto
- Verifica que la ruta del endpoint sea correcta

### Error de conexión:
- Verifica que el servidor esté ejecutándose
- Comprueba que no haya firewall bloqueando el puerto 8080
