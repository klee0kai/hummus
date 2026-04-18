# DesignComponentsRegistryProcessor

KSP процессор который автоматически регистрирует все composable методы, аннотированные `@DesignComponent`, и предоставляет удобный API для работы с ними.

## Как это работает

1. **Обнаружение**: Процессор находит все функции с аннотацией `@DesignComponent`
2. **Анализ**: Извлекает информацию о параметрах (имя, тип, дефолтное значение, является ли composable)
3. **Генерация**: Создает `DesignComponentsRegistry` с полным списком компонентов
4. **Вызов**: Для методов со всеми параметрами-дефолтами генерирует `content` лямбду

## Файлы процессора

- **`DesignComponentsRegistryProcessor.kt`** - главный процессор
- **`ProcessorProvider.kt`** - регистрация процессора

## Структуры данных

### DesignComponentMethod
```kotlin
class DesignComponentMethod(
    val pkg: String,                        // com.example.components
    val methodName: String,                 // MyButton
    val parameters: List<ComponentParameter>,
    val content: @Composable () -> Unit     // Вызов с дефолтами
)
```

### ComponentParameter
```kotlin
data class ComponentParameter(
    val name: String,                // "text"
    val type: String,                // "kotlin.String"
    val isComposable: Boolean,       // true если @Composable
    val hasDefault: Boolean          // true если есть дефолт
)
```

### ComponentParameterBuilder
```kotlin
class ComponentParameterBuilder {
    fun set(name: String, value: Any?): ComponentParameterBuilder
    fun setIfPresent(name: String, value: Any?): ComponentParameterBuilder
    fun <T : Any> setOfType(name: String, value: T): ComponentParameterBuilder
    fun getOrNull(name: String): Any?
    fun getMissingRequired(): List<String>
    fun isComplete(): Boolean
}
```

## Примеры использования

### Получение информации о компонентах
```kotlin
@Composable
fun ComponentDocs() {
    DesignComponentsRegistry.components.forEach { component ->
        Column {
            Text("${component.methodName} (${component.pkg})")
            Text("Параметры:")
            component.parameters.forEach { param ->
                val default = if (param.hasDefault) " = default" else ""
                Text("  - ${param.name}: ${param.type}$default")
            }
        }
    }
}
```

### Поиск и вызов компонентов
```kotlin
@Composable
fun ShowComponent(methodName: String) {
    val component = DesignComponentsRegistry.findComponent(methodName)
    if (component != null && component.parameters.all { it.hasDefault }) {
        component.content()  // Вызов с дефолтными параметрами
    }
}
```

### Создание UI для выбора компонентов
```kotlin
@Composable
fun ComponentShowcase() {
    val componentsByPackage = DesignComponentsRegistry.components
        .groupBy { it.pkg }
    
    LazyColumn {
        componentsByPackage.forEach { (pkg, components) ->
            item { Text("Package: $pkg") }
            items(components) { component ->
                Button(onClick = {
                    if (component.parameters.all { it.hasDefault }) {
                        component.content()
                    }
                }) {
                    Text(component.methodName)
                }
            }
        }
    }
}
```

## Интеграция в build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    ksp(project(":design-ksp"))
}
```

## Использование с многоплатформностью

Для мультиплатформных проектов:

```kotlin
dependencies {
    ksp(project(":design-ksp"))
}
```

Генерированный файл находится в:
- JVM: `build/generated/ksp/jvm/jvmMain/kotlin/...`
- JS: `build/generated/ksp/js/jsMain/kotlin/...`
- Другие платформы соответственно

## Лучшие практики

1. **Дефолтные параметры**: Используйте дефолтные значения для всех параметров если хотите автоматического вызова через `content`
2. **Документирование**: Аннотируйте параметры документацией в коде
3. **Группировка**: Используйте имена пакетов для логической группировки компонентов
4. **Типизация**: Избегайте `Any` типов - используйте конкретные типы для лучшей инспекции

## Примеры из проекта

### SimpleButton
```kotlin
@DesignComponent
@Composable
fun SimpleButton(
    text: String = "Click me",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) { ... }
```

### StyledCard
```kotlin
@DesignComponent
@Composable
fun StyledCard(
    title: String = "Card Title",
    subtitle: String = "Subtitle",
    isExpanded: Boolean = false,
) { ... }
```

## Сгенерированный код

`DesignComponentsRegistry` содержит:
- `components` - полный список всех компонентов
- `findComponent(methodName)` - поиск по имени метода
- `findComponents(filter)` - поиск по предикату

## Ограничения и заметки

1. Процессор работает только с `@Composable` функциями
2. Параметры composable типов отмечаются с `isComposable = true`
3. Для методов с требуемыми параметрами `content` будет пустой лямбдой
4. Процессор должен быть добавлен в конфигурацию KSP для каждого модуля который использует `@DesignComponent`
