import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * TechParts Web — магазин комплектующих для ПК и телефонов.
 * Главный класс: запускает локальный веб-сервер (без сторонних библиотек)
 * и открывает сайт в браузере. Все решения принимает Java (класс Logic).
 */
public class Main {

    static Path webDir;

    public static void main(String[] args) throws Exception {
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        webDir = findWebDir();
        if (webDir == null) {
            System.out.println("Не найдена папка web рядом с программой.");
            System.out.println("Запускайте программу из папки проекта (через run.bat / run.sh).");
            return;
        }

        HttpServer server = null;
        int port = 8080;
        for (; port < 8100; port++) {
            try {
                server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
                break;
            } catch (IOException busy) {
                // порт занят — пробуем следующий
            }
        }
        if (server == null) {
            System.out.println("Не удалось найти свободный порт (8080–8099).");
            return;
        }

        server.createContext("/api/", Main::api);
        server.createContext("/", Main::staticFile);
        server.start();

        String url = "http://localhost:" + port + "/";
        System.out.println("============================================");
        System.out.println(" TechParts запущен: " + url);
        System.out.println(" Логин: admin   Пароль: 1234");
        System.out.println(" Чтобы остановить сервер, закройте это окно (Ctrl+C).");
        System.out.println("============================================");
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.out.println("Откройте адрес в браузере вручную: " + url);
        }
    }

    /** Ищет папку web: сначала в текущей папке, затем выше папки с классами (удобно для запуска из IDE). */
    static Path findWebDir() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        if (Files.isDirectory(cwd.resolve("web"))) {
            return cwd.resolve("web");
        }
        try {
            Path p = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath();
            for (int i = 0; i < 5 && p != null; i++) {
                if (Files.isDirectory(p.resolve("web"))) {
                    return p.resolve("web").normalize();
                }
                p = p.getParent();
            }
        } catch (Exception ignored) {
            // не нашли — вернём null ниже
        }
        return null;
    }

    // ---------------- API ----------------

    static synchronized void api(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String json;
        int code = 200;
        try {
            Map<String, String> form = new HashMap<>();
            if (ex.getRequestMethod().equals("POST")) {
                form = parseForm(new String(readAll(ex.getRequestBody()), StandardCharsets.UTF_8));
            }

            if (path.equals("/api/state")) {
                json = stateJson(authed(ex));
            } else if (path.equals("/api/login")) {
                json = login(ex, form);
            } else if (path.equals("/api/logout")) {
                if (authed(ex)) {
                    Session.logout();
                }
                json = "{\"ok\":true}";
            } else if (path.equals("/api/reset")) {
                Session.clearJournal();
                json = "{\"ok\":true}";
            } else if (path.equals("/api/report")) {
                if (!authed(ex)) {
                    code = 401;
                    json = Result.error("Сначала войдите в систему.");
                } else {
                    json = reportJson();
                }
            } else if (path.startsWith("/api/task/")) {
                String id = path.substring("/api/task/".length());
                if (!authed(ex)) {
                    code = 401;
                    json = Result.error("Сначала войдите в систему.");
                } else if (!id.matches("[0-9]{1,2}[a-e]?")) {
                    code = 404;
                    json = Result.error("Неизвестное задание.");
                } else if (Session.blocked && Logic.moduleOf(id) >= 2) {
                    json = Result.error("Пользователь заблокирован: модули 2–5 недоступны. "
                            + "Снять блокировку можно в модуле 1 (задание 3).");
                } else {
                    json = Logic.run(id, new Params(form)).toJson();
                }
            } else {
                code = 404;
                json = Result.error("Не найдено.");
            }
        } catch (Params.InputError e) {
            json = Result.error(e.getMessage());
        } catch (Exception e) {
            code = 500;
            json = Result.error("Внутренняя ошибка: " + e);
        }
        send(ex, code, "application/json; charset=utf-8", json.getBytes(StandardCharsets.UTF_8));
    }

    /** Задание 5: вход. Три неверные попытки — пауза 15 секунд. */
    static String login(HttpExchange ex, Map<String, String> form) {
        long now = System.currentTimeMillis();
        if (now < Session.lockUntil) {
            long left = (Session.lockUntil - now + 999) / 1000;
            return Result.error("Попытки исчерпаны. Подождите " + left + " сек. и повторите вход.");
        }
        String l = form.getOrDefault("login", "").trim();
        String p = form.getOrDefault("password", "").trim();
        if (l.isEmpty() || p.isEmpty()) {
            return Result.error("Введите логин и пароль: поля не должны быть пустыми.");
        }
        if (Logic.checkCredentials(l, p)) {
            Session.loggedIn = true;
            Session.login = l;
            Session.token = java.util.UUID.randomUUID().toString();
            ex.getResponseHeaders().add("Set-Cookie", "sid=" + Session.token + "; Path=/; HttpOnly; SameSite=Strict");
            Session.failedAttempts = 0;
            Session.log("Вход в систему выполнен: пользователь " + l);
            return "{\"ok\":true}";
        }
        Session.failedAttempts++;
        if (Session.failedAttempts >= 3) {
            Session.failedAttempts = 0;
            Session.lockUntil = now + 15000;
            return Result.error("ПРЕДУПРЕЖДЕНИЕ: неверный логин или пароль. Попытки исчерпаны, вход закрыт на 15 секунд.");
        }
        return Result.error("ПРЕДУПРЕЖДЕНИЕ: неверный логин или пароль. Осталось попыток: "
                + (3 - Session.failedAttempts) + ".");
    }

    /** Вошёл ли именно этот браузер: проверяем cookie, выданный при входе. */
    static boolean authed(HttpExchange ex) {
        if (!Session.loggedIn || Session.token.isEmpty()) {
            return false;
        }
        String cookies = ex.getRequestHeaders().getFirst("Cookie");
        if (cookies == null) {
            return false;
        }
        for (String part : cookies.split(";")) {
            String c = part.trim();
            if (c.equals("sid=" + Session.token)) {
                return true;
            }
        }
        return false;
    }

    static String stateJson(boolean authed) {
        if (!authed) {
            return "{\"ok\":true,\"loggedIn\":false}";
        }
        return "{\"ok\":true,\"loggedIn\":" + Session.loggedIn
                + ",\"login\":\"" + Result.esc(Session.login) + "\""
                + ",\"status\":\"" + Result.esc(Session.userStatus) + "\""
                + ",\"blocked\":" + Session.blocked
                + ",\"risk\":\"" + Result.esc(Session.lastRisk) + "\""
                + ",\"clientLevel\":\"" + Result.esc(Session.lastClientLevel) + "\""
                + ",\"count\":" + Session.decisions.size() + "}";
    }

    /** Задание 25: итоговый отчёт. */
    static String reportJson() {
        String[] fin = Logic.finalDecision();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ok\":true,\"user\":\"").append(Result.esc(Session.login)).append("\"");
        sb.append(",\"status\":\"").append(Result.esc(Session.userStatus)).append("\"");
        sb.append(",\"risk\":\"").append(Result.esc(Session.lastRisk)).append("\"");
        sb.append(",\"clientLevel\":\"").append(Result.esc(Session.lastClientLevel)).append("\"");
        sb.append(",\"count\":").append(Session.decisions.size());
        sb.append(",\"finalLevel\":\"").append(fin[0]).append("\"");
        sb.append(",\"finalText\":\"").append(Result.esc(fin[1])).append("\"");
        sb.append(",\"decisions\":[");
        for (int i = 0; i < Session.decisions.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('"').append(Result.esc(Session.decisions.get(i))).append('"');
        }
        sb.append("]}");
        return sb.toString();
    }

    // ---------------- статические файлы ----------------

    static void staticFile(HttpExchange ex) throws IOException {
        String rel = ex.getRequestURI().getPath();
        if (rel.equals("/")) {
            rel = "/index.html";
        }
        Path file = webDir.resolve(rel.substring(1)).normalize();
        if (!file.startsWith(webDir) || !Files.isRegularFile(file)) {
            send(ex, 404, "text/plain; charset=utf-8", "404 — страница не найдена".getBytes(StandardCharsets.UTF_8));
            return;
        }
        send(ex, 200, contentType(file.getFileName().toString()), Files.readAllBytes(file));
    }

    static String contentType(String name) {
        if (name.endsWith(".html")) return "text/html; charset=utf-8";
        if (name.endsWith(".css")) return "text/css; charset=utf-8";
        if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }

    // ---------------- утилиты ----------------

    static void send(HttpExchange ex, int code, String type, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    static Map<String, String> parseForm(String body) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (body.isEmpty()) {
            return map;
        }
        for (String pair : body.split("&")) {
            int i = pair.indexOf('=');
            String k = i < 0 ? pair : pair.substring(0, i);
            String v = i < 0 ? "" : pair.substring(i + 1);
            map.put(URLDecoder.decode(k, "UTF-8"), URLDecoder.decode(v, "UTF-8"));
        }
        return map;
    }
}
