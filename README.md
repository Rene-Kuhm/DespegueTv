# DespegueTV

<img src="assets/brand/app_logo_wordmark.png" alt="DespegueTV Logo" width="200"/>

**DespegueTV** es una aplicación de streaming para Android TV desarrollada en Kotlin con Jetpack Compose.

## 🎬 Características

- 📱 **Interfaz optimizada para TV** - Navegación por D-pad, diseño enfocado en televisores
- 🎧 **Soporte para múltiples addons** - Compatible con el ecosistema de Stremio
- 🇪🇸 **Español e Español Latino** - Configurado por defecto en `es-419` (Latino) y `es` (España)
- 🎨 **Calidad de video** - Auto-reproducción desde 720p+, filtro sin CAM/TS
- 📺 **Subtítulos** - Soporte para múltiples idiomas con preferencia español
- 🎨 **Reproductor interno** - Basado en ExoPlayer/mpv con soporte para torrents
- 🔧 **Plugins JavaScript** - Sistema de plugins configurable (`feature flag: pluginsEnabled`)
- 🎨 **Temas** - Soporte para múltiples temas de colores
- 📱 **Perfiles múltiples** - Gestión de perfiles de usuario

## 📦 Addons preconfigurados

DespegueTV incluye estos addons configurados por defecto (todos gratuitos):

| Addon | Contenido |
|-------|-----------|
| Torrentio | Torrents con opción de configuración para español |
| ThePirateBay+ | Catálogo masivo de torrents |
| Marvel | Marvel Cinematic Universe catalog |
| DC Universe | DC Comics catalog (preconfigurado) |
| Latino Movies | Especializado en español latino |
| Cinemeta | Metadatos y catálogos |
| OpenSubtitles | Subtítulos |

## 🚀 Instalación

### Requisitos
- Android TV o dispositivo con Android 5.0+
- JDK 21 (se recomienda `/usr/lib/jvm/java-21-openjdk`)
- Android SDK configurado en `ANDROID_HOME`

### Compilación

```bash
git clone https://github.com/Rene-Kuhm/DespegueTv.git
cd DespegueTV

# Configurar local.properties con tu Android SDK
echo "sdk.dir=/home/tecnodespegue/Android/Sdk" > local.properties

# Compilar versión completa (full flavor)
JAVA_HOME="/usr/lib/jvm/java-21-openjdk" \
ANDROID_HOME="/home/tecnodespegue/Android/Sdk" \
ANDROID_SDK_ROOT="/home/tecnodespegue/Android/Sdk" \
./gradlew assembleFullDebug
```

El APK se genera en: `app/build/outputs/apk/full/debug/app-full-x86-debug.apk`

### Instalación en el emulador o dispositivo

```bash
# Iniciar emulador (Android TV API 36)
ANDROID_HOME="/home/tecnodespegue/Android/Sdk" \
ANDROID_SDK_ROOT="/home/tecnodespegue/Android/Sdk" \
emulator -avd DespegueTV_TV_API36 -no-snapshot-save &

# Instalar APK
adb install -r app/build/outputs/apk/full/debug/app-full-x86-debug.apk

# Lanzar aplicación
adb shell am start -n com.despeguedebug.com/com.despegue.tv.MainActivity
```

## 🔧 Configuración

### Preferencias de audio y subtítulos (por defecto)
- **Audio principal**: Español Latino (`es-419`)
- **Audio secundario**: Español (`es`)
- **Subtítulos principales**: Español Latino (`es-419`)
- **Subtítulos secundarios**: Español (`es`)
- **TMDB**: Idioma `es-419` para metadatos localizados

### Calidad de video (por defecto)
- **Mínimo**: 720p (filtro sin CAM/TS)
- **Auto-reproducción**: Activada con expresión regular configurada
- **Filtro**: `^(?!.*\b(cam|hdcam|ts|telesync)\b).*?(?i)(720p|1080p|2160p|4k|web-dl|bluray|brrip|remux)`

## 🛠️ Desarrollo

### Estructura del proyecto
```
DespegueTV/
├── app/
│   ├── src/main/          # Código principal
│   ├── src/full/           # Configuración para versión completa
│   └── src/playstore/      # Configuración para Play Store
├── assets/
│   └── brand/               # Recursos de marca (logos)
└── build.gradle.kts            # Configuración de Gradle
```

### Características de la versión (Full vs Play Store)
| Característica | Full | Play Store |
|-----------------|------|-------------|
| Plugins habilitados | ✅ | ❌ |
| Actualizaciones en app | ✅ | ❌ |
| Tráilers en app | ✅ | ❌ |
| Reproducción externa | ✅ | ✅ |

## 📄 Licencia

Este proyecto es privado. Todos los derechos reservados.

---

**DespegueTV** - Streaming para Android TV en español 🇪🇸
