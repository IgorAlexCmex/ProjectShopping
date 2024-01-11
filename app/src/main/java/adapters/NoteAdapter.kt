package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmex.myproject.databinding.ItemNoteBinding
import constants.Constants
import tables.NoteList
import utilities.utilByteArrayInBitmap
import utilities.utilSetFormatTime

class NoteAdapter(private val listener:ListenerNoteAdapter):RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
      private lateinit var binding: ItemNoteBinding
       val listNote=ArrayList<NoteList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
      val inflanter=LayoutInflater.from(parent.context)
        binding=ItemNoteBinding.inflate(inflanter,parent,false)
        return NoteHolder(binding,listener)
    }

    override fun getItemCount(): Int {
       return listNote.size
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.setData(listNote[position])
    }

    class NoteHolder(val binding: ItemNoteBinding, private val listener: ListenerNoteAdapter):RecyclerView.ViewHolder(binding.root){
       fun setData(noteList:NoteList)= with(binding){
           val pref =PreferenceManager.getDefaultSharedPreferences(binding.root.context)
             tvTitleNote.text=noteList.title
           tvTitleNote.textSize= pref.getString(Constants.TEXT_SIZE,"16")?.toFloat()!!
            // tvDescriptionNote.text=noteList.description
           tvTimeNote.text = utilSetFormatTime( noteList.time,pref)
           ivPhotoNote.setImageBitmap(noteList.image?.let { utilByteArrayInBitmap(it) })
           itemView.setOnClickListener {
               listener.onClickItem(noteList)
           }
       }
    }
    fun updateNoteAdapter(newList: List<NoteList>){
        val diffUtil= DiffUtil.calculateDiff(DiffUtilListNote(listNote,newList))
        diffUtil.dispatchUpdatesTo(this)
        listNote.clear()
        listNote.addAll(newList)
        notifyDataSetChanged()
    }

interface ListenerNoteAdapter{
   fun onClickItem(noteList:NoteList)
}
}