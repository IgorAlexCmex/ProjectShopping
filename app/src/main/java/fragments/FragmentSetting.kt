package fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.cmex.myproject.R

class FragmentSetting:PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
       setPreferencesFromResource(R.xml.settings,rootKey)
    }
}