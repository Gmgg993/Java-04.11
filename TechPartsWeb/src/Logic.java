import java.util.Locale;

/**
 * Вся логика принятия решений магазина TechParts.
 * Здесь собраны if-else, if-else if, &&, ||, !, вложенные if и switch (задания 2–24).
 */
public class Logic {

    // ---------- вспомогательное ----------

    static String money(double v) {
        return String.format(Locale.US, "%,.0f", v).replace(',', ' ') + " ₸";
    }

    static String pct(double p) {
        return (p == Math.floor(p) ? String.valueOf((long) p) : String.valueOf(p)) + "%";
    }

    /** Номер модуля по номеру задания (нужен, чтобы закрыть модули для заблокированного). */
    static int moduleOf(String id) {
        int n = Integer.parseInt(id.replaceAll("[^0-9]", ""));
        if (n <= 5) return 1;
        if (n <= 10) return 2;
        if (n <= 15) return 3;
        if (n <= 20) return 4;
        return 5;
    }

    /** Задание 5: проверка логина и пароля. */
    static boolean checkCredentials(String login, String password) {
        return (login.equals("admin") && password.equals("1234"))
                || (login.equals("seller") && password.equals("5678"));
    }

    static Result run(String id, Params p) throws Params.InputError {
        switch (id) {
            case "2": return dataCheck(p);
            case "3": return userStatus(p);
            case "4": return stock(p);
            case "5": return access(p);
            case "6": return ageCategory(p);
            case "7": return installment(p);
            case "8": return vip(p);
            case "9": return canBuy(p);
            case "10": return receiving(p);
            case "11": return discount(p);
            case "12": return finance(p);
            case "13": return grade(p);
            case "14": return security(p);
            case "15": return recommend(p);
            case "16": return catalog(p);
            case "17": return category(p);
            case "18": return calculator(p);
            case "19": return payment(p);
            case "20a": return delivery(p);
            case "20b": return warranty(p);
            case "20c": return refund(p);
            case "20d": return tradeIn(p);
            case "20e": return dayPromo(p);
            case "21": return rules();
            case "23": return analysis(p);
            case "24": return personal();
            default: throw new Params.InputError("Неизвестное задание: " + id);
        }
    }

    // ---------- МОДУЛЬ 1: основы (2–5) ----------

    /** Задание 2. Проверка введённых данных. */
    static Result dataCheck(Params p) throws Params.InputError {
        String name = p.str("name", "Имя клиента");
        int qty = p.integer("qty", "Количество", 1, 100);
        double price = p.number("price", "Цена за единицу", 1, 100000000);
        double total = qty * price;
        Session.log("Проверка ввода: клиент " + name + ", сумма " + money(total));
        return new Result("good", "Данные приняты")
                .line("Клиент: " + name)
                .line(qty + " × " + money(price))
                .line("Итого: " + money(total));
    }

    /** Задание 3. Статус пользователя: активный / неактивный / заблокированный. */
    static Result userStatus(Params p) throws Params.InputError {
        int violations = p.integer("violations", "Нарушения правил", 0, 20);
        int days = p.integer("days", "Дней без активности", 0, 3650);
        String status;
        Result r;
        if (violations >= 3) {
            status = "ЗАБЛОКИРОВАННЫЙ";
            Session.blocked = true;
            r = new Result("bad", "Статус: " + status)
                    .line("Нарушений: " + violations + " (3 и больше — блокировка).")
                    .line("Модули 2–5 закрыты. Чтобы снять блокировку, повторите проверку с меньшим числом нарушений.");
        } else if (days > 30) {
            status = "НЕАКТИВНЫЙ";
            Session.blocked = false;
            r = new Result("warn", "Статус: " + status)
                    .line("Нет активности " + days + " дн. (больше 30).");
        } else {
            status = "АКТИВНЫЙ";
            Session.blocked = false;
            r = new Result("good", "Статус: " + status)
                    .line("Нарушений мало, пользователь недавно был в системе.");
        }
        Session.userStatus = status;
        Session.log("Статус пользователя: " + status);
        return r;
    }

