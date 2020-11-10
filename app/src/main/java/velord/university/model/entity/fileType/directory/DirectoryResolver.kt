package velord.university.model.entity.fileType.directory

import android.content.Context
import android.os.Environment
import android.os.storage.StorageVolume
import androidx.core.content.ContextCompat.getSystemService
import java.io.File

class DirectoryResolver(
    private val root: File = getDefaultRoot()
) {

    private var current: File = root

    fun getDirectory(): File = current

    fun getPath(): String = current.path

    fun isRoot(): Boolean = (getPath() == root.path)

    fun setDirectory(file: File) {
        current = file
    }

    fun setParent(): File {
        setDirectory(current.parentFile!!)
        return current
    }

    fun getFilesInDirectory(): Array<File> {
        val path = getPath()
        val file = File(path)
        return file.listFiles() ?: arrayOf()
    }

    private fun changeRootToSdCard(context: Context) =
        setDirectory(getExternalCardDirectory(context))

    private fun changeRootToDefault() =
        setDirectory(getDefaultRoot())

    fun changeRoot(context: Context) {
        if (isRoot()) changeRootToSdCard(context)
        else changeRootToDefault()
    }

    companion object {

        fun getDefaultRoot(): File =
            Environment.getExternalStorageDirectory()

        //https://gist.github.com/PauloLuan/4bcecc086095bce28e22
        fun getExternalCardDirectory(
            context: Context
        ): File {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE)
            try {
                val storageVolumeClazz = Class
                    .forName("android.os.storage.StorageVolume")
                val getVolumeList = storageManager
                    .javaClass.getMethod("getVolumeList")
                val getPath = storageVolumeClazz
                    .getMethod("getPath")
                val isRemovable = storageVolumeClazz
                    .getMethod("isRemovable")
                val result = getVolumeList
                    .invoke(storageManager) as Array<StorageVolume>
                result.forEach {
                    if (isRemovable.invoke(it) as Boolean)
                        return File(getPath.invoke(it) as String)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            //empty if not one found
            return File("")
        }
    }
}