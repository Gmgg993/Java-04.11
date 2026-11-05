/* TechParts — интерфейс. Все решения принимает Java-сервер, здесь только формы и показ ответов. */
'use strict';

const YN = [['yes', 'Да'], ['no', 'Нет']];

const MODULES = [
  { id: 1, name: 'Основы', nav: 'Основы', range: 'Задания 2–5', tasks: ['2', '3', '4', '5'],
    desc: 'Проверка ввода, статус пользователя, остаток на складе и доступ.' },
  { id: 2, name: 'Логические условия', nav: 'Логика', range: 'Задания 6–10', tasks: ['6', '7', '8', '9', '10'],
    desc: 'if-else if, операторы &&, || и !, вложенные проверки.' },
  { id: 3, name: 'Практика', nav: 'Практика', range: 'Задания 11–15', tasks: ['11', '12', '13', '14', '15'],
    desc: 'Скидки, финансы покупателя, оценка, безопасность заказа и рекомендации.' },
  { id: 4, name: 'Switch и сценарии', nav: 'Switch', range: 'Задания 16–20', tasks: ['16', '17', '18', '19', '20a', '20b', '20c', '20d', '20e'],
    desc: 'Каталог, услуги, калькулятор, обработка ошибок и пять сценариев решений.' },
  { id: 5, name: 'Анализ клиента', nav: 'Клиент', range: 'Задания 21–24', tasks: ['21', '23', '24'],
    desc: 'Индивидуальный модуль: пять параметров, три уровня и персональные рекомендации.' }
];

