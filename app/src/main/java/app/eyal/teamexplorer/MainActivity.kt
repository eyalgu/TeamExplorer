package app.eyal.teamexplorer

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.eyal.teamexplorer.ui.MainFragment
import app.eyal.teamexplorer.wiring.Component
import app.eyal.teamexplorer.wiring.RealComponent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
