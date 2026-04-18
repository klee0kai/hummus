package com.github.klee0kai.design.ksp

import com.github.klee0kai.crossbox.processor.TargetKSPProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return TargetKSPProcessor(
            targetProcessors = arrayOf(
                PreviewsRegistryTargetProcessor(),
            ),
            options = environment.options,
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
        )
    }

}