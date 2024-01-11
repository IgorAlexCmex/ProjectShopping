package adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmex.myproject.R
import com.cmex.myproject.databinding.ItemLibraryBinding
import com.cmex.myproject.databinding.ItemShoppingItemBinding
import constants.Constants
import tables.ShoppingList
import utilities.MyDialog
import utilities.myLog

class ItemAdapter(private val listenerItem: ListenerItem): RecyclerView.Adapter< ItemAdapter.ItemHolder>(){
      private var listItemShop=ArrayList<ShoppingList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if(viewType==0) ItemHolder.createItem(parent)
        else ItemHolder.createLibrary(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return listItemShop[position].itemType
    }
    override fun getItemCount(): Int {
       return listItemShop.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
       return if(listItemShop[position].itemType==0) holder.setDataItem(listItemShop[position],listenerItem)
              else holder.setDataLibrary(listItemShop[position],listenerItem)
    }


    class ItemHolder(val view: View) :RecyclerView.ViewHolder(view){

        fun setDataItem(item:ShoppingList,listenerItem: ListenerItem){

          val binding=ItemShoppingItemBinding.bind(view)
            val pref=PreferenceManager.getDefaultSharedPreferences(binding.root.context)
            binding.tvItemTitle.text=item.nameItem
           binding.tvInfoItem.text=item.infoItem
            if(pref.getString(Constants.THEME,"1")=="1") {
                binding.clItem.setBackgroundResource(R.drawable.fon_whatsapp)
            } else{
                binding.clItem.setBackgroundResource(R.drawable.fon_blue_white)
            }
          binding.tvInfoItem.visibility= if(item.infoItem=="")View.GONE
             else View.VISIBLE
             binding.checkBoxItem.isChecked=item.checkedItem
             onSetColorAndPainFlag(binding)
            binding.checkBoxItem.setOnClickListener {
                onSetColorAndPainFlag(binding)
                listenerItem.onClick(item.copy(checkedItem = binding.checkBoxItem.isChecked),
                    EDIT_ITEM)
            }
             itemView.setOnClickListener {
                 val context=binding.root.context
              MyDialog.editItemShoppingList(context,item,listenerItem)
                listenerItem.onClick(item, EDIT_ITEM)
             }

         }
         fun setFon(){

        }
        fun setDataLibrary(item:ShoppingList,listenerItem: ListenerItem){
            val binding=ItemLibraryBinding.bind(view)
            binding.tvTitleLib.text = item.nameItem
            binding.ibtnEditItemLibrary.setOnClickListener {
                val context=binding.root.context
                MyDialog.editItemShoppingList(context,item,listenerItem)
                listenerItem.onClick(item, EDIT_LIBRARY)
            }
            itemView.setOnClickListener {
                listenerItem.onClick(item, PRESS_ITEM)
            }

        }

        private fun onSetColorAndPainFlag(binding: ItemShoppingItemBinding){
            if(binding.checkBoxItem.isChecked){
                binding.tvItemTitle.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvItemTitle.setTextColor(binding.root.context.getColor(R.color.gray0))
            } else {
                binding.tvItemTitle.paintFlags=Paint.ANTI_ALIAS_FLAG
                binding.tvItemTitle.setTextColor(binding.root.context.getColor(R.color.gold))
            }
        }
        companion object{
            fun createItem(parent: ViewGroup) :ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context,)
                    .inflate(R.layout.item_shopping_item,parent,false))
            }
            fun createLibrary(parent: ViewGroup) :ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_library,parent,false))
            }
        }
    }
    fun updateAdapterItem(newItemListShop: List<ShoppingList>){
        val diffUtil= DiffUtil.calculateDiff(DiffUtilItemList(listItemShop,newItemListShop))
        diffUtil.dispatchUpdatesTo(this)
        listItemShop.clear()
        listItemShop.addAll(newItemListShop)
        notifyDataSetChanged()
    }

    class ItemDiff:DiffUtil.ItemCallback<ShoppingList>(){
        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
        return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
           return oldItem==newItem
        }

    }
interface ListenerItem{
    fun onClick(item:ShoppingList,state:Int)
}
companion object{
    const val EDIT_ITEM=0
    const val PRESS_ITEM=1
    const val EDIT_LIBRARY=2
}
}


