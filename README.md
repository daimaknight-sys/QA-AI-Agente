QA-AI-Agent

Agente de QA con IA que analiza un sitio web de punta a punta: lo recorre, entiende su estructura, genera tests ejecutables automáticamente, los corre de verdad, y reporta los hallazgos — con una interfaz de chat para preguntarle sobre lo que encontró.

Qué hace hoy
Crawler multi-página (BFS, mismo dominio, límite configurable) que recorre el sitio completo
Análisis de página: detecta inputs, botones y formularios, incluyendo campos ocultos que se revelan al interactuar con otros elementos
Generación automática de Page Objects y tests Playwright/TestNG ejecutables, sin escribir un test a mano
Detección de endpoints de API (sniffing de tráfico xhr/fetch durante el crawl, filtrando ruido de tracking de terceros) y generación de tests GET/POST/PUT/DELETE con validación de status code, tiempo de respuesta y autenticación
Escaneo de XSS reflejado en los campos del sitio
Reporte HTML interactivo con resumen, casos generados y resultados reales
Interfaz de chat conversacional (con IA vía OpenRouter) para preguntar sobre lo que el agente encontró
CI/CD con GitHub Actions: compila y corre los tests en cada push a main
Cómo se ve

Un ejemplo real sobre un e-commerce (Tata): el agente encontró un campo de búsqueda que acepta envío vacío sin validar, botones sin etiqueta accesible, y —al construir el módulo de API testing— un bug propio: un request GraphQL replicado sin Content-Type, que causaba un timeout 504 indistinguible de un bug real del sitio hasta que se comparó contra Chrome DevTools.

Arquitectura
agent/        → orquesta todo el flujo (AgentServer) y sirve la interfaz web
crawler/      → recorre el sitio (WebCrawler)
analyzer/     → extrae inputs, botones, forms de cada página (PageAnalyzer)
api/          → detecta endpoints de API reales (ApiEndpointSniffer)
generator/    → genera Page Objects, tests UI y tests de API
execution/    → ejecuta los tests reales contra la página (PlaywrightTestExecutor)
security/     → escaneo de XSS reflejado (XssScanner)
writer/       → escribe los archivos .java generados a disco
util/         → reportes (HtmlReportWriter)
ai/           → integración con IA para el chat conversacional
Stack técnico

Java, Maven, Playwright, TestNG, OpenRouter (IA), GitHub Actions.

Roadmap
Generación automática de tests Playwright con Page Object Model
API Testing (detección de endpoints + tests GET/POST/PUT/DELETE)
CI/CD con GitHub Actions
Performance Testing con k6
Detección inteligente de bugs por reglas de negocio
Generación automática de Bug Reports estructurados
Evidencia enriquecida (screenshots, HTML, consola, red)
Accessibility Testing (ARIA, labels, navegación por teclado, contraste)
Cross-browser testing (Chromium, Firefox, WebKit)
Risk-Based Testing Engine
Convertirlo en plataforma web: interfaz sin código, análisis asíncrono, dashboard multi-usuario con historial