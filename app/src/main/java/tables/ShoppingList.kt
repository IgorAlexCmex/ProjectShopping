package tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="shopping_list")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    @ColumnInfo(name="nameItem")
    val nameItem:String,
    @ColumnInfo(name="infoItem")
    val infoItem:String="",
    @ColumnInfo(name="idNameList")
    val idsNameList:Int,
    @ColumnInfo(name="checkedItem")
    val checkedItem:Boolean=false,
    @ColumnInfo(name="itemType")
     val itemType:Int=0,
    @ColumnInfo(name="image",ColumnInfo.BLOB)
    val image:ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingList

        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        return image?.contentHashCode() ?: 0
    }
}