const TASKS = {
  '2': { title: 'Проверка введённых данных', tag: 'проверка ввода', btn: 'Проверить',
    spec: 'Пустое значение или недопустимое число — сообщение об ошибке. Попробуйте оставить поле пустым или ввести буквы.',
    fields: [
      { n: 'name', l: 'Имя клиента', t: 'text', ph: 'Например, Аня' },
      { n: 'qty', l: 'Количество', t: 'num', ph: '1–100' },
      { n: 'price', l: 'Цена за единицу, ₸', t: 'num', ph: 'Например, 45000' }] },
  '3': { title: 'Статус пользователя', tag: 'if-else', btn: 'Определить статус',
    spec: 'Активный, неактивный или заблокированный. Три нарушения и больше — блокировка.',
    fields: [
      { n: 'violations', l: 'Нарушений правил', t: 'num', ph: '0–20' },
      { n: 'days', l: 'Дней без активности', t: 'num', ph: '0–3650' }] },
  '4': { title: 'Остаток товара на складе', tag: 'if-else', btn: 'Проверить остаток',
    spec: 'Один из трёх результатов в зависимости от числа.',
    fields: [{ n: 'stock', l: 'Количество на складе', t: 'num', ph: '0–100000' }] },
  '5': { title: 'Проверка доступа', tag: 'логин и пароль', btn: 'Проверить доступ',
    spec: 'Верные данные открывают систему, неверные — предупреждение. Для проверки: admin и 1234.',
    fields: [
      { n: 'login', l: 'Логин', t: 'text' },
      { n: 'password', l: 'Пароль', t: 'pass' }] },

  '6': { title: 'Возрастная категория покупателя', tag: 'if-else if', btn: 'Определить категорию',
    spec: 'Четыре возрастные категории.',
    fields: [{ n: 'age', l: 'Возраст', t: 'num', ph: '0–120' }] },
  '7': { title: 'Рассрочка на покупку', tag: '&&', btn: 'Проверить условия',
    spec: 'Рассрочка — только если выполнены оба требования сразу.',
    fields: [
      { n: 'age', l: 'Возраст', t: 'num', ph: '0–120' },
      { n: 'income', l: 'Доход в месяц, ₸', t: 'num', ph: 'Например, 150000' }] },
  '8': { title: 'Статус VIP', tag: '||', btn: 'Проверить условия',
    spec: 'Статус даётся, если выполняется хотя бы одно условие.',
    fields: [
      { n: 'yearSum', l: 'Покупки за год, ₸', t: 'num', ph: 'Например, 520000' },
      { n: 'count', l: 'Количество покупок', t: 'num', ph: '0–1000' },
      { n: 'partner', l: 'Партнёрская карта', t: 'yn' }] },
  '9': { title: 'Может ли клиент покупать', tag: '!', btn: 'Проверить клиента',
    spec: 'Клиент не заблокирован и без долгов — значит, может пользоваться системой.',
    fields: [
      { n: 'blocked', l: 'Аккаунт заблокирован', t: 'yn' },
      { n: 'debt', l: 'Есть задолженность', t: 'yn' }] },
  '10': { title: 'Получение заказа', tag: 'вложенные if', btn: 'Оформить заказ',
    spec: 'Результат зависит минимум от трёх проверок подряд: наличие, способ получения, адрес и сумма.',
    fields: [
      { n: 'inStock', l: 'Товар в наличии', t: 'yn', def: 'yes' },
      { n: 'way', l: 'Способ получения', t: 'sel', opts: [['1', 'Самовывоз'], ['2', 'Доставка']], show: [['inStock', 'yes']] },
      { n: 'inCity', l: 'Адрес в пределах города', t: 'yn', show: [['inStock', 'yes'], ['way', '2']] },
      { n: 'sum', l: 'Сумма заказа, ₸', t: 'num', ph: 'Например, 35000', show: [['inStock', 'yes'], ['way', '2'], ['inCity', 'yes']] },
      { n: 'online', l: 'Оплатить заранее онлайн', t: 'yn', show: [['inStock', 'yes'], ['way', '1']] }] },

  '11': { title: 'Расчёт скидки', tag: 'if-else if', btn: 'Рассчитать скидку',
    spec: 'Размер скидки зависит от суммы покупки и статуса клиента.',
    fields: [
      { n: 'amount', l: 'Сумма покупки, ₸', t: 'num', ph: 'Например, 200000' },
      { n: 'status', l: 'Статус клиента', t: 'sel', opts: [['1', 'Новый'], ['2', 'Постоянный'], ['3', 'VIP']] }] },
  '12': { title: 'Финансовое состояние покупателя', tag: 'доходы и расходы', btn: 'Оценить',
    spec: 'Программа сравнивает доходы и расходы и определяет финансовый статус.',
    fields: [
      { n: 'income', l: 'Доход в месяц, ₸', t: 'num', ph: 'Например, 250000' },
      { n: 'expenses', l: 'Расходы в месяц, ₸', t: 'num', ph: 'Например, 120000' }] },
  '13': { title: 'Тест продавца-консультанта', tag: 'баллы', btn: 'Выставить оценку',
    spec: 'Оценка и результат прохождения по набранным баллам.',
    fields: [{ n: 'points', l: 'Набрано баллов', t: 'num', ph: '0–100' }] },
  '14': { title: 'Безопасность заказа', tag: 'уровень риска', btn: 'Оценить риск',
    spec: 'Уровень риска считается по нескольким параметрам сразу.',
    fields: [
      { n: 'sum', l: 'Сумма заказа, ₸', t: 'num', ph: 'Например, 320000' },
      { n: 'first', l: 'Первый заказ клиента', t: 'yn' },
      { n: 'diff', l: 'Адрес отличается от адреса карты', t: 'yn' },
      { n: 'perHour', l: 'Заказов с устройства за час', t: 'num', ph: '0–50' }] },
  '15': { title: 'Подбор техники', tag: 'рекомендации', btn: 'Подобрать',
    spec: 'Один из нескольких вариантов решения по цели и бюджету.',
    fields: [
      { n: 'goal', l: 'Для чего нужна техника', t: 'sel', opts: [['1', 'Игры'], ['2', 'Работа и монтаж'], ['3', 'Учёба и офис'], ['4', 'Смартфон']] },
      { n: 'budget', l: 'Бюджет, ₸', t: 'num', ph: 'Например, 350000' }] },

  '16': { title: 'Каталог по разделам', tag: 'switch', btn: 'Показать раздел',
    spec: 'Переключение между пятью разделами программы.',
    fields: [{ n: 'section', l: 'Раздел', t: 'sel', opts: [['1', 'Процессоры'], ['2', 'Видеокарты'], ['3', 'Память и накопители'], ['4', 'Аксессуары для телефонов'], ['5', 'Запчасти для телефонов']] }] },
  '17': { title: 'Категория услуги', tag: 'switch', btn: 'Показать возможности',
    spec: 'Для каждой категории — свои возможности.',
    fields: [{ n: 'category', l: 'Категория', t: 'sel', opts: [['1', 'Сборка ПК'], ['2', 'Ремонт телефона'], ['3', 'Диагностика и чистка'], ['4', 'Апгрейд компьютера']] }] },
  '18': { title: 'Калькулятор услуги', tag: 'switch', btn: 'Рассчитать',
    spec: 'Стоимость зависит от тарифа и дополнительных параметров.',
    fields: [
      { n: 'tariff', l: 'Тариф', t: 'sel', opts: [['1', 'Базовый, 5 000 ₸'], ['2', 'Стандарт, 10 000 ₸'], ['3', 'Премиум, 20 000 ₸']] },
      { n: 'devices', l: 'Количество устройств', t: 'num', ph: '1–10' },
      { n: 'urgent', l: 'Срочно (+30%)', t: 'yn' },
      { n: 'visit', l: 'Выезд мастера (+5 000 ₸)', t: 'yn' },
      { n: 'ext', l: 'Гарантия 12 месяцев (+10%)', t: 'yn' }] },
  '19': { title: 'Способ оплаты', tag: 'обработка ошибок', btn: 'Выбрать',
    spec: 'При неправильном выборе — сообщение и предложение повторить. Введите номер: 1 карта, 2 наличные, 3 рассрочка, 4 кошелёк.',
    fields: [{ n: 'pay', l: 'Номер способа оплаты', t: 'text', ph: 'Например, 2 (или ошибочное 9)' }] },
  '20a': { title: 'Сценарий 1: доставка', tag: 'switch и if', btn: 'Рассчитать доставку',
    spec: 'Стоимость зависит от города и веса.',
    fields: [
      { n: 'city', l: 'Город', t: 'sel', opts: [['1', 'Астана'], ['2', 'Алматы'], ['3', 'Другой регион']] },
      { n: 'weight', l: 'Вес посылки, кг', t: 'num', ph: '0.1–50' }] },
  '20b': { title: 'Сценарий 2: гарантийный случай', tag: '!, && и if-else', btn: 'Принять решение',
    spec: 'Гарантия действует без следов повреждений, с чеком и в срок.',
    fields: [
      { n: 'months', l: 'Месяцев с покупки', t: 'num', ph: '0–60' },
      { n: 'receipt', l: 'Есть чек или талон', t: 'yn' },
      { n: 'damaged', l: 'Следы падения или жидкости', t: 'yn' }] },
  '20c': { title: 'Сценарий 3: возврат товара', tag: 'switch, || и !', btn: 'Принять решение',
    spec: 'Условия возврата зависят от причины, срока и состояния товара.',
    fields: [
      { n: 'type', l: 'Причина возврата', t: 'sel', opts: [['1', 'Не подошёл'], ['2', 'Брак']] },
      { n: 'days', l: 'Дней с покупки', t: 'num', ph: '0–365' },
      { n: 'used', l: 'Товар использовался', t: 'yn' }] },
  '20d': { title: 'Сценарий 4: трейд-ин', tag: 'switch, if-else и ||', btn: 'Оценить устройство',
    spec: 'Оценка старого устройства при обмене.',
    fields: [
      { n: 'device', l: 'Устройство', t: 'sel', opts: [['1', 'Смартфон'], ['2', 'Видеокарта'], ['3', 'Процессор']] },
      { n: 'age', l: 'Возраст, лет', t: 'num', ph: '0–15' },
      { n: 'cond', l: 'Состояние', t: 'sel', opts: [['1', 'Отличное'], ['2', 'Хорошее'], ['3', 'Плохое']] }] },
  '20e': { title: 'Сценарий 5: акция дня', tag: 'switch и ||', btn: 'Показать акцию',
    spec: 'Акция зависит от дня недели, бонус — от суммы или выходного.',
    fields: [
      { n: 'day', l: 'День недели', t: 'sel', opts: [['1', 'Понедельник'], ['2', 'Вторник'], ['3', 'Среда'], ['4', 'Четверг'], ['5', 'Пятница'], ['6', 'Суббота'], ['7', 'Воскресенье']] },
      { n: 'sum', l: 'Сумма покупки, ₸', t: 'num', ph: 'Например, 90000' }] },

  '21': { title: 'Правила модуля «Анализ клиента»', tag: 'пять условий', btn: 'Показать правила',
    spec: 'Предметная область модуля — лояльность клиента. Пять условий, по которым начисляются баллы.', fields: [] },
  '23': { title: 'Анализ клиента', tag: 'пять параметров, три уровня', btn: 'Проанализировать',
    spec: 'Пять параметров дают итоговый результат: низкий, средний или высокий уровень.',
    fields: [
      { n: 'purchases', l: 'Покупок за год', t: 'num', ph: '0–1000' },
      { n: 'avg', l: 'Средний чек, ₸', t: 'num', ph: 'Например, 90000' },
      { n: 'returns', l: 'Возвратов', t: 'num', ph: '0–100' },
      { n: 'review', l: 'Оставил отзыв о магазине', t: 'yn' },
      { n: 'months', l: 'Месяцев с нами', t: 'num', ph: '0–600' }] },
  '24': { title: 'Персональные рекомендации', tag: 'комбинации условий', btn: 'Показать рекомендации',
    spec: 'Рекомендации зависят от комбинации условий. Сначала нужен анализ клиента.', fields: [] }
};

