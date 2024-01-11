package adapters

import androidx.recyclerview.widget.DiffUtil
import tables.ShoppingListName

 class DiffUtilNameList(
     private val oldListName:ArrayList<ShoppingListName>,
     private val newListName: List<ShoppingListName>
 ):DiffUtil.Callback(){

   override fun getOldListSize(): Int {
     return oldListName.size
   }

   override fun getNewListSize(): Int {
     return newListName.size
   }

   override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
     return  oldListName[oldItemPosition].id==newListName[newItemPosition].id
   }

   override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
     return oldListName[oldItemPosition]==newListName[newItemPosition]
   }

 }