    /** Задание 4. Числовой показатель — один из трёх результатов. */
    static Result stock(Params p) throws Params.InputError {
        int stock = p.integer("stock", "Остаток на складе", 0, 100000);
        Result r;
        if (stock <= 5) {
            r = new Result("bad", "Запас низкий").line("Нужно срочно пополнить склад.");
        } else if (stock <= 20) {
            r = new Result("good", "Запас нормальный").line("Пополнение пока не требуется.");
        } else {
            r = new Result("info", "Запас большой").line("Можно проводить распродажу.");
        }
        Session.log("Остаток " + stock + " шт.: " + r.headline);
        return r;
    }

    /** Задание 5. Проверка доступа: логин и пароль. */
    static Result access(Params p) throws Params.InputError {
        String login = p.str("login", "Логин");
        String password = p.str("password", "Пароль");
        if (checkCredentials(login, password)) {
            Session.log("Проверка доступа: пользователь " + login + " подтверждён");
            return new Result("good", "Доступ разрешён").line("Добро пожаловать, " + login + "!");
        }
        return new Result("bad", "ПРЕДУПРЕЖДЕНИЕ: неверный логин или пароль")
                .line("Доступ не подтверждён.");
    }

    // ---------- МОДУЛЬ 2: логические условия (6–10) ----------

    /** Задание 6. Четыре возрастные категории (if – else if). */
    static Result ageCategory(Params p) throws Params.InputError {
        int age = p.integer("age", "Возраст", 0, 120);
        Result r;
        if (age < 14) {
            r = new Result("warn", "Ребёнок").line("Покупки только вместе с родителями.");
        } else if (age < 18) {
            r = new Result("info", "Подросток").line("Покупка с согласия родителей, без рассрочки.");
        } else if (age < 60) {
            r = new Result("good", "Взрослый").line("Доступны все услуги магазина.");
        } else {
            r = new Result("good", "Пенсионный возраст")
                    .line("Скидка 5% и бесплатная помощь в настройке устройства.");
        }
        Session.log("Возраст " + age + ": " + r.headline);
        return r;
    }

    /** Задание 7. Оператор && — два условия одновременно. */
    static Result installment(Params p) throws Params.InputError {
        int age = p.integer("age", "Возраст", 0, 120);
        double income = p.number("income", "Доход в месяц", 0, 100000000);
        Result r;
        if (age >= 18 && income >= 100000) {
            r = new Result("good", "Рассрочка одобрена")
                    .line("Выполнены оба условия: возраст от 18 лет И доход от 100 000 ₸.");
        } else {
            r = new Result("bad", "В рассрочке отказано")
                    .line("Нужно одновременно: возраст от 18 лет и доход от 100 000 ₸.")
                    .line("Возраст: " + (age >= 18 ? "подходит" : "не подходит")
                            + ", доход: " + (income >= 100000 ? "подходит" : "не подходит") + ".");
        }
        Session.log(r.headline);
        return r;
    }

    /** Задание 8. Оператор || — достаточно одного условия. */
    static Result vip(Params p) throws Params.InputError {
        double yearSum = p.number("yearSum", "Сумма покупок за год", 0, 1000000000);
        int count = p.integer("count", "Количество покупок", 0, 1000);
        boolean partner = p.yes("partner", "Партнёрская карта");
        Result r;
        if (yearSum >= 500000 || count >= 10 || partner) {
            r = new Result("good", "Присвоен статус VIP").line("Выполнено хотя бы одно условие:");
            if (yearSum >= 500000) r.line("покупки за год от 500 000 ₸");
            if (count >= 10) r.line("10 и больше покупок за год");
            if (partner) r.line("есть партнёрская карта");
        } else {
            r = new Result("info", "Статус VIP не присвоен")
                    .line("Нужно одно из: 500 000 ₸ за год, 10 покупок или партнёрская карта.");
        }
        Session.log(r.headline);
        return r;
    }

