package adapters


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmex.myproject.R
import com.cmex.myproject.databinding.ItemNameBinding
import constants.Constants
import tables.ShoppingListName
import utilities.myToastShort
import utilities.utilByteArrayInBitmap
import utilities.utilSetFormatTime
import kotlin.random.Random
import kotlin.random.nextInt

class NameAdapter(val onClickShopName:ListenerEditShopName):RecyclerView.Adapter<NameAdapter.NameHolder>() {
    private lateinit var binding:ItemNameBinding
    private var listName=ArrayList<ShoppingListName>()

     private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameHolder {
       val inflanter=LayoutInflater.from(parent.context)
         context=parent.context
       /* val viewMy=inflanter.inflate(R.layout.item_name,parent,false)
        binding=ItemNameBinding.bind(viewMy)*/
        binding=ItemNameBinding.inflate(inflanter,parent,false)
        return NameHolder(binding,context,onClickShopName)
    }

    override fun getItemViewType(position: Int): Int {

        return super.getItemViewType(position)
    }
    override fun getItemCount(): Int {
       return listName.size
    }

    override fun onBindViewHolder(holder: NameHolder, position: Int) {

       holder.setData(listName[position])
    }
    class NameHolder(private val binding: ItemNameBinding, val context: Context,
                     private val onClickShopName: ListenerEditShopName):RecyclerView.ViewHolder(binding.root){
        private var bitmap:Bitmap?=null
        private val pref=PreferenceManager.getDefaultSharedPreferences(context)
      fun setData(nameList:ShoppingListName)= with(binding){
          tvTitleName.text=nameList.nameList
          tvTimeName.text= utilSetFormatTime(nameList.time,pref)
          tvCountName.text="${nameList.totalNumberShopping}/${nameList.counterShopping}"
          setFon()
          if (nameList.image!=null) {
              ivPhotoName.visibility=View.VISIBLE
              bitmap = nameList.image?.let { utilByteArrayInBitmap(it) }
              ivPhotoName.setImageBitmap(bitmap)
          } else  ivPhotoName.visibility=View.GONE
          ibEditName.setOnClickListener {
              onClickShopName.onClickEditName(nameList)
          }
          itemView.setOnClickListener {
             onClickShopName.onClickEditItem(nameList)
          }
          val colorState=ColorStateList.valueOf(setColorProgressBar(nameList,context))
          progressBarName.progressTintList=colorState
          progressBarName.max=nameList.totalNumberShopping
          progressBarName.progress=nameList.counterShopping

      }
        private fun setFon(){
            if(pref.getString(Constants.THEME,"1")=="1") {
                binding.clContainerName.setBackgroundResource(R.drawable.fon_whatsapp)
            } else{
                binding.clContainerName.setBackgroundResource(R.drawable.fon_blue_white)
            }
        }
       private fun setColorProgressBar(name: ShoppingListName,context: Context):Int{
            var color=0
            if(name.counterShopping==name.totalNumberShopping){
                color= ContextCompat.getColor(context,R.color.lime)
            } else  if(name.counterShopping<name.totalNumberShopping/2){
                color= ContextCompat.getColor(context,R.color.red1)
            } else   if(name.counterShopping>=name.totalNumberShopping/2){
                color= ContextCompat.getColor(context,R.color.gold)
            }
            return color
        }
    }

     fun updateAdapterName(newListName: List<ShoppingListName>){
        val diffUtil= DiffUtil.calculateDiff(DiffUtilNameList(listName,newListName))
        diffUtil.dispatchUpdatesTo(this)
        listName.clear()
        listName.addAll(newListName)
        notifyDataSetChanged()
    }
    interface ListenerEditShopName{
       fun onClickEditName(name:ShoppingListName)
       fun onClickEditItem(name:ShoppingListName)
    }

}