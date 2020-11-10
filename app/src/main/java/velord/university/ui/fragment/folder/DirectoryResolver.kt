package velord.university.ui.fragment.folder

import android.os.Environment
import java.io.File

class DirectoryResolver {

    private var current: File = Environment.getExternalStorageDirectory()

    fun getDirectory(): File = current

    fun getPath(): String = current.path

    fun isRoot(): Boolean =
        (getPath() == Environment.getExternalStorageDirectory().path)

    fun setParent(): File {
        current = current.parentFile!!
        return current
    }

    fun setDirectory(file: File) {
        current = file
    }
}