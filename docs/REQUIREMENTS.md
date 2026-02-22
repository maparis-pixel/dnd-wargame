# D&D Wargames - Requisitos Funcionales

**Estado:** Actualizado - Wargame Scale  
**Versión:** 2.0  
**Fecha:** 2026-02-08

---

## 0. Visión General del Sistema

El juego es un simulador de combate a **escala de batallón** (wargames) con dos tipos de unidades:

1. **Personajes Individuales**: Héroes únicos (Guerreros, Magos, Clérigos) que actúan solos
2. **Unidades de Combate**: Grupos de monstruos (30 trasgos, 20 esqueletos) representados como un único símbolo con **Puntos de Fuerza (PF)**

---

## 1. Requisitos Funcionales (FR)

## 1. Requisitos Funcionales (FR)

### FR-1: Sistema de Unidades Duales
**Descripción**: Soportar dos tipos de unidades en el campo de batalla

#### FR-1.1: Personajes Individuales
- Crear personaje con stats D&D estándar (STR, DEX, CON, INT, WIS, CHA)
- Rastrear HP (Puntos de Vida) del personaje
- Soportar clases: Guerrero, Mago, Clérigo, Bardo, Pícaro
- Soportar habilidades especiales por clase

#### FR-1.2: Unidades de Combate
- Crear unidad de monstruos con:
  - **Puntos de Fuerza (PF)**: Número de criaturas en la unidad
  - **Tipo de Criatura**: Trasgo, Esqueleto, Ogro, etc.
  - **Clase de Armadura (CA)**: Umbral de defensa
  - **Bono de Ataque**: Modificador de ataque base
  - **Daño Base**: Daño promedio sin bonificadores
  - **Dureza** (opcional): Para criaturas grandes (HP > 20)
  
- Unidad puede tener un **Personaje Especial Líder** (Capitán, Sargento)

### FR-2: Mecánica de Combate a Escala
**Descripción**: Resolver ataques entre unidades sin tiradas masivas

#### FR-2.1: Ataque General
- Una unidad lanza **1d20** (no múltiples dados)
- Suma: Bono de Ataque + Bono de Masa
  - Bono de Masa = +1 por cada 5 PF actuales
  - Ejemplo: Unidad con 12 PF suma +2
- Compara contra CA del objetivo

#### FR-2.2: Daño de Unidad a Unidad
- Impacto = **1 PF** de daño
- Si supera CA por 5+: **+1 PF** adicional (Regla de Hender)
- Reducir PF representa muertes en la unidad

#### FR-2.3: Daño a Personaje Individual
- Unidad que impacta personaje le causa:
  - Daño = (Daño Base Promedio) + (Bono de Masa)
  - Daño va a HP del personaje, no a PF

#### FR-2.4: Dureza para Criaturas Grandes
- Criaturas con HP < 20: 1 impacto = 1 PF
- Criaturas Grandes (HP 20-50): Dureza 3
  - Necesita 3 impactos exitosos para causar 1 PF de daño
- Criaturas Muy Grandes (HP > 50): Dureza 5+

### FR-3: Combate Personaje Individual vs Unidad
**Descripción**: Interacción asimétrica cuando un héroe enfrenta una turba

#### FR-3.1: Ataque del Personaje a Unidad
- Personaje ataca con **Ventaja** contra unidades (fácil acertar masa)
- Cada impacto exitoso = **1 PF** de daño
- Si supera CA por 5+: Causa +1 PF (Regla de Hender)
- Ataque Extra del personaje: Múltiples ataques en un turno

#### FR-3.2: Ataque de Unidad a Personaje
- Unidad suma: Bono Ataque + Bono de Masa
- Compara contra CA del personaje
- Si impacta: Daño a HP del personaje (D&D normal)

#### FR-3.3: Zona de Amenaza
- Unidad de combate tiene zona de amenaza de 5 pies
- Si personaje entra/se mueve dentro: Unidad puede hacer Ataque de Oportunidad con **Desventaja**

#### FR-3.4: Desafío al Líder (Duelo)
- Personaje puede atacar específicamente al Líder de una unidad
- Ataque con **Desventaja**
- Si impacta: Daño va directo al Líder (no reduce PF masivo)
- Si Líder muere: Unidad hace TS de Moral (Sabiduría CD 10) o queda Aturdida 1 turno

#### FR-3.5: Arrollar (Overrun)
- Unidades Grandes/Muy Grandes pueden intentar Overrun
- Personaje hace TS Destreza o Fuerza
- Si falla: Personaje es derribado (Prone)
- Sufre daño automático del Overrun

### FR-4: Reacciones Heroicas (2 por ronda contra diferentes unidades)
**Descripción**: Habilidades especiales de personajes solos para no ser arrollados

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

### FR-5: Sistema de Magia a Gran Escala
**Descripción**: Hechizos de personajes afectan a unidades, no a individuos

#### FR-5.1: Hechizos de Daño (Magos)
- Bola de Fuego, Trueno, etc.
- Unidad objetivo hace TS (Destreza, Constitución, etc.)
- Si falla: Pierde PF = Nivel de Hechizo + Modificador de Inteligencia
- Si pasa: Mitad del daño

#### FR-5.2: Hechizos de Control (Magos)
- Muro de Fuego, Muro de Hielo
- Crea obstáculo infranqueable para unidades durante X turnos
- Puede bloquear movimiento entre sectores

#### FR-5.3: Hechizos de Sostenimiento (Clérigos)
- Palabra de Sanación Masiva:
  - Recupera 1d4 PF a una unidad aliada
  - Representa soldados rezagados que se reincorporan
- Bendición Marcial:
  - Unidad suma 1d4 a tiradas de ataque y TS durante combate

#### FR-5.4: Efectos de Bardo (Estandarte/Táctica)
- Inspiración de Batalla:
  - Bardo se une a unidad (movimiento compartido)
  - Unidad puede repetir 1 tirada de ataque fallida/turno
  - OR sumar dado de inspiración (ej. d8) al daño causado

### FR-6: Sistema de Moral y Cohesión
**Descripción**: Las unidades pueden romperse bajo presión

#### FR-6.1: Chequeo de Moral
- Unidad hace TS Sabiduría cuando:
  - Pierde 50%+ de PF en un turno
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

#### FR-11.1: Configuración de compañías
- Mostrar tipos de unidad disponibles.
- Para cada tipo, solicitar:
  - Equipo (`Aliados` o `Enemigos`)
  - Número de compañías
  - PF por compañía

#### FR-11.2: Ejecución por bloques de turnos
- Solicitar cuántos turnos ejecutar de seguido.
- Ejecutar el bloque solicitado.
- Mostrar resultado parcial (estado + log del bloque).
- Volver a solicitar nuevo número de turnos mientras la batalla siga activa.

#### FR-11.3: Finalización
- Detectar fin automático cuando un equipo quede sin unidades vivas.
- Mostrar ganador final y permitir iniciar nueva batalla.

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
- PF de unidades
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

## Versión
- v2.0 - Wargame Scale - Actualizado: 2026-02-08
- v1.0 - Definición Inicial - 2026-02-08
