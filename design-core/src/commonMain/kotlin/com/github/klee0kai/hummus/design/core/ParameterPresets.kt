package com.github.klee0kai.hummus.design.core

/**
 * Предподготовленный набор значений для параметра
 */
data class ParameterPreset(
    val name: String,
    val description: String = "",
    val values: Map<String, Any?>,
)

/**
 * Коллекция предподготовленных наборов для компонента
 */
data class ComponentPresets(
    val methodName: String,
    val presets: List<ParameterPreset>,
) {
    fun getPreset(name: String): ParameterPreset? = presets.firstOrNull { it.name == name }
}

/**
 * Реестр предподготовленных параметров для компонентов
 */
object ComponentPresetsRegistry {
    private val presetsByComponent = mutableMapOf<String, ComponentPresets>()

    fun register(presets: ComponentPresets) {
        presetsByComponent[presets.methodName] = presets
    }

    fun getPresets(methodName: String): ComponentPresets? = presetsByComponent[methodName]

    fun getAllPresets(): List<ComponentPresets> = presetsByComponent.values.toList()

    fun getPreset(methodName: String, presetName: String): ParameterPreset? =
        presetsByComponent[methodName]?.getPreset(presetName)
}

/**
 * Builder для создания preset'ов
 */
class PresetBuilder(val name: String) {
    private val values = mutableMapOf<String, Any?>()
    var description: String = ""

    fun param(name: String, value: Any?) {
        values[name] = value
    }

    fun build(): ParameterPreset = ParameterPreset(name, description, values.toMap())
}

/**
 * Builder для ComponentPresets
 */
class ComponentPresetsBuilder(val methodName: String) {
    private val presets = mutableListOf<ParameterPreset>()

    fun preset(name: String, builder: PresetBuilder.() -> Unit) {
        val preset = PresetBuilder(name).apply(builder).build()
        presets.add(preset)
    }

    fun build(): ComponentPresets = ComponentPresets(methodName, presets.toList())
}

fun componentPresets(methodName: String, builder: ComponentPresetsBuilder.() -> Unit): ComponentPresets {
    return ComponentPresetsBuilder(methodName).apply(builder).build()
}
