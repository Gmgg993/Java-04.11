import java.util.Map;

/**
 * Проверка введённых данных (задания 2 и 19).
 * Пустое значение или недопустимое число — InputError с понятным сообщением.
 */
public class Params {

    static class InputError extends Exception {
        InputError(String message) {
            super(message);
        }
    }

    private final Map<String, String> m;

    Params(Map<String, String> m) {
        this.m = m;
    }

    String str(String key, String label) throws InputError {
        String v = m.get(key);
        if (v == null || v.trim().isEmpty()) {
            throw new InputError("Поле «" + label + "» пустое. Введите значение.");
        }
        return v.trim();
    }

    int integer(String key, String label, int min, int max) throws InputError {
        String s = str(key, label);
        int v;
        try {
            v = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new InputError("«" + label + "»: значение \"" + s + "\" не является целым числом.");
        }
        if (v < min || v > max) {
            throw new InputError("«" + label + "»: число должно быть от " + min + " до " + max + ".");
        }
        return v;
    }

    double number(String key, String label, double min, double max) throws InputError {
        String s = str(key, label).replace(',', '.');
        double v;
        try {
            v = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new InputError("«" + label + "»: значение \"" + s + "\" не является числом.");
        }
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            throw new InputError("«" + label + "»: введите обычное число.");
        }
        if (v < min || v > max) {
            throw new InputError("«" + label + "»: число должно быть от " + trim(min) + " до " + trim(max) + ".");
        }
        return v;
    }

    boolean yes(String key, String label) throws InputError {
        String v = str(key, label);
        if (v.equals("yes")) {
            return true;
        }
        if (v.equals("no")) {
            return false;
        }
        throw new InputError("«" + label + "»: выберите «Да» или «Нет».");
    }

    private static String trim(double d) {
        return d == Math.floor(d) ? String.valueOf((long) d) : String.valueOf(d);
    }
}
