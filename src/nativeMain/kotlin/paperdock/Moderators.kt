package paperdock

import kotlinx.coroutines.*
import okio.ByteString
import okio.FileSystem
import okio.Path

class NativeFileModerator: AbstractFileModerator {

    val ioDispatcher = newFixedThreadPoolContext(75,"paperdock-native-io")

    override fun write(path: Path, content: String) {
        runBlocking(ioDispatcher) {
            FileSystem.SYSTEM.write(path, true) {
                write(ByteString.of(*content.encodeToByteArray()))
            }
        }
    }


    override fun writeFiles(vararg pairs: Pair<Path, String>) {
        runBlocking(ioDispatcher) {
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
        runBlocking(ioDispatcher) {
            FileSystem.SYSTEM.read(path) {
                read(path)
            }
        }

    override fun readResource(path: Path): String {
        if (path.toString() == "run.sh") {
            return """
                ./gradlew build
                docker-compose build
                docker-compose up -d
                docker attach paperdock
            """.trimIndent()
        }
        TODO("Not yet implemented")
    }
}

actual fun fileModerator(): AbstractFileModerator = NativeFileModerator()