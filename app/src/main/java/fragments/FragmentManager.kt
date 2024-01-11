package fragments

import androidx.appcompat.app.AppCompatActivity
import com.cmex.myproject.R

object FragmentManager {
    var currentFragment:MainFragment?=null
    fun setFragManager(newFragment:MainFragment,activity: AppCompatActivity){

        val transaction=activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder,newFragment)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .commit()
        currentFragment=newFragment
    }
}