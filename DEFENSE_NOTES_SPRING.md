# Spring — шпаргалка для защиты (по этому проекту)

## 1) Коротко про Spring и IoC

### Что такое IoC (Inversion of Control)
Spring управляет созданием объектов (бинов) и их жизненным циклом.
Твой код не делает `new` для зависимостей напрямую — вместо этого зависимости **внедряются контейнером**.

### DI (Dependency Injection)
Способы DI:
- **Constructor injection** (рекомендовано)
- Setter injection
- Field injection (не рекомендовано)

Почему в учебных и реальных проектах любят constructor injection:
- зависимости `final`
- объект нельзя создать в неконсистентном состоянии
- проще тестировать

### Что такое Spring Container / ApplicationContext (очень простыми словами)
`ApplicationContext` — это “фабрика и реестр” объектов.
Он:
- читает конфигурацию (аннотации `@ComponentScan`, `@Configuration`, `@Bean`)
- находит классы-компоненты (`@Component`, `@Service`, `@Repository`)
- **создаёт** объекты-бин
- **внедряет** зависимости (DI)
- управляет **жизненным циклом** (инициализация/уничтожение)

В твоём проекте контейнер создаётся вручную:
- `new AnnotationConfigApplicationContext(MainAnnotation.class)`

Важно: когда ты делаешь `ctx.getBean(SomeClass.class)`:
- для singleton бина: контейнер вернёт уже созданный экземпляр
- для prototype бина: контейнер создаст новый экземпляр

---

## 2) Как Spring находит бины

### Component scanning
В проекте используется:
- `@ComponentScan(basePackages = "com.ehu.javacafe")`
- контекст создаётся через `AnnotationConfigApplicationContext`

Файл:
- `src/main/java/com/ehu/javacafe/MainAnnotation.java`

Stereotype-аннотации:
- `@Component` — общий компонент
- `@Service` — сервисный слой
- `@Repository` — слой доступа к данным
- `@Configuration` — Java-конфигурация

### Чем отличаются @Component / @Service / @Repository (по сути)
Технически для контейнера это все “компоненты”, которые можно найти сканированием.
Разница в смысле:
- `@Service` — бизнес-логика.
- `@Repository` — доступ к данным.

Полезная деталь: в больших приложениях `@Repository` участвует в “переводе” низкоуровневых исключений доступа к данным в единый тип Spring (`DataAccessException`).

### Как Spring выбирает, какой бин внедрять (autowire resolution)
Когда у тебя constructor injection по типу:

```java
public CoffeeService(BeverageRepository beverageRepository) { ... }
```

Spring ищет бин по **типу** `BeverageRepository`.
Дальше варианты:
- **ровно 1 бин** такого типа -> внедрит его
- **0 бинов** -> ошибка `NoSuchBeanDefinitionException`
- **2+ бина** -> ошибка `NoUniqueBeanDefinitionException` (если не подсказать, кого выбрать)

Как подсказать:
- `@Primary` на одном из бинов (выбор “по умолчанию”)
- `@Qualifier("beanName")` на месте инъекции (явный выбор)
- `@Profile` (разные бины для разных окружений)

---

## 3) Что реализовано в проекте (по пунктам задания)

### 3.1 Constructor injection репозитория в selector
**Цель:** селектор напитков должен получать `BeverageRepository` через конструктор.

Сделано в:
- `src/main/java/com/ehu/javacafe/service/BeverageSelectorRandom.java`

Факты для защиты:
- `BeverageSelector` — интерфейс, поэтому DI делается в конкретной реализации.
- `BeverageSelectorRandom` — Spring бин (`@Component`).
- `BeverageRepository` передаётся через конструктор.

### Почему “инжектить в интерфейс” нельзя (простое объяснение)
Интерфейс — это только набор методов (контракт), а не объект.
Spring может создать только **класс** (экземпляр), поэтому зависимости инжектятся в:
- конструктор класса
- поля/сеттеры класса

То есть в заданиях обычно подразумевается: “инжектить репозиторий в реализацию `BeverageSelector`”.

---

### 3.2 Перевод на репозиторий, читающий напитки из файла
**Идея:** есть 2 реализации `BeverageRepository`:
- `DBBeverageRepository` — данные из H2/Spring Data JDBC
- `JSONBeverageRepository` — данные из `beverages.json`

Сделано:
- `JSONBeverageRepository` сделан Spring-бином:
  - добавлено `@Repository`
  - добавлено `@Primary`
  - убран ручной singleton (`getInstance()`), чтобы не мешал DI

Файлы:
- `src/main/java/com/ehu/javacafe/repository/impl/JSONBeverageRepository.java`
- `src/main/resources/beverages.json`

Как объяснить на защите:
- Spring видит 2 бина типа `BeverageRepository`.
- `@Primary` говорит: «используй этот бин по умолчанию при инъекции по типу».

