# Guía de Inicio Rápido - Pruebas de Integración

## ⚡ Inicio en 3 Pasos

### Paso 1: Verificar Prerequisitos
```cmd
# Verificar Java
java -version
# Debe mostrar: Java version "21.x.x"

# Verificar Maven
mvn -version
# Debe mostrar: Maven 3.8.x o superior

# Verificar Docker
docker --version
docker-compose --version
```

### Paso 2: Levantar Servicios
```cmd
# Desde el directorio raíz del proyecto
docker-compose up -d

# Esperar ~90 segundos
timeout /t 90

# Verificar que los servicios están corriendo
docker-compose ps
```

### Paso 3: Ejecutar Pruebas
```cmd
# Opción A: Script automatizado (MÁS FÁCIL)
run-integration-tests.cmd

# Opción B: Verificación rápida
cd integration-tests
verify-services.cmd

# Opción C: Maven directo
cd integration-tests
mvn clean test
```

## 📊 Interpretación de Resultados

### ✅ Éxito
```
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
¡Todas las pruebas pasaron! El sistema está funcionando correctamente.

### ❌ Fallo
```
[ERROR] Tests run: 10, Failures: 2, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```
Algunas pruebas fallaron. Revisa los reportes en `target/surefire-reports/`

## 🐛 Resolución Rápida de Problemas

### Error: "Connection refused"
**Causa**: Servicios no están corriendo.
```cmd
docker-compose up -d
timeout /t 90
```

### Error: "Port already in use"
**Causa**: Puerto ocupado por otra aplicación.
```cmd
# Ver qué está usando el puerto 8080 (ejemplo)
netstat -ano | findstr :8080

# Detener servicios y reiniciar
docker-compose down
docker-compose up -d
```

### Error: "Timeout waiting for services"
**Causa**: Servicios tardan en iniciar.
```cmd
# Aumentar tiempo de espera (editar application.properties)
integration.test.async-timeout-seconds=60
```

### Error: "Tests pass but report not generated"
**Causa**: Problema con Maven Surefire.
```cmd
# Limpiar y reconstruir
cd integration-tests
mvn clean
mvn test -X
```

## 📈 Siguientes Pasos

Después de ejecutar las pruebas exitosamente:

1. **Ver Reportes**
   - Abrir `integration-tests/target/cucumber-reports.html` en navegador
   - Revisar `integration-tests/target/surefire-reports/`

2. **Explorar Código**
   - Ver `SystemIntegrationTest.java` para pruebas JUnit
   - Ver `system-integration.feature` para escenarios BDD

3. **Añadir Nuevas Pruebas**
   - Leer `integration-tests/README.md`
   - Seguir la estructura existente

4. **Integrar con CI/CD**
   - Usar `Jenkinsfile` incluido
   - O adaptar `.github-workflows-example.yml`

## 🎯 Comandos Útiles

```cmd
# Ver logs de un servicio específico
docker-compose logs -f msvc-security

# Reiniciar un servicio
docker-compose restart msvc-security

# Detener todo
docker-compose down

# Limpiar volúmenes (CUIDADO: borra datos)
docker-compose down -v

# Ejecutar solo un test específico
cd integration-tests
mvn test -Dtest=SystemIntegrationTest#test02_userRegistrationAndLogin

# Ver logs detallados de Maven
mvn test -X

# Ejecutar sin compilar de nuevo
mvn surefire:test
```

## 📞 ¿Necesitas Ayuda?

1. **Documentación**
   - `README.md` - Información general
   - `PRUEBAS_INTEGRACION.md` - Resumen de pruebas
   - `integration-tests/README.md` - Documentación completa
   - `integration-tests/FLUJO_PRUEBAS.md` - Diagramas de flujo

2. **Logs**
   - Revisar `docker-compose logs <servicio>`
   - Revisar `integration-tests/target/surefire-reports/`

3. **Estado del Sistema**
   ```cmd
   cd integration-tests
   verify-services.cmd
   ```

## ✨ Tips Pro

- 💡 Ejecuta `verify-services.cmd` antes de las pruebas completas
- 💡 Mantén Docker Desktop abierto mientras ejecutas pruebas
- 💡 Si las pruebas fallan, ejecuta primero el smoke test
- 💡 Los reportes Cucumber son más fáciles de leer en HTML
- 💡 Puedes ejecutar pruebas individuales con `-Dtest=NombreTest`

---

**¿Todo funcionó?** ✅ ¡Excelente! Tu sistema está completamente integrado y probado.

**¿Algo falló?** ❌ No te preocupes, revisa la sección de resolución de problemas arriba.