function taskLabel(id) {
  if (id === '23') return 'Задания 22–23';
  return 'Задание ' + id.replace(/[a-e]$/, '');
}

const ICON = {
  good: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9.5"/><path d="M7.5 12.5l3 3 6-6.5"/></svg>',
  warn: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 3.5l9.5 16.5h-19z"/><path d="M12 10v4.5M12 17.2v.2"/></svg>',
  bad: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9.5"/><path d="M8.5 8.5l7 7M15.5 8.5l-7 7"/></svg>',
  info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9.5"/><path d="M12 11v6M12 7.6v.2"/></svg>'
};

const LOGO = '<svg viewBox="0 0 32 32" aria-hidden="true"><rect width="32" height="32" rx="6" fill="#0d5c48"/><rect x="9" y="9" width="14" height="14" rx="2" fill="#10211c"/><path d="M12 5v4M16 5v4M20 5v4M12 23v4M16 23v4M20 23v4M5 12h4M5 16h4M5 20h4M23 12h4M23 16h4M23 20h4" stroke="#d9a92b" stroke-width="2"/></svg>';

/* ---------- утилиты ---------- */
const app = document.getElementById('app');
let state = { loggedIn: false };

function esc(s) {
  return String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

async function api(path, data) {
  try {
    const opt = data === undefined ? {} : {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams(data).toString()
    };
    const r = await fetch(path, opt);
    return await r.json();
  } catch (e) {
    return { ok: false, error: 'Нет связи с программой. Проверьте, что окно с запущенным сервером не закрыто, и обновите страницу.' };
  }
}

function statusClass(s) {
  if (s === 'АКТИВНЫЙ') return 'good';
  if (s === 'НЕАКТИВНЫЙ') return 'warn';
  if (s === 'ЗАБЛОКИРОВАННЫЙ') return 'bad';
  return '';
}

/* ---------- вход ---------- */
function renderLogin(message) {
  app.innerHTML = `
  <div class="login">
    <section class="login-art" aria-hidden="false">
      <svg viewBox="0 0 600 800" preserveAspectRatio="xMidYMid slice" aria-hidden="true">
        <path class="faint" d="M0 120H180L230 170V420M600 90H420L380 130V300M0 640H140L200 580H420L480 640H600"/>
        <path class="trace" pathLength="1" d="M0 260H150L200 310H330L380 360V560H600"/>
        <path class="trace" pathLength="1" d="M600 420H470L420 470V700H250L200 750H0"/>
        <path class="trace" pathLength="1" d="M300 0V90L340 130H600"/>
        <circle class="pad" cx="330" cy="310" r="8"/><circle class="pad" cx="380" cy="560" r="8"/>
        <circle class="pad" cx="420" cy="700" r="8"/><circle class="pad" cx="340" cy="130" r="8"/>
      </svg>
      <div class="copy">
        <h1>TechParts</h1>
        <p>Магазин комплектующих для ПК и телефонов</p>
      </div>
    </section>
    <section class="login-form">
      <form id="login-form" novalidate>
        <h2>Вход в систему</h2>
        <div id="login-fail" role="alert">${message ? `<div class="fail">${esc(message)}</div>` : ''}</div>
        <div class="field"><label class="lbl" for="lg">Логин</label><input type="text" id="lg" name="login" autocomplete="username" autofocus></div>
        <div class="field"><label class="lbl" for="pw">Пароль</label><input type="password" id="pw" name="password" autocomplete="current-password"></div>
        <button class="btn" type="submit">Войти</button>
        <p class="hint">Для проверки: логин admin, пароль 1234. После трёх неверных попыток вход закрывается на 15 секунд.</p>
      </form>
    </section>
  </div>`;
}

/* ---------- каркас ---------- */
function navItems(route) {
  const items = [['home', 'Главная']]
    .concat(MODULES.map(m => ['m' + m.id, m.nav]))
    .concat([['report', 'Отчёт']]);
  return items.map(([id, name]) => {
    const locked = state.blocked && /^m[2-5]$/.test(id);
    return `<a href="#${id}"${route === id ? ' aria-current="page"' : ''}${locked ? ' class="locked" title="Модуль закрыт: пользователь заблокирован"' : ''}>${esc(name)}</a>`;
  }).join('');
}

function chromeHTML(route) {
  return `
  <header class="top"><div class="top-in">
    <a class="brand" href="#home">${LOGO}<span>TechParts</span></a>
    <nav class="nav" aria-label="Разделы">${navItems(route)}</nav>
    <div class="who">
      <span class="chip ${statusClass(state.status)}">Статус: ${esc(String(state.status).toLowerCase())}</span>
      <b class="who-name">${esc(state.login)}</b>
      <button class="link" id="logout" type="button">Выйти</button>
    </div>
  </div></header>
  ${state.blocked ? '<div class="banner" role="status">Пользователь заблокирован: модули 2–5 закрыты. Снять блокировку можно в модуле «Основы», задание 3.</div>' : ''}`;
}

function drawChrome() {
  const route = currentRoute();
  const holder = document.getElementById('chrome');
  if (holder) holder.innerHTML = chromeHTML(route);
}

function currentRoute() {
  const h = location.hash.slice(1);
  return /^(home|m[1-5]|report)$/.test(h) ? h : 'home';
}

async function route() {
  state = await api('/api/state');
  if (!state.loggedIn) { renderLogin(); return; }
  const r = currentRoute();
  let view;
  if (r === 'home') view = homeHTML();
  else if (r === 'report') view = await reportHTML();
  else view = moduleHTML(Number(r.slice(1)));
  app.innerHTML = `<div id="chrome">${chromeHTML(r)}</div><main id="main" class="wrap">${view}</main>`;
  document.title = 'TechParts — ' + titleFor(r);
  window.scrollTo(0, 0);
  document.querySelectorAll('form.task-form').forEach(applyShow);
}

function titleFor(r) {
  if (r === 'home') return 'комплектующие для ПК и телефонов';
  if (r === 'report') return 'итоговый отчёт';
  return MODULES[Number(r.slice(1)) - 1].name;
}

/* ---------- главная ---------- */
function boardSVG() {
  const node = (x, y, href, t1, t2) => `
    <a class="node" href="${href}" aria-label="${esc(t1)}, ${esc(t2)}">
      <rect x="${x}" y="${y}" width="240" height="70"/>
      <text class="t1" x="${x + 16}" y="${y + 30}">${esc(t1)}</text>
      <text class="t2" x="${x + 16}" y="${y + 52}">${esc(t2)}</text>
    </a>`;
  return `
  <svg class="board" viewBox="0 0 960 380" role="group" aria-label="Схема системы: модули подключены к центральному блоку">
    <rect class="base" width="960" height="380" rx="12"/>
    <path class="faint" d="M0 20H120L150 50M960 360H820L790 330M300 360H200L170 340M960 20H860"/>
    <g fill="none">
      <path class="trace" pathLength="1" d="M270 75H300L330 105V160H370"/>
      <path class="trace" pathLength="1" d="M270 305H300L330 275V220H370"/>
      <path class="trace" pathLength="1" d="M690 75H660L630 105V160H590"/>
      <path class="trace" pathLength="1" d="M690 305H660L630 275V220H590"/>
      <path class="trace" pathLength="1" d="M480 88V135"/>
      <path class="trace" pathLength="1" d="M480 292V245"/>
    </g>
    <g class="pad">
      <circle cx="270" cy="75" r="6"/><circle cx="270" cy="305" r="6"/><circle cx="690" cy="75" r="6"/>
      <circle cx="690" cy="305" r="6"/><circle cx="480" cy="88" r="6"/><circle cx="480" cy="292" r="6"/>
    </g>
    <rect class="chip-body" x="370" y="135" width="220" height="110" rx="6"/>
    <g class="pad"><rect x="362" y="156" width="10" height="8"/><rect x="362" y="216" width="10" height="8"/><rect x="588" y="156" width="10" height="8"/><rect x="588" y="216" width="10" height="8"/><rect x="476" y="127" width="8" height="10"/><rect x="476" y="243" width="8" height="10"/></g>
    <text class="chip-name" x="480" y="188">TechParts</text>
    <text class="chip-sub" x="480" y="212">ПК и телефоны</text>
    ${node(30, 40, '#m1', 'Основы', 'Задания 2–5')}
    ${node(30, 270, '#m2', 'Логические условия', 'Задания 6–10')}
    ${node(360, 18, '#m3', 'Практика', 'Задания 11–15')}
    ${node(690, 40, '#m4', 'Switch и сценарии', 'Задания 16–20')}
    ${node(690, 270, '#m5', 'Анализ клиента', 'Задания 21–24')}
    ${node(360, 292, '#report', 'Итоговый отчёт', 'Задание 25')}
  </svg>`;
}

function homeHTML() {
  const rows = MODULES.map(m => `
    <div class="index-row">
      <div><h3><a href="#m${m.id}">${esc(m.name)}</a></h3><div class="range">${esc(m.range)}</div></div>
      <ul>${m.tasks.map(t => `<li>${esc(TASKS[t].title)}</li>`).join('')}</ul>
      <a class="link" href="#m${m.id}">Открыть модуль</a>
    </div>`).join('');
  return `
  <section class="hero">
    <h1>Комплектующие для ПК и телефонов</h1>
    <p>Учебная система магазина. Скидки, рассрочку, риски, гарантию и рекомендации считает программа на Java по 25 заданиям: выберите модуль на схеме или в списке ниже.</p>
    ${boardSVG()}
  </section>
  <section class="index">
    <h2>Что умеет система</h2>
    ${rows}
    <div class="index-row">
      <div><h3><a href="#report">Итоговый отчёт</a></h3><div class="range">Задание 25</div></div>
      <ul><li>Журнал всех решений</li><li>Итоговое решение системы</li></ul>
      <a class="link" href="#report">Открыть отчёт</a>
    </div>
  </section>`;
}

/* ---------- модуль и карточки заданий ---------- */
function fieldHTML(taskId, f) {
  const id = taskId + '-' + f.n;
  const show = f.show ? ` data-show='${JSON.stringify(f.show)}'` : '';
  let control;
  if (f.t === 'yn') {
    const val = f.def || 'no';
    control = `<div class="seg" role="group" aria-labelledby="${id}-l">${YN.map(([v, l]) =>
      `<button type="button" data-val="${v}" aria-pressed="${v === val}">${l}</button>`).join('')}</div>
      <input type="hidden" name="${f.n}" value="${val}">`;
    return `<div class="field" data-name="${f.n}"${show}><span class="lbl" id="${id}-l">${esc(f.l)}</span>${control}</div>`;
  }
  if (f.t === 'sel') {
    control = `<select id="${id}" name="${f.n}">${f.opts.map(([v, l]) => `<option value="${v}">${esc(l)}</option>`).join('')}</select>`;
  } else {
    const type = f.t === 'pass' ? 'password' : 'text';
    const mode = f.t === 'num' ? ' inputmode="decimal"' : '';
    control = `<input type="${type}" id="${id}" name="${f.n}"${mode} autocomplete="off" placeholder="${esc(f.ph || '')}">`;
  }
  return `<div class="field" data-name="${f.n}"${show}><label class="lbl" for="${id}">${esc(f.l)}</label>${control}</div>`;
}

function taskHTML(id) {
  const t = TASKS[id];
  return `
  <article class="task" id="task-${id}">
    <div class="task-head"><span class="tab">${taskLabel(id)}</span><span class="tech">${esc(t.tag)}</span></div>
    <form class="task-form task-body" data-id="${id}" novalidate>
      <div><h3>${esc(t.title)}</h3><p class="spec">${esc(t.spec)}</p></div>
      ${t.fields.length ? `<div class="fields">${t.fields.map(f => fieldHTML(id, f)).join('')}</div>` : ''}
      <button class="btn" type="submit">${esc(t.btn)}</button>
      <div class="result" aria-live="polite"></div>
    </form>
  </article>`;
}

function moduleHTML(n) {
  const m = MODULES[n - 1];
  const head = `<div class="page-head"><h1>${esc(m.name)}</h1><p>${esc(m.desc)}</p><p class="range">${esc(m.range)}</p></div>`;
  if (state.blocked && n >= 2) {
    return head + `<div class="locked-panel"><h3>Модуль закрыт</h3>
      <p>Пользователь заблокирован, поэтому модули 2–5 недоступны. Чтобы снять блокировку, откройте «Основы», задание 3, и укажите меньше трёх нарушений.</p>
      <a class="link" href="#m1">Перейти в «Основы»</a></div>`;
  }
  return head + `<div class="tasks">${m.tasks.map(taskHTML).join('')}</div>`;
}

function resultHTML(r) {
  if (!r.ok) {
    return `<div class="res bad">${ICON.bad}<div class="txt"><p class="title">Не получилось</p><p>${esc(r.error)}</p><button type="button" class="link retry">Повторить ввод</button></div></div>`;
  }
  const lines = r.lines.length ? `<ul>${r.lines.map(l => `<li>${esc(l)}</li>`).join('')}</ul>` : '';
  const items = r.items.length ? `<div class="items">${r.items.map(i => `<div class="item"><span>${esc(i.name)}</span><b>${esc(i.price)}</b></div>`).join('')}</div>` : '';
  return `<div class="res ${r.level}">${ICON[r.level] || ICON.info}<div class="txt"><p class="title">${esc(r.headline)}</p>${lines}${items}</div></div>`;
}

/* поля, которые появляются только при нужных ответах (вложенные if) */
function applyShow(form) {
  const values = {};
  form.querySelectorAll('[name]').forEach(el => { values[el.name] = el.value; });
  form.querySelectorAll('.field[data-show]').forEach(box => {
    const rules = JSON.parse(box.dataset.show);
    const visible = rules.every(([name, val]) => values[name] === val);
    box.hidden = !visible;
    box.querySelectorAll('[name]').forEach(el => { el.disabled = !visible; });
  });
}

async function submitTask(form) {
  const id = form.dataset.id;
  const data = {};
  form.querySelectorAll('[name]').forEach(el => { if (!el.disabled) data[el.name] = el.value; });
  const box = form.querySelector('.result');
  const r = await api('/api/task/' + id, data);
  box.innerHTML = resultHTML(r);
  const wasBlocked = state.blocked;
  state = await api('/api/state');
  drawChrome();
  const m = /^m([2-5])$/.exec(currentRoute());
  if (state.blocked && !wasBlocked && m) { route(); }
}

/* ---------- отчёт ---------- */
async function reportHTML() {
  const r = await api('/api/report');
  if (!r.ok) return `<div class="page-head"><h1>Итоговый отчёт</h1><p>${esc(r.error)}</p></div>`;
  const journal = r.decisions.length
    ? `<ol class="journal">${r.decisions.map(d => `<li><span>${esc(d)}</span></li>`).join('')}</ol>`
    : '<p class="empty">Решений пока нет. Выполните задания в модулях, и они появятся здесь.</p>';
  return `
  <div class="page-head"><h1>Итоговый отчёт</h1><p>Что решила система в этой сессии.</p></div>
  <div class="verdict ${r.finalLevel}"><div class="small">Итоговое решение</div><h2>${esc(r.finalText)}</h2></div>
  <div class="facts">
    <div class="fact"><span>Пользователь</span><b>${esc(r.user)}</b></div>
    <div class="fact"><span>Статус</span><b>${esc(String(r.status).toLowerCase())}</b></div>
    <div class="fact"><span>Риск по заказу</span><b>${esc(String(r.risk).toLowerCase())}</b></div>
    <div class="fact"><span>Уровень клиента</span><b>${esc(String(r.clientLevel).toLowerCase())}</b></div>
    <div class="fact"><span>Принято решений</span><b>${r.count}</b></div>
  </div>
  <h2>Журнал решений</h2>
  ${journal}
  <div class="actions">
    <button class="btn" type="button" id="print">Распечатать отчёт</button>
    <button class="btn ghost" type="button" id="reset">Очистить журнал</button>
  </div>`;
}

/* ---------- события ---------- */
document.addEventListener('submit', async e => {
  const f = e.target;
  if (f.id === 'login-form') {
    e.preventDefault();
    const r = await api('/api/login', { login: f.login.value, password: f.password.value });
    if (r.ok) { location.hash = '#home'; route(); }
    else {
      const box = document.getElementById('login-fail');
      box.innerHTML = `<div class="fail">${esc(r.error)}</div>`;
      f.password.value = '';
      f.password.focus();
    }
  } else if (f.classList.contains("task-form")) {
    e.preventDefault();
    submitTask(f);
  }
});

document.addEventListener('click', async e => {
  const t = e.target;
  const seg = t.closest('.seg button');
  if (seg) {
    const wrap = seg.parentElement;
    wrap.querySelectorAll('button').forEach(b => b.setAttribute('aria-pressed', b === seg));
    wrap.nextElementSibling.value = seg.dataset.val;
    applyShow(seg.closest('form'));
    return;
  }
  if (t.classList.contains('retry')) {
    const form = t.closest('form');
    const first = form.querySelector('.field:not([hidden]) input:not([type=hidden]), .field:not([hidden]) select');
    if (first) { if ('value' in first && first.tagName === 'INPUT') first.value = ''; first.focus(); }
    return;
  }
  if (t.id === 'logout') { await api('/api/logout', {}); location.hash = ''; route(); return; }
  if (t.id === 'print') { window.print(); return; }
  if (t.id === 'reset') { await api('/api/reset', {}); route(); }
});

document.addEventListener('change', e => {
  const form = e.target.closest && e.target.closest('form.task-form');
  if (form) applyShow(form);
});

window.addEventListener('hashchange', route);
route();
