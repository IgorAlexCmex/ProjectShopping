package activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.preference.PreferenceManager
import com.cmex.myproject.R
import com.cmex.myproject.databinding.ActivityMainBinding
import constants.Constants
import db.MainViewModel
import fragments.FragmentManager
import fragments.NameFragment
import fragments.NoteFragment
import utilities.myLog


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model: MainViewModel by viewModels()
    private lateinit var ab:ActionBar
    private lateinit var pref:SharedPreferences
    private  var state=""

    override fun onCreate(savedInstanceState: Bundle?) {
        pref=PreferenceManager.getDefaultSharedPreferences(this)
        selectStyle()
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        state =pref.getString(Constants.THEME,"1").toString()
        setContentView(binding.root)

       onClickBottomNavView()
       setActionBar()
      setColorBottomMenu()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavViewMain.selectedItemId=R.id.note
        changeTheme()
    }
    private fun changeTheme(){
        if (state!=pref.getString(Constants.THEME,"1"))recreate()
    }
    private fun setActionBar(){
        setSupportActionBar(binding.tbMain)
     ab= supportActionBar!!

    }
    private fun setColorBottomMenu(){
        binding.bottomNavViewMain.setBackgroundResource(R.color.blue_dark)
        val stateLayout=pref.getString(Constants.THEME,"1")
        val colorForWhatsapp=getColor(R.color.my_color)
        val colorForBlue=getColor(R.color.for_blue_color)
        if (stateLayout=="1"){
            binding.bottomNavViewMain.setBackgroundResource(R.color.whatsapp)
            val colorStateList=ColorStateList.valueOf(colorForWhatsapp)
            binding.bottomNavViewMain.itemIconTintList=colorStateList
        }else {
            binding.bottomNavViewMain.setBackgroundResource(R.color.blue_dark)
            val colorStateList=ColorStateList.valueOf(colorForBlue)
            binding.bottomNavViewMain.itemIconTintList=colorStateList
        }
    }
     private fun selectStyle(){
         val stateStyle=pref.getString(Constants.THEME,"1")
         if(stateStyle=="1") setTheme(R.style.MyThemeWhatsapp)
         else setTheme(R.style.MyThemeBlue)
     }
    private fun onClickBottomNavView(){
        binding.bottomNavViewMain.setOnItemSelectedListener {
            when(it.itemId){
              R.id.setting->{
                  ab.setIcon(R.drawable.settings)
                  ab.title=getString(R.string.setting)
                  startActivity(Intent(this,SettingActivity::class.java))
              }
              R.id.note->{
                 FragmentManager.setFragManager(NoteFragment.newInstance(),this)
                  ab.setIcon(R.drawable.note)
                  ab.title=getString(R.string.note)
              }
                R.id.listShopping->{
                    ab.setIcon(R.drawable.list_numbered)
                 ab.title=getString(R.string.purchases)
                    FragmentManager.setFragManager(NameFragment.newInstance(),this)
                }
                R.id.newAdd-> {

                FragmentManager.currentFragment?.onClickNew()
                }
            }
            true
        }
    }

}