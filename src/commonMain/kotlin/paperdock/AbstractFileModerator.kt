package paperdock

import okio.Path

interface AbstractFileModerator {

    fun write(path: Path, content: String)
    fun writeFiles(vararg pairs: Pair<Path, String>)

    fun read(path: Path): String
    fun readResource(path: Path): String
}

expect fun fileModerator(): AbstractFileModerator