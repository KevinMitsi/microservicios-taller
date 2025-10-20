# Documentación Jenkins y SonarQube

## Servicios Agregados al Docker Compose

### 1. Jenkins - Motor de Integración Continua (CI)

**Imagen:** `jenkins/jenkins:lts-jdk17`
**Puerto:** `8084` (Web UI)
**Puerto Agentes:** `50000`

#### Características:
- Jenkins con Java 17 LTS
- Acceso al Docker socket para construir imágenes
- Volumen persistente para configuración y datos
- Usuario root para permisos completos

#### Acceso:
- URL: `http://localhost:8084`
- Usuario inicial: `admin`
- Contraseña inicial: Se obtiene ejecutando:
  ```bash
  docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
  ```

#### Configuración Inicial:

1. **Iniciar Jenkins:**
   ```bash
   docker-compose up -d jenkins
   ```

2. **Obtener contraseña inicial:**
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

3. **Acceder a la interfaz web:**
   - Abrir navegador en `http://localhost:9090`
   - Pegar la contraseña inicial
   - Instalar plugins sugeridos

4. **Plugins Recomendados para el Proyecto:**
   - Maven Integration
   - Docker Pipeline
   - SonarQube Scanner
   - Git
   - GitHub
   - Pipeline
   - Blue Ocean (UI moderna)

