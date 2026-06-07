# Backend import/export design

## Цель

Импорт и экспорт лучше реализовывать как отдельный backend use case, а не как набор вспомогательных методов внутри обычного CRUD-контроллера. В текущем проекте есть слои `controller`, `service`, `repository`, `dto`, `mapper`; импорт/экспорт должен сохранить это разделение:

- controller отвечает за HTTP-контракт, файлы, status code и `Content-Type`;
- service отвечает за сценарий импорта/экспорта, транзакции, валидацию и правила слияния данных;
- mapper отвечает за преобразование между DTO, строками файла и `TodoEntity`;
- repository отвечает только за чтение/запись в БД;
- DTO описывают стабильный формат API и файла, а не раскрывают JPA entity наружу.

## Рекомендуемые форматы

Для этого проекта основной формат стоит сделать JSON, потому что поля `priority`, `category` и `deadline` уже хорошо сериализуются через Jackson:

```json
{
  "version": 1,
  "exportedAt": "2026-06-05T12:00:00Z",
  "items": [
    {
      "title": "Buy milk",
      "description": "2 bottles",
      "priority": "medium",
      "category": "personal",
      "deadline": "2026-06-10"
    }
  ]
}
```

CSV можно добавить вторым форматом, если нужен Excel-friendly обмен. Для первой реализации JSON надежнее: меньше неоднозначностей с датами, enum, кавычками, пустыми значениями и кодировкой.

## Предлагаемая структура классов

```text
src/main/java/com/example/demo/
  controller/
    TodoImportExportController.java
  service/
    TodoImportExportService.java
  dto/
    TodoExportDto.java
    TodoImportRequestDto.java
    TodoImportResultDto.java
    TodoImportErrorDto.java
  mapper/
    TodoImportExportMapper.java
  repository/
    TodoRepository.java
```

Можно оставить `TodoController` только для CRUD, а импорт/экспорт вынести в `TodoImportExportController`. Это делает API понятнее и не смешивает одиночные операции с batch-сценариями.

## Controller layer

### Экспорт всех задач

```java
@GetMapping(value = "/api/todos/export", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<TodoExportDto> exportTodos() {
    TodoExportDto body = importExportService.exportTodos();
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.json\"")
            .body(body);
}
```

Ответственность controller:

- принять HTTP-запрос;
- выставить `Content-Type` и `Content-Disposition`;
- вернуть DTO, `byte[]` или stream;
- не выполнять бизнес-валидацию и не обращаться напрямую к repository.

### Импорт задач

Для JSON body:

```java
@PostMapping(value = "/api/todos/import", consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<TodoImportResultDto> importTodos(@Valid @RequestBody TodoImportRequestDto request) {
    TodoImportResultDto result = importExportService.importTodos(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
}
```

Для файла:

```java
@PostMapping(value = "/api/todos/import-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<TodoImportResultDto> importTodosFile(@RequestPart("file") MultipartFile file) {
    TodoImportResultDto result = importExportService.importTodos(file);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
}
```

Для текущего проекта JSON body проще и чище. `MultipartFile` понадобится, если frontend реально отправляет файл.

## Service layer

Рекомендуемый сервис:

```java
@Service
@RequiredArgsConstructor
public class TodoImportExportService {
    private final TodoRepository todoRepository;
    private final TodoService todoService;
    private final TodoImportExportMapper mapper;

    @Transactional(readOnly = true)
    public TodoExportDto exportTodos() {
        List<TodoEntity> todos = todoRepository.findAllByOrderByDeadlineAscIdAsc();
        return mapper.toExportDto(todos);
    }

    @Transactional
    public TodoImportResultDto importTodos(TodoImportRequestDto request) {
        // validate request version and size
        // map each item to TodoEntity
        // reuse TodoService.create(entity) so normalization and required checks stay centralized
        // collect imported count and per-row errors
    }
}
```

Важный момент: при импорте не стоит делать `todoRepository.save(...)` напрямую, если уже есть `TodoService.create(...)`. Сейчас `TodoService.create(...)` нормализует `title` и `description`, проверяет `priority`, `category`, `deadline`. Импорт должен переиспользовать эту бизнес-логику, иначе данные из файла могут попасть в БД по другим правилам, чем данные из обычного API.

### Стратегии импорта

Нужно заранее выбрать одну стратегию:

- `all-or-nothing`: если одна строка невалидна, откатывается весь импорт. Проще для консистентности, лучше для небольших файлов.
- `partial success`: валидные строки сохраняются, ошибки возвращаются по строкам. Удобнее пользователю, но сложнее транзакционно и требует аккуратного результата.

Для текущего todo-приложения практичнее начать с `all-or-nothing`. Когда появится UI для отображения ошибок по строкам, можно перейти к `partial success`.

## Repository layer

Для экспорта желательно иметь стабильный порядок:

```java
List<TodoEntity> findAllByOrderByDeadlineAscIdAsc();
```

Если появятся пользователи, методы должны быть scoped:

