package paperdock

import kotlinx.coroutines.*
import okio.ByteString
import okio.FileSystem
import okio.Path

class JvmFileModerator: AbstractFileModerator {


    override fun write(path: Path, content: String) {
        runBlocking(Dispatchers.IO) {
            FileSystem.SYSTEM.write(path, true) {
                write(ByteString.of(*content.encodeToByteArray()))
            }
        }
    }


    override fun writeFiles(vararg pairs: Pair<Path, String>) {
        runBlocking(Dispatchers.IO) {
            pairs.map {
                async {
                    FileSystem.SYSTEM.write(it.first) {
                        write(ByteString.of(*it.second.encodeToByteArray()))
                    }
                }
            }.forEach { it.await() }
        }
    }


    override fun read(path: Path): String =
        runBlocking(Dispatchers.IO) {
            FileSystem.SYSTEM.read(path) {
                read(path)
            }
        }

    override fun readResource(path: Path): String {
        return runBlocking(Dispatchers.IO) {
            FileSystem.RESOURCES.read(path) {
                this.readString(Charsets.UTF_8)
            }
        }
    }
}

actual fun fileModerator(): AbstractFileModerator = JvmFileModerator()

//fun Path.toPathString()