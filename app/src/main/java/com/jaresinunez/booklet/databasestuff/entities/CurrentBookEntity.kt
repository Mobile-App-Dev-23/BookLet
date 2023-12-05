package com.jaresinunez.booklet.databasestuff.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "current_book_table", foreignKeys = [ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["id"])])
data class CurrentBookEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Long,
    @Embedded(prefix = "currentBooks_") val currentBooks: BookEntity?
)
