package com.github.klee0kai.hummus.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Представляет текст в UI, который может быть обычной строкой (String),
 * строковым ресурсом (StringResource) или строковым ресурсом с параметрами форматирования.
 *
 * На уровне Compose преобразуется в актуальный текст через [asString].
 */
@Immutable
sealed interface UiText {

    /**
     * Преобразует [UiText] в строку [String] внутри Composable-контекста,
     * используя актуальное Compose-окружение (локаль, тему и т.д.).
     */
    @Composable
    fun asString(): String

    /**
     * Обычная строка (например, полученная из сети, ввода пользователя или константа).
     */
    data class DynamicString(
        val value: String,
    ) : UiText {
        @Composable
        override fun asString(): String = value
    }

    /**
     * Строковый ресурс Compose Multiplatform [StringResource]
     * с поддержкой опциональных аргументов форматирования.
     */
    data class Resource(
        val resource: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText {

        constructor(resource: StringResource, vararg args: Any) : this(resource, args.toList())

        @Composable
        override fun asString(): String {
            return if (args.isEmpty()) {
                stringResource(resource)
            } else {
                val resolvedArgs = args.map { arg ->
                    if (arg is UiText) arg.asString() else arg
                }.toTypedArray()
                stringResource(resource, *resolvedArgs)
            }
        }
    }

    companion object {
        val Empty: UiText = DynamicString("")

        fun of(value: String): UiText = DynamicString(value)

        fun of(resource: StringResource): UiText = Resource(resource)

        fun of(resource: StringResource, vararg args: Any): UiText =
            Resource(resource, args.toList())
    }
}

/**
 * Extension-функция для преобразования [String] в [UiText].
 */
fun String.asUiText(): UiText = UiText.DynamicString(this)

/**
 * Extension-функция для преобразования [StringResource] в [UiText] с опциональными аргументами.
 */
fun StringResource.asUiText(vararg args: Any): UiText =
    if (args.isEmpty()) UiText.Resource(this) else UiText.Resource(this, args.toList())
