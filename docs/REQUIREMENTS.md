# D&D Wargames - Requisitos Funcionales

**Estado:** Actualizado - Warhammer HP System  
**Versión:** 3.2  
**Fecha:** 2026-02-24

---

## 0. Visión General del Sistema

El juego es un simulador de combate a **escala de batallón** (wargames) con mecánicas híbridas D&D + Warhammer:

### Tipos de Unidades:
1. **Personajes Individuales**: Héroes únicos (Guerreros, Magos, Clérigos) que actúan solos
2. **Unidades de Combate**: Compañías de criaturas con **HP total por compañía** (suma de HP individuales)

### Mecánicas Clave (v3.0 - Sistema Warhammer):
- **HP por Compañía**: En lugar de PF, cada compañía tiene HP = (criaturas × HP individual)
- **Formación**: Frente, flancos, filas atacantes (5ft=1, 10ft=2, 15ft=3)
- **Frente efectivo de ataque**: Máximo frente enemigo +2 (con extensión si completa 2 filas)
- **Multi-ataques**: Ataques según frente efectivo × filas alcanzables con criaturas disponibles
- **Bono por filas**: +2 al ataque por fila ocupada desde la segunda
- **Moral Warhammer**: Chequeo 2d6 vs valor de moral (2-12) cuando porta estandarte cae o 50% de bajas
- **Huida**: Si falla moral, la unidad huye del combate

### Objetivo Cloud (Plan v3.4)
- Desplegar el modo web en AKS con suscripción **Azure for Students**.
- Mantener coste mínimo operativo dentro del crédito disponible.

---

## 1. Requisitos Funcionales (FR)

### FR-1: Sistema de Unidades Duales
**Descripción**: Soportar dos tipos de unidades en el campo de batalla

#### FR-1.1: Personajes Individuales
- Crear personaje con stats D&D estándar (STR, DEX, CON, INT, WIS, CHA)
- Rastrear HP (Puntos de Vida) del personaje
- Soportar clases: Guerrero, Mago, Clérigo, Bardo, Pícaro
- Soportar habilidades especiales por clase

#### FR-1.2: Unidades de Combate (Sistema v3.0)
- Crear unidad de criaturas con:
  - **Hit Points (HP)**: HP total de la compañía = criaturas × HP individual
  - **Criaturas Count**: Número de criaturas en la compañía
  - **Tipo de Criatura**: Trasgo, Esqueleto, Ogre, Guardia Humano, Orco
  - **Clase de Armadura (CA)**: Umbral de defensa (AC individual)
  - **Bono de Ataque**: Modificador de ataque base
  - **Daño Base**: Daño promedio por ataque
  - **Alcance (Reach)**: 5ft estándar, 10ft para criaturas grandes
  - **Formación**:
    - `frontWidth`: Ancho del frente en criaturas
    - `flankExposure`: Exposición de flancos en criaturas
    - `rowsAttacking`: Filas máximas por alcance (5ft=1, 10ft=2, 15ft=3)
  - **Moral Warhammer**: Valor entre 2-12 para chequeo 2d6
  - **Porta Estandarte**: Boolean (true si compañía ≥ 10 criaturas)
  
- Daño recibido reduce HP total, recalculando `creaturesCount` dinámicamente
- Formación se recalcula automáticamente al perder criaturas

### FR-2: Mecánica de Combate a Escala Warhammer
**Descripción**: Resolver ataques con formación y multi-ataques

#### FR-2.1: Ataque General
- Unidad lanza **múltiples d20** según formación:
  - Frente efectivo inicial ≤ `frente enemigo + 2`
  - Si completa dos filas, puede extender el frente con criaturas sobrantes
  - Ataques disponibles = min(`criaturesCount`, `frenteEfectivo × filasMaxAlcance`)
- Cada ataque: `d20 + Bono de Ataque` vs CA del objetivo
- Modificador adicional por formación: `+2` por cada fila ocupada desde la segunda
- Impactos causan daño en HP (no más PF)

