package paperdock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaperdockCompose(val services: Map<String, PaperdockServiceCompose>)
@Serializable
data class PaperdockServiceCompose(@SerialName("container_name") val containerName: String, val environment: PaperdockEnvironmentCompose, @SerialName("stdin_open") val stdinOpen: Boolean, val tty: Boolean, val image: String, val ports: Array<String>, val volumes: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PaperdockServiceCompose

        if (containerName != other.containerName) return false
        if (environment != other.environment) return false
        if (stdinOpen != other.stdinOpen) return false
        if (tty != other.tty) return false
        if (image != other.image) return false
        if (!ports.contentEquals(other.ports)) return false
        if (!volumes.contentEquals(other.volumes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerName.hashCode()
        result = 31 * result + environment.hashCode()
        result = 31 * result + stdinOpen.hashCode()
        result = 31 * result + tty.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + ports.contentHashCode()
        result = 31 * result + volumes.contentHashCode()
        return result
    }
}
@Serializable
data class PaperdockEnvironmentCompose(@SerialName("MC_VERSION") val minecraftVersion: String)