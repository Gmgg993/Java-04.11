import java.util.ArrayList;
import java.util.List;

/** Общее состояние системы: кто вошёл, статусы и журнал принятых решений. */
public class Session {
    static boolean loggedIn = false;
    static String login = "";
    static String token = "";   // cookie браузера, который вошёл
    static String userStatus = "не определён";
    static boolean blocked = false;

    static String lastRisk = "не оценивался";
    static String lastClientLevel = "не оценивался";

    // Защита входа: 3 неверные попытки блокируют вход на 15 секунд
    static int failedAttempts = 0;
    static long lockUntil = 0;

    // Результат последнего анализа клиента (задания 22–24)
    static boolean analysed = false;
    static int aPurchases, aReturns, aMonths, aScore;
    static double aAvgCheck;
    static boolean aReview;
    static String aLevel = "";

    static final List<String> decisions = new ArrayList<>();

    static void log(String text) {
        decisions.add(text);
    }

    static void logout() {
        loggedIn = false;
        login = "";
        token = "";
    }

    static void clearJournal() {
        decisions.clear();
        userStatus = "не определён";
        blocked = false;
        lastRisk = "не оценивался";
        lastClientLevel = "не оценивался";
        analysed = false;
    }
}
