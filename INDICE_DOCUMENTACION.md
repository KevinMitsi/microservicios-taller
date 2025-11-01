# 📚 Índice de Documentación - Pruebas de Integración

## 🚀 Por Dónde Empezar

**¿Primera vez aquí?** Sigue este orden:

1. 📖 **[INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md)** ⭐ EMPIEZA AQUÍ
   - Guía de inicio en 3 pasos
   - Configuración rápida
   - Solución de problemas comunes

2. 📋 **[PRUEBAS_INTEGRACION.md](PRUEBAS_INTEGRACION.md)**
   - Resumen ejecutivo del sistema
   - Tipos de pruebas incluidas
   - Servicios verificados

3. 📘 **[integration-tests/README.md](integration-tests/README.md)**
   - Documentación técnica completa
   - Estructura del proyecto
   - Casos de uso avanzados

4. 🔄 **[integration-tests/FLUJO_PRUEBAS.md](integration-tests/FLUJO_PRUEBAS.md)**
   - Diagramas de flujo visuales
   - Cobertura por servicio
   - Arquitectura de pruebas

5. 📊 **[integration-tests/RESUMEN_ARCHIVOS.md](integration-tests/RESUMEN_ARCHIVOS.md)**
   - Lista completa de archivos creados
   - Estadísticas del proyecto
   - Checklist de validación

## 📂 Documentación por Categoría

### 🎯 Para Comenzar
- **[INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md)** - Inicio en 3 pasos
- **[PRUEBAS_INTEGRACION.md](PRUEBAS_INTEGRACION.md)** - Visión general

### 📖 Documentación Técnica
- **[integration-tests/README.md](integration-tests/README.md)** - Guía completa
- **[integration-tests/FLUJO_PRUEBAS.md](integration-tests/FLUJO_PRUEBAS.md)** - Diagramas
- **[integration-tests/RESUMEN_ARCHIVOS.md](integration-tests/RESUMEN_ARCHIVOS.md)** - Lista de archivos

### 🔧 Configuración
- **[integration-tests/pom.xml](integration-tests/pom.xml)** - Dependencias Maven
- **[integration-tests/src/test/resources/application.properties](integration-tests/src/test/resources/application.properties)** - Configuración

### 🚀 Ejecución
- **[run-integration-tests.cmd](run-integration-tests.cmd)** - Desde el raíz
- **[integration-tests/run-tests.cmd](integration-tests/run-tests.cmd)** - Script completo
- **[integration-tests/verify-services.cmd](integration-tests/verify-services.cmd)** - Verificación rápida

### 🧪 Código de Pruebas
- **[SystemIntegrationTest.java](integration-tests/src/test/java/com/taller/integration/tests/SystemIntegrationTest.java)** - Pruebas JUnit
- **[SmokeTest.java](integration-tests/src/test/java/com/taller/integration/tests/SmokeTest.java)** - Pruebas rápidas
- **[system-integration.feature](integration-tests/src/test/resources/features/system-integration.feature)** - Escenarios BDD

### 🔄 CI/CD
- **[integration-tests/Jenkinsfile](integration-tests/Jenkinsfile)** - Pipeline Jenkins
- **[integration-tests/.github-workflows-example.yml](integration-tests/.github-workflows-example.yml)** - GitHub Actions

## 🎯 Guías por Rol

### 👨‍💼 Para Gerentes de Proyecto
1. **[PRUEBAS_INTEGRACION.md](PRUEBAS_INTEGRACION.md)** - Resumen ejecutivo
2. **[integration-tests/RESUMEN_ARCHIVOS.md](integration-tests/RESUMEN_ARCHIVOS.md)** - Estadísticas

### 👨‍💻 Para Desarrolladores
1. **[INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md)** - Setup inicial
2. **[integration-tests/README.md](integration-tests/README.md)** - Documentación técnica
3. **[integration-tests/FLUJO_PRUEBAS.md](integration-tests/FLUJO_PRUEBAS.md)** - Arquitectura

### 🧪 Para QA/Testers
1. **[INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md)** - Cómo ejecutar
2. **[system-integration.feature](integration-tests/src/test/resources/features/system-integration.feature)** - Escenarios de prueba
3. **[integration-tests/README.md](integration-tests/README.md)** - Casos de prueba

