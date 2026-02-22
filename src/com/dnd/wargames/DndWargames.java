package com.dnd.wargames;

import com.dnd.wargames.cli.CommandLineInterface;

/**
 * Punto de entrada principal para D&D Wargames.
 * Inicia la interfaz de línea de comandos.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class DndWargames {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  ⚔️ D&D Wargames - Simulador de Combate ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Iniciar interfaz de línea de comandos
            CommandLineInterface cli = new CommandLineInterface();
            cli.start();

        } catch (Exception e) {
            System.err.println("✗ Error al iniciar la aplicación:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
