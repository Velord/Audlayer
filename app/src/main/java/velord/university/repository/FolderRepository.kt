package velord.university.repository

import android.os.Environment
import java.io.File

object FolderRepository {

    fun getApplicationDir() =
        File(Environment.getExternalStorageDirectory(), "Audlayer")

    fun getApplicationVkDir() =
        File(getApplicationDir(), "Vk")

    fun getApplicationRadioDir() =
        File(getApplicationDir(), "Radio")

    fun createFolder() {
        val mainExist = getApplicationDir()
        if (mainExist.exists().not()) {
            mainExist.mkdirs()

            val vkExist = getApplicationVkDir()
            if (vkExist.exists().not())
                vkExist.mkdirs()

            val radioExist = getApplicationRadioDir()
            if (radioExist.exists().not())
                radioExist.mkdirs()
        }
    }
}