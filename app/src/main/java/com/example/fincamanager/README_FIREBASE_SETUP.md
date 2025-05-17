# Configuración de Firebase para FincaManager

Para utilizar la autenticación de Firebase en la aplicación FincaManager, sigue estos pasos:

## 1. Crea un proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto"
3. Sigue los pasos para crear un nuevo proyecto
4. Asigna un nombre al proyecto (por ejemplo, "FincaManager")

## 2. Registra la aplicación Android en Firebase

1. En el panel de tu proyecto en Firebase Console, haz clic en el icono de Android para añadir una aplicación
2. Introduce el paquete de la aplicación: `com.example.fincamanager`
3. (Opcional) Introduce un apodo para la aplicación
4. Haz clic en "Registrar aplicación"

## 3. Descarga el archivo de configuración

1. Firebase generará un archivo `google-services.json`
2. Descarga este archivo
3. Coloca el archivo en el directorio `app/` de tu proyecto Android

## 4. Habilita Authentication en Firebase

1. En el panel lateral izquierdo de Firebase Console, haz clic en "Authentication"
2. Haz clic en "Comenzar"
3. En la pestaña "Sign-in method", habilita el método "Correo electrónico/contraseña"
4. Guarda los cambios

## 5. Verificación final

1. Asegúrate de que el plugin de Google Services está aplicado en build.gradle.kts a nivel de proyecto
2. Verifica que las dependencias de Firebase están correctamente agregadas en el build.gradle.kts de la app
3. Sincroniza el proyecto con los archivos gradle

Una vez completados estos pasos, la autenticación con Firebase debería funcionar correctamente en la aplicación FincaManager. 