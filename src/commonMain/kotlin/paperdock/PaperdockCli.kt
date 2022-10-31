package paperdock

import kotlinx.cli.ExperimentalCli

object PaperdockCli {

    @OptIn(ExperimentalCli::class)
    fun run(args: Array<String>) {
        val init = InitCommand()
        PaperdockParser.subcommands(init)
        PaperdockParser.parse(args)
    }
}