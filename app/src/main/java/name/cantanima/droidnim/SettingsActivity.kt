package name.cantanima.droidnim


import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.MenuItem
import android.support.v4.app.NavUtils
import yuku.ambilwarna.widget.AmbilWarnaPreference

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = GeneralPreferenceFragment()
        setupActionBar()
        fragmentManager.beginTransaction().replace(android.R.id.content, settings).commit()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean =
        GeneralPreferenceFragment::class.java.name == fragmentName

    override fun onMenuItemSelected(featureId: Int, item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment 
        : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener
    {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
        }

        override fun onResume() {
            super.onResume()
            PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            PreferenceManager.getDefaultSharedPreferences(activity).unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
            if (isAdded) {
                when (key) {
                    getString(R.string.color_droid_happy_key) -> {
                        val pref = findPreference(getString(R.string.color_droid_happy_key)) as AmbilWarnaPreference
                        pref.forceSetValue(prefs!!.getInt(getString(R.string.color_droid_happy_key), 0))
                    }
                    getString(R.string.color_droid_worry_key) -> {
                        val pref = findPreference(getString(R.string.color_droid_worry_key)) as AmbilWarnaPreference
                        pref.forceSetValue(prefs!!.getInt(getString(R.string.color_droid_worry_key), 0))
                    }
                    getString(R.string.color_droid_deact_key) -> {
                        val pref = findPreference(getString(R.string.color_droid_deact_key)) as AmbilWarnaPreference
                        pref.forceSetValue(prefs!!.getInt(getString(R.string.color_droid_deact_key), 0))
                    }
                    getString(R.string.color_eyes_happy_key) -> {
                        val pref = findPreference(getString(R.string.color_eyes_happy_key)) as AmbilWarnaPreference
                        pref.forceSetValue(prefs!!.getInt(getString(R.string.color_eyes_happy_key), 0))
                    }
                    getString(R.string.color_eyes_worry_key) -> {
                        val pref = findPreference(getString(R.string.color_eyes_worry_key)) as AmbilWarnaPreference
                        pref.forceSetValue(prefs!!.getInt(getString(R.string.color_eyes_worry_key), 0))
                    }
                    getString(R.string.color_eyes_deact_key) -> {
                        val pref = findPreference(getString(R.string.color_eyes_deact_key)) as AmbilWarnaPreference
                        pref.forceSetValue(prefs!!.getInt(getString(R.string.color_droid_deact_key), 0))
                    }
                }
            }
        }
        
    }

}