    /** Задание 9. Оператор ! — проверка отрицательного состояния. */
    static Result canBuy(Params p) throws Params.InputError {
        boolean blocked = p.yes("blocked", "Аккаунт заблокирован");
        boolean debt = p.yes("debt", "Есть задолженность");
        Result r;
        if (!blocked && !debt) {
            r = new Result("good", "Покупки разрешены").line("Клиент не заблокирован и без долгов.");
        } else if (blocked) {
            r = new Result("bad", "Покупки недоступны").line("Аккаунт клиента заблокирован.");
        } else {
            r = new Result("warn", "Сначала погасите долг").line("Есть неоплаченная задолженность.");
        }
        Session.log("Проверка покупки: " + r.headline);
        return r;
    }

    /** Задание 10. Вложенные if — не меньше трёх последовательных проверок. */
    static Result receiving(Params p) throws Params.InputError {
        boolean inStock = p.yes("inStock", "Товар в наличии");
        Result r;
        if (inStock) {
            int way = p.integer("way", "Способ получения", 1, 2);
            if (way == 2) {
                boolean inCity = p.yes("inCity", "Адрес в пределах города");
                if (inCity) {
                    double sum = p.number("sum", "Сумма заказа", 1, 100000000);
                    if (sum >= 30000) {
                        r = new Result("good", "Курьер по городу — бесплатно")
                                .line("Сумма заказа от 30 000 ₸.");
                    } else {
                        r = new Result("info", "Курьер по городу — 1 500 ₸")
                                .line("Бесплатно — при заказе от 30 000 ₸.");
                    }
                } else {
                    r = new Result("info", "Транспортная компания").line("Срок доставки 3–5 дней.");
                }
            } else {
                boolean online = p.yes("online", "Оплата онлайн");
                if (online) {
                    r = new Result("good", "Заказ оплачен").line("Он будет ждать вас на кассе самовывоза.");
                } else {
                    r = new Result("info", "Заказ забронирован на 2 дня").line("Оплата при получении.");
                }
            }
        } else {
            r = new Result("warn", "Товара нет в наличии").line("Предлагаем предзаказ, срок 7–10 дней.");
        }
        Session.log("Получение заказа: " + r.headline);
        return r;
    }

    // ---------- МОДУЛЬ 3: практика (11–15) ----------

    /** Задание 11. Скидка зависит от суммы и статуса клиента. */
    static Result discount(Params p) throws Params.InputError {
        double amount = p.number("amount", "Сумма покупки", 1, 1000000000);
        int status = p.integer("status", "Статус клиента", 1, 3);
        double percent;
        if (amount >= 300000) {
            percent = 8;
        } else if (amount >= 150000) {
            percent = 5;
        } else if (amount >= 50000) {
            percent = 3;
        } else {
            percent = 0;
        }
        double bonus = 0;
        if (status == 2) {
            bonus = 2;
        } else if (status == 3) {
            bonus = 5;
        }
        double total = percent + bonus;
        double sum = amount * total / 100;
        Session.log("Скидка " + pct(total) + " на сумму " + money(amount));
        return new Result(total > 0 ? "good" : "info", "Скидка " + pct(total))
                .line("За сумму покупки: " + pct(percent) + ", за статус клиента: " + pct(bonus))
                .line("Экономия: " + money(sum))
                .line("К оплате: " + money(amount - sum));
    }

    /** Задание 12. Финансовое состояние по доходам и расходам. */
    static Result finance(Params p) throws Params.InputError {
        double income = p.number("income", "Доход в месяц", 1, 1000000000);
        double expenses = p.number("expenses", "Расходы в месяц", 0, 1000000000);
        Result r;
        if (expenses > income) {
            r = new Result("bad", "Дефицит бюджета").line("Крупную покупку лучше отложить.");
        } else {
            double ratio = expenses / income * 100;
            if (ratio <= 50) {
                r = new Result("good", "Отличное состояние").line("Можно покупать технику без кредита.");
            } else if (ratio <= 80) {
                r = new Result("warn", "Стабильное состояние").line("Подойдёт покупка в рассрочку.");
            } else {
                r = new Result("warn", "Напряжённое состояние").line("Выбирайте недорогие комплектующие.");
            }
            r.line("Расходы составляют " + Math.round(ratio) + "% от дохода.");
        }
        Session.log("Финансовый статус: " + r.headline);
        return r;
    }

