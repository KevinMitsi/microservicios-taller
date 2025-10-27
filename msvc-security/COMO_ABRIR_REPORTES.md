# 📊 Guía Visual de Reportes - Jenkins y SonarQube

## 📍 Ubicación de Reportes en Jenkins

Después de ejecutar el pipeline, verás varios enlaces en la página del build:

```
msvc-security-pipeline #1
├── 📊 Test Result (JUnit)
├── 📈 Coverage Report (JaCoCo)
├── 🥒 Cucumber reports
├── 📄 Cucumber HTML Report
├── 📦 Build Artifacts
└── 📝 Console Output
```

---

## 1️⃣ Test Result (JUnit)

### ¿Qué muestra?
Resultados de todas las pruebas unitarias ejecutadas.

### Interpretación:

```
╔══════════════════════════════════════════╗
║        TEST RESULTS SUMMARY              ║
╠══════════════════════════════════════════╣
║ Total Tests:      25                     ║
║ ✅ Passed:        23   (92%)             ║
║ ❌ Failed:        2    (8%)              ║
║ ⏭️ Skipped:       0    (0%)              ║
║ ⏱️ Duration:      45.3 seconds           ║
╚══════════════════════════════════════════╝
```

### Qué buscar:
- ✅ **100% pasando**: Perfecto, continuar
- ⚠️ **Algunos fallando**: Revisar detalles del fallo
- 🔴 **Muchos fallando**: Problemas serios, no desplegar

### Navegación:
1. Click en nombre del test fallido
2. Ver el stacktrace completo
3. Identificar la causa
4. Corregir código o test

---

## 2️⃣ Coverage Report (JaCoCo)

### ¿Qué muestra?
Porcentaje de código cubierto por tests.

### Métricas:

| Métrica | Descripción | Meta |
|---------|-------------|------|
| **Instruction Coverage** | % de instrucciones ejecutadas | > 80% |
| **Branch Coverage** | % de condicionales (if/else) cubiertos | > 70% |
| **Line Coverage** | % de líneas ejecutadas | > 80% |
| **Method Coverage** | % de métodos ejecutados | > 75% |
| **Class Coverage** | % de clases cubiertas | > 80% |

### Interpretación:

```
Package: com.taller.msvc_security.Services.Implementation
┌────────────────────────────────────────────────────┐
│ Class: UserServiceImpl                             │
├────────────────────────────────────────────────────┤
│ Lines:    120 / 150    (80%)  🟢 GOOD              │
│ Branches:  45 / 60     (75%)  🟡 ACCEPTABLE        │
│ Methods:   10 / 12     (83%)  🟢 GOOD              │
└────────────────────────────────────────────────────┘

Métodos sin cubrir:
❌ deleteUserPermanently()  - 0% coverage
❌ exportUsersToCSV()       - 0% coverage
```

### Qué buscar:
- 🟢 **Verde (> 80%)**: Excelente cobertura
- 🟡 **Amarillo (60-80%)**: Aceptable, mejorar si es posible
- 🔴 **Rojo (< 60%)**: Crítico, añadir tests

### Navegación:
1. Click en un paquete
2. Click en una clase
3. Ver líneas específicas cubiertas/no cubiertas
4. Las líneas verdes = cubiertas
5. Las líneas rojas = NO cubiertas

---

## 3️⃣ Cucumber Reports

### ¿Qué muestra?
Resultados de pruebas BDD (Behavior Driven Development).

### Estructura:

```
Feature: Gestión de Usuarios
  
  ✅ Scenario: Crear un usuario exitosamente
     ✅ Given el sistema está disponible
     ✅ When envío una solicitud POST a /api/users con datos válidos
     ✅ Then recibo una respuesta 201 Created
     ✅ And el usuario se crea en la base de datos
     ⏱️ Duration: 1.2s
  
  ❌ Scenario: Crear usuario con email duplicado
     ✅ Given existe un usuario con email "test@example.com"
     ✅ When intento crear otro usuario con el mismo email
     ❌ Then recibo una respuesta 409 Conflict
        Expected: 409, but got: 500
        at UserSteps.java:45
     ⏱️ Duration: 0.8s
```