#### FR-2.2: Daño de Unidad a Unidad
- Cada impacto = `Daño Base` del atacante
- Si supera CA por 5+: Daño × 1.5 (Regla de Hender)
- Daño total acumulado reduce HP de la unidad defensora
- HP perdidos reducen `creaturesCount` = ceil(HP / HP_individual)

#### FR-2.3: Formación y Filas Atacantes
- Formación cuadrada aproximada: `frontWidth = ceil(sqrt(criaturesCount))`
- Depth (profundidad) = `ceil(creaturesCount / frontWidth)`
- `flankExposure` = min(depth, creaturesCount)
- **Filas atacantes**:
  - 1 fila si `reachFeet <= 5`
  - 2 filas si `reachFeet >= 10`
  - 3 filas si `reachFeet >= 15`

### FR-2.5: Turnos y Rondas
- Un turno se compone de **dos rondas**:
  1) ronda del equipo con mayor iniciativa viva,
  2) ronda del equipo contrario.
- Al inicio de batalla, cada combatiente tira iniciativa una sola vez: `1d20 + mod DEX`.
- La unidad/personaje con mayor iniciativa viva define el equipo que abre el turno.

#### FR-2.4: Daño a Personaje Individual
- Unidad que impacta personaje le causa:
  - Daño total = suma de todos los impactos × Daño Base
  - Daño va a HP del personaje

### FR-3: Combate Personaje Individual vs Unidad
**Descripción**: Interacción asimétrica cuando un héroe enfrenta una turba

#### FR-3.1: Ataque del Personaje a Unidad
- Personaje ataca con **Ventaja** contra unidades (fácil acertar masa)
- Cada impacto exitoso = Daño del personaje a HP de la unidad
- Si supera CA por 5+: Daño × 1.5 (Regla de Hender)
- Ataque Extra del personaje: Múltiples ataques en un turno

#### FR-3.2: Ataque de Unidad a Personaje
- Unidad usa formación completa (múltiples ataques)
- Cada impacto causa Daño Base a HP del personaje
- Si impacta: Daño a HP del personaje (D&D normal)

### FR-4: Sistema de Moral Warhammer

#### FR-4.1: Repliegue Táctico
- Disponible para: Pícaros, Monjes, Guardabosques
- Activación: Unidad entra en zona de amenaza
- Efecto: Personaje se mueve 50% velocidad SIN provocar AoO
- Resultado: Reposiciona, evita flanqueo

#### FR-4.2: Torbellino de Acero
- Disponible para: Guerreros, Bárbaros, Paladines
- Activación: Unidad intenta rodiar
- Efecto: Personaje hace ataque inmediato contra unidad
- Si impacta: Unidad pierde 1 PF + detiene movimiento
- Si crítico: Unidad queda Aturdida hasta próximo turno

#### FR-4.3: Escudo de Repulsión
- Disponible para: Magos, Clérigos, Brujos
- Activación: Unidad entra en zona de amenaza
- Gasto: 1 espacio de hechizo nivel 1
- Efecto: Unidad hace TS Fuerza contra CD de salvación
- Si falla: Empujada 10 pies atrás + pierde acción de ataque ese turno
### FR-4: Sistema de Moral Warhammer
**Descripción**: Chequeos de moral 2d6 vs valor de moral (2-12)

#### FR-4.1: Valor de Moral
- Cada tipo de criatura tiene valor de moral entre 2-12:
  - **Goblin**: 5 (cobardes)
  - **Ogre**: 6 (estúpidos pero fuertes)
  - **Orc**: 7 (agresivos pero indisciplinados)
  - **Human Guard**: 8 (disciplinados)
  - **Skeleton**: 10 (sin miedo)
- Mayor valor = más difícil fallar chequeo

