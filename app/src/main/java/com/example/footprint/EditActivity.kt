package com.example.footprint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.footprint.*
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditActivity : AppCompatActivity() {

    lateinit var mode: ModeInEdit

    val PERMISSION = arrayOf(android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION)

    var isCameraEnabled = false
    var isWriteStorageEnabled = false
    var isLocationAccessEnabled = false


    var contentUri: Uri? = null

//    var selectedPhotoInfo = PhotoInfoModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener{
                finish()
            }
        }

        mode = intent.extras?.getSerializable(IntentKey.EDIT_MODE.name) as ModeInEdit

        if (mode == ModeInEdit.SHOOT){
            if (Build.VERSION.SDK_INT >= 23) permissionCheck() else launchCamera()
        } else {

        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState?.putParcelable(IntentKey.CONTENT_URI.name,contentUri)
    }

    private fun launchCamera() {

        val contentFileName = SimpleDateFormat("yyyyMMdd_HHmmss_z"). format((Date()))
        contentUri = generateContentUriFromFileName(contentFileName)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            val content = applicationContext
            val resolvedIntentActivities = content.packageManager
                .queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY)
            for (resolvedIntentInfo in resolvedIntentActivities){
                val packageName = resolvedIntentInfo.activityInfo.packageName
                content.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }
        startActivityForResult(intent, RQ_CODE_CAMERA)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK){
            Toast.makeText(this@EditActivity, getString(R.string.shoot_failed), Toast.LENGTH_SHORT).show()
            return
        }
        if (requestCode != RQ_CODE_CAMERA){
            Toast.makeText(this@EditActivity, getString(R.string.shoot_failed), Toast.LENGTH_SHORT).show()
            return
        }
        if (contentUri == null){
            Toast.makeText(this@EditActivity, getString(R.string.shoot_failed), Toast.LENGTH_SHORT).show()
            return
        }
        imageView.setImage(ImageSource.uri(contentUri!!))

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            applicationContext.revokeUriPermission(contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
//        selectedPhotoInfo.stringContentUri = contentUri.toString()
//        selectedPhotoInfo.dateTime = SimpleDateFormat("yyyyMMdd_HHmmss_z").format(Date())

    }

    private fun generateContentUriFromFileName(contentFileName: String?): Uri? {
        val contentFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        contentFolder?.mkdirs()
        val contentFilePath = contentFolder?.path + "/" + contentFileName + ".jpg"

        val contentFile = File(contentFilePath)
        return FileProvider.getUriForFile(
            this@EditActivity,
            applicationContext.packageName + ".fileprovider",
            contentFile
        )

    }

    private fun permissionCheck() {
        val permissionCheckCamera: Int = ContextCompat.checkSelfPermission(this@EditActivity, PERMISSION[0])
        val permissionCheckWriteStorage: Int = ContextCompat.checkSelfPermission(this@EditActivity, PERMISSION[1])
        val permissionCheckLocationAccess: Int = ContextCompat.checkSelfPermission(this@EditActivity, PERMISSION[2])


        if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED) isCameraEnabled = true
        if (permissionCheckWriteStorage == PackageManager.PERMISSION_GRANTED) isWriteStorageEnabled = true
        if (permissionCheckLocationAccess == PackageManager.PERMISSION_GRANTED) isLocationAccessEnabled = true


        if (isCameraEnabled && isWriteStorageEnabled && isLocationAccessEnabled) launchCamera() else permissionRequest()



    }

    private fun permissionRequest() {
        val isNeedExplainForCameraPermission = ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity, PERMISSION[0])
        val isNeedExplainForWriteStoragePermission = ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity, PERMISSION[1])
        val isNeedExplainForLocationAccessPermission = ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity, PERMISSION[2])

        val isNeedExplainForPermission = if (isNeedExplainForCameraPermission || isNeedExplainForWriteStoragePermission || isNeedExplainForLocationAccessPermission){
            true
        } else false

        val requestPermissionList = ArrayList<String>()


        if (!isCameraEnabled) requestPermissionList.add(PERMISSION[0])
        if (!isWriteStorageEnabled) requestPermissionList.add(PERMISSION[1])
        if (!isLocationAccessEnabled) requestPermissionList.add(PERMISSION[2])

        if (isNeedExplainForPermission){
            ActivityCompat.requestPermissions(
                this@EditActivity,
                requestPermissionList.toArray(arrayOfNulls(requestPermissionList.size)),
                RQ_CODE_PERMISSION

            )
            return

        }

        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle(getString(R.string.permission_request_title))
            setMessage(getString(R.string.permission_request_message))
            setPositiveButton(getString(R.string.admit)){ dialogInterface, i ->
                ActivityCompat.requestPermissions(
                    this@EditActivity,
                    requestPermissionList.toArray(arrayOfNulls(requestPermissionList.size)),
                    RQ_CODE_PERMISSION)

            }
            setNegativeButton(getString(R.string.reject)){ dialogInterface, i ->
                Toast.makeText(this@EditActivity, getString(R.string.cannot_go_any_further),
                    Toast.LENGTH_SHORT).show()
                finish()
            }
            show()
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode != RQ_CODE_PERMISSION){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size <= 0) return

        for (i in 0.. permissions.size -1){
            when(permissions[i]){
                PERMISSION[0] -> {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this@EditActivity, getString(R.string.cannot_go_any_further),
                            Toast.LENGTH_SHORT).show()
                        finish()
                        return

                    }
                    isCameraEnabled = true
                }
                PERMISSION[1] -> {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this@EditActivity, getString(R.string.cannot_go_any_further),
                            Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    isWriteStorageEnabled = true
                }
                PERMISSION[2] -> {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this@EditActivity, getString(R.string.cannot_go_any_further),
                            Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    isLocationAccessEnabled = true
                }
            }

        }
        if (isCameraEnabled && isWriteStorageEnabled && isLocationAccessEnabled) launchCamera() else finish()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply {
            findItem(R.id.action_settings).isVisible = true
            findItem(R.id.action_share).isVisible = false
            findItem(R.id.action_comment).isVisible = false
            findItem(R.id.action_delete).isVisible = true
            findItem(R.id.action_edit).isVisible = false
            findItem(R.id.action_camera).isVisible= mode == ModeInEdit.SHOOT

        }
        return true


    }

}