### @Primary vs @Qualifier — в чём разница
`@Primary`:
- выбор “по умолчанию”
- удобно, когда в приложении в 90% случаев нужна одна реализация

`@Qualifier`:
- точечный выбор конкретного бина
- удобно, когда разные части приложения должны работать с разными реализациями

Если бы нужно было выбирать репозиторий явно, это выглядело бы так:

```java
public CoffeeService(@Qualifier("DBBeverageRepository") BeverageRepository repo) {
  this.beverageRepository = repo;
}
```

---

### 3.3 Prototype scope для BeverageSelector
Сделано:
- `@Scope("prototype")` на `BeverageSelectorRandom`

Файл:
- `src/main/java/com/ehu/javacafe/service/BeverageSelectorRandom.java`

Что говорить:
- prototype значит: **каждый `ctx.getBean(...)` создаёт новый объект**.
- удобно, потому что `amountOfBeverages` генерируется случайно при создании экземпляра.

### Важно: prototype и внедрение в singleton
Prototype создаётся контейнером каждый раз при запросе.
Но если prototype “вколоть” в singleton как поле, то он будет создан один раз при создании singleton.
Чтобы получать новый prototype на каждый вызов, используют:
- `ObjectProvider<BeverageSelectorRandom>`
- либо `ctx.getBean(BeverageSelectorRandom.class)` в месте использования

---

### 3.4 Добавлен 1 дополнительный бин (мой вариант)
Добавлен бин статистики выбора:
- `BeverageSelectionStatsService`

Файл:
- `src/main/java/com/ehu/javacafe/service/BeverageSelectionStatsService.java`

Интеграция:
- внедрён в `BeverageSelectorRandom` через конструктор
- после формирования списка напитков вызывается `recordSelection(order)`

Что делает:
- считает, сколько раз выбирали напитки (по `id`)
- логирует snapshot статистики

---

### 3.5 Lazy бин + запрос после N секунд
Добавлен lazy бин:
- `DailyBeverageDiscountService` с `@Lazy`

Файлы:
- `src/main/java/com/ehu/javacafe/service/DailyBeverageDiscountService.java`
- `src/main/java/com/ehu/javacafe/MainAnnotation.java`

Как демонстрировать:
- приложение стартует
- ждёт `N` секунд (`Thread.sleep`)
- после паузы вызывается `ctx.getBean(DailyBeverageDiscountService.class)`
- только в этот момент lazy бин создаётся

### Типовые вопросы: "почему он lazy, но всё равно создался?"
Lazy бин создастся сразу, если:
- кто-то другой (не-lazy) бин запросил его при старте
- ты сам сделал `ctx.getBean(...)` раньше

В твоём варианте он создаётся позже, потому что ты запрашиваешь его после `Thread.sleep`.

---

## 4) Жизненный цикл бина: @PostConstruct и @PreDestroy

Добавлено в lazy сервис:
- `@PostConstruct` — вызывается после создания и инъекций
- `@PreDestroy` — вызывается при закрытии контекста (`ctx.close()`)

Файл:
- `src/main/java/com/ehu/javacafe/service/DailyBeverageDiscountService.java`

Что сказать:
- `@PostConstruct` срабатывает при первом `getBean(...)` (так как бин lazy)
- `@PreDestroy` срабатывает при `ctx.close()`

### Что важно понимать про lifecycle и scope
- Для **singleton** бинов:
  - создаются при старте контекста (если не `@Lazy`)
  - `@PostConstruct` вызывается при старте
  - `@PreDestroy` вызывается при закрытии контекста
- Для **prototype** бинов:
  - создаются при каждом `getBean(...)`
  - `@PostConstruct` будет вызываться после каждого создания
  - `@PreDestroy` **обычно не вызывается контейнером автоматически**, потому что контейнер не управляет уничтожением prototype экземпляров (их должен “убрать” код, который их создал/использует)

### Практика в твоём проекте
Если ты вернёшь `@PostConstruct/@PreDestroy` в `DailyBeverageDiscountService`:
- `@PostConstruct` будет видно после `ctx.getBean(DailyBeverageDiscountService.class)`
- `@PreDestroy` будет видно на `ctx.close()`

---

## 5) Unit tests

### Что добавлено
В `pom.xml` добавлены:
- JUnit 5 (`junit-jupiter`)
- `maven-surefire-plugin`, чтобы тесты запускались

### Тест 1: JSON репозиторий читает beverages.json
Файл:
- `src/test/java/com/ehu/javacafe/repository/impl/JSONBeverageRepositoryTest.java`

### Тест 2: Spring выбирает JSONBeverageRepository как primary
Файл:
- `src/test/java/com/ehu/javacafe/SpringRepositoryWiringTest.java`

Как запускать:
- `mvn test`

