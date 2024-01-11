package fragments

import activity.MainAppShoppingList
import activity.NewAddNoteActivity
import adapters.NoteAdapter
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cmex.myproject.R
import com.cmex.myproject.databinding.FragmentNoteBinding
import constants.Constants
import db.MainViewModel
import tables.NoteList
import utilities.MyDialog
import utilities.myToast


class NoteFragment : MainFragment(),NoteAdapter.ListenerNoteAdapter {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var adapterNotes: NoteAdapter
    private lateinit var noteList: NoteList
    private var listNotes=ArrayList<NoteList>()
    private var flagEntry:Boolean=false
    private var pref:SharedPreferences?=null
    private lateinit var setNoteLancher: ActivityResultLauncher<Intent>
    private val model: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainAppShoppingList).db)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMainViewModel()
        onSetNoteResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingRecyclerViewAdapter()
        getItemTouch()?.attachToRecyclerView(binding.rvItemNotes)
        //initMainViewModel()
    }

    override fun onClickNew() {
        setNoteLancher.launch(Intent(activity, NewAddNoteActivity::class.java))
    }

    private fun initMainViewModel() {
        model.notesListData.observe(this) {
            adapterNotes.updateNoteAdapter(it)
            if (adapterNotes.itemCount == 0) binding.ivEmptyNote.visibility = View.VISIBLE
            else binding.ivEmptyNote.visibility = View.GONE
        }
    }

    private fun onSetNoteResult() {
        setNoteLancher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        noteList = it.data?.getSerializableExtra(
                            NOTE_LIST_KEY,
                            NoteList::class.java
                        ) as NoteList

                    } else {
                        @Suppress("DEPRECATION")
                        noteList =
                            it.data?.getSerializableExtra(NOTE_LIST_KEY) as NoteList

                    }
                     flagEntry= it.data!!.getBooleanExtra(FLAG_ENTRY,false)
                    if(flagEntry) model.updateNote(noteList)
                    else model.insertNote(noteList)
                }
            }
    }

    private fun settingRecyclerViewAdapter() {
        pref= activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        adapterNotes = NoteAdapter(this)
        binding.rvItemNotes.layoutManager = selectLayoutManager()
        binding.rvItemNotes.adapter = adapterNotes
    }
    private fun selectLayoutManager():RecyclerView.LayoutManager{
        val state=pref?.getString(Constants.STYLE_LIST,"1")
        return if(state=="1") LinearLayoutManager(activity)
              else StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
    }

    private fun getItemTouch(): ItemTouchHelper? {
        return ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = 0
                position = viewHolder.adapterPosition
              listNotes.clear()
                listNotes.addAll(adapterNotes.listNote)
                MyDialog.dialogDel(
                    getString(R.string.titleDeleteNote),
                    activity as AppCompatActivity,
                    object : MyDialog.SelectYesNO {
                        override fun onClickType(type: MyDialog.ClickType) {
                            if(type==MyDialog.ClickType.NO) {
                                myToast("NO")
                                adapterNotes.updateNoteAdapter(listNotes)
                            }  else if(type==MyDialog.ClickType.YES){
                                myToast("YES")
                                model.deleteNote(listNotes[position].id!!)
                               // adapterNotes.updateNoteAdapter(adapterNotes.listNote)
                            }
                        }
                    })
            }
        })
    }

    override fun onClickItem(noteList: NoteList) {
     val intent=Intent(activity,NewAddNoteActivity::class.java).apply {
         putExtra(NOTE_LIST_KEY,noteList)
         putExtra(FLAG_ENTRY,true)
     }
        setNoteLancher.launch(intent)
    }

    companion object {
        const val NOTE_LIST_KEY = "noteList_key"
        const val FLAG_ENTRY="flag_entry"
        @JvmStatic
        fun newInstance() = NoteFragment()

    }
}