### Interpretación:

```
╔══════════════════════════════════════════╗
║      CUCUMBER BDD RESULTS                ║
╠══════════════════════════════════════════╣
║ Features:     2                          ║
║ Scenarios:    12                         ║
║   ✅ Passed:  10   (83.3%)               ║
║   ❌ Failed:  2    (16.7%)               ║
║   ⏭️ Skipped: 0    (0%)                  ║
║ Steps:        48                         ║
║   ✅ Passed:  44   (91.7%)               ║
║   ❌ Failed:  4    (8.3%)                ║
╚══════════════════════════════════════════╝
```

### Qué buscar:
- **Scenarios pasando**: Funcionalidades trabajando correctamente
- **Steps fallando**: Identificar qué parte del flujo falla
- **Duration alta**: Optimizar tests lentos

---

## 4️⃣ Cucumber HTML Report (Masterthought)

### ¿Qué muestra?
Reporte visual mejorado con gráficos y tendencias.

### Secciones:

#### A. Overview Dashboard
```
┌─────────────────────────────────────────┐
│  📊 FEATURES OVERVIEW                   │
├─────────────────────────────────────────┤
│  Total Features:    2                   │
│  Passed Features:   2     [██████] 100% │
│  Failed Features:   0     [      ]   0% │
│                                         │
│  📈 SCENARIOS STATUS                    │
│  Passed: 10  [████████  ] 83%          │
│  Failed: 2   [██        ] 17%          │
│                                         │
│  ⏱️ EXECUTION TIME                      │
│  Total Duration: 45.3s                  │
│  Average Scenario: 3.8s                 │
└─────────────────────────────────────────┘
```

#### B. Features Detail
- Lista completa de features
- Status de cada scenario
- Tags asociados
- Duración individual

#### C. Trends
```
Build History (últimos 10 builds):
Build #10 ✅ 100% passed
Build #9  ✅ 100% passed
Build #8  ⚠️  92% passed
Build #7  ✅ 100% passed
Build #6  ❌  75% passed
...
```

#### D. Failures
```
❌ Failed Scenarios:
1. "Crear usuario con email duplicado"
   Feature: users.feature:25
   Error: Expected 409, got 500
   
2. "Login con contraseña incorrecta"
   Feature: users.feature:45
   Error: JWT token not generated
```

---

## 5️⃣ SonarQube Dashboard

### Acceso:
http://localhost:9000 → Click en proyecto "msvc-security"

### A. Overview Principal

```
╔════════════════════════════════════════════════════╗
║             MSVC-SECURITY PROJECT                  ║
╠════════════════════════════════════════════════════╣
║                                                    ║
║  QUALITY GATE: ✅ PASSED                          ║
║                                                    ║
║  ┌─────────────┬─────────────┬─────────────┐     ║
║  │   BUGS      │ VULNERAB.   │ CODE SMELLS │     ║
║  │             │             │             │     ║
║  │      3      │      0      │     24      │     ║
║  │      🟡     │      🟢     │     🟡      │     ║
║  └─────────────┴─────────────┴─────────────┘     ║
║                                                    ║
║  ┌─────────────┬─────────────┬─────────────┐     ║
║  │  COVERAGE   │ DUPLICAT.   │ TECH DEBT   │     ║
║  │             │             │             │     ║
║  │    82.5%    │     1.8%    │     2h      │     ║
║  │      🟢     │      🟢     │     🟢      │     ║
║  └─────────────┴─────────────┴─────────────┘     ║
║                                                    ║
║  LINES OF CODE: 3,245                             ║
║  LANGUAGE: Java                                   ║
╚════════════════════════════════════════════════════╝
```

### B. Issues Tab

