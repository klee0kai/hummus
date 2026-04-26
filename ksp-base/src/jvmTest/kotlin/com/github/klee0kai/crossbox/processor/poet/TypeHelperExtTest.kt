package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import org.junit.Test
import kotlin.test.assertEquals

class TypeHelperExtTest {

    @Test
    fun `rawType returns first type argument for parameterized type`() {
        val listStringType = List::class.asClassName().parameterizedBy(String::class.asClassName())
        val result = listStringType.rawType()

        assertEquals(List::class.asClassName(), result)
    }

    @Test
    fun `rawType returns first type argument for map with multiple type arguments`() {
        val mapType = Map::class.asClassName()
            .parameterizedBy(String::class.asClassName(), Int::class.asClassName())
        val result = mapType.rawType()

        assertEquals(Map::class.asClassName(), result)
    }

    @Test
    fun `rawType returns out type for wildcard type`() {
        val wildcardType = WildcardTypeName.producerOf(Number::class.asClassName())
        val result = wildcardType.rawType()

        assertEquals(Number::class.asClassName(), result)
    }

    @Test
    fun `rawType removes nullability from simple type`() {
        val simpleType = String::class.asClassName().copy(nullable = true)
        val result = simpleType.rawType()

        assertEquals(String::class.asClassName().copy(nullable = false), result)
    }

    @Test
    fun `rawType returns non-nullable version of simple type`() {
        val simpleType = String::class.asClassName()
        val result = simpleType.rawType()

        assertEquals(simpleType.copy(nullable = false), result)
    }

    @Test
    fun `rawType recursively extracts from nested parameterized types`() {
        val listType = List::class.asClassName()
        val listOfList = listType.parameterizedBy(listType.parameterizedBy(String::class.asClassName()))
        val result = listOfList.rawType()

        assertEquals(List::class.asClassName(), result)
    }

    @Test
    fun `rawType handles nullable parameterized type`() {
        val listStringType = List::class.asClassName()
            .parameterizedBy(String::class.asClassName())
            .copy(nullable = true)
        val result = listStringType.rawType()

        assertEquals(List::class.asClassName(), result)
    }
}
