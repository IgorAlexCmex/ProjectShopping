package fragments

import activity.MainAppShoppingList
import activity.SetNameListShopping
import adapters.NameAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmex.myproject.R
import com.cmex.myproject.databinding.FragmentNameBinding
import db.MainViewModel
import tables.ShoppingListName
import utilities.MyDialog
import utilities.myToast


class NameFragment : MainFragment(), NameAdapter.ListenerEditShopName {
    private lateinit var binding: FragmentNameBinding
    private lateinit var setNameListLaunch: ActivityResultLauncher<Intent>
    private var listName = ArrayList<ShoppingListName>()
    private lateinit var shopList: ShoppingListName
    private lateinit var adapterName : NameAdapter
    private var flagState: Int = 0
    private val model: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainAppShoppingList).db)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcherSetNameListShopping()
        initMainViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNameBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getItemTouch()?.attachToRecyclerView(binding.rvNameList)
        init()
    }

    private fun initMainViewModel() {
        model.namesListData.observe(this) {
            if (it.isEmpty()) binding.ivlistEmpty.visibility = View.VISIBLE
            else binding.ivlistEmpty.visibility = View.GONE
            adapterName.updateAdapterName(it)
        }
    }

    private fun init() {
        adapterName= NameAdapter(this)
        binding.rvNameList.layoutManager = LinearLayoutManager(activity)
        binding.rvNameList.adapter = adapterName
    }

    override fun onClickNew() {
        onClickMenuListShopping()
    }

    private fun onClickMenuListShopping() {
        flagState=0
        val intent = Intent(activity, SetNameListShopping::class.java)
        intent.putExtra(SetNameListShopping.FLAG_STATE,flagState)
        setNameListLaunch.launch(intent)
    }

    private fun launcherSetNameListShopping() {

        setNameListLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {

                    flagState =
                        it.data?.getIntExtra(SetNameListShopping.FLAG_STATE, 0)!!
                    @Suppress("DEPRECATION")
                    shopList =
                        it.data?.getSerializableExtra(SetNameListShopping.LIST_SHOP_KEY) as ShoppingListName
                    if (flagState==1) model.updateName(shopList)
                    else model.insertName(shopList)

                }
            }
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
                listName.clear()
                model.namesListData.value?.let { listName.addAll(it) }
                MyDialog.dialogDel(
                    getString(R.string.titleDeleteNote),
                    activity as AppCompatActivity,
                    object : MyDialog.SelectYesNO {
                        override fun onClickType(type: MyDialog.ClickType) {
                            if (type == MyDialog.ClickType.NO) {
                                myToast("NO")
                                adapterName.updateAdapterName(listName)
                            } else if (type == MyDialog.ClickType.YES) {
                                myToast("YES")
                                model.deleteNameAndListItem(listName[position].id!!)
                                // adapterNotes.updateNoteAdapter(adapterNotes.listNote)
                            }
                        }
                    })
            }
        })
    }

    override fun onClickEditName(name: ShoppingListName) {
        flagState=1
        val intent = Intent(context, SetNameListShopping::class.java).apply {
            putExtra(SetNameListShopping.LIST_SHOP_KEY, name)
            putExtra(SetNameListShopping.FLAG_STATE, flagState)
        }
        setNameListLaunch.launch(intent)

    }

    override fun onClickEditItem(name: ShoppingListName) {

        flagState=2
        val intent = Intent(context, SetNameListShopping::class.java).apply {
            putExtra(SetNameListShopping.LIST_SHOP_KEY, name)
            putExtra(SetNameListShopping.FLAG_STATE, flagState)
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NameFragment()
    }


}