#### FR-4.2: Triggers de Chequeo de Moral
Compania debe realizar chequeo 2d6 vs moral cuando:
- **Porta Estandarte cae**: `hasStandardBearer` pasa de true → false

#### FR-4.2.b: Rota por 50% (obligatorio)
- **50% de bajas**: al cruzar 50% de HP, estado **Rota** y retirada obligatoria (sin tirada)

#### FR-4.3: Resolucion del Chequeo
- Tirar **2d6**
- Si resultado ≤ valor de moral: **Pasa** (mantiene posicion)
- Si resultado > valor de moral: **Falla** (huye del combate)
  - `hasFledBattle = true`
  - `moraleStatus = FRIGHTENED`
  - Unidad sale del combate (no puede atacar ni ser atacada)

#### FR-4.4: Retirada obligatoria (Rota)
- Al 50% de HP la unidad entra en estado **Rota** y se retira automaticamente.
- `moraleStatus = BROKEN`
- No hay tirada de moral en este caso.

#### FR-4.5: Inmunidad al Miedo
- **Skeleton** con moral ≥ 10: Automáticamente pasa chequeos (sin miedo)
- Otras unidades no-muertas pueden tener inmunidad específica

#### FR-4.6: Persecucion o retarget tras huida
- La unidad atacante decide si **persigue** a la unidad en huida.
- Si no persigue y hay otro objetivo, puede **retarget** en el mismo turno.

#### FR-4.7: Reagrupamiento
- Una unidad en retirada puede intentar **reagruparse** una sola vez.
- Requiere personaje aliado vivo en trayectoria.
- Tirada **2d6** vs moral: si pasa, vuelve al combate.

### FR-5: Sistema de Magia a Gran Escala
**Descripción**: Hechizos de personajes afectan a unidades, no a individuos

#### FR-5.1: Hechizos de Daño (Magos)
- Bola de Fuego, Trueno, etc.
- Unidad objetivo hace TS (Destreza, Constitución, etc.)
- Si falla: Pierde HP = Nivel de Hechizo + Modificador de Inteligencia
- Si pasa: Mitad del daño

#### FR-5.2: Hechizos de Control (Magos)
- Muro de Fuego, Muro de Hielo
- Crea obstáculo infranqueable para unidades durante X turnos
- Puede bloquear movimiento entre sectores

#### FR-5.3: Hechizos de Sostenimiento (Clérigos)
- Palabra de Sanación Masiva:
  - Recupera 1d4 HP a una unidad aliada
  - Representa soldados rezagados que se reincorporan
- Bendición Marcial:
  - Unidad suma 1d4 a tiradas de ataque y TS durante combate

#### FR-5.4: Efectos de Bardo (Estandarte/Táctica)
- Inspiración de Batalla:
  - Bardo se une a unidad (movimiento compartido)
  - Unidad puede repetir 1 tirada de ataque fallida/turno
  - OR sumar dado de inspiración (ej. d8) al daño causado

### FR-6: Sistema de Moral y Cohesion
**Descripción**: Las unidades pueden romperse bajo presión

#### FR-6.1: Chequeo de Moral
- Unidad hace TS Sabiduria cuando:
  - Pierde 50%+ de HP en un turno
  - Su Líder muere
  - Aliados importantes huyen
- CD 10 típicamente
- Fallo: Unidad queda Asustada o Confundida
- Éxito: Continúa combatiendo normalmente

#### FR-6.2: Estados Especiales
- **Asustada**: -2 a tiradas de ataque de unidad
- **Confundida**: -1 a tiradas de ataque de unidad
- **Enfurecida**: +2 a tiradas de ataque de unidad
- **Aturdida**: Pierde acción dedicada siguiente turno

### FR-11: Interfaz Web de Configuración y Ejecución por Bloques
**Descripción**: Permitir configurar batalla y ejecutar turnos desde navegador.

