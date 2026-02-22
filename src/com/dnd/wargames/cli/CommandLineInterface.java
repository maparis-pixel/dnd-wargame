package com.dnd.wargames.cli;

import com.dnd.wargames.units.*;
import com.dnd.wargames.battle.WargameBattleEngine;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaz de línea de comandos para D&D Wargames.
 * Maneja la interacción con el usuario a través de la terminal.
 * 
 * @author Lead Developer
 * @version 2.0
 */
public class CommandLineInterface {
    private Scanner scanner;
    private boolean running;
    private List<com.dnd.wargames.units.Character> characters;
    private List<CombatUnit> units;
    private WargameBattleEngine battleEngine;
    private boolean inBattle;

    /**
     * Constructor de la interfaz CLI.
     */
    public CommandLineInterface() {
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.characters = new ArrayList<>();
        this.units = new ArrayList<>();
        this.battleEngine = new WargameBattleEngine();
        this.inBattle = false;
    }

    /**
     * Inicia la interfaz de línea de comandos.
     */
    public void start() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  ⚔️ D&D Wargames - CLI Mode            ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        showMainMenu();

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            try {
                processCommand(input);
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }

        System.out.println("¡Gracias por jugar D&D Wargames!");
    }

    /**
     * Muestra el menú principal.
     */
    private void showMainMenu() {
        System.out.println("=== MENÚ PRINCIPAL ===");
        if (!inBattle) {
            System.out.println("1. Crear Personaje");
            System.out.println("2. Crear Unidad");
            System.out.println("3. Ver Unidades Creadas");
            System.out.println("4. Configurar Batalla");
            System.out.println("5. Ayuda");
            System.out.println("6. Salir");
        } else {
            System.out.println("1. Ejecutar Turno");
            System.out.println("2. Ver Estado de Batalla");
            System.out.println("3. Terminar Batalla");
            System.out.println("4. Ayuda");
            System.out.println("5. Salir");
        }
        System.out.println();
    }

    /**
     * Procesa un comando del usuario.
     */
    private void processCommand(String command) {
        if (!inBattle) {
            processPreBattleCommand(command);
        } else {
            processBattleCommand(command);
        }
    }

    /**
     * Procesa comandos antes de la batalla.
     */
    private void processPreBattleCommand(String command) {
        switch (command.toLowerCase()) {
            case "1":
            case "crear":
            case "personaje":
                createCharacter();
                break;
            case "2":
            case "unidad":
                createUnit();
                break;
            case "3":
            case "ver":
                showUnits();
                break;
            case "4":
            case "configurar":
            case "batalla":
                setupBattle();
                break;
            case "5":
            case "ayuda":
            case "help":
                showHelp();
                break;
            case "6":
            case "salir":
            case "exit":
            case "quit":
                running = false;
                break;
            default:
                System.out.println("Comando no reconocido. Escribe 'ayuda' para ver comandos disponibles.");
                break;
        }
    }

    /**
     * Procesa comandos durante la batalla.
     */
    private void processBattleCommand(String command) {
        switch (command.toLowerCase()) {
            case "1":
            case "turno":
            case "ejecutar":
                executeTurn();
                break;
            case "2":
            case "estado":
            case "ver":
                battleEngine.showBattleStatus();
                System.out.println("Presiona Enter para continuar...");
                scanner.nextLine();
                showMainMenu();
                break;
            case "3":
            case "terminar":
            case "fin":
                endBattle();
                break;
            case "4":
            case "ayuda":
            case "help":
                showBattleHelp();
                break;
            case "5":
            case "salir":
            case "exit":
            case "quit":
                endBattle();
                running = false;
                break;
            default:
                System.out.println("Comando no reconocido. Escribe 'ayuda' para ver comandos disponibles.");
                break;
        }
    }

    /**
     * Inicia una nueva batalla.
     */
    private void startNewBattle() {
        System.out.println("🚀 Iniciando nueva batalla...");
        System.out.println("Funcionalidad de batalla próximamente disponible.");
        System.out.println("Presiona Enter para volver al menú principal.");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Configura la batalla asignando unidades a equipos.
     */
    private void setupBattle() {
        if (characters.isEmpty() && units.isEmpty()) {
            System.out.println("❌ No tienes unidades creadas. Crea algunas primero.");
            System.out.println("Presiona Enter para continuar...");
            scanner.nextLine();
            showMainMenu();
            return;
        }

        System.out.println("=== CONFIGURACIÓN DE BATALLA ===");
        System.out.println("Asigna tus unidades a los equipos:");
        System.out.println();

        // Mostrar unidades disponibles
        System.out.println("UNIDADES DISPONIBLES:");
        int index = 1;
        for (com.dnd.wargames.units.Character c : characters) {
            System.out.println(index + ". " + c);
            index++;
        }
        for (CombatUnit u : units) {
            System.out.println(index + ". " + u);
            index++;
        }

        System.out.println();
        System.out.println("EQUIPO 1 (Aliados):");
        assignUnitsToTeam(1);

        System.out.println();
        System.out.println("EQUIPO 2 (Enemigos):");
        assignUnitsToTeam(2);

        // Iniciar batalla
        battleEngine.startBattle();
        inBattle = true;
        showMainMenu();
    }

    /**
     * Asigna unidades a un equipo.
     */
    private void assignUnitsToTeam(int teamNumber) {
        List<com.dnd.wargames.units.Character> availableChars = new ArrayList<>(characters);
        List<CombatUnit> availableUnits = new ArrayList<>(units);

        while (true) {
            System.out.println("Unidades en Equipo " + teamNumber + ":");
            if (teamNumber == 1) {
                System.out.println("  Personajes: " + battleEngine.getTeam1Characters().size());
                System.out.println("  Unidades: " + battleEngine.getTeam1Units().size());
            } else {
                System.out.println("  Personajes: " + battleEngine.getTeam2Characters().size());
                System.out.println("  Unidades: " + battleEngine.getTeam2Units().size());
            }

            System.out.println("Opciones:");
            System.out.println("1. Agregar personaje");
            System.out.println("2. Agregar unidad");
            System.out.println("3. Finalizar equipo");
            System.out.print("Elige opción: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("3")) break;

            if (choice.equals("1") && !availableChars.isEmpty()) {
                // Mostrar personajes disponibles
                System.out.println("Personajes disponibles:");
                for (int i = 0; i < availableChars.size(); i++) {
                    System.out.println((i + 1) + ". " + availableChars.get(i).getName());
                }
                System.out.print("Elige personaje (número): ");
                try {
                    int charIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (charIndex >= 0 && charIndex < availableChars.size()) {
                        com.dnd.wargames.units.Character selected = availableChars.remove(charIndex);
                        if (teamNumber == 1) {
                            battleEngine.addToTeam1(selected);
                        } else {
                            battleEngine.addToTeam2(selected);
                        }
                        System.out.println("✓ " + selected.getName() + " agregado al Equipo " + teamNumber);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Número inválido.");
                }
            } else if (choice.equals("2") && !availableUnits.isEmpty()) {
                // Mostrar unidades disponibles
                System.out.println("Unidades disponibles:");
                for (int i = 0; i < availableUnits.size(); i++) {
                    System.out.println((i + 1) + ". " + availableUnits.get(i).getName());
                }
                System.out.print("Elige unidad (número): ");
                try {
                    int unitIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (unitIndex >= 0 && unitIndex < availableUnits.size()) {
                        CombatUnit selected = availableUnits.remove(unitIndex);
                        if (teamNumber == 1) {
                            battleEngine.addToTeam1(selected);
                        } else {
                            battleEngine.addToTeam2(selected);
                        }
                        System.out.println("✓ " + selected.getName() + " agregado al Equipo " + teamNumber);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Número inválido.");
                }
            } else {
                System.out.println("❌ Opción inválida o no hay unidades disponibles.");
            }
        }
    }

    /**
     * Ejecuta un turno de batalla.
     */
    private void executeTurn() {
        battleEngine.executeTurn();
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Termina la batalla actual.
     */
    private void endBattle() {
        inBattle = false;
        battleEngine = new WargameBattleEngine(); // Reset
        System.out.println("🏁 Batalla terminada. Volviendo al menú principal.");
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Crea un nuevo personaje.
     */
    private void createCharacter() {
        System.out.println("=== CREAR PERSONAJE ===");
        System.out.println("Clases disponibles:");
        System.out.println("1. Guerrero");
        System.out.println("2. Mago");
        System.out.println("3. Clérigo");
        System.out.print("Elige clase (1-3): ");

        int classChoice = Integer.parseInt(scanner.nextLine().trim());
        CharacterClass charClass = null;

        switch (classChoice) {
            case 1:
                charClass = CharacterClass.WARRIOR;
                break;
            case 2:
                charClass = CharacterClass.WIZARD;
                break;
            case 3:
                charClass = CharacterClass.CLERIC;
                break;
            default:
                System.out.println("Opción inválida.");
                showMainMenu();
                return;
        }

        System.out.print("Nombre del personaje: ");
        String name = scanner.nextLine().trim();

        System.out.print("Nivel (1-20): ");
        int level = Integer.parseInt(scanner.nextLine().trim());

        com.dnd.wargames.units.Character character = UnitFactory.createWarrior(name, level);
        if (charClass == CharacterClass.WIZARD) {
            character = UnitFactory.createWizard(name, level);
        } else if (charClass == CharacterClass.CLERIC) {
            character = UnitFactory.createCleric(name, level);
        }

        characters.add(character);
        System.out.println("✓ Personaje creado: " + character);
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Crea una nueva unidad.
     */
    private void createUnit() {
        System.out.println("=== CREAR UNIDAD ===");
        System.out.println("Tipos disponibles:");
        System.out.println("1. Guardias Humanos (disciplinados)");
        System.out.println("2. Orcos (agresivos)");
        System.out.println("3. Trasgos");
        System.out.println("4. Esqueletos");
        System.out.println("5. Ogros");
        System.out.print("Elige tipo (1-5): ");

        int typeChoice = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Cantidad de criaturas: ");
        int count = Integer.parseInt(scanner.nextLine().trim());

        CombatUnit unit = null;

        switch (typeChoice) {
            case 1:
                unit = UnitFactory.createHumanGuards(count);
                break;
            case 2:
                unit = UnitFactory.createOrcs(count);
                break;
            case 3:
                unit = UnitFactory.createGoblins(count);
                break;
            case 4:
                unit = UnitFactory.createSkeletons(count);
                break;
            case 5:
                unit = UnitFactory.createOgres(count);
                break;
            default:
                System.out.println("Opción inválida.");
                showMainMenu();
                return;
        }

        units.add(unit);
        System.out.println("✓ Unidad creada: " + unit);
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Muestra todas las unidades creadas.
     */
    private void showUnits() {
        System.out.println("=== UNIDADES CREADAS ===");
        System.out.println();

        if (characters.isEmpty() && units.isEmpty()) {
            System.out.println("No hay unidades creadas aún.");
        } else {
            System.out.println("PERSONAJES:");
            for (int i = 0; i < characters.size(); i++) {
                System.out.println((i + 1) + ". " + characters.get(i));
            }
            System.out.println();

            System.out.println("UNIDADES DE COMBATE:");
            for (int i = 0; i < units.size(); i++) {
                System.out.println((i + 1) + ". " + units.get(i));
            }
        }

        System.out.println();
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Carga una batalla guardada.
     */
    private void loadBattle() {
        System.out.println("📁 Cargando batalla...");
        System.out.println("Funcionalidad de carga próximamente disponible.");
        System.out.println("Presiona Enter para volver al menú principal.");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Muestra la ayuda.
     */
    private void showHelp() {
        System.out.println("=== AYUDA - D&D Wargames CLI ===");
        System.out.println();
        System.out.println("COMANDOS DISPONIBLES:");
        System.out.println("1, crear, personaje  - Crear un nuevo personaje");
        System.out.println("2, unidad           - Crear una unidad de combate");
        System.out.println("3, ver              - Ver todas las unidades creadas");
        System.out.println("4, configurar       - Configurar equipos para batalla");
        System.out.println("5, ayuda, help      - Mostrar esta ayuda");
        System.out.println("6, salir, exit      - Salir del programa");
        System.out.println();
        System.out.println("TIPOS DE UNIDADES:");
        System.out.println("- Guardias Humanos: Altamente disciplinados, buena armadura");
        System.out.println("- Orcos: Agresivos, fuertes pero indisciplinados");
        System.out.println("- Trasgos: Numerosos, débiles individualmente");
        System.out.println("- Esqueletos: No tienen moral, pero son frágiles");
        System.out.println("- Ogros: Muy fuertes, alta dureza");
        System.out.println();
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }

    /**
     * Muestra la ayuda durante la batalla.
     */
    private void showBattleHelp() {
        System.out.println("=== AYUDA - Modo Batalla ===");
        System.out.println();
        System.out.println("COMANDOS DURANTE BATALLA:");
        System.out.println("1, turno, ejecutar  - Ejecutar el siguiente turno");
        System.out.println("2, estado, ver      - Ver estado actual de la batalla");
        System.out.println("3, terminar, fin    - Terminar la batalla actual");
        System.out.println("4, ayuda, help      - Mostrar esta ayuda");
        System.out.println("5, salir, exit      - Salir del programa");
        System.out.println();
        System.out.println("COMBATE:");
        System.out.println("- Las unidades atacan automáticamente");
        System.out.println("- Personajes tienen ventaja vs unidades");
        System.out.println("- Unidades tienen bono de masa (+1 por cada 5 PF)");
        System.out.println("- La batalla termina cuando un equipo es derrotado");
        System.out.println();
        System.out.println("Presiona Enter para continuar...");
        scanner.nextLine();
        showMainMenu();
    }
}