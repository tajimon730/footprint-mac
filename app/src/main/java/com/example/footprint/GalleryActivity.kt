package com.example.footprint

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_gallery.toolbar
import kotlinx.android.synthetic.main.content_gallery.*

class GalleryActivity : AppCompatActivity() {

    lateinit var realm:Realm
    lateinit var results: RealmResults<PhotoInfoModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener{
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val selectedLocation: String = getSelectedLocation()


        realm = Realm.getDefaultInstance()
        results = realm.where(PhotoInfoModel::class.java)
            .equalTo(PhotoInfoModel::location.name, selectedLocation)
            .findAll().sort(PhotoInfoModel::dateTime.name)


        setGallery(results)

        }

    private fun setGallery(results: RealmResults<PhotoInfoModel>?) {


        val screenOrientation = resources.configuration.orientation
        myRecyckerView.layoutManager = if(screenOrientation == Configuration.ORIENTATION_PORTRAIT){
            GridLayoutManager(this, 2)
            } else {
                GridLayoutManager(this, 4)
            }

        val adapter = MyRecyclerViewAdapter(results!!)
        myRecyckerView.adapter =adapter

        }


    private fun getSelectedLocation(): String {

        val selectedLatitude = intent.extras?.getDouble(IntentKey.LATITUDE.name)
        val selectedLongitude = intent.extras?.getDouble(IntentKey.LONGITUDE.name)
        return selectedLatitude.toString() + selectedLongitude.toString()



    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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


        when (item.itemId) {
            R.id.action_camera -> {
                val intent = Intent(this@GalleryActivity, EditActivity::class.java).apply {
                    putExtra(IntentKey.EDIT_MODE.name, ModeInEdit.SHOOT)
                }
                startActivity(intent)
                finish()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    }