#### FR-11.1: Configuracion de companias
- Mostrar tipos de unidad disponibles.
- Para cada tipo, solicitar:
  - Equipo (`Aliados` o `Enemigos`)
  - Numero de companias
  - Criaturas por compania

#### FR-11.2: Ejecucion por bloques de turnos
- Solicitar cuántos turnos ejecutar de seguido.
- Ejecutar el bloque solicitado.
- Mostrar resultado parcial (estado + log del bloque).
- Mostrar tiradas de moral y resumen de estado por turno.
- Volver a solicitar nuevo número de turnos mientras la batalla siga activa.

#### FR-11.3: Finalización
- Detectar fin automático cuando un equipo quede sin unidades vivas.
- Mostrar ganador final y permitir iniciar nueva batalla.

### FR-12: Visualización de estadísticas de unidad
**Descripción**: Mostrar el bloque de estadísticas de cada tipo de unidad en CLI y Web.

#### FR-12.1: Campos minimos visibles
- AC
- HP actual/maximo de compania
- HP por criatura + formula de dados
- Velocidad
- STR/DEX/CON/INT/WIS/CHA
- Ataque principal y secundario
- Estado de moral

#### FR-12.2: Cobertura de unidades base
- Guard
- Orc
- Goblin
- Skeleton
- Ogre

### FR-13: Despliegue en AKS (Azure for Students)
**Descripción**: Ejecutar la interfaz web del sistema en Kubernetes administrado (AKS) para pruebas cloud.

#### FR-13.1: Entorno mínimo AKS
- Crear clúster AKS en `tier free`.
- Ejecutar aplicación con **1 nodo** de tamaño pequeño (ej. `Standard_B2s`).
- Desplegar aplicación web con `Deployment` + `Service` + `Ingress`.

#### FR-13.2: Contenerización y configuración
- Proveer imagen de contenedor ejecutable del modo web.
- Parametrizar configuración por variables de entorno.
- Permitir actualización de imagen sin reconstruir manifiestos base.

#### FR-13.3: Operatividad mínima
- Exponer endpoint web accesible para pruebas funcionales.
- Incluir endpoints de salud para liveness/readiness.
- Permitir redeploy sin intervención manual compleja.

### NFR-7: Restricción de coste (Azure for Students)
- El entorno cloud debe diseñarse con consumo mínimo.
- Deben existir alertas de presupuesto y seguimiento de gasto.
- Se evitarán recursos no esenciales en fase MVP (multi-zona, componentes premium no requeridos).

### FR-7: Sistema de Battlefield (Campo de Batalla)
**Descripción**: Mapa hexagonal o cuadriculado para posicionamiento

#### FR-7.1: Movimiento
- Cada unidad tiene velocidad (30, 40, 60 pies típicamente)
- Hexágonos o cuadros de 5 pies
- Personaje individual: Movimiento D&D estándar

#### FR-7.2: Sectores/Zonas
- Posiciones tácticas (terreno alto, cobertura, etc.)
- Bonificadores según posición:
  - Terreno alto: +1 a tiradas de ataque
  - Cobertura: +1 CA
  - Terreno difícil: Movimiento reducido

### FR-8: Sistema de Inventario y Equipamiento
**Descripción**: Items y equipamiento afectan a personajes, no a unidades

#### FR-8.1: Armas
- Modifican bono de ataque y daño
- Personaje jugador: Afecta ataques individuales
- Unidades: Parte del stat base de la unidad

#### FR-8.2: Armadura
- Modifica CA
- Personaje jugador: CA individual
- Unidades: Parte del stat base de la unidad

### FR-9: Persistencia (Guardar/Cargar Batalla)
**Descripción**: Guardar estado completo del campo de batalla

#### FR-9.1: Guardar Batalla
- Número y posición de unidades
- HP de personajes individuales
- HP de unidades
- Estados especiales (Asustada, Aturdida, etc.)
- Historia de movimientos

#### FR-9.2: Cargar Batalla
- Restaurar estado completo
- Reanudar desde turno guardado

