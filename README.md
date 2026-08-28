# RescueSync BPMN

Modelo de proceso BPMN desarrollado en Bonita BPM para el Trabajo Práctico Integrador de la materia **Desarrollo de Software en Sistemas Distribuidos (Curso 2026)**.

## Descripción

RescueSync es una plataforma destinada a coordinar la respuesta ante desastres naturales mediante la colaboración entre municipios, centros coordinadores regionales y organizaciones no gubernamentales (ONGs).

Este repositorio contiene el modelo BPMN encargado de orquestar el ciclo de vida de una emergencia, desde su registro inicial hasta el cierre operativo de las actividades realizadas.

## Objetivos del Proceso

- Registrar emergencias reportadas por municipios afectados.
- Permitir la generación y publicación de lotes de necesidades.
- Coordinar la recepción de ofertas de ayuda por parte de ONGs.
- Gestionar ventanas temporales mediante eventos de temporizador.
- Validar ofertas mediante integración con servicios externos.
- Permitir la adjudicación de recursos.
- Monitorear la ejecución de las actividades.
- Registrar la finalización y cierre de los proyectos.
- Liberar recursos comprometidos en el sistema nacional.

## Actores Identificados

### Operador Municipal
Responsable de registrar emergencias y adjudicar las ofertas validadas.

### Centro Coordinador Regional
Responsable de analizar emergencias, generar lotes de necesidades, publicar convocatorias y supervisar la ejecución.

### Representante de ONG
Responsable de registrar ofertas de ayuda, actualizar recursos disponibles y reportar la finalización de actividades.

### Auditor / Directivo
Responsable de consultar indicadores, métricas y reportes del sistema.

## Tecnologías

- Bonita BPM Community
- BPMN 2.0
- Git
- GitHub

## Estructura del Repositorio

```text
app/
└── diagrams/
    └── Diagram-1.0.proc
```

Archivo principal del proceso:

`app/diagrams/Diagram-1.0.proc`

## Requisitos Funcionales Implementados

- Registro de emergencia.
- Generación de lotes de necesidades.
- Convocatoria de ofertas.
- Recepción de propuestas de ONGs.
- Control de ventana temporal mediante Timer Events.
- Evaluación de cobertura de lotes.
- Escenarios alternativos ante cobertura insuficiente.
- Adjudicación de recursos.
- Seguimiento operativo.
- Cierre y liberación de recursos.

## Integraciones Previstas

### Aplicación Web Local

Encargada de:

- Gestión de usuarios.
- Administración de ofertas.
- Gestión de inventario de ONGs.
- Comunicación con Bonita mediante API REST.

### Sistema Nacional de Gestión de Recursos y Riesgos

Encargado de:

- Autenticación JWT.
- Validación de competencias.
- Consulta de certificaciones.
- Bloqueo concurrente de recursos.
- Liberación de recursos al finalizar actividades.

## Instalación

Clonar el repositorio:

```bash
git clone https://github.com/Miguel-Alejandro-Castillo/RescueSync-BonitaBPM.git
```

Importar el archivo `.proc` en Bonita Studio.

## Autor

Miguel Alejandro Castillo Figueroa

## Materia

Desarrollo de Software en Sistemas Distribuidos

Trabajo Práctico Integrador 2026
