package data.persistance.login

import android.arch.persistence.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(userEntity: UserEntity): Long

    @Update
    fun update(userEntity: UserEntity): Int

    @Delete
    fun delete(userEntity: UserEntity): Int

    @Query("SELECT * FROM User WHERE email=:email AND password=:password")
    fun findUserByEmailAndPassword(email: String, password: String): UserEntity?

    @Query("SELECT * FROM User WHERE id=:id")
    fun findUserById(id: Long): UserEntity?
}