### FR-10: Sistema de Turnos y Rondas
**Descripción**: Gestión del flujo de combate

#### FR-10.1: Iniciativa
- Todos los combatientes tiran 1d20 + DEX
- Personajes individuales: Usan DEX completa
- Unidades: Usan DEX del tipo de criatura

#### FR-10.2: Orden de Turno
- Mayor resultado primero
- Alternar jugador vs enemigos
- Personalajes especiales Líderes usan su iniciativa propia

#### FR-10.3: Acciones por Turno
- Movimiento (hasta velocidad máxima)
- Acción (Atacar, Lanzar Hechizo, Usar Item)
- Reacción (Ataque de Oportunidad, Repliegue, etc.)


## 2. Requisitos No Funcionales (NFR)

### NFR-1: Performance
- Cálculo de turno < 500ms incluso con 20+ unidades
- Sin lag en posicionamiento de mapa
- Renderizado fluido de estado de batalla

### NFR-2: Escalabilidad
- Soportar 50+ unidades simultáneamente
- Mapa flexible (desde 20x20 hasta 100x100 hexágonos)
- Arquitectura modular para agregar nuevas mecánicas

### NFR-3: Mantenibilidad
- Código bien documentado (JavaDoc)
- 80%+ test coverage
- Separación clara entre lógica de combate y presentación

### NFR-4: Compatibilidad
- JDK 25.0.2 (Eclipse Adoptium)
- Windows, Linux, macOS
- Almacenamiento de batallas en JSON o formato portable

### NFR-5: Usabilidad
- Interfaz clara en texto/consola
- Mensajes descriptivos de combate
- Help/tutorial disponible
- Mostrar estado de unidades claramente

---

## 3. Criterios de Aceptación

### CA-1: Unidad de Combate Funciona
```
GIVEN: Una unidad de 10 Trasgos (10 PF)
WHEN: Se ataca con bono de ataque +2, resultado 18 vs CA 13
THEN: Unidad pierde 1 PF (ahora 9 PF)
```

### CA-2: Personaje vs Unidad Desigual
```
GIVEN: Guerrero (18 STR) vs Unidad de 15 Trasgos
WHEN: Guerrero ataca con Ventaja, Ataque Extra
THEN: Puede reducir varios PF en un turno
AND: Unidad puede hacer Ataque de Oportunidad si entra en zona amenaza
```

### CA-3: Reacciones Heroicas Funcionan
```
GIVEN: Pícaro con Repliegue Táctico activado
WHEN: Unidad entra en su zona de amenaza
THEN: Pícaro puede moverse 50% velocidad SIN provocar AoO
```

### CA-4: Magia a Gran Escala
```
GIVEN: Mago lanza Bola de Fuego contra Unidad de 12 Ogros
WHEN: Unidad falla TS Destreza
THEN: Pierde PF = Nivel 3 + Modificador INT (ejemplo: 4 PF)
```

### CA-5: Moral y Cohesión
```
GIVEN: Unidad pierde 50% de sus PF en un turno
WHEN: Hace TS Sabiduría
THEN: Si falla, queda Asustada o Confundida
```

### CA-6: Persistencia Funciona
```
GIVEN: Batalla guardada con 3 unidades jugador vs 4 enemigas
WHEN: Se carga la batalla
THEN: Mismo número, PF, posiciones, estados se restauran
```

---

## 4. Out of Scope (Fase 2+)

- [ ] Multiplayer tiempo real
- [ ] Campañas dinámicas con campaña
- [ ] Inteligencia artificial avanzada
- [ ] Gráficos visuales (sigue siendo texto/consola)
- [ ] Sistema de experiencia y leveleo
- [ ] Customización de razas/clases

---

## 5. Prioridades MVP (Fase 1)

