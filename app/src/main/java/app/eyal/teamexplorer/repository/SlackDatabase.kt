package app.eyal.teamexplorer.repository

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow

/**
 * Keeps the association between a Post and a feed
 */
@Entity(
    primaryKeys = ["userId"],
    indices = [Index("userId", unique = true), Index("feedOrder", unique = false)],
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE)]
)
data class FeedEntity(
    val userId: String,
    val displayName: String,
    val imageUrl: String,
    val feedOrder: Int
)

@Entity(primaryKeys = ["id"])
data class UserEntity(
    val id: String,
    val image_24: String,
    val image_32: String,
    val image_48: String,
    val image_72: String,
    val image_192: String,
    val image_512: String,
    val image_original: String?,
    val title: String,
    val team: String,
    val status_text: String,
    val real_name: String,
    val display_name: String,
    val real_name_normalized: String,
    val display_name_normalized: String,
    val deleted: Boolean
)

@Dao
abstract class SlackDao {

    @Transaction
    open suspend fun insertUserList(userEntities: List<UserEntity>) {
        // first clear the feed
        clearUserList()
        // convert them into database models
        val feedEntities = userEntities.toFeed()
        // save them into the database
        insertUserList(userEntities, feedEntities)
        // delete posts that are not part of any feed
        clearObseleteUsers()
    }

    @Query("DELETE FROM FeedEntity")
    abstract suspend fun clearUserList()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertUserList(
        userEntities: List<UserEntity>,
        feedEntries: List<FeedEntity>
    )

    @Query("DELETE FROM UserEntity WHERE id NOT IN (SELECT DISTINCT(userId) FROM FeedEntity)")
    protected abstract suspend fun clearObseleteUsers()

    @Query("SELECT * FROM FeedEntity ORDER BY FeedEntity.feedOrder ASC LIMIT :limit")
    abstract fun loadUserList(limit: Int): Flow<List<FeedEntity>>

    @Query("SELECT * FROM UserEntity WHERE id=:userId LIMIT 1")
    abstract fun loadUser(userId: String): Flow<UserEntity>

}

fun User.toEntity() = UserEntity(
        id = id,
        image_24 = profile.image_24,
        image_32 = profile.image_32,
        image_48 = profile.image_48,
        image_72 = profile.image_72,
        image_192 = profile.image_192,
        image_512 = profile.image_512,
        image_original = profile.image_original,
        title = profile.title,
        team = profile.team,
        status_text = profile.status_text,
        real_name = profile.real_name,
        display_name = profile.display_name,
        real_name_normalized = profile.real_name_normalized,
        display_name_normalized = profile.display_name_normalized,
        deleted = deleted
    )

fun List<UserEntity>.toFeed() = mapIndexed { index, user ->
    FeedEntity(
        userId = user.id,
        displayName = user.display_name,
        imageUrl = user.image_192 ,
        feedOrder = index
    )
}

@Database(
    version = 1,
    exportSchema = false,
    entities = [UserEntity::class, FeedEntity::class]
)
// @TypeConverters(SlackTypeConverters::class)
abstract class SlackDatabase : RoomDatabase() {
    abstract fun slackDao(): SlackDao
}