    /** Задание 13. Баллы → оценка и результат. */
    static Result grade(Params p) throws Params.InputError {
        int points = p.integer("points", "Баллы", 0, 100);
        String mark;
        if (points >= 85) {
            mark = "5 (отлично)";
        } else if (points >= 70) {
            mark = "4 (хорошо)";
        } else if (points >= 50) {
            mark = "3 (удовлетворительно)";
        } else {
            mark = "2 (неудовлетворительно)";
        }
        boolean passed = points >= 50;
        Session.log("Тест консультанта: " + points + " баллов, оценка " + mark);
        return new Result(passed ? "good" : "bad", "Оценка: " + mark)
                .line("Результат: " + (passed ? "тест сдан" : "тест не сдан"));
    }

    /** Задание 14. Уровень риска по нескольким параметрам. */
    static Result security(Params p) throws Params.InputError {
        double sum = p.number("sum", "Сумма заказа", 1, 1000000000);
        boolean first = p.yes("first", "Первый заказ");
        boolean diff = p.yes("diff", "Другой адрес доставки");
        int perHour = p.integer("perHour", "Заказов за час", 0, 50);

        int score = 0;
        Result r;
        StringBuilder why = new StringBuilder();
        if (sum >= 300000) {
            score += 3;
            why.append("крупная сумма (+3); ");
        } else if (sum >= 100000) {
            score += 1;
            why.append("сумма выше средней (+1); ");
        }
        if (first) {
            score += 2;
            why.append("первый заказ (+2); ");
        }
        if (diff) {
            score += 2;
            why.append("адрес не совпадает (+2); ");
        }
        if (perHour >= 3) {
            score += 3;
            why.append("частые заказы (+3); ");
        }

        if (score >= 6) {
            r = new Result("bad", "Риск ВЫСОКИЙ").line("Заказ передан на ручную проверку.");
            Session.lastRisk = "ВЫСОКИЙ";
        } else if (score >= 3) {
            r = new Result("warn", "Риск СРЕДНИЙ").line("Нужно подтверждение по телефону.");
            Session.lastRisk = "СРЕДНИЙ";
        } else {
            r = new Result("good", "Риск НИЗКИЙ").line("Заказ можно оформлять.");
            Session.lastRisk = "НИЗКИЙ";
        }
        String reasons = why.toString().trim();
        if (reasons.endsWith(";")) {
            reasons = reasons.substring(0, reasons.length() - 1);
        }
        r.line("Баллы риска: " + score + (reasons.isEmpty() ? "" : " — " + reasons));
        Session.log("Проверка безопасности заказа: " + r.headline);
        return r;
    }

    /** Задание 15. Рекомендации в зависимости от цели и бюджета. */
    static Result recommend(Params p) throws Params.InputError {
        int goal = p.integer("goal", "Цель", 1, 4);
        double budget = p.number("budget", "Бюджет", 1, 1000000000);
        String advice;
        if (goal == 1) {
            if (budget >= 600000) {
                advice = "Игровой ПК: Core i7 / Ryzen 7, видеокарта верхнего уровня, 32 ГБ ОЗУ.";
            } else if (budget >= 300000) {
                advice = "Игровой ПК среднего уровня: Ryzen 5 / Core i5, видеокарта среднего уровня, 16 ГБ ОЗУ.";
            } else {
                advice = "Бюджетная сборка: процессор со встроенной графикой, недорогая видеокарта, 16 ГБ ОЗУ.";
            }
        } else if (goal == 2) {
            if (budget >= 400000) {
                advice = "Рабочая станция: многоядерный процессор, 32 ГБ ОЗУ, быстрый NVMe-накопитель.";
            } else {
                advice = "Универсальный ПК: 6-ядерный процессор, 16 ГБ ОЗУ, SSD 512 ГБ.";
            }
        } else if (goal == 3) {
            advice = "Офисный ПК: процессор начального уровня, 8–16 ГБ ОЗУ, SSD 256–512 ГБ.";
        } else {
            if (budget >= 400000) {
                advice = "Флагманский смартфон с хорошей камерой и 256 ГБ памяти.";
            } else if (budget >= 150000) {
                advice = "Смартфон среднего класса с 8 ГБ ОЗУ.";
            } else {
                advice = "Бюджетный смартфон; добавьте защитное стекло и чехол.";
            }
        }
        Session.log("Рекомендация: " + advice);
        return new Result("info", "Мы рекомендуем").line(advice);
    }

