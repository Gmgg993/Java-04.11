import java.util.ArrayList;
import java.util.List;

/** Ответ на проверку: цвет (good / warn / bad / info), заголовок, строки пояснений и товары. */
public class Result {
    String level;
    String headline;
    final List<String> lines = new ArrayList<>();
    final List<String[]> items = new ArrayList<>();

    Result(String level, String headline) {
        this.level = level;
        this.headline = headline;
    }

    Result line(String s) {
        lines.add(s);
        return this;
    }

    Result item(String name, String price) {
        items.add(new String[]{name, price});
        return this;
    }

    String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ok\":true,\"level\":\"").append(esc(level)).append("\",");
        sb.append("\"headline\":\"").append(esc(headline)).append("\",\"lines\":[");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('"').append(esc(lines.get(i))).append('"');
        }
        sb.append("],\"items\":[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append("{\"name\":\"").append(esc(items.get(i)[0])).append("\",\"price\":\"")
              .append(esc(items.get(i)[1])).append("\"}");
        }
        sb.append("]}");
        return sb.toString();
    }

    static String error(String message) {
        return "{\"ok\":false,\"error\":\"" + esc(message) + "\"}";
    }

    static String esc(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
