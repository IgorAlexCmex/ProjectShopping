package activity

import android.R.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.preference.PreferenceManager
import com.cmex.myproject.R
import com.cmex.myproject.databinding.ActivityAddNewNoteBinding
import constants.Constants
import fragments.NoteFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tables.NoteList
import utilities.ImageInBitmap
import utilities.MyDialog
import utilities.MyTouch
import utilities.myLog
import utilities.myToast
import utilities.utilBitmapInByteArray
import utilities.utilByteArrayInBitmap
import utilities.utilGetHtml
import utilities.utilGetTime
import utilities.utilHtmlToString
import utilities.utilUriPermission

class NewAddNoteActivity : AppCompatActivity(),MyDialog.SelectStyle {
    private lateinit var binding:ActivityAddNewNoteBinding
    private var selectImage: ActivityResultLauncher<Intent>? = null
    private var bitmap:Bitmap?=null
    private var uriImage: Uri?=null
    private var byteArray:ByteArray?=null
    private var noteList: NoteList?=null
    private  var flagEntry:Boolean=false
    private var pref:SharedPreferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        flagEntry=intent.getBooleanExtra(NoteFragment.FLAG_ENTRY,false)
        init()
        initPref()
        setTextSizeToTitle()

    }
   @SuppressLint("ClickableViewAccessibility")
   private fun init(){
       binding.tabColor.setOnTouchListener(MyTouch())
       onClickSelectColorText()
       initToolbar()
       onSelectImage()
       onClickCamera()
       onClickAddImage()
       actionMenuCallBack()

       if(flagEntry) getNote()
        else {
            binding.ivPhotoTitleNote.visibility=View.GONE
        }
   }
    private fun initPref(){
        pref=PreferenceManager.getDefaultSharedPreferences(this)
    }
    private fun getNote(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            noteList = intent.getSerializableExtra(
                NoteFragment.NOTE_LIST_KEY,
                NoteList::class.java
            ) as NoteList

        } else {
            @Suppress("DEPRECATION")
            noteList =
                intent.getSerializableExtra(NoteFragment.NOTE_LIST_KEY) as NoteList

        }
        checkedNote()
    }
    private fun checkedNote(){
        if(noteList!=null){
          fillNote()
        }
    }
    private fun fillNote() = with(binding){
      etTitleNote.setText(noteList?.title)
        etDescriptionNote.setText(utilGetHtml( noteList?.description!!))

        if(noteList?.image!=null) {
            byteArray = noteList?.image
            val bitmap = noteList?.image?.let { utilByteArrayInBitmap(it) }
            ivPhotoTitleNote.setImageBitmap(bitmap)
        } else ivPhotoTitleNote.visibility=View.GONE
    }
   private fun initToolbar(){
       setSupportActionBar(binding.tbNew)
       val ab=supportActionBar
       ab?.setDisplayShowTitleEnabled(false)
       if(flagEntry) ab?.setIcon(R.drawable.edit_note)
       else ab?.setIcon(R.drawable.new_note)
       ab?.setDisplayHomeAsUpEnabled(true)

   }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_new,menu)
        return super.onCreateOptionsMenu(menu)
    }
    private fun onImageInBitmapAndByteArray(uri: Uri){
        CoroutineScope(Dispatchers.Main).launch {
            bitmap = uri?.let { ImageInBitmap.onImageInBitmap(it, this@NewAddNoteActivity) }
            byteArray = bitmap?.let { utilBitmapInByteArray(it) }

        }

    }
    private fun onClickAddImage(){
        binding.ibAddImageSd.setOnClickListener {
            getImage()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.saveNewAdd -> {
                sendingResult()
                finish()
            }
            R.id.selectStyleText -> {
                var style=""
                MyDialog.selectStyleText(this,this)

             //  selectionStyleText()
            }
            R.id.selectColorText->{
                if(binding.tabColor.isShown) closeColorSelect()
               else openColorSelect()
            }
            id.home -> {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }
    // ==============----изменяет цвет текста--==============================
    private fun setSelectionColorText(color:Int)=with(binding){
        val startPosition=etDescriptionNote.selectionStart
        val endPosition=etDescriptionNote.selectionEnd
        val colorText=etDescriptionNote.text.getSpans(startPosition,endPosition,ForegroundColorSpan::class.java)
        if(colorText.isNotEmpty()){
            etDescriptionNote.text.removeSpan(colorText[0])
        }
       etDescriptionNote.text.setSpan(
           ForegroundColorSpan(ContextCompat.getColor(this@NewAddNoteActivity,color)),
           startPosition,endPosition,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
           )
        etDescriptionNote.text.trim()
        etDescriptionNote.setSelection(startPosition)
    }
    //===============================================

    // ==============----изменяет стиль текста--==============================
    private fun selectionStyleText(style: String)=with(binding){
        val startPosition=etDescriptionNote.selectionStart
        val endPosition=etDescriptionNote.selectionEnd
        val stylesText=etDescriptionNote.text.getSpans(startPosition,endPosition,StyleSpan::class.java)
          var boltStyle:StyleSpan?=null
        /*if (stylesText.isNotEmpty()){
            etDescriptionNote.text.removeSpan(stylesText[0])
        } else{*/
        if (stylesText.isNotEmpty()){
            etDescriptionNote.text.removeSpan(stylesText[0])
        }
            when(style){
                BOLD ->{
                    boltStyle= StyleSpan(Typeface.BOLD)
                }
                ITALIC->{
                    boltStyle= StyleSpan(Typeface.ITALIC)
                }
                ITALIC_BOLD->{
                    boltStyle= StyleSpan(Typeface.BOLD_ITALIC)
                }
                NORMAL->{
                    boltStyle= StyleSpan(Typeface.NORMAL)
                }
            }

       // }
        etDescriptionNote.text.setSpan(boltStyle,startPosition,endPosition,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        etDescriptionNote.text.trim()
        etDescriptionNote.setSelection(startPosition)

    }
        //========================================================

    private fun sendingResult() {
        var tempNote:NoteList?=null
        if(flagEntry) tempNote=updateNoteList()
        else tempNote=setNoteList()
        val intent=Intent().apply {
            putExtra(NoteFragment.NOTE_LIST_KEY,tempNote)
            putExtra(NoteFragment.FLAG_ENTRY,flagEntry)
        }
        setResult(RESULT_OK,intent)
    }

    private fun setNoteList():NoteList = with(binding){
        return NoteList(null,
            etTitleNote.text.toString(),
            utilHtmlToString(etDescriptionNote.text),
            utilGetTime(System.currentTimeMillis()),
            byteArray
        )
    }
      private fun updateNoteList():NoteList? =with(binding){
          return noteList?.copy(
              title=etTitleNote.text.toString(),
              description = utilHtmlToString(etDescriptionNote.text),
              time = utilGetTime(System.currentTimeMillis()),
              image = byteArray
              )
      }

    private fun onSelectImage() {
        selectImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    if (it.data!!.data != null) {
                        uriImage=it.data!!.data!!
                        uriImage?.let { utilUriPermission(it,this) }

                    } else Toast.makeText(this, "not it.data.data", Toast.LENGTH_SHORT).show()

                }  else Toast.makeText(this, "not it.data", Toast.LENGTH_SHORT).show()
                uriImage?.let { it1 -> onImageInBitmapAndByteArray(it1) }
                binding.ivPhotoTitleNote.visibility=View.VISIBLE
                binding.ivPhotoTitleNote.setImageURI(uriImage)
            }else{

                 Toast.makeText(this, "not Result Ok", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getImage(){
           val intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
           intent.type="image/*"
           selectImage?.launch(intent)
    }

    private fun onClickCamera() {
        binding.ibtnCamera.setOnClickListener {
            pickImageCamera()
        }
    }


    //подключение Камеры рабочий вариант **************************
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
            binding.ivPhotoTitleNote.visibility=View.VISIBLE
            binding.ivPhotoTitleNote.setImageURI(uriImage)
            uriImage?.let { onImageInBitmapAndByteArray(it) }
        } else {
            //Cancelled
            Toast.makeText(this, "Невышло...!", Toast.LENGTH_SHORT).show()
        }
    }
        // *******************************************************************_END


    private fun onClickSelectColorText() =with(binding){
        fabtnBlue.setOnClickListener {
          setSelectionColorText(R.color.light_blue)
        }
        fabtnGold.setOnClickListener {
            setSelectionColorText(R.color.gold)
        }
        fabtnGreen.setOnClickListener {
            setSelectionColorText(R.color.green)
        }
        fabtnRed.setOnClickListener {
            setSelectionColorText(R.color.red)
        }
      fabtnDarkBlue.setOnClickListener {
          setSelectionColorText(R.color.purple_700)
      }
      fabtnYellow.setOnClickListener {
          setSelectionColorText(R.color.yellow)
      }
    }
//========---открытие и закрытие меню выбор цвета---============
    private fun openColorSelect(){
        binding.tabColor.visibility=View.VISIBLE
            val anim=AnimationUtils.loadAnimation(this,R.anim.color_select_open)
            binding.tabColor.startAnimation(anim)
    }
    private fun closeColorSelect(){
        val anim=AnimationUtils.loadAnimation(this,R.anim.color_select_close)
       anim.setAnimationListener(object :Animation.AnimationListener{
           override fun onAnimationStart(p0: Animation?) {}

           override fun onAnimationEnd(p0: Animation?) {
               binding.tabColor.visibility=View.GONE
           }

           override fun onAnimationRepeat(p0: Animation?) {}
       })
       binding.tabColor.startAnimation(anim)
    }
    //=======================================================

    //=============---При выделение текста появляется меню, убираем это меню---======
    private fun actionMenuCallBack(){
      val actionCallback=object :ActionMode.Callback{
          override fun onCreateActionMode(p0: ActionMode?, menu: Menu?): Boolean {
             menu?.clear()
              return true
          }

          override fun onPrepareActionMode(p0: ActionMode?, menu: Menu?): Boolean {
            menu?.clear()
              return true
          }

          override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            return true
          }

          override fun onDestroyActionMode(p0: ActionMode?) {}
      }
        binding.etDescriptionNote.customSelectionActionModeCallback=actionCallback
    }
    private fun setTextSizeToTitle(){
      // binding.etTitleNote.setMyTextSize(pref?.getString("text_size","12"))
        binding.etTitleNote.textSize= pref?.getString(Constants.TEXT_SIZE,"12")?.toFloat()!!
    }

    private fun EditText.setMyTextSize(size:String?){
        if (size!=null){
            this.textSize=size.toFloat()
        }

    }
  //=========================================================
   companion object{
       const val BOLD="bold"
       const val ITALIC="italic"
       const val ITALIC_BOLD="italic_bold"
       const val NORMAL="normal"
   }

    override fun onClickSelectStyle(style: String) {
        selectionStyleText(style)
    }
}