**MUST HAVE:**
1. Unidades de combate con PF
2. Combate Unidad vs Unidad (1d20 con bono masa)
3. Personaje vs Unidad asimétrico
4. Sistema de Reacciones Heroicas (mínimo 1 tipo)
5. Sistema de dados D20 estándar

**SHOULD HAVE:**
6. Magia básica (daño de área)
7. Moral y Cohesión
8. Posicionamiento en mapa
9. Múltiples tipos de unidades

**NICE TO HAVE:**
10. Guardar/Cargar batalla
11. Líder/Capitán en unidades
12. Sistema de Bardo/Inspiración
13. Terreno y cobertura

---

## 6. Definiciones y Glosario

| Término | Definición |
| --- | --- |
| **PF (Puntos de Fuerza)** | Número de criaturas individuales en una unidad. 1 PF = 1 criatura. |
| **Bono de Masa** | +1 por cada 5 PF actuales. Representa ventaja numérica. |
| **CA (Clase de Armadura)** | Número a batir en d20 para impactar. Igual entre unidades y personajes. |
| **Dureza** | Multiplicador de resistencia para criaturas grandes. Ej. Dureza 3 = necesita 3 impactos para 1 PF. |
| **Regla de Hender** | Si superas CA por 5+, causa +1 PF adicional. |
| **Ventaja** | Tira 2d20 y usa el mayor. |
| **Desventaja** | Tira 2d20 y usa el menor. |
| **AoO (Ataque de Oportunidad)** | Ataque reactivo cuando enemigo abandona zona de amenaza. |
| **Reacción Heroica** | Habilidad especial de personaje solo contra unidades. |
| **Líder/Capitán** | Personaje especial dentro de una unidad que mejora su moral. |
| **TS (Tirada de Salvación)** | d20 + Modificador de atributo vs CD, para resistir efectos. |
| **Moral** | Capacidad de una unidad para mantener cohesión bajo estrés. |
| **Overrun** | Intento de unidad grande de atropellar personaje resultando en Prone. |

---

## 7. Plan Funcional v3.2 y Decisiones Cerradas

### Decisiones Cerradas
- Se mantiene el **modo detallado** como sistema final principal.
- Escala táctica objetivo: **20 ft** por casilla.
- Fallo de moral por 50% de bajas: unidad **Rota** con retirada obligatoria.
- Persecución post-huida: decisión manual de jugador en CLI.

### Requisitos Funcionales de la Siguiente Iteración
- FR-13: Estado `Rota` bloquea acciones ofensivas y fuerza retirada.
- FR-14: Tras huida, el atacante elige entre perseguir o cambiar objetivo.
- FR-15: Reagrupamiento posible una sola vez si hay personaje aliado en ruta.
- FR-16: La salida de combate debe reportar chequeo de moral, retirada, persecución y reagrupamiento.

### Requisitos Funcionales de Plataforma (v3.3)
- FR-17: La web debe incluir una página de **Alta de tipo de unidad**.
- FR-18: El usuario puede introducir una URL de ficha D&D para iniciar importación.
- FR-19: Un agente transforma la URL a un esquema estándar de entrada de unidad.
- FR-20: El usuario puede revisar/editar los campos importados antes de guardar.
- FR-21: El tipo guardado queda disponible para crear unidades nuevas en combate.
- FR-22: Debe registrarse trazabilidad mínima (`sourceUrl`, fecha de importación, errores/ajustes).

---

## Versión
- v2.0 - Wargame Scale - Actualizado: 2026-02-08
- v1.0 - Definición Inicial - 2026-02-08

## Changelog Corto
- 2026-02-22: Reglas de filas por alcance actualizadas a 5/10/15ft = 1/2/3.
- 2026-02-22: Nuevo requisito de frente efectivo limitado por frente enemigo +2.
- 2026-02-22: Añadidos bono por filas y estructura de turno con 2 rondas por turno.
- 2026-02-22: Nuevos FR-17..FR-22 para alta de tipos de unidad por URL con agente.
