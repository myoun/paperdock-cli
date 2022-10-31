package paperdock

import kotlinx.coroutines.*
import okio.ByteString
import okio.FileSystem
import okio.Path
import kotlin.coroutines.suspendCoroutine

class JvmFileModerator: AbstractFileModerator {


    override suspend fun write(path: Path, content: String) {
        withContext(Dispatchers.IO) {
            FileSystem.SYSTEM.write(path, true) {
                write(ByteString.of(*content.encodeToByteArray()))
            }
        }
    }


    override suspend fun writeFiles(vararg pairs: Pair<Path, String>) {
        withContext(Dispatchers.IO) {
            pairs.map {
                async(Dispatchers.IO) {
                    FileSystem.SYSTEM.write(it.first) {
                        write(ByteString.of(*it.second.encodeToByteArray()))
                    }
                }
            }.forEach { it.start() }
        }
    }


    override suspend fun read(path: Path): String {
        return coroutineScope {
            val deferred = async {
                withContext(Dispatchers.IO) {
                    FileSystem.SYSTEM.read(path) {
                        read(path)
                    }
                }
            }
            deferred.await()
        }
    }

    override suspend fun readResource(path: Path): String {
        return withContext(Dispatchers.IO) {
            FileSystem.RESOURCES.read(path) {
                this.readString(Charsets.UTF_8)
            }
        }
    }
}

actual fun fileModerator(): AbstractFileModerator = JvmFileModerator()

//fun Path.toPathString()