#### Jenkinsfile Ejemplo para tus Microservicios:

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.0'
        jdk 'JDK 21'
    }
    
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-credentials')
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/tu-usuario/microservicios.git'
            }
        }
        
        stage('Build - msvc-security') {
            steps {
                dir('msvc-security') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Test - msvc-security') {
            steps {
                dir('msvc-security') {
                    sh 'mvn test'
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('SonarQube Analysis - msvc-security') {
            steps {
                dir('msvc-security') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir('msvc-security') {
                    sh 'docker build -t kevinmitsi/msvc-security:${BUILD_NUMBER} .'
                    sh 'docker tag kevinmitsi/msvc-security:${BUILD_NUMBER} kevinmitsi/msvc-security:latest'
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
                sh 'docker push kevinmitsi/msvc-security:${BUILD_NUMBER}'
                sh 'docker push kevinmitsi/msvc-security:latest'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline ejecutado exitosamente!'
        }
        failure {
            echo 'Pipeline falló!'
        }
    }
}
```

---

### 2. SonarQube - Análisis de Calidad de Código

**Imagen:** `sonarqube:10.3.0-community`
**Puerto:** `9000`
**Base de Datos:** PostgreSQL 15

#### Características:
- Análisis estático de código
- Detección de bugs, vulnerabilidades y code smells
- Métricas de calidad y cobertura
- Persistencia de datos en PostgreSQL

#### Acceso:
- URL: `http://localhost:9000`
- Usuario por defecto: `admin`
- Contraseña por defecto: `admin` (cambiar en primer login)

#### Configuración Inicial:

1. **Iniciar SonarQube:**
   ```bash
   docker-compose up -d sonarqube sonar-db
   ```

2. **Acceder a la interfaz web:**
   - Abrir navegador en `http://localhost:9000`
   - Login con `admin/admin`
   - Cambiar contraseña cuando se solicite

3. **Crear Token de Acceso:**
   - Ir a: My Account > Security > Generate Tokens
   - Nombre: `msvc-security-token`
   - Tipo: `Global Analysis Token`
   - Copiar y guardar el token generado

4. **Configurar Proyecto:**
   - Click en "Create Project" > "Manually"
   - Project key: `msvc-security`
   - Display name: `MSvc Security`
   - Main branch: `main`

#### Configuración en Maven (pom.xml):

Agregar en cada microservicio:

```xml
<properties>
    <!-- ...existing properties... -->
    <sonar.projectKey>msvc-security</sonar.projectKey>
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.login>TU_TOKEN_AQUI</sonar.login>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>

<build>
    <plugins>
        <!-- ...existing plugins... -->
        
        <!-- JaCoCo para Cobertura de Código -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

#### Ejecutar Análisis de SonarQube:

```bash
# Desde el directorio del microservicio
cd msvc-security

# Ejecutar análisis
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=msvc-security \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=TU_TOKEN_AQUI
```

#### Métricas que Analiza SonarQube:

- **Bugs:** Errores evidentes en el código
- **Vulnerabilidades:** Problemas de seguridad
- **Code Smells:** Patrones que dificultan el mantenimiento
- **Cobertura:** % de código cubierto por tests
- **Duplicación:** Código duplicado
- **Complejidad:** Complejidad ciclomática
- **Deuda técnica:** Tiempo estimado para arreglar problemas

---

## Comandos Útiles

### Levantar Todos los Servicios:
```bash
docker-compose up -d
```

### Levantar Solo Jenkins y SonarQube:
```bash
docker-compose up -d jenkins sonarqube sonar-db
```

### Ver Logs:
```bash
# Jenkins
docker-compose logs -f jenkins

# SonarQube
docker-compose logs -f sonarqube
```

### Detener Servicios:
```bash
docker-compose down
```

### Eliminar Volúmenes (Cuidado: Borra datos):
```bash
docker-compose down -v
```

---

## Integración Jenkins + SonarQube

### 1. Configurar SonarQube en Jenkins:

1. Ir a: `Manage Jenkins` > `Configure System`
2. Buscar sección `SonarQube servers`
3. Click en `Add SonarQube`
4. Configurar:
   - Name: `SonarQube`
   - Server URL: `http://sonarqube:9000`
   - Server authentication token: Agregar credencial con el token de SonarQube

### 2. Instalar SonarQube Scanner en Jenkins:

1. Ir a: `Manage Jenkins` > `Global Tool Configuration`
2. Buscar sección `SonarQube Scanner`
3. Click en `Add SonarQube Scanner`
4. Configurar:
   - Name: `SonarQube Scanner`
   - Install automatically: ✓
   - Version: Seleccionar la más reciente

---

## Pipeline Completo CI/CD

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK 21'
    }
    
    environment {
        SONAR_TOKEN = credentials('sonarqube-token')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=msvc-security \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Build & Push Docker Image') {
            when {
                branch 'main'
            }
            steps {
                script {
                    docker.build("kevinmitsi/msvc-security:${env.BUILD_NUMBER}")
                    docker.build("kevinmitsi/msvc-security:latest")
                    
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-credentials') {
                        docker.image("kevinmitsi/msvc-security:${env.BUILD_NUMBER}").push()
                        docker.image("kevinmitsi/msvc-security:latest").push()
                    }
                }
            }
        }
        
        stage('Deploy to Development') {
            when {
                branch 'develop'
            }
            steps {
                sh 'docker-compose -f docker-compose.dev.yml up -d msvc-security'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo '✅ Pipeline ejecutado exitosamente'
        }
        failure {
            echo '❌ Pipeline falló'
        }
    }
}
```

---

## URLs de Acceso

| Servicio | URL | Usuario | Contraseña |
|----------|-----|---------|------------|
| Jenkins | http://localhost:8084 | admin | (ver comando arriba) |
| SonarQube | http://localhost:9000 | admin | admin |
| Grafana | http://localhost:3000 | admin | admin |
| RabbitMQ | http://localhost:15672 | admin | admin |

---

## Troubleshooting

### Jenkins no inicia:
```bash
# Ver logs
docker logs jenkins

# Reiniciar
docker-compose restart jenkins
```

### SonarQube no inicia:
```bash
# Aumentar límites de memoria virtual (Linux/Mac)
sudo sysctl -w vm.max_map_count=524288
sudo sysctl -w fs.file-max=131072

# Windows WSL2
wsl -d docker-desktop
sysctl -w vm.max_map_count=524288
```

### Error de permisos en Jenkins:
```bash
# Dar permisos al volumen
docker exec -u root jenkins chmod -R 777 /var/jenkins_home
```

---

## Mejores Prácticas

1. **Jenkins:**
   - Usar Jenkinsfile en el repositorio (Pipeline as Code)
   - Configurar webhooks para builds automáticos
   - Usar credenciales de Jenkins para secretos
   - Implementar stages paralelos cuando sea posible

2. **SonarQube:**
   - Configurar Quality Gates personalizados
   - Revisar regularmente los reportes
   - Mantener cobertura de código > 80%
   - Resolver bugs y vulnerabilidades críticas primero

3. **Integración:**
   - Ejecutar análisis de SonarQube en cada commit
   - Bloquear merge si Quality Gate falla
   - Generar reportes de Cucumber y JaCoCo
   - Automatizar despliegue solo si todos los checks pasan

