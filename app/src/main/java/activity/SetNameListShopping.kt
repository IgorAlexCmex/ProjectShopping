package activity

import adapters.ItemAdapter
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmex.myproject.R
import com.cmex.myproject.databinding.ActivitySetNameListShopppingBinding
import constants.Constants
import db.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tables.LibraryElements
import tables.ShoppingList
import tables.ShoppingListName
import utilities.ImageInBitmap
import utilities.MyDialog
import utilities.myLog
import utilities.myToast
import utilities.utilBitmapInByteArray
import utilities.utilByteArrayInBitmap
import utilities.utilGetTime
import utilities.utilShareShoppingList
import utilities.utilUriPermission

class SetNameListShopping : AppCompatActivity(),ItemAdapter.ListenerItem {
    private lateinit var binding: ActivitySetNameListShopppingBinding
    private var selectImage: ActivityResultLauncher<Intent>? = null
    private var bitmap: Bitmap? = null
    private   var shopList: ShoppingListName?=null
    private var uriImage: Uri? = null
    private var byteArray: ByteArray? = null
    private var byteArrayItem: ByteArray? = null
    private  var flagState:Int=0
    private lateinit var itemShopList:ShoppingList
    private lateinit var saveItem:MenuItem
    private lateinit var inputItem:MenuItem
    private var listItem=ArrayList<ShoppingList>()
    private var edTextItem:EditText?=null
    private lateinit var adapterItem:ItemAdapter
    private lateinit var textSearch:TextWatcher
    private lateinit var pref:SharedPreferences
    private val model: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((this?.applicationContext as MainAppShoppingList).db)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetNameListShopppingBinding.inflate(layoutInflater)
        pref=PreferenceManager.getDefaultSharedPreferences(this)
        setFon()
        backPressed()
        setContentView(binding.root)
        onClickButton()
        onSelectImage()
        initToolbar()
        initRecyclerView()
        flagState=intent.getIntExtra(FLAG_STATE,0)
        if(flagState==1) getIntentShopList()
        else if(flagState==2){
            getIntentShopList()
            binding.containerName.visibility=View.GONE
        }
        else binding.containerItem.visibility=View.GONE

