package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 디버그 빌드일 경우 1초뒤에 helltakeractivity로 넘어감
        if(BuildConfig.DEBUG) {
            Handler(Looper.getMainLooper()).postDelayed({
                startGameActivity()
            }, 1000)
        }
    }

    fun startGameActivity() {
        val intent = Intent(this, HelltakerMobileActivity::class.java)
        startActivity(intent)
    }

    fun onBtnStartGame(view: View) {
        startGameActivity()
    }
}