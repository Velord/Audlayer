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
    //don't work
    @Query("UPDATE RadioStation SET url =:url WHERE name =:nameValue")
    fun updateUrlByName(nameValue: String, url: String)

    @Update
    fun update(vararg radio: RadioStation)

    @Insert
    fun insertAll(vararg radio: RadioStation)

    @Query("Delete From RadioStation Where name = :name")
    fun deleteByName(name: String)

    @Query("Delete From RadioStation Where id = :id")
    fun deleteById(id: String)

    @Query("Delete From RadioStation")
    fun nukeTable()
}