### Зачем нужны тесты в контексте Spring
Здесь у тебя 2 вида проверок:
- “обычный unit test” без Spring: создаём объект через `new` и проверяем его логику
- “проверка wiring контейнера” (мини-интеграционный тест): поднимаем `ApplicationContext` и проверяем, что Spring выбрал правильный бин

В проекте это отражено так:
- `JSONBeverageRepositoryTest` — unit
- `SpringRepositoryWiringTest` — wiring/контекст

Важно для защиты: wiring тест доказует, что `@Primary` реально влияет на выбор бина.

---

## 6) Конфигурация БД и Spring Data JDBC (что тут происходит)

### Файл конфигурации
- `src/main/java/com/ehu/javacafe/configuration/DatabaseConfiguration.java`

Что делает этот класс:
- `@Configuration` — говорит Spring: “это конфиг-класс, в нём есть `@Bean` методы”
- `@EnableJdbcRepositories(basePackages = "com.ehu.javacafe.repository")` — включает Spring Data JDBC репозитории
- создаёт `DataSource` (встроенная H2) + `JdbcTemplate` + `TransactionManager`

### Как появляется BeverageCrudRepository
У тебя есть интерфейс:
- `BeverageCrudRepository extends CrudRepository<Beverage, Long>`

Spring Data JDBC автоматически создаёт реализацию этого интерфейса во время старта контекста.
Потом `DBBeverageRepository` получает `BeverageCrudRepository` через конструктор.

### Почему даже при JSON репозитории стартует H2
Потому что:
- `DatabaseConfiguration` всё равно поднимается при component-scan
- `@EnableJdbcRepositories` сканирует JDBC репозитории
- контекст создаёт `DataSource` и связанные бины

Это не мешает логике выбора `BeverageRepository`, потому что выбор конкретной реализации репозитория решается через `@Primary`.

Если на защите спросят “как отключить БД”, правильный ответ:
- вынести DB-конфигурацию в `@Profile("db")`
- а JSON репозиторий в `@Profile("file")`
- и включать нужный профиль через properties/параметры запуска

---

## 6) Частые вопросы на защите (краткие ответы)

### Почему не field injection?
Плохо тестируется, нельзя сделать зависимости `final`, сложнее контролировать создание объекта.

### Зачем `@Primary`?
Когда есть несколько бинов одного типа, `@Primary` выбирает бин «по умолчанию».

### Чем singleton Spring отличается от prototype?
- singleton: один объект на контекст
- prototype: новый объект на каждый запрос `getBean`

### Зачем @Lazy?
Экономит ресурсы: бин создаётся только если реально понадобился.

### Что такое BeanDefinition (очень коротко)
Это “описание” бина в контейнере: какой класс, какой scope, lazy или нет, имя и т.д.
По этим определениям Spring потом создаёт реальные объекты.

### Почему @PostConstruct/@PreDestroy иногда “не срабатывают”
Типовые причины:
- бин не Spring-овский (создан через `new`)
- контекст не закрывается (`ctx.close()` не вызван)
- бин `@Lazy` и ты ни разу его не запросил
- для prototype `@PreDestroy` обычно не вызывается контейнером

### Почему у меня два репозитория и не падает с NoUniqueBeanDefinitionException?
Потому что один из бинов помечен `@Primary`, и Spring выбирает его по умолчанию.

---

## 7) Как показать работу вживую (мини-сценарий)

1. Запустить `MainAnnotation`.
2. Убедиться, что напитки берутся из `beverages.json`.
3. Показать, что `BeverageSelectorRandom` prototype:
   - запросить бин 2 раза и увидеть разные значения `amountOfBeverages` (если добавить вывод/лог).
4. Подождать `N` секунд и увидеть, что `DailyBeverageDiscountService` создаётся только при `getBean`.
5. При завершении/`ctx.close()` увидеть `@PreDestroy`.

---

## 8) Вопросы-ответы (готовые формулировки)

### Q: Что такое DI в одном предложении?
A: DI — это когда объект не создаёт свои зависимости сам, а получает их извне (от контейнера Spring).

### Q: Какой DI ты использовал и почему?
A: Constructor injection — зависимости `final`, объект нельзя создать без зависимостей, проще тестировать.

### Q: Почему ты использовал @Primary?
A: Потому что есть две реализации `BeverageRepository` (DB и JSON), а я хотел, чтобы по умолчанию выбиралась JSON-реализация.

### Q: Что делает @Scope("prototype")?
A: Каждый вызов `getBean` возвращает новый экземпляр.

### Q: Что делает @Lazy?
A: Откладывает создание бина до первого использования.

### Q: Когда вызовется @PostConstruct и @PreDestroy?
A: `@PostConstruct` — после создания бина и инъекции зависимостей, `@PreDestroy` — при закрытии контекста (для singleton).

