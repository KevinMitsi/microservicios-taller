# 📊 Guía para Abrir Reportes de Cucumber

## 🚀 Paso 1: Ejecutar los Tests

Abre una terminal en el directorio `msvc-security` y ejecuta:

```bash
cd C:\Users\Kevin\Desktop\Universidad\2025-2\Microservicios\microservicios\msvc-security

# Opción 1: Solo ejecutar tests (genera reportes básicos)
mvn test -Dtest=TestRunner

# Opción 2: Ejecutar tests + generar reportes HTML mejorados (RECOMENDADO)
mvn verify
```

**Diferencia:**
- `mvn test`: Solo ejecuta tests y genera reportes básicos
- `mvn verify`: Ejecuta tests + genera reportes HTML mejorados de Masterthought

## 📁 Paso 2: Ubicación de los Reportes

Después de ejecutar los tests, se generan los siguientes reportes:

### 1. 🌟 Reporte HTML Mejorado (Masterthought) - EL MEJOR
**Ubicación:** 
```
target\cucumber-report-html\cucumber-html-reports\overview-features.html
```

**Cómo abrirlo:**
- Opción A: Doble clic en el archivo
- Opción B: Desde el navegador: File > Open > seleccionar el archivo
- Opción C: Ejecutar el script `abrir-reportes.cmd` que creé para ti

**Características:**
✅ Dashboard interactivo
✅ Gráficos de resultados
✅ Detalles de cada escenario
✅ Stack traces de errores
✅ Filtros por estado (passed/failed)
✅ Estadísticas de duración

**Otros reportes Masterthought:**
- `overview-failures.html` - Solo escenarios fallidos
- `overview-steps.html` - Detalle de cada paso
- `overview-tags.html` - Agrupado por tags

### 2. 📄 Reporte Básico de Cucumber
**Ubicación:**
```
target\cucumber-reports\cucumber.html
```

**Características:**
✅ Reporte simple
✅ Lista de escenarios
✅ Más ligero

### 3. 📊 Reporte de Cobertura JaCoCo
**Ubicación:**
```
target\site\jacoco\index.html
```

**Características:**
✅ Porcentaje de cobertura de código
✅ Líneas cubiertas/no cubiertas
✅ Detalle por paquete y clase
✅ Código resaltado

### 4. 📝 Reportes JSON/XML (para CI/CD)
**Ubicación:**
```
target\cucumber-reports\cucumber.json
target\cucumber-reports\cucumber.xml
```

Estos se usan para integración con Jenkins, SonarQube, etc.

## 🖱️ Paso 3: Abrir los Reportes

### Opción A: Usar el Script Automático (MÁS FÁCIL)

Ejecuta el archivo que creé para ti:
```bash
.\abrir-reportes.cmd
```

Este script:
1. Verifica que existan los reportes
2. Abre el reporte principal de Masterthought
3. Te pregunta si quieres abrir también el de JaCoCo

### Opción B: Abrir Manualmente

#### Desde Windows Explorer:
1. Navega a: `msvc-security\target\cucumber-report-html\cucumber-html-reports`
2. Haz doble clic en `overview-features.html`

#### Desde la Terminal:
```bash
# Abrir reporte Masterthought
start target\cucumber-report-html\cucumber-html-reports\overview-features.html

# Abrir reporte JaCoCo
start target\site\jacoco\index.html

# Abrir reporte básico
start target\cucumber-reports\cucumber.html
```

### Opción C: Desde IntelliJ IDEA

1. En el panel izquierdo, expande: `target > cucumber-report-html > cucumber-html-reports`
2. Clic derecho en `overview-features.html`
3. Selecciona "Open in Browser" o "Open with System Editor"

## 🎯 Comandos Completos

```bash
# 1. Ir al directorio del proyecto
cd C:\Users\Kevin\Desktop\Universidad\2025-2\Microservicios\microservicios\msvc-security

# 2. Ejecutar tests y generar reportes
mvn clean verify

# 3. Abrir reportes
.\abrir-reportes.cmd
```

## 📸 ¿Qué verás en el Reporte Masterthought?

### Dashboard Principal (overview-features.html)
- **Resumen general**: Total de features, escenarios, pasos
- **Gráfico circular**: Passed/Failed/Skipped
- **Gráfico de barras**: Duración de cada feature
- **Tabla de features**: Con % de éxito y duración
- **Timeline**: Línea de tiempo de ejecución

### Al hacer clic en un Feature:
- Lista de todos los escenarios
- Estado de cada escenario (✅ o ❌)
- Duración individual
- Botón para ver detalles

### Al hacer clic en un Escenario:
- Cada paso Given/When/Then
- Estado de cada paso
- Duración de cada paso
- Screenshots (si están configurados)
- Stack trace completo si falló

## 🔍 Troubleshooting

### Problema: No se generan los reportes
**Solución:**
```bash
# Limpiar y regenerar
mvn clean verify
```

### Problema: El reporte HTML no se ve bien
**Solución:**
- Asegúrate de estar abriendo el archivo desde `target/cucumber-report-html/cucumber-html-reports/`
- No el de `target/cucumber-reports/` (ese es el básico)

### Problema: Faltan algunos reportes
**Solución:**
- Usa `mvn verify` en lugar de `mvn test`
- El plugin de Masterthought se ejecuta en la fase `verify`

### Problema: El reporte está desactualizado
**Solución:**
```bash
# Limpiar todo y regenerar
mvn clean verify
```

## 📋 Resumen Rápido

| Reporte | Archivo | Comando |
|---------|---------|---------|
| **🌟 Masterthought (MEJOR)** | `target\cucumber-report-html\cucumber-html-reports\overview-features.html` | `mvn verify` |
| Básico Cucumber | `target\cucumber-reports\cucumber.html` | `mvn test` |
| Cobertura JaCoCo | `target\site\jacoco\index.html` | `mvn test` |

## 💡 Recomendación

**Siempre usa:** `mvn verify` para obtener todos los reportes, especialmente el de Masterthought que es el más completo y profesional.

**Abre primero:** `overview-features.html` que está en `target\cucumber-report-html\cucumber-html-reports\`

## 🎨 Ejemplo de Ejecución Completa

```bash
# Desde el directorio raíz del microservicio
C:\Users\Kevin\Desktop\Universidad\2025-2\Microservicios\microservicios\msvc-security

# Ejecutar
mvn clean verify

# Esperar a ver este mensaje:
# [INFO] BUILD SUCCESS

# Abrir reportes
.\abrir-reportes.cmd

# O manualmente:
start target\cucumber-report-html\cucumber-html-reports\overview-features.html
```

¡Listo! Ahora deberías poder ver tus hermosos reportes de Cucumber con gráficos interactivos 🎉