        getItemTouch()?.attachToRecyclerView(binding.rvItem)
    }
     fun backPressed() {
         val callback:OnBackPressedCallback = object:OnBackPressedCallback(true) {
             override fun handleOnBackPressed() {
             countShoppingListName()
                 finish()
             }
         }

         this@SetNameListShopping.onBackPressedDispatcher.addCallback(this@SetNameListShopping, callback)
    }

    override fun onResume() {
        super.onResume()
        initShopListObserver()

    }
    private fun setFon(){
        val stateLayout=pref.getString(Constants.THEME,"1")
        if(stateLayout=="1") binding.clSetName.setBackgroundResource(R.drawable.fon_whatsapp_white)
        else binding.clSetName.setBackgroundResource(R.drawable.fon_blue_white)
    }

    private fun onImageInBitmapAndByteArray(uri: Uri) {
        CoroutineScope(Dispatchers.Main).launch {
            bitmap = uri?.let { ImageInBitmap.onImageInBitmap(it, this@SetNameListShopping) }
            byteArray = bitmap?.let { utilBitmapInByteArray(it) }

        }
    }
    private fun initShopListObserver(){
        myLog("ShopListObserver")
        if(shopList!=null) {
            model.readItemByIdName(shopList!!.id!!).observe(this) {
                if(it.isEmpty()) binding.ivEmptyItem.visibility=View.VISIBLE
                else binding.ivEmptyItem.visibility=View.GONE
                adapterItem.updateAdapterItem(it)
                listItem.clear()
                listItem.addAll(it)

            }
        }
    }
    private  fun initLibraryObserver(){
        model.libraryElements.observe(this){
           val tempListShop=ArrayList<ShoppingList>()
            it.forEach {item->
              val shopListItem=ShoppingList(
                  item.id,
                  item.nameElement!!,
                  "",
                  0,
                  true,
                  1,
                  null
              )
                tempListShop.add(shopListItem)
                listItem.clear()
                listItem.addAll(tempListShop)
            }
            myLog("listItem=$listItem")
            adapterItem.updateAdapterItem(listItem)
        }
    }
    private fun initRecyclerView(){
        adapterItem= ItemAdapter(this)
        binding.rvItem.layoutManager=LinearLayoutManager(this)
        binding.rvItem.adapter=adapterItem

    }
    private fun setItemShopList(){
        if(edTextItem?.text.toString().isEmpty())return
       itemShopList=ShoppingList(
           null,
           edTextItem?.text.toString(),
           "",
           shopList?.id!!,
           false,
           0,
           byteArrayItem
       )
        model.insertItem(itemShopList)
    }
    private fun initToolbar(){
        setSupportActionBar(binding.toolbarName)
        val ab=supportActionBar
        ab?.setBackgroundDrawable(
            ContextCompat.getDrawable(
            this,
            R.drawable.background_item
        ))

        ab?.setDisplayShowTitleEnabled(false)
        ab?.setDisplayHomeAsUpEnabled(true)
    }
    // рисуем меню в тулбар
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item_list_shopping,menu)
        saveItem=menu?.findItem(R.id.saveItem)!!
        val expand=menu.findItem(R.id.inputItem)
        expand.setOnActionExpandListener(expandEditText())
        edTextItem=expand.actionView?.findViewById(R.id.etItemName)
        saveItem.isVisible=false
        textSearch=textSearchInDb()

        return super.onCreateOptionsMenu(menu)
    }
       private fun textSearchInDb():TextWatcher{
    return object :TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
           myLog("Вводимый текс=$char")
            model.readLibrary("%$char%")
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.saveItem->{
                setItemShopList()
                edTextItem?.setText("")
            }
            R.id.clearList->model.deleteItemListByName(shopList?.id!!)
            R.id.shareList->{startActivity(utilShareShoppingList(listItem,shopList?.nameList!!))}
        }
        return super.onOptionsItemSelected(item)
    }
    //=======нажатие на кнопку появляется edittext
    private fun expandEditText():MenuItem.OnActionExpandListener{
    return    object : OnActionExpandListener{
        //открытый edittext
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
              saveItem.isVisible=true
              edTextItem?.addTextChangedListener(textSearch)
               binding.ivNameImage.visibility=View.GONE
            initLibraryObserver()
            model.readItemByIdName(shopList?.id!!).removeObservers(this@SetNameListShopping)
            model.readLibrary("%%")
                return true
            }
        //закрыли edittext
            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
               saveItem.isVisible=false
               binding.ivNameImage.visibility=View.VISIBLE
               edTextItem?.removeTextChangedListener(textSearch)
                invalidateOptionsMenu()
            model.libraryElements.removeObservers(this@SetNameListShopping)
            initShopListObserver()
                return true
            }

        }

    }
  //============================
    private fun onSelectImage() {
        selectImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    if (it.data!!.data != null) {
                        uriImage = it.data!!.data!!
                        utilUriPermission(uriImage!!, this)

                    } else Toast.makeText(this, "not it.data.data", Toast.LENGTH_SHORT).show()

                } else Toast.makeText(this, "not it.data", Toast.LENGTH_SHORT).show()
                uriImage?.let { it1 -> onImageInBitmapAndByteArray(it1) }
                binding.ivNameImage.visibility = View.VISIBLE
                binding.ivNameImage.setImageURI(uriImage)
            } else {

                Toast.makeText(this, "not Result Ok", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun onClickButton() = with(binding) {
        ibSelectImage.setOnClickListener {
            getImage()
        }
        ibCamera.setOnClickListener {
            pickImageCamera()
        }
        ibSaveName.setOnClickListener {
            getShoppingListName()
        }
    }

    private fun getIntentShopList(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            shopList = intent.getSerializableExtra(
                LIST_SHOP_KEY,
                ShoppingListName::class.java
            ) as ShoppingListName

        } else {
            @Suppress("DEPRECATION")
            shopList =
                intent.getSerializableExtra(LIST_SHOP_KEY) as ShoppingListName

        }
        flagState=intent.getIntExtra(FLAG_STATE,0)

        onCheckingState()
    }
    private fun onCheckingState()= with(binding){
           if(flagState==1){
             setWindow(shopList!!)
           }
    }
    private fun setShoppingListName():ShoppingListName = with(binding){
     return  ShoppingListName(
         null,
         etNameListShopping.text.toString(),
        utilGetTime(System.currentTimeMillis()),
         0,
         0,
         "",
         byteArray
     )

    }
    private fun countShoppingListName(){
        var count=0
        listItem.forEach {
            if(it.checkedItem)count++
        }
        if(shopList!=null) {
            val tempShopList = shopList!!.copy(
                totalNumberShopping = listItem.size,
                counterShopping = count
            )
            model.updateName(tempShopList)
        }
    }
    private fun updateShoppingListName():ShoppingListName = with(binding){

        return shopList!!.copy(
            nameList = etNameListShopping.text.toString(),
            time = utilGetTime(System.currentTimeMillis()),
            image = byteArray,

        )
    }
    private fun setWindow(shopList:ShoppingListName) =with(binding){
        etNameListShopping.setText(shopList.nameList)
        byteArray=shopList.image
        if(shopList.image!=null) {
            ivNameImage.visibility=View.VISIBLE
            bitmap = utilByteArrayInBitmap(shopList.image!!)
            ivNameImage.setImageBitmap(bitmap)
        } else     ivNameImage.visibility=View.GONE
    }
    private fun getImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        selectImage?.launch(intent)
    }

    private fun pickImageCamera() {

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Временное изображение")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Описание временного изображения")
        //Uri of the image to be captured from camera
        // Uri фото, которое нужно захватить с камеры.
        uriImage =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        //Intent to launch camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            //no need to get image uri here we will have it in pickImageCamera() function
            //здесь нет необходимости получать URI изображения, мы получим его в функции PickImageCamera()
            //А здесь использования
            binding.ivNameImage.visibility = View.VISIBLE
            binding.ivNameImage.setImageURI(uriImage)
            myLog("onActivityResult: imageUri: $uriImage")
            uriImage?.let { onImageInBitmapAndByteArray(it) }
        } else {
            //Cancelled
            Toast.makeText(this, "Невышло...!", Toast.LENGTH_SHORT).show()
        }
    }

    // *******************************************************************_END
    private fun getShoppingListName() {
        if(flagState==1){

            shopList=updateShoppingListName()
            flagState=1


        } else {
            flagState=0
            shopList=setShoppingListName()
        }
        val intent = Intent().apply {
            putExtra(LIST_SHOP_KEY, shopList)
            putExtra(FLAG_STATE,flagState)
        }
        if (binding.etNameListShopping.text.isNotEmpty()){
            setResult(RESULT_OK, intent)
            finish()
    }  else myToast(getString(R.string.errorSetNameListShopping))
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
                val tempList=listItem
                MyDialog.dialogDel(
                    getString(R.string.titleDeleteNote),
                    this@SetNameListShopping,
                    object : MyDialog.SelectYesNO {
                        override fun onClickType(type: MyDialog.ClickType) {
                            if (type == MyDialog.ClickType.NO) {
                               adapterItem.notifyDataSetChanged()
                            } else if (type == MyDialog.ClickType.YES) {
                                if(listItem[position].itemType==0) {
                                    model.deleteItem(listItem[position].id!!)
                                }  else {
                                    model.deleteItemLibrary(listItem[position].id!!)
                                    model.readLibrary("%%")
                                }
                            }
                        }
                    })
            }
        })
    }
    override fun onClick(item: ShoppingList,state:Int) {
        when(state) {
           ItemAdapter.EDIT_ITEM-> model.updateItem(item)
           ItemAdapter.EDIT_LIBRARY->{
                   myLog("item= $item")
                   model.updateItemLibrary(
                       LibraryElements(
                           item.id,
                           item.nameItem,
                           null
                       )
                   )
                   model.readLibrary("%${edTextItem?.text}%")
               }

            ItemAdapter.PRESS_ITEM->{
                edTextItem?.setText(item.nameItem)
                setItemShopList()

            }
        }
    }

companion object{
    const val FLAG_STATE="state"
    const val LIST_SHOP_KEY="listShopping"
}

}