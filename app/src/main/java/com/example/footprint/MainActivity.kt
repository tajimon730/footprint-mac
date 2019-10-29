package com.example.footprint

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply {
            findItem(R.id.action_settings).isVisible = true
            findItem(R.id.action_share).isVisible = false
            findItem(R.id.action_comment).isVisible = false
            findItem(R.id.action_delete).isVisible = false
            findItem(R.id.action_edit).isVisible = false
            findItem(R.id.action_camera).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        when (item.itemId) {
            R.id.action_camera -> {
                val intent = Intent(this@MainActivity, EditActivity::class.java).apply {
                    putExtra(IntentKey.EDIT_MODE.name, ModeInEdit.SHOOT)
                }
                startActivity(intent)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}
