package tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_list")
data class NoteList(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    @ColumnInfo(name="title")
    val title:String,
    @ColumnInfo(name="description")
    val description:String,
    @ColumnInfo(name="time")
    val time:String,
    @ColumnInfo(name="image",ColumnInfo.BLOB)
    var image:ByteArray?
):Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteList

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (time != other.time) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}