```java
List<TodoEntity> findAllByUserIdOrderByDeadlineAscIdAsc(Integer userId);
Optional<TodoEntity> findByUserIdAndId(Integer userId, Integer id);
```

Repository не должен знать про JSON, CSV, файлы, версии формата или ошибки импорта. Его задача - дать нужные запросы к БД.

## DTO layer

DTO для экспорта:

```java
public record TodoExportDto(
        int version,
        Instant exportedAt,
        List<TodoExportItemDto> items
) {}

public record TodoExportItemDto(
        String title,
        String description,
        Priority priority,
        Category category,
        LocalDate deadline
) {}
```

DTO для импорта:

```java
public record TodoImportRequestDto(
        Integer version,
        List<TodoImportItemDto> items
) {}

public record TodoImportItemDto(
        String title,
        String description,
        Priority priority,
        Category category,
        LocalDate deadline
) {}

public record TodoImportResultDto(
        int requested,
        int imported,
        List<TodoImportErrorDto> errors
) {}

public record TodoImportErrorDto(
        int row,
        String field,
        String message
) {}
```

Не стоит использовать `TodoEntity` как формат импорта/экспорта. Entity содержит database identity и может измениться из-за JPA/таблиц, а формат файла должен быть стабильным.

## Mapper layer

Отдельный mapper упрощает поддержку формата:

```java
@Component
public class TodoImportExportMapper {
    public TodoExportDto toExportDto(List<TodoEntity> entities) {
        // entity -> export DTO
    }

    public TodoEntity toEntity(TodoImportItemDto item) {
        // import DTO -> entity without id
    }
}
```

Импортируемый `id` лучше не принимать в первой версии. Если принимать `id`, надо решить, это создание, обновление или upsert. Без этого легко случайно перезаписать чужие или старые данные.

## Validation and limits

Минимальные правила:

- максимальный размер файла или массива, например 1000 задач за импорт;
- `version` должен быть поддерживаемым, например `1`;
- `items` не должен быть `null`;
- `title` обязателен и ограничен 100 символами, как в `TodoEntity`;
- `priority`, `category`, `deadline` обязательны;
- `deadline` должен парситься как ISO date: `yyyy-MM-dd`;
- пустое `description` можно нормализовать в `null`, как сейчас делает `TodoService`.

Для DTO можно использовать Bean Validation:

```java
public record TodoImportItemDto(
        @NotBlank @Size(max = 100) String title,
        String description,
        @NotNull Priority priority,
        @NotNull Category category,
        @NotNull LocalDate deadline
) {}
```

## Error handling

Нужен единый `@RestControllerAdvice`, который превратит ошибки в понятный JSON:

```json
{
  "code": "IMPORT_VALIDATION_FAILED",
  "message": "Import file contains invalid todo items",
  "errors": [
    { "row": 3, "field": "priority", "message": "Unknown priority: critical" }
  ]
}
```

Для `all-or-nothing` при ошибке лучше возвращать `400 Bad Request`. Для успешного импорта - `201 Created` или `200 OK` с результатом.

## Security and ownership

В проекте уже есть миграция `users`, поэтому при появлении авторизации импорт/экспорт обязательно должен учитывать владельца:

- export возвращает только задачи текущего пользователя;
- import создает задачи только для текущего пользователя;
- id из файла нельзя доверять;
- нельзя импортировать `userId` из файла без проверки.

## Performance

Для маленьких todo-файлов достаточно `save` через `TodoService.create(...)`. Для больших импортов:

- валидировать весь файл до записи;
- использовать batch insert через `saveAll`;
- включить Hibernate batch settings;
- не держать огромный файл целиком в памяти, если формат станет большим;
- возвращать job id и выполнять импорт асинхронно, если импорт занимает секунды или минуты.

На текущем этапе асинхронность не нужна.

## Testing

Минимальные тесты:

- export возвращает все поля и стабильный порядок;
- import создает задачи с нормализацией title/description;
- import отклоняет неподдерживаемую версию;
- import отклоняет пустой title;
- import отклоняет unknown priority/category;
- import не сохраняет частичные данные при `all-or-nothing`;
- controller возвращает правильные status code и headers для export.

## Recommended first implementation

Для этого проекта оптимальный первый шаг:

1. Создать DTO `TodoExportDto`, `TodoExportItemDto`, `TodoImportRequestDto`, `TodoImportItemDto`, `TodoImportResultDto`, `TodoImportErrorDto`.
2. Создать `TodoImportExportMapper`.
3. Добавить в `TodoRepository` метод `findAllByOrderByDeadlineAscIdAsc()`.
4. Создать `TodoImportExportService` с методами `exportTodos()` и `importTodos(TodoImportRequestDto request)`.
5. Создать `TodoImportExportController` с endpoint-ами:
   - `GET /api/todos/export`
   - `POST /api/todos/import`
6. Добавить `@RestControllerAdvice` для ошибок валидации и импорта.
7. Покрыть сервис тестами, затем controller тестом на HTTP-контракт.

Такой дизайн оставляет CRUD простым, не привязывает JPA entity к формату файла и дает понятное место для будущих CSV, Excel, ownership и async import jobs.
