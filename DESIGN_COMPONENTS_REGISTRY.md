# Design Components Registry

Процессор генерирует реестр всех composable методов, аннотированных `@DesignComponent`, с поддержкой вызова методов через типизированный builder параметров.

## Использование

### 1. Аннотируйте composable методы с @DesignComponent

```kotlin
@DesignComponent
@Composable
fun MyButton(
    text: String = "Click",
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    // composable content
}

@DesignComponent
@Composable
fun StyledCard(
    title: String,
    subtitle: String = "",
    isExpanded: Boolean = false
) {
    // composable content
}
```

### 2. Используйте сгенерированный DesignComponentsRegistry

#### Получение всех компонентов:
```kotlin
val allComponents = DesignComponentsRegistry.components
allComponents.forEach { component ->
    println("${component.methodName} in ${component.pkg}")
    component.parameters.forEach { param ->
        println("  - ${param.name}: ${param.type} (default: ${param.hasDefault})")
    }
}
```

#### Поиск компонента по имени:
```kotlin
val component = DesignComponentsRegistry.findComponent("MyButton")
if (component != null) {
    // Found the component
}
```

#### Поиск компонентов по фильтру:
```kotlin
val withRequiredParams = DesignComponentsRegistry.findComponents { method ->
    method.requiredParameters().isNotEmpty()
}

val inSpecificPackage = DesignComponentsRegistry.findComponents { method ->
    method.pkg.contains("buttons")
}
```

### 3. Вызов методов через content

```kotlin
val myButton = DesignComponentsRegistry.findComponent("MyButton")
if (myButton != null) {
    // Используем content для вызова с дефолтными параметрами
    myButton.content()  // Вызывает MyButton с дефолтными значениями
}

// Для вызова с кастомными параметрами используйте Builder для инспекции параметров,
// а сами вызывайте методы напрямую через свой код
```

### 4. Работа с ComponentParameterBuilder

```kotlin
val component = DesignComponentsRegistry.findComponent("StyledCard")!!
val builder = component.createBuilder()

// Проверка требуемых параметров
val missingRequired = builder.getMissingRequired()
println("Missing: $missingRequired")

// Получение параметра компонента
val titleParam = component.parameterByName("title")
println("Title type: ${titleParam?.type}")
println("Has default: ${titleParam?.hasDefault}")

// Установка параметров с проверкой
builder.setIfPresent("title", "My Title")
builder.setIfPresent("subtitle", "Subtitle")

// Проверка завершенности
if (builder.isComplete()) {
    component.invoke(builder)
}

// Или с исключением
try {
    val params = builder.buildOrThrow()
} catch (e: IllegalStateException) {
    println(e.message) // "Missing required parameters: ..."
}
```

## Структуры данных

### DesignComponentMethod
```kotlin
class DesignComponentMethod(
    val pkg: String,                    // Package name
    val methodName: String,             // Method name
    val parameters: List<ComponentParameter>,
    val content: @Composable () -> Unit, // Default call
    private val invoker: @Composable (ComponentParameterBuilder) -> Unit
) {
    fun createBuilder(): ComponentParameterBuilder
    fun parameterByName(name: String): ComponentParameter?
    fun requiredParameters(): List<ComponentParameter>
    fun optionalParameters(): List<ComponentParameter>
    
    @Composable
    fun invoke(builder: ComponentParameterBuilder)
}
```

### ComponentParameter
```kotlin
data class ComponentParameter(
    val name: String,
    val type: String,              // Full type name
    val isComposable: Boolean = false,
    val hasDefault: Boolean = false
) {
    fun isBoolean(): Boolean
    fun isInt(): Boolean
    fun isString(): Boolean
    fun isFloat(): Boolean
    fun isDouble(): Boolean
    fun isList(): Boolean
    fun isLambda(): Boolean
}
```

### ComponentParameterBuilder
```kotlin
class ComponentParameterBuilder(private val method: DesignComponentMethod) {
    fun set(name: String, value: Any?): ComponentParameterBuilder
    fun setIfPresent(name: String, value: Any?): ComponentParameterBuilder
    fun <T : Any> setOfType(name: String, value: T): ComponentParameterBuilder
    
    fun getOrNull(name: String): Any?
    fun get(name: String): Any?
    
    fun getMissingRequired(): List<String>
    fun isComplete(): Boolean
    fun build(): Map<String, Any?>
    fun buildOrThrow(): Map<String, Any?>
}
```

## Примеры

### Условный рендер на основе типов параметров

```kotlin
@Composable
fun RenderComponent(methodName: String) {
    val component = DesignComponentsRegistry.findComponent(methodName) ?: return
    
    val builder = component.createBuilder()
    
    component.parameters.forEach { param ->
        when {
            param.isString() -> builder.set(param.name, "default value")
            param.isBoolean() -> builder.set(param.name, false)
            param.isInt() -> builder.set(param.name, 0)
            param.isFloat() -> builder.set(param.name, 0f)
            param.isLambda() -> builder.set(param.name, {})
            else -> if (param.hasDefault) builder.setIfPresent(param.name, null)
        }
    }
    
    component.invoke(builder)
}
```

### Динамическое создание предпросмотров

```kotlin
@Composable
fun ComponentPreviews() {
    val componentsToDemonstrate = DesignComponentsRegistry.findComponents { method ->
        method.methodName.startsWith("Styled")
    }
    
    componentsToDemonstrate.forEach { component ->
        val builder = component.createBuilder()
        
        // Fill with defaults
        component.requiredParameters().forEach { param ->
            when {
                param.isString() -> builder.set(param.name, "Sample")
                param.isBoolean() -> builder.set(param.name, true)
                else -> builder.set(param.name, null)
            }
        }
        
        component.invoke(builder)
    }
}
```

### Каллбэк для всех компонентов

```kotlin
@Composable
fun AllComponentsShowcase() {
    Column {
        DesignComponentsRegistry.components.forEach { component ->
            Text(text = "Component: ${component.methodName}")
            
            val builder = component.createBuilder()
            
            // Set any required parameters
            component.requiredParameters().forEach { param ->
                builder.set(param.name, when {
                    param.isString() -> "Demo"
                    param.isBoolean() -> true
                    param.isInt() -> 1
                    else -> null
                })
            }
            
            component.invoke(builder)
            
            Divider()
        }
    }
}
```

## Аннотация @DesignComponent

```kotlin
@Retention(AnnotationRetention.BINARY)
annotation class DesignComponent()
```

Используется для маркировки composable методов, которые должны быть включены в реестр.
Работает с методами, имеющими параметры с дефолтными значениями и без них.

## Генерируемый код

Процессор `DesignComponentsRegistryProcessor` генерирует файл `DesignComponentsRegistry.kt` в пакете, содержащем общий родитель всех аннотированных методов.

Сгенерированный код содержит:
- `DesignComponentsRegistry.components` - список всех найденных компонентов
- `DesignComponentsRegistry.findComponent(methodName)` - поиск по имени
- `DesignComponentsRegistry.findComponents(filter)` - фильтрованный поиск
- `DesignComponentsRegistry.callComponent(methodName, builderBlock)` - вызов с лямбдой

Каждый метод компонента получает свою invoker лямбду, которая правильно вызывает его с типизированными параметрами из builder'а.
