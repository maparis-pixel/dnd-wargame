package com.dnd.wargames.web;

import com.dnd.wargames.battle.WargameBattleEngine;
import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.CreatureType;
import com.dnd.wargames.units.UnitFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servidor web simple para configurar y ejecutar batallas desde navegador.
 */
public class WebBattleServer {
    private static final int PORT = 8080;

    private final Map<String, BattleSession> sessions = new ConcurrentHashMap<>();

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", this::handleHome);
        server.createContext("/start", this::handleStartBattle);
        server.createContext("/run", this::handleRunTurns);

        server.setExecutor(null);
        server.start();

        System.out.println("🌐 Interfaz web iniciada en: http://localhost:" + PORT);
        System.out.println("Pulsa Ctrl+C para detener el servidor.");
    }

    private void handleHome(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Método no permitido");
            return;
        }

        sendHtml(exchange, renderHomePage("Configura compañías, PF por compañía y equipo (Aliados/Enemigos)."));
    }

    private void handleStartBattle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Método no permitido");
            return;
        }

        Map<String, String> form = parseForm(exchange);
        BattleSession session = createSessionFromForm(form);

        if (session.totalCompanies == 0) {
            sendHtml(exchange, renderHomePage("❌ Debes añadir al menos 1 compañía."));
            return;
        }

        if (session.team1Units.isEmpty() || session.team2Units.isEmpty()) {
            sendHtml(exchange, renderHomePage("❌ Debes tener unidades en ambos equipos (Aliados y Enemigos)."));
            return;
        }

        session.engine.startBattle();

        String battleId = UUID.randomUUID().toString();
        sessions.put(battleId, session);

        sendHtml(exchange, renderBattlePage(battleId, session, "✅ Batalla creada. Indica cuántos turnos ejecutar.", ""));
    }

    private void handleRunTurns(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Método no permitido");
            return;
        }

        Map<String, String> form = parseForm(exchange);
        String battleId = form.get("battleId");
        int turnsToRun = parseInt(form.get("turns"), 1);

        BattleSession session = sessions.get(battleId);
        if (session == null) {
            sendHtml(exchange, renderHomePage("❌ Sesión no encontrada. Crea una batalla nueva."));
            return;
        }

        if (turnsToRun < 1) {
            turnsToRun = 1;
        }

        int executed = 0;
        StringBuilder batchLog = new StringBuilder();

        while (executed < turnsToRun && session.isBattleActive()) {
            batchLog.append(captureTurnOutput(session.engine));
            executed++;
            session.totalTurnsExecuted++;
        }

        String message;
        if (!session.isBattleActive()) {
            message = "🏁 Batalla finalizada tras " + session.totalTurnsExecuted + " turnos totales.";
        } else {
            message = "✅ Ejecutados " + executed + " turnos. Puedes ejecutar otro bloque de turnos.";
        }

        sendHtml(exchange, renderBattlePage(battleId, session, message, batchLog.toString()));
    }

    private BattleSession createSessionFromForm(Map<String, String> form) {
        BattleSession session = new BattleSession();

        for (CreatureType type : getSupportedWebTypes()) {
            String key = type.name();
            String team = form.getOrDefault("team_" + key, "none");
            int companies = parseInt(form.get("companies_" + key), 0);
            int pfPerCompany = parseInt(form.get("pf_" + key), 0);

            if (companies <= 0 || pfPerCompany <= 0 || "none".equals(team)) {
                continue;
            }

            for (int i = 1; i <= companies; i++) {
                CombatUnit unit = createUnit(type, pfPerCompany);
                unit.setName(type.getDisplayName() + " Cía " + i + " (" + pfPerCompany + " PF)");

                if ("ally".equals(team)) {
                    session.engine.addToTeam1(unit);
                    session.team1Units.add(unit);
                } else if ("enemy".equals(team)) {
                    session.engine.addToTeam2(unit);
                    session.team2Units.add(unit);
                }

                session.totalCompanies++;
            }
        }

        return session;
    }

    private String captureTurnOutput(WargameBattleEngine engine) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            engine.executeTurn();
        } finally {
            System.setOut(originalOut);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private List<CreatureType> getSupportedWebTypes() {
        List<CreatureType> list = new ArrayList<>();
        list.add(CreatureType.HUMAN_GUARD);
        list.add(CreatureType.ORC);
        list.add(CreatureType.GOBLIN);
        list.add(CreatureType.SKELETON);
        list.add(CreatureType.OGRE);
        return list;
    }

    private CombatUnit createUnit(CreatureType type, int pfPerCompany) {
        switch (type) {
            case HUMAN_GUARD:
                return UnitFactory.createHumanGuards(pfPerCompany);
            case ORC:
                return UnitFactory.createOrcs(pfPerCompany);
            case GOBLIN:
                return UnitFactory.createGoblins(pfPerCompany);
            case SKELETON:
                return UnitFactory.createSkeletons(pfPerCompany);
            case OGRE:
                return UnitFactory.createOgres(pfPerCompany);
            default:
                throw new IllegalArgumentException("Tipo no soportado en web: " + type);
        }
    }

    private Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        byte[] data = exchange.getRequestBody().readAllBytes();
        String body = new String(data, StandardCharsets.UTF_8);

        Map<String, String> map = new HashMap<>();
        if (body.isBlank()) {
            return map;
        }

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";
            map.put(key, value);
        }

        return map;
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String renderHomePage(String message) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head><meta charset='UTF-8'><title>D&D Wargames Web</title>")
            .append("<style>")
            .append("body{font-family:Arial,sans-serif;margin:24px;}table{border-collapse:collapse;width:100%;}")
            .append("th,td{border:1px solid #ccc;padding:8px;text-align:left;}th{background:#f2f2f2;}")
            .append("input,select{width:100%;padding:6px;}.box{background:#f8f8f8;border:1px solid #ddd;padding:12px;margin-bottom:16px;}")
            .append(".btn{padding:10px 16px;border:none;background:#222;color:#fff;cursor:pointer;}")
            .append("</style></head><body>")
            .append("<h1>⚔️ D&D Wargames - Configuración Web</h1>")
            .append("<div class='box'>").append(escapeHtml(message)).append("</div>")
            .append("<form method='post' action='/start'>")
            .append("<table><thead><tr><th>Tipo de unidad</th><th>Equipo</th><th>Nº compañías</th><th>PF por compañía</th></tr></thead><tbody>");

        for (CreatureType type : getSupportedWebTypes()) {
            String key = type.name();
            html.append("<tr>")
                .append("<td>").append(escapeHtml(type.getDisplayName())).append("</td>")
                .append("<td><select name='team_").append(key).append("'>")
                .append("<option value='none'>No incluir</option>")
                .append("<option value='ally'>Aliados</option>")
                .append("<option value='enemy'>Enemigos</option>")
                .append("</select></td>")
                .append("<td><input type='number' min='0' value='0' name='companies_").append(key).append("'></td>")
                .append("<td><input type='number' min='1' value='5' name='pf_").append(key).append("'></td>")
                .append("</tr>");
        }

        html.append("</tbody></table><br>")
            .append("<button class='btn' type='submit'>Crear batalla</button>")
            .append("</form>")
            .append("<p><strong>Moral en combate:</strong> Enfurecido (+2 ataque), Confundido (-1), Asustado (-2). El estado cambia según PF restantes.</p>")
            .append("</body></html>");

        return html.toString();
    }

    private String renderBattlePage(String battleId, BattleSession session, String message, String log) {
        StringBuilder html = new StringBuilder();

        String status = session.isBattleActive()
                ? "🟢 Batalla activa"
                : "🔴 Batalla finalizada - " + session.getWinner();

        html.append("<html><head><meta charset='UTF-8'><title>D&D Wargames Web - Batalla</title>")
            .append("<style>")
            .append("body{font-family:Arial,sans-serif;margin:24px;}.grid{display:grid;grid-template-columns:1fr 1fr;gap:16px;}")
            .append(".box{background:#f8f8f8;border:1px solid #ddd;padding:12px;margin-bottom:16px;}")
            .append(".btn{padding:10px 16px;border:none;background:#222;color:#fff;cursor:pointer;}input{padding:8px;width:120px;}")
            .append("pre{white-space:pre-wrap;background:#fff;border:1px solid #ddd;padding:8px;}")
            .append("</style></head><body>")
            .append("<h1>⚔️ D&D Wargames - Batalla</h1>")
            .append("<div class='box'><strong>Estado:</strong> ").append(escapeHtml(status))
            .append("<br><strong>Turnos totales ejecutados:</strong> ").append(session.totalTurnsExecuted).append("</div>")
            .append("<div class='box'>").append(message).append("</div>")
            .append("<div class='grid'>")
            .append("<div class='box'><h3>Aliados</h3>").append(formatUnits(session.team1Units)).append("</div>")
            .append("<div class='box'><h3>Enemigos</h3>").append(formatUnits(session.team2Units)).append("</div>")
            .append("</div>");

        if (session.isBattleActive()) {
            html.append("<form method='post' action='/run'>")
                .append("<input type='hidden' name='battleId' value='").append(escapeHtml(battleId)).append("'>")
                .append("<label>Turnos a ejecutar ahora:</label><br>")
                .append("<input type='number' min='1' value='1' name='turns'> ")
                .append("<button class='btn' type='submit'>Ejecutar bloque</button>")
                .append("</form>");
        } else {
            html.append("<p>La batalla terminó. <a href='/'>Crear nueva batalla</a></p>");
        }

        if (log != null && !log.isBlank()) {
            String trimmed = log.length() > 8000 ? log.substring(log.length() - 8000) : log;
            html.append("<h3>Detalle del bloque ejecutado</h3><pre>")
                .append(escapeHtml(trimmed))
                .append("</pre>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String formatUnits(List<CombatUnit> units) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (CombatUnit unit : units) {
            sb.append("<li>")
              .append(escapeHtml(unit.getName()))
              .append(" - PF ")
              .append(unit.getStrengthPoints())
              .append("/")
              .append(unit.getMaxStrengthPoints())
              .append(" - Moral: ")
              .append(escapeHtml(unit.getMoraleStatus().toString()))
              .append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static class BattleSession {
        private final WargameBattleEngine engine = new WargameBattleEngine();
        private final List<CombatUnit> team1Units = new ArrayList<>();
        private final List<CombatUnit> team2Units = new ArrayList<>();
        private int totalCompanies = 0;
        private int totalTurnsExecuted = 0;

        private boolean isBattleActive() {
            return hasAlive(team1Units) && hasAlive(team2Units);
        }

        private String getWinner() {
            boolean alliesAlive = hasAlive(team1Units);
            boolean enemiesAlive = hasAlive(team2Units);

            if (alliesAlive && !enemiesAlive) {
                return "Aliados";
            }
            if (!alliesAlive && enemiesAlive) {
                return "Enemigos";
            }
            return "Empate";
        }

        private boolean hasAlive(List<CombatUnit> units) {
            for (CombatUnit unit : units) {
                if (unit.isAlive()) {
                    return true;
                }
            }
            return false;
        }
    }
}
