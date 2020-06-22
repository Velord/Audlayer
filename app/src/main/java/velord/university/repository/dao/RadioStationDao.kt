package velord.university.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import velord.university.model.entity.RadioStation

@Dao
interface RadioStationDao {

    @Query("Select * From RadioStation")
    fun getAll(): List<RadioStation>

    @Query("Select * From RadioStation Where name = :name")
    fun getByName(name: String): RadioStation

    @Query("Select * From RadioStation Where url =:url")
    fun getByUrl(url: String): RadioStation

    @Query("Select * From RadioStation Where id =:id")
    fun getById(id: Int): RadioStation

    @Query("UPDATE RadioStation SET liked =:liked WHERE url =:url")
    fun updateLikeByUrl(url: String, liked: Boolean)

    @Update
    fun update(vararg radio: RadioStation)

    @Insert
    fun insertAll(vararg radio: RadioStation)

    @Query("Delete From RadioStation Where name = :name")
    fun deleteByName(name: String)

    @Query("Delete From RadioStation Where id = :id")
    fun deleteById(id: String)

    @Query("Delete From RadioStation")
    fun nudeTable()
}