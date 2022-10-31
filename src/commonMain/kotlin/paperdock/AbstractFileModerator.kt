package paperdock

import okio.Path

interface AbstractFileModerator {

    suspend fun write(path: Path, content: String)
    suspend fun writeFiles(vararg pairs: Pair<Path, String>)

    suspend fun read(path: Path): String
    suspend fun readResource(path: Path): String
}

expect fun fileModerator(): AbstractFileModerator