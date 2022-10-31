package paperdock

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import net.mamoe.yamlkt.Yaml
import okio.Path.Companion.toPath

@OptIn(ExperimentalCli::class)
class InitCommand: Subcommand("init", "initialize the project") {

    private val fileModerator = fileModerator()

    val projectName by PaperdockParser.argument(ArgType.String, "project_name", "Project Name")
    val mcVersion by PaperdockParser.argument(ArgType.String,"mc_version", "Minecraft Version")


    override fun execute() {
        val compose = PaperdockCompose(
            mapOf(
                projectName to PaperdockServiceCompose(
                    containerName = projectName,
                    environment = PaperdockEnvironmentCompose(
                        minecraftVersion = mcVersion
                    ),
                    stdinOpen = true,
                    tty = true,
                    image = "devmyoun/paperdock:latest",
                    ports = arrayOf("25565:25565"),
                    volumes = arrayOf("./build/libs/:/root/server/plugins")
                )
            )
        )
        val composeYaml = Yaml.encodeToString(compose)
        val composePath = "compose.yaml".toPath()

        val runScriptPath = "run.sh".toPath()

        val runScript = fileModerator.readResource(runScriptPath)

        fileModerator.writeFiles(composePath to composeYaml, runScriptPath to runScript)
    }
}