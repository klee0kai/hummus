package com.github.klee0kai.storybook.examples

import com.github.klee0kai.hummus.design.core.*

/**
 * Инициализирует предподготовленные наборы параметров для компонентов
 */
fun initializeComponentPresets() {
    // SimpleButton presets
    ComponentPresetsRegistry.register(
        componentPresets("SimpleButton") {
            preset("Default") {
                param("text", "Click me")
                param("enabled", true)
                param("onClick", {})
                description = "Default button state"
            }

            preset("Disabled") {
                param("text", "Disabled Button")
                param("enabled", false)
                param("onClick", {})
                description = "Disabled button state"
            }

            preset("Long Text") {
                param("text", "This is a very long button text that might wrap")
                param("enabled", true)
                param("onClick", {})
                description = "Button with long text"
            }

            preset("Empty") {
                param("text", "")
                param("enabled", true)
                param("onClick", {})
                description = "Button with empty text"
            }
        }
    )

    // StyledCard presets
    ComponentPresetsRegistry.register(
        componentPresets("StyledCard") {
            preset("Basic Card") {
                param("title", "Card Title")
                param("subtitle", "Card Subtitle")
                param("isExpanded", false)
                description = "Basic collapsed card"
            }

            preset("Expanded") {
                param("title", "Expanded Card")
                param("subtitle", "This card is expanded to show more details")
                param("isExpanded", true)
                description = "Expanded card with details"
            }

            preset("No Subtitle") {
                param("title", "Title Only")
                param("subtitle", "")
                param("isExpanded", false)
                description = "Card without subtitle"
            }

            preset("Long Content") {
                param("title", "Long Title Card")
                param("subtitle", "This is a very long subtitle that contains a lot of text and might wrap to multiple lines")
                param("isExpanded", true)
                description = "Card with long content"
            }
        }
    )

    // DropDownField presets
    ComponentPresetsRegistry.register(
        componentPresets("DropDownField") {
            preset("Basic") {
                param("selectedIndex", 0)
                param("variants", listOf("Option 1", "Option 2", "Option 3"))
                param("expanded", false)
                param("isSkeleton", false)
                description = "Basic dropdown field"
            }

            preset("Selected Item") {
                param("selectedIndex", 1)
                param("variants", listOf("First", "Second", "Third", "Fourth"))
                param("expanded", false)
                param("isSkeleton", false)
                description = "Dropdown with second item selected"
            }

            preset("Empty") {
                param("selectedIndex", 0)
                param("variants", emptyList<String>())
                param("expanded", false)
                param("isSkeleton", false)
                description = "Dropdown with no options"
            }

            preset("Loading") {
                param("selectedIndex", 0)
                param("variants", listOf("Loading..."))
                param("expanded", false)
                param("isSkeleton", true)
                description = "Dropdown in loading state"
            }

            preset("Many Options") {
                param("selectedIndex", 0)
                param("variants", (1..20).map { "Option $it" })
                param("expanded", false)
                param("isSkeleton", false)
                description = "Dropdown with many options"
            }
        }
    )

    // BackMenuIcon presets
    ComponentPresetsRegistry.register(
        componentPresets("BackMenuIcon") {
            preset("Back") {
                param("isMenu", false)
                description = "Back arrow icon"
            }

            preset("Menu") {
                param("isMenu", true)
                description = "Menu hamburger icon"
            }
        }
    )

    // AddCheckedIcon presets
    ComponentPresetsRegistry.register(
        componentPresets("AddCheckedIcon") {
            preset("Unchecked") {
                param("isAdded", false)
                description = "Not added state"
            }

            preset("Added") {
                param("isAdded", true)
                description = "Added/checked state"
            }
        }
    )

    // DotsFlashing presets
    ComponentPresetsRegistry.register(
        componentPresets("DotsFlashing") {
            preset("Default") {
                param("dotsCount", 3)
                description = "Default animated dots"
            }

            preset("Two Dots") {
                param("dotsCount", 2)
                description = "Two animated dots"
            }

            preset("Many Dots") {
                param("dotsCount", 5)
                description = "Five animated dots"
            }
        }
    )
}

/**
 * Вспомогательная функция для получения примера значения параметра
 */
fun getExampleValue(parameter: ComponentParameter): Any? {
    return when {
        parameter.isBoolean() -> true
        parameter.isString() -> "Example text"
        parameter.isInt() -> 42
        parameter.isFloat() -> 3.14f
        parameter.isDouble() -> 2.718
        parameter.isList() -> listOf("item1", "item2", "item3")
        parameter.type is ParameterType.Generic -> {
            val generic = parameter.type as ParameterType.Generic
            when {
                generic.isFunction() -> {}
                else -> null
            }
        }
        else -> null
    }
}

/**
 * Вспомогательная функция для получения примера preset для компонента
 */
fun getDefaultPreset(component: DesignComponentMethod): ParameterPreset? {
    return ComponentPresetsRegistry.getPresets(component.methodName)?.presets?.firstOrNull()
}
