package tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_elements")
data class LibraryElements(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    @ColumnInfo(name="nameElement")
    val nameElement:String?,
    @ColumnInfo(name="image",ColumnInfo.BLOB)
    var image:ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibraryElements

        if (id != other.id) return false
        if (nameElement != other.nameElement) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + nameElement.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}

