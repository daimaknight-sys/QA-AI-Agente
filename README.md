# QA-AI-Agent

Agente de QA automatizado que recorre páginas web, analiza sus elementos interactivos y genera sugerencias de casos de prueba, combinando automatización con Playwright y lógica de análisis en Java.

## 🎯 Objetivo del proyecto

Explorar cómo la automatización de pruebas tradicional puede combinarse con generación inteligente de casos de prueba, como base para un asistente de QA que reduzca el trabajo manual de exploración de una aplicación web.

## 🧱 Arquitectura

El proyecto está organizado en módulos independientes, cada uno con una responsabilidad clara:
QA-AI-Agent
├── execution   → Orquesta el navegador (Playwright) y dispara el flujo completo
├── crawler     → Navega una URL y recolecta los links encontrados en la página
├── analyzer    → Analiza el DOM de la página: cuenta y extrae detalles de inputs, botones, forms, links e imágenes
├── generator   → A partir del análisis, genera sugerencias de casos de prueba
├── agent       → (en construcción) Orquestación general del agente
└── util        → Utilidades compartidas

## ⚙️ Stack técnico

- **Java 17**
- **Maven** (gestión de dependencias y build)
- **Playwright** (automatización de navegador)
- **TestNG** (framework de testing)
- **Jackson** (manejo de JSON, para futuras integraciones)

## ✅ Estado actual

- [x] Navegación automatizada con Playwright
- [x] Crawler que recolecta links de una página
- [x] Analyzer que detecta inputs, botones, forms, links e imágenes
- [x] Generator que sugiere casos de prueba a partir del análisis (inputs)
- [ ] Generator: casos de prueba para botones y formularios
- [ ] Manejo de inputs sin atributo `name` (fallback a `id`)
- [ ] Integración con IA para análisis de resultados de pruebas fallidas
- [ ] Generación de reportes exportables

## ▶️ Cómo correr el proyecto

1. Cloná el repositorio
2. Abrí el proyecto en IntelliJ IDEA
3. Asegurate de tener los navegadores de Playwright instalados (correr una vez `CLI.main(new String[]{"install"})`)
4. Ejecutá la clase `TestRunner` (paquete `execution`)

## 🚧 Próximos pasos

Este proyecto está en desarrollo activo como parte de mi portfolio de QA. Los próximos módulos van a enfocarse en generar casos de prueba ejecutables (no solo sugerencias en texto) y en incorporar análisis con IA sobre los resultados de las pruebas.

## 👩‍💻 Autora

Daima — QA Engineer con foco en automatización y calidad de software.