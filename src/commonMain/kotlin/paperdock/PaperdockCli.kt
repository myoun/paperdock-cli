package paperdock

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.cli.ExperimentalCli

internal val httpClient = HttpClient(CIO) { }

object PaperdockCli {

    @OptIn(ExperimentalCli::class)
    fun run(args: Array<String>) {
        val init = InitCommand()
        PaperdockParser.subcommands(init)
        PaperdockParser.parse(args)
    }
}