    // ---------- МОДУЛЬ 4: switch и комбинированные условия (16–20) ----------

    /** Задание 16. Переключение между пятью разделами каталога (switch). */
    static Result catalog(Params p) throws Params.InputError {
        int section = p.integer("section", "Раздел каталога", 1, 5);
        Result r;
        switch (section) {
            case 1:
                r = new Result("info", "Процессоры")
                        .item("Процессор 6 ядер", "85 000 ₸")
                        .item("Процессор 8 ядер", "140 000 ₸")
                        .item("Процессор 12 ядер", "230 000 ₸");
                break;
            case 2:
                r = new Result("info", "Видеокарты")
                        .item("Видеокарта 8 ГБ", "190 000 ₸")
                        .item("Видеокарта 12 ГБ", "310 000 ₸")
                        .item("Видеокарта 16 ГБ", "480 000 ₸");
                break;
            case 3:
                r = new Result("info", "Память и накопители")
                        .item("ОЗУ DDR5 16 ГБ", "38 000 ₸")
                        .item("SSD NVMe 1 ТБ", "45 000 ₸")
                        .item("HDD 2 ТБ", "32 000 ₸");
                break;
            case 4:
                r = new Result("info", "Аксессуары для телефонов")
                        .item("Защитное стекло", "3 000 ₸")
                        .item("Чехол", "4 500 ₸")
                        .item("Зарядное устройство 65 Вт", "9 000 ₸");
                break;
            default:
                r = new Result("info", "Запчасти для телефонов")
                        .item("Экран смартфона", "35 000 ₸")
                        .item("Аккумулятор", "12 000 ₸")
                        .item("Разъём зарядки", "6 000 ₸");
                break;
        }
        Session.log("Просмотрен раздел каталога: " + r.headline);
        return r;
    }

    /** Задание 17. Категория услуги — разные возможности. */
    static Result category(Params p) throws Params.InputError {
        int cat = p.integer("category", "Категория", 1, 4);
        Result r;
        switch (cat) {
            case 1:
                r = new Result("info", "Сборка ПК")
                        .line("Подбор совместимых деталей")
                        .line("Сборка и аккуратная прокладка кабелей")
                        .line("Тест под нагрузкой 24 часа");
                break;
            case 2:
                r = new Result("info", "Ремонт телефона")
                        .line("Замена экрана и аккумулятора")
                        .line("Ремонт разъёма зарядки")
                        .line("Влагозащита после ремонта");
                break;
            case 3:
                r = new Result("info", "Диагностика и чистка")
                        .line("Проверка температур")
                        .line("Замена термопасты")
                        .line("Чистка от пыли");
                break;
            default:
                r = new Result("info", "Апгрейд компьютера")
                        .line("Добавление оперативной памяти")
                        .line("Замена накопителя на SSD")
                        .line("Установка новой видеокарты");
                break;
        }
        Session.log("Выбрана категория услуги: " + r.headline);
        return r;
    }

