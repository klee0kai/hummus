package com.github.klee0kai.hummus.storybook

import com.github.klee0kai.hummus.storybook.desktop.DesktopCmd
import com.github.klee0kai.hummus.storybook.server.ServerCmd
import picocli.CommandLine
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "design-storybook",
    description = ["Design Components Viewer - Interactive component browser and testing tool"],
    mixinStandardHelpOptions = true,
    version = ["1.0.0"],
    subcommands = [
        DesktopCmd::class,
        ServerCmd::class,
    ]
)
class DesignStorybookApp : Callable<Int> {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val exitCode = CommandLine(DesignStorybookApp()).execute(*args)
            exitProcess(exitCode)
        }

    }

    /**
     * No arg run - show help
     */
    override fun call(): Int {
        CommandLine(this).usage(System.out)
        return 0
    }

}
