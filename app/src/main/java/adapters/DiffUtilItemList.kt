package adapters

import androidx.recyclerview.widget.DiffUtil
import tables.ShoppingList

class DiffUtilItemList(private val oldListItem:ArrayList<ShoppingList>,
                       private val newListItem: List<ShoppingList>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int {
       return oldListItem.size
    }

    override fun getNewListSize(): Int {
       return newListItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldListItem[oldItemPosition].id==newListItem[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldListItem[oldItemPosition]==newListItem[newItemPosition]
    }

}