    /** Задание 18. Калькулятор стоимости услуги. */
    static Result calculator(Params p) throws Params.InputError {
        int tariff = p.integer("tariff", "Тариф", 1, 3);
        int devices = p.integer("devices", "Количество устройств", 1, 10);
        boolean urgent = p.yes("urgent", "Срочное выполнение");
        boolean visit = p.yes("visit", "Выезд мастера");
        boolean ext = p.yes("ext", "Расширенная гарантия");

        double price;
        String name;
        switch (tariff) {
            case 1:
                price = 5000;
                name = "Базовый";
                break;
            case 2:
                price = 10000;
                name = "Стандарт";
                break;
            default:
                price = 20000;
                name = "Премиум";
                break;
        }
        Result r = new Result("good", "");
        price = price * devices;
        r.line("Тариф «" + name + "» × " + devices + " = " + money(price));
        if (urgent) {
            price = price * 1.3;
            r.line("Срочно: +30%");
        }
        if (visit) {
            price = price + 5000;
            r.line("Выезд мастера: +" + money(5000));
        }
        if (ext) {
            price = price * 1.1;
            r.line("Гарантия 12 месяцев: +10%");
        }
        r.headline = "Итого: " + money(price);
        Session.log("Калькулятор услуг: тариф " + name + ", " + r.headline);
        return r;
    }

    /** Задание 19. Обработка ошибок: неверный выбор → сообщение и предложение повторить. */
    static Result payment(Params p) throws Params.InputError {
        String choice = p.str("pay", "Способ оплаты");
        Result r;
        switch (choice) {
            case "1":
                r = new Result("good", "Оплата банковской картой");
                break;
            case "2":
                r = new Result("good", "Оплата наличными");
                break;
            case "3":
                r = new Result("good", "Оплата в рассрочку");
                break;
            case "4":
                r = new Result("good", "Оплата онлайн-кошельком");
                break;
            default:
                throw new Params.InputError("Способа оплаты \"" + choice
                        + "\" не существует. Введите число от 1 до 4 и повторите выбор.");
        }
        r.line("Способ оплаты выбран.");
        Session.log("Способ оплаты: " + r.headline);
        return r;
    }

    /** Задание 20, сценарий 1. Доставка: switch + if. */
    static Result delivery(Params p) throws Params.InputError {
        int city = p.integer("city", "Город", 1, 3);
        double weight = p.number("weight", "Вес посылки", 0.1, 50);
        double cost;
        switch (city) {
            case 1:
                cost = 1000;
                break;
            case 2:
                cost = 1500;
                break;
            default:
                cost = 3000;
                break;
        }
        if (weight > 5 && city == 3) {
            cost += (weight - 5) * 300;
        } else if (weight > 10) {
            cost += 500;
        }
        Session.log("Сценарий «Доставка»: " + money(cost));
        return new Result("info", "Доставка: " + money(cost))
                .line("Вес " + weight + " кг. Для других регионов тяжёлые посылки дороже.");
    }

    /** Задание 20, сценарий 2. Гарантийный случай: ! + && + if-else. */
    static Result warranty(Params p) throws Params.InputError {
        int months = p.integer("months", "Месяцев с покупки", 0, 60);
        boolean receipt = p.yes("receipt", "Есть чек или талон");
        boolean damaged = p.yes("damaged", "Следы падения или жидкости");
        Result r;
        if (!damaged && receipt && months <= 12) {
            r = new Result("good", "Бесплатный гарантийный ремонт");
        } else if (!damaged && receipt && months <= 24) {
            r = new Result("warn", "Расширенная гарантия")
                    .line("Запчасти бесплатно, работа — 50% стоимости.");
        } else {
            r = new Result("bad", "Платный ремонт")
                    .line("Гарантия не действует: нет чека, срок истёк или есть следы повреждений.");
        }
        Session.log("Сценарий «Гарантия»: " + r.headline);
        return r;
    }

    /** Задание 20, сценарий 3. Возврат товара: switch + || + !. */
    static Result refund(Params p) throws Params.InputError {
        int type = p.integer("type", "Причина возврата", 1, 2);
        int days = p.integer("days", "Дней с покупки", 0, 365);
        boolean used = p.yes("used", "Товар использовался");
        Result r;
        switch (type) {
            case 1:
                if (days <= 14 && !used) {
                    r = new Result("good", "Возврат денег полностью");
                } else {
                    r = new Result("bad", "Возврат невозможен")
                            .line("Нужно уложиться в 14 дней, и товар не должен быть в употреблении.");
                }
                break;
            default:
                if (days <= 30 || !used) {
                    r = new Result("good", "Возврат денег или обмен на исправный товар");
                } else {
                    r = new Result("warn", "Нужна экспертиза в сервисном центре");
                }
                break;
        }
        Session.log("Сценарий «Возврат»: " + r.headline);
        return r;
    }

