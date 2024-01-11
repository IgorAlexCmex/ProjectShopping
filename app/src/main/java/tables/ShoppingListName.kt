package tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping_list_names")
data class ShoppingListName(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    @ColumnInfo(name = "name_list_shopping")
    var nameList:String,
    @ColumnInfo(name="time")
    val time:String,
    @ColumnInfo(name="totalNumberShopping") // общее количество покупок
    val totalNumberShopping:Int,
    @ColumnInfo(name="counterShopping") // счетчик покупок
    val counterShopping:Int,
    @ColumnInfo(name="idsItemShopping")
    val idsItemShopping:String,
    @ColumnInfo(name="image",ColumnInfo.BLOB)
    var image:ByteArray?
):Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingListName

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
