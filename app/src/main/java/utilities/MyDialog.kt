package utilities


import activity.NewAddNoteActivity
import adapters.ItemAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.cmex.myproject.R
import com.cmex.myproject.databinding.DialogEditItemBinding
import com.cmex.myproject.databinding.DialogProgressBarBinding
import com.cmex.myproject.databinding.DialogSelectBinding
import com.cmex.myproject.databinding.DialogSelestStyleTextBinding
import com.cmex.myproject.databinding.MassageDialogBinding
import tables.ShoppingList

object MyDialog {
    lateinit var dialog: Dialog
    private var selectImage: ActivityResultLauncher<Intent>? = null
    fun massageDialog(context: Context, massage: String) {
        dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val binding = MassageDialogBinding.inflate(inflater)

        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.background_item
            )
        )

        dialog.setContentView(binding.root)
        val windowLP = dialog.window?.attributes;
        windowLP?.width = 1000
        windowLP?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = windowLP
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        binding.tvMessageDialog.text = massage
        binding.btnMessage.setOnClickListener {
            dialog.dismiss()
        }

    }
    fun dialogDel(title:String,  context: Context,  onClick: MyDialog.SelectYesNO) {
        dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val binding = DialogSelectBinding.inflate(inflater)

        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.background_item
            )
        )

        dialog.setContentView(binding.root)
        val windowLP = dialog.window?.attributes;
        windowLP?.width = 1000
        windowLP?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = windowLP
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        binding.tvTitleDialog.text=title
        binding.ibNoDialog.setOnClickListener {
            onClick.onClickType(ClickType.NO)
            dialog.dismiss()
        }
        binding.ibYesDialog.setOnClickListener {
            onClick.onClickType(ClickType.YES)
            dialog.dismiss()
        }

    }
    fun selectStyleText(context: Context,onClick: SelectStyle){
       dialog= Dialog(context)
        val inflater=LayoutInflater.from(context)
        val binding=DialogSelestStyleTextBinding.inflate(inflater)

        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.background_item
            )
        )
        dialog.setContentView(binding.root)
        val windowLP = dialog.window?.attributes;
        windowLP?.width = 1000
        dialog.show()
        var text=""
        binding.tvTitleStyleText.text = "Выбор стиль шрифта"
       binding.rbtnGoupStyle.setOnCheckedChangeListener { radioGroup, i ->
           myLog("$radioGroup.id+ $i")
           when(i){
               R.id.rbtnNormal->{
                   myLog(NewAddNoteActivity.NORMAL)
                   text=NewAddNoteActivity.NORMAL
                   onClick.onClickSelectStyle(text)
               }
               R.id.rbtnBold->{
                   myLog(NewAddNoteActivity.BOLD)
                   text=NewAddNoteActivity.BOLD
                   onClick.onClickSelectStyle(text)
               }
               R.id.rbtnItalic->{
                   myLog(NewAddNoteActivity.ITALIC)
                   text=NewAddNoteActivity.ITALIC
                   onClick.onClickSelectStyle(text)
               }
               R.id.rbtnBoldItalic->{
                   myLog(NewAddNoteActivity.ITALIC_BOLD)
                   text=NewAddNoteActivity.ITALIC_BOLD
                   onClick.onClickSelectStyle(text)
               }
           }

           dialog.dismiss()
       }


    }
    fun editItemShoppingList(context: Context,item:ShoppingList,listener:ItemAdapter.ListenerItem){
        dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val binding = DialogEditItemBinding.inflate(inflater)
        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.background_item
            )
        )
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        val windowLP = dialog.window?.attributes;
        windowLP?.width = 850
        dialog.show()
         binding.etEditNameItem.setText(item.nameItem)
        if(item.itemType==0) binding.etEditInfoItem.setText(item.infoItem)
        else binding.etEditInfoItem.visibility=View.GONE
        binding.ibSaveEditItem.setOnClickListener {
            if(binding.etEditNameItem.text.isNotEmpty()) {
                val state= if(item.itemType==0) ItemAdapter.EDIT_ITEM else ItemAdapter.EDIT_LIBRARY
                listener.onClick(item.copy(nameItem =binding.etEditNameItem.text.toString()
                    , infoItem =binding.etEditInfoItem.text.toString()),state)
            }
            dialog.dismiss()
        }
    }

    fun onProgressBarDialog(context: Context):Dialog {
        dialog = Dialog(context)

        val inflater = LayoutInflater.from(context)
        val binding = DialogProgressBarBinding.inflate(inflater)


        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.background_item
            )
        )

        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }



    interface SelectStyle{
        fun onClickSelectStyle(style:String);
    }

   interface SelectYesNO{
       fun onClickType(type:ClickType)

   }

    enum class ClickType() {
        YES,
        NO,
        DEL_ALL
    }

}
