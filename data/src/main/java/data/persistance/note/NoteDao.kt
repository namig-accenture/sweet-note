package data.persistance.note

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteEntity: NoteEntity): Long

    @Update
    fun update(noteEntity: NoteEntity): Int

    @Delete
    fun delete(noteEntity: NoteEntity): Int

    @Query("SELECT * FROM Notes WHERE id=:noteId")
    fun findNoteById(noteId: Long): Maybe<NoteEntity>

    @Query("SELECT * FROM Notes WHERE userId=:userId ORDER BY id DESC LIMIT :pageSize OFFSET :position")
    fun findNotesByUserId(userId: Long, pageSize: Int, position: Int): Flowable<List<NoteEntity>>

    @Query("SELECT COUNT(*) FROM Notes WHERE userId=:userId")
    fun findNotesCountByUserId(userId: Long): Single<Int>
}