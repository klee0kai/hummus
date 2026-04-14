package com.github.klee0kai.ksp.base.sample.processor

import com.github.klee0kai.crossbox.processor.TargetKSPProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return TargetKSPProcessor(
            targetProcessors = arrayOf(
                ClassInfoTargetProcessor(),
            ),
            options = environment.options,
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
        )
    }

}