#### Bugs 🐛
```
Severity: MAJOR
File: UserServiceImpl.java:145
Issue: Possible NullPointerException
Code:
    public void updateUser(User user) {
        user.getEmail().toLowerCase(); // 🔴 user podría ser null
    }
Recommended:
    if (user != null && user.getEmail() != null) {
        user.getEmail().toLowerCase();
    }
```

#### Vulnerabilities 🔒
```
Severity: CRITICAL
File: SecurityConfig.java:32
Issue: Hard-coded password
Code:
    String password = "admin123"; // 🔴 Contraseña hardcodeada
Recommended:
    Use environment variables or secure vault
```

#### Code Smells 👃
```
Severity: MINOR
File: UserController.java:78
Issue: Method too long (125 lines)
Recommended:
    Refactor into smaller methods (max 50 lines)
```

### C. Coverage Tab

```
Overall Coverage: 82.5% 🟢

Por Componente:
┌────────────────────────────────────────────────┐
│ Controllers/      ████████░░  85.2%  🟢       │
│ Services/         █████████░  92.3%  🟢       │
│ Repositories/     ██████████ 100.0%  🟢       │
│ Models/           ░░░░░░░░░░   0.0%  ⚪ (excluded) │
│ Config/           ██████░░░░  65.5%  🟡       │
└────────────────────────────────────────────────┘

Archivos sin cobertura suficiente:
❌ NotificationServiceImpl.java    45.2%
❌ JwtAuthenticationFilter.java    58.7%
```

### D. Duplications Tab

```
Total Duplications: 1.8% 🟢

Bloques duplicados encontrados: 3

1. UserServiceImpl.java:45-67 (23 lines)
   ≈ UserEventServiceImpl.java:89-111
   Similarity: 95%

2. UserController.java:120-135 (16 lines)
   ≈ UserController.java:200-215
   Similarity: 88%
```

---

## 🎯 Checklist de Build Exitoso

Verifica cada punto después de ejecutar el pipeline:

### ✅ En Jenkins Console Output:

```
[Pipeline] stage (Checkout)
✅ [SUCCESS] Clonando repositorio del proyecto...

[Pipeline] stage (Build)
✅ [SUCCESS] Compilando proyecto...

[Pipeline] stage (Unit & Integration Tests)
✅ [SUCCESS] Ejecutando pruebas...
   Tests run: 25, Failures: 0, Errors: 0, Skipped: 0

[Pipeline] stage (SonarQube Analysis)
✅ [SUCCESS] Analizando calidad de código...
   Analysis uploaded successfully

[Pipeline] stage (Quality Gate)
✅ [SUCCESS] Quality Gate passed

[Pipeline] stage (Package)
✅ [SUCCESS] Empaquetando aplicación...

[Pipeline] stage (Build Docker Image)
✅ [SUCCESS] Imagen Docker construida

[Pipeline] stage (Generate Test Report Summary)
╔════════════════════════════════════════════╗
║    RESUMEN DE EJECUCIÓN - MSVC SECURITY    ║
╠════════════════════════════════════════════╣
║ Build: #15                                 ║
║ Branch: main                               ║
║ Tests Totales: 25                          ║
║ Tests Exitosos: 25                         ║
║ Tests Fallidos: 0                          ║
║ Tests Omitidos: 0                          ║
║                                            ║
║ 📊 Reportes disponibles:                   ║
║ - JUnit Test Results                       ║
║ - JaCoCo Coverage Report                   ║
║ - Cucumber BDD Report                      ║
║ - SonarQube Quality Analysis               ║
╚════════════════════════════════════════════╝

✅ [SUCCESS] Pipeline ejecutado exitosamente!
```

### ✅ En Jenkins UI:

- [ ] Build status: **Azul/Verde** ✅
- [ ] Test Result: **25 tests, 0 failures**
- [ ] Coverage: **> 80%**
- [ ] Cucumber Report: **Todos los scenarios pasando**
- [ ] Artefactos: **JAR generado y archivado**