    /** Задание 20, сценарий 4. Трейд-ин: switch + if-else + ||. */
    static Result tradeIn(Params p) throws Params.InputError {
        int device = p.integer("device", "Устройство", 1, 3);
        int age = p.integer("age", "Возраст устройства", 0, 15);
        int cond = p.integer("cond", "Состояние", 1, 3);
        double value;
        switch (device) {
            case 1:
                value = 100000;
                break;
            case 2:
                value = 120000;
                break;
            default:
                value = 60000;
                break;
        }
        if (age >= 5) {
            value *= 0.2;
        } else if (age >= 3) {
            value *= 0.5;
        } else {
            value *= 0.8;
        }
        if (cond == 3 || age > 8) {
            value *= 0.5;
        } else if (cond == 1 && age <= 2) {
            value *= 1.1;
        }
        Session.log("Сценарий «Трейд-ин»: " + money(value));
        return new Result("info", "Оценка: " + money(value))
                .line("Стоимость, которую мы засчитаем при покупке нового устройства.");
    }

    /** Задание 20, сценарий 5. Акция дня: switch + ||. */
    static Result dayPromo(Params p) throws Params.InputError {
        int day = p.integer("day", "День недели", 1, 7);
        double sum = p.number("sum", "Сумма покупки", 1, 1000000000);
        String promo;
        switch (day) {
            case 1:
                promo = "Понедельник: скидка 5% на аксессуары для телефонов";
                break;
            case 3:
                promo = "Среда: бесплатная чистка ПК при покупке комплектующих";
                break;
            case 5:
                promo = "Пятница: скидка 7% на накопители и память";
                break;
            default:
                promo = "Сегодня специальных акций нет";
                break;
        }
        Result r = new Result(day == 1 || day == 3 || day == 5 ? "good" : "info", promo);
        boolean weekend = day == 6 || day == 7;
        if (weekend || sum >= 100000) {
            r.line("Бонус: подарочная защитная плёнка или коврик для мыши.");
        }
        Session.log("Сценарий «Акция дня»: " + promo);
        return r;
    }

    // ---------- МОДУЛЬ 5: индивидуальный — анализ клиента (21–24) ----------

    /** Задание 21. Пять условий предметной области. */
    static Result rules() {
        return new Result("info", "Правила начисления баллов")
                .line("1) Покупок за год: 12+ — 3 балла, 5+ — 2, 1+ — 1.")
                .line("2) Средний чек: от 200 000 ₸ — 3, от 80 000 ₸ — 2, от 20 000 ₸ — 1.")
                .line("3) Возвраты: 0 — +2, 1–2 — +1, больше двух — минус 1.")
                .line("4) Оставлен отзыв о магазине — +1.")
                .line("5) Срок клиентства: 24+ мес — 2, 6+ мес — 1.")
                .line("Уровни: 8 и больше — ВЫСОКИЙ, 4–7 — СРЕДНИЙ, меньше 4 — НИЗКИЙ.");
    }