### 🔧 Para DevOps
1. **[integration-tests/Jenkinsfile](integration-tests/Jenkinsfile)** - Pipeline
2. **[integration-tests/.github-workflows-example.yml](integration-tests/.github-workflows-example.yml)** - GitHub Actions
3. **[integration-tests/README.md](integration-tests/README.md)** - Integración CI/CD

## 🔍 Buscar por Tema

### Configuración Inicial
- [INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md) - Paso 1: Verificar Prerequisitos
- [application.properties](integration-tests/src/test/resources/application.properties) - URLs de servicios

### Ejecución de Pruebas
- [run-integration-tests.cmd](run-integration-tests.cmd) - Script desde raíz
- [run-tests.cmd](integration-tests/run-tests.cmd) - Script completo con verificaciones
- [verify-services.cmd](integration-tests/verify-services.cmd) - Solo smoke test

### Tipos de Pruebas
- [SystemIntegrationTest.java](integration-tests/src/test/java/com/taller/integration/tests/SystemIntegrationTest.java) - 10 casos JUnit
- [SmokeTest.java](integration-tests/src/test/java/com/taller/integration/tests/SmokeTest.java) - Verificación rápida
- [system-integration.feature](integration-tests/src/test/resources/features/system-integration.feature) - 7 escenarios BDD

### Arquitectura y Flujos
- [FLUJO_PRUEBAS.md](integration-tests/FLUJO_PRUEBAS.md) - Diagramas completos
- [README.md](README.md) - Arquitectura del sistema
- [PRUEBAS_INTEGRACION.md](PRUEBAS_INTEGRACION.md) - Servicios verificados

### Resolución de Problemas
- [INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md) - Sección "Resolución Rápida"
- [integration-tests/README.md](integration-tests/README.md) - Sección "Solución de Problemas"

### CI/CD
- [Jenkinsfile](integration-tests/Jenkinsfile) - Pipeline completo
- [.github-workflows-example.yml](integration-tests/.github-workflows-example.yml) - GitHub Actions
- [integration-tests/README.md](integration-tests/README.md) - Sección "Integración con CI/CD"

## 📊 Reportes y Resultados

Después de ejecutar las pruebas:
- **Maven Surefire**: `integration-tests/target/surefire-reports/`
- **Cucumber HTML**: `integration-tests/target/cucumber-reports.html`
- **Logs**: Salida en consola con detalles

## 🆘 Ayuda Rápida

**¿Los servicios no inician?**
→ Ver [INICIO_RAPIDO.md - Error: Connection refused](integration-tests/INICIO_RAPIDO.md#error-connection-refused)

**¿Las pruebas fallan?**
→ Ver [INICIO_RAPIDO.md - Resolución de Problemas](integration-tests/INICIO_RAPIDO.md#-resolución-rápida-de-problemas)

**¿Cómo añadir nuevas pruebas?**
→ Ver [integration-tests/README.md - Extensión de las Pruebas](integration-tests/README.md#extensión-de-las-pruebas)

**¿Cómo integrar con Jenkins?**
→ Ver [integration-tests/Jenkinsfile](integration-tests/Jenkinsfile)

**¿Dónde están los reportes?**
→ `integration-tests/target/cucumber-reports.html`

## 📞 Contacto y Soporte

Para más ayuda, consulta:
1. Documentación en este índice
2. Logs en `integration-tests/target/surefire-reports/`
3. Verificar servicios con `verify-services.cmd`

## ✅ Checklist Rápido

- [ ] Leí [INICIO_RAPIDO.md](integration-tests/INICIO_RAPIDO.md)
- [ ] Verifiqué prerequisitos (Java 21, Maven, Docker)
- [ ] Levanté servicios con `docker-compose up -d`
- [ ] Ejecuté `run-integration-tests.cmd`
- [ ] Revisé reportes en `target/cucumber-reports.html`

---

**Última actualización**: Octubre 2025  
**Versión del sistema**: 1.0.0  
**Estado**: ✅ Documentación completa