### ✅ En SonarQube:

- [ ] Quality Gate: **PASSED** ✅
- [ ] Bugs: **0** (o todos menores)
- [ ] Vulnerabilities: **0**
- [ ] Coverage: **> 80%**
- [ ] Duplications: **< 3%**
- [ ] Maintainability Rating: **A o B**

---

## 🔴 Señales de Alerta

### Crítico - Detener deployment:
- ❌ Quality Gate: **FAILED**
- ❌ Vulnerabilities: **CRITICAL o HIGH**
- ❌ Tests failing: **> 5%**
- ❌ Build: **FAILED**

### Advertencia - Revisar pero puede continuar:
- ⚠️ Coverage: **< 70%**
- ⚠️ Code Smells: **> 50**
- ⚠️ Duplications: **> 5%**
- ⚠️ Tests skipped: **> 10%**

### Informativo - Mejorar en próximo sprint:
- ℹ️ Coverage: **70-80%**
- ℹ️ Technical Debt: **> 1 day**
- ℹ️ Minor bugs: **< 5**
- ℹ️ Info level issues: **cualquier cantidad**

---

## 📈 Tendencias a Monitorear

### Gráficos en Jenkins:
```
Test Results Trend (últimos 30 builds):
30 ┤          ●●●●●●●●●●
25 ┤      ●●●●
20 ┤    ●●
15 ┤  ●●
10 ┤●●
    └────────────────────────────
    Builds: #1 → #30
    
    Interpretación: Tests aumentando = más cobertura ✅
```

```
Coverage Trend:
90% ┤              ●●●●
80% ┤          ●●●●
70% ┤      ●●●●
60% ┤  ●●●●
    └────────────────────────────
    Builds: #1 → #30
    
    Interpretación: Coverage subiendo = mejorando calidad ✅
```

### Gráficos en SonarQube:
```
Activity Timeline:
Bugs         ●━━━━━━━━━━━━● (Disminuyendo ✅)
Vulnerab.    ●━━━━━━━●━━━● (Estable ✅)
Code Smells  ●━━━━━━━━━━━● (Disminuyendo ✅)
Coverage     ━━━━━━━━━━━━● (Aumentando ✅)
```

---

## 🎓 Tips para Mejorar Reportes

### 1. Mejorar Coverage:
```java
// Antes (sin test):
public void deleteUser(String id) {
    userRepository.deleteById(id);  // 🔴 No cubierto
}

// Después (con test):
@Test
void shouldDeleteUser() {
    userService.deleteUser("123");
    verify(userRepository).deleteById("123");  // ✅ Cubierto
}
```

### 2. Resolver Code Smells:
```java
// Antes (Code Smell):
public void process(User u) {  // 🔴 Nombre ambiguo
    // método muy largo (100+ líneas)
}

// Después (Refactorizado):
public void validateAndSaveUser(User user) {  // ✅ Nombre claro
    validateUser(user);
    saveUser(user);
    notifyAdministrators(user);
}
```

### 3. Fijar Bugs:
```java
// Antes (Bug potencial):
String email = user.getEmail().toLowerCase();  // 🔴 NPE posible

// Después (Seguro):
String email = Optional.ofNullable(user)
    .map(User::getEmail)
    .map(String::toLowerCase)
    .orElse("");  // ✅ Null-safe
```

---

## 📞 Comandos Útiles para Debugging

```cmd
# Ver reportes localmente después de mvn test
start msvc-security\target\site\jacoco\index.html
start msvc-security\target\cucumber-report-html\overview-features.html

# Ejecutar solo tests específicos
cd msvc-security
mvn test -Dtest=UserServiceTest

# Ejecutar solo features específicos
mvn test -Dcucumber.features="src/test/resources/features/users.feature"

# Ver coverage en tiempo real
mvn clean test jacoco:report
```

---

**¡Usa esta guía como referencia cada vez que ejecutes el pipeline! 📊**

