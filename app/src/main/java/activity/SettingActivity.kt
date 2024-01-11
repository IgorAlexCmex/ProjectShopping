package activity


import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager
import com.cmex.myproject.R
import com.cmex.myproject.databinding.ActivitySettingsBinding
import constants.Constants
import fragments.FragmentSetting


class SettingActivity : AppCompatActivity() {
    private lateinit var ab:ActionBar
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var pref:SharedPreferences
    private var stateStyle=""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref=PreferenceManager.getDefaultSharedPreferences(this)
        stateStyle=pref.getString(Constants.THEME,"1").toString()
        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment,FragmentSetting()).commit()
        }
        initToolBar()
    }

    override fun onResume() {
        super.onResume()
        setFon()
    }
   private fun initToolBar(){
      setSupportActionBar(binding.tbSettings)
       ab= supportActionBar!!
       if(stateStyle=="1") {
           ab.setBackgroundDrawable(getDrawable(R.drawable.fon_whatsapp_white))
       } else{
           ab.setBackgroundDrawable(getDrawable(R.drawable.fon_blue_white))
       }
      ab.setIcon(R.drawable.settings)
       ab.title=getString(R.string.setting)
       ab.setDisplayHomeAsUpEnabled(true)
       ab.setHomeAsUpIndicator(R.drawable.arrow_exit)

   }
  private fun setFon(){
      if(stateStyle=="1") {
          ab.setBackgroundDrawable(getDrawable(R.drawable.fon_whatsapp_white))
      } else{
          ab.setBackgroundDrawable(getDrawable(R.drawable.fon_blue_white))
      }
  }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)

    }
}