    /** Задания 22–23. Пять параметров → три уровня результата. */
    static Result analysis(Params p) throws Params.InputError {
        int purchases = p.integer("purchases", "Покупок за год", 0, 1000);
        double avg = p.number("avg", "Средний чек", 0, 1000000000);
        int returns = p.integer("returns", "Возвратов", 0, 100);
        boolean review = p.yes("review", "Отзыв о магазине");
        int months = p.integer("months", "Месяцев с нами", 0, 600);

        int score = 0;
        Result r = new Result("info", "");

        int s1;
        if (purchases >= 12) s1 = 3;
        else if (purchases >= 5) s1 = 2;
        else if (purchases >= 1) s1 = 1;
        else s1 = 0;
        score += s1;
        r.line("Покупки: +" + s1);

        int s2;
        if (avg >= 200000) s2 = 3;
        else if (avg >= 80000) s2 = 2;
        else if (avg >= 20000) s2 = 1;
        else s2 = 0;
        score += s2;
        r.line("Средний чек: +" + s2);

        int s3;
        if (returns == 0) s3 = 2;
        else if (returns <= 2) s3 = 1;
        else s3 = -1;
        score += s3;
        r.line("Возвраты: " + (s3 > 0 ? "+" : "") + s3);

        int s4 = review ? 1 : 0;
        score += s4;
        r.line("Отзыв: +" + s4);

        int s5;
        if (months >= 24) s5 = 2;
        else if (months >= 6) s5 = 1;
        else s5 = 0;
        score += s5;
        r.line("Срок клиентства: +" + s5);

        String level;
        if (score >= 8) {
            level = "ВЫСОКИЙ";
            r.level = "good";
        } else if (score >= 4) {
            level = "СРЕДНИЙ";
            r.level = "warn";
        } else {
            level = "НИЗКИЙ";
            r.level = "bad";
        }
        r.headline = "Уровень лояльности: " + level + " (баллов: " + score + ")";

        Session.analysed = true;
        Session.aPurchases = purchases;
        Session.aAvgCheck = avg;
        Session.aReturns = returns;
        Session.aReview = review;
        Session.aMonths = months;
        Session.aScore = score;
        Session.aLevel = level;
        Session.lastClientLevel = level;
        Session.log("Анализ клиента: балл " + score + ", уровень " + level);
        return r;
    }

    /** Задание 24. Персональные рекомендации — зависят от комбинации условий. */
    static Result personal() throws Params.InputError {
        if (!Session.analysed) {
            throw new Params.InputError("Сначала выполните анализ клиента (задания 22–23), "
                    + "тогда система сможет подготовить рекомендации.");
        }
        String level = Session.aLevel;
        Result r = new Result("info", "Рекомендации для уровня " + level);
        if (level.equals("ВЫСОКИЙ") && !Session.aReview) {
            r.line("Предложить оставить отзыв: за него клиент получит бонус 5 000 ₸.");
        }
        if (level.equals("ВЫСОКИЙ") && Session.aReturns <= 2) {
            r.line("Выдать карту VIP: скидка 10% и приоритетный сервис.");
        } else if (level.equals("ВЫСОКИЙ")) {
            r.line("Карта VIP со скидкой 5%; сначала выяснить причины возвратов.");
        }
        if (level.equals("СРЕДНИЙ") && Session.aAvgCheck < 80000) {
            r.line("Предложить комплект «процессор + память» со скидкой, чтобы вырос средний чек.");
        }
        if (level.equals("СРЕДНИЙ") && Session.aPurchases < 5) {
            r.line("Предложить накопительную карту: бонусы за каждую следующую покупку.");
        }
        if (level.equals("НИЗКИЙ") && Session.aMonths < 6) {
            r.line("Новому клиенту: приветственный купон 3% и консультация по подбору.");
        }
        if (level.equals("НИЗКИЙ") && Session.aReturns > 2) {
            r.line("Много возвратов: предложить бесплатную консультацию перед покупкой.");
        }
        if (Session.aMonths >= 24 && Session.aPurchases == 0) {
            r.line("Давно не покупал: отправить персональное предложение «Мы скучаем».");
        }
        if (r.lines.isEmpty()) {
            r.line("Особых действий не требуется — обслуживайте клиента в обычном режиме.");
        }
        Session.log("Выданы персональные рекомендации для уровня " + level);
        return r;
    }

    // ---------- Итоговое решение (задание 25) ----------

    static String[] finalDecision() {
        if (Session.blocked) {
            return new String[]{"bad", "Обслуживание запрещено: пользователь заблокирован."};
        } else if (Session.lastRisk.equals("ВЫСОКИЙ")) {
            return new String[]{"bad", "Требуется ручная проверка: высокий риск по заказу."};
        } else if (Session.lastClientLevel.equals("ВЫСОКИЙ")) {
            return new String[]{"good", "Приоритетное обслуживание: клиент с высоким уровнем лояльности."};
        } else {
            return new String[]{"info", "Стандартное обслуживание клиента."};
        }
    }
}
