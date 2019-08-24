package app.actionmobile.notepix

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_item_detail.*
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.android.synthetic.main.item_detail.*
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [ItemListActivity].
 */
class ItemDetailActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        setSupportActionBar(detail_toolbar)

        takePic.setOnClickListener { view ->
            //setUpImageFile()
            dispatchTakePictureIntent()
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        ItemDetailFragment.ARG_ITEM_ID,
                        intent.getStringExtra(ItemDetailFragment.ARG_ITEM_ID)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                navigateUpTo(Intent(this, ItemListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun grabImage(){
        this.contentResolver.notifyChange(mImageUri, null)
        val cr = this.contentResolver
        val bitmap: Bitmap
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri)
            mainImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            Log.i("Main", "Failed to load", e)
        }

    }

    val REQUEST_IMAGE_CAPTURE = 1
    var photoFile : File? = null

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.i("Main", "Failed to create file.")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "app.actionmobile.notepix.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //val imageBitmap = data!!.extras.get("data") as Bitmap
            //mainImageView.setImageBitmap(imageBitmap)
            var myBitmap = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath());
            //setPic()
            Log.i("Main", " ${mainImageView.height} x ${mainImageView.width}")
            mainImageView.setImageBitmap(myBitmap)
        }
    }
    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = mainImageView.width
        val targetH: Int = mainImageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            mainImageView.setImageBitmap(bitmap)
        }
    }


    var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }



//        private fun dispatchTakePictureIntent() {
//        val photoPickerIntent = Intent(Intent.ACTION_PICK)
//        photoPickerIntent.type = "image/*"
//        startActivityForResult(photoPickerIntent, 1)
//
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (takePictureIntent.resolveActivity(packageManager) != null) {
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo!!.path)//"data/data/app.actionmobile.notepix/files/seconds.png")
//            Log.i("Main","test")
//            startActivityForResult(takePictureIntent, 0)
//        }
//    }
    var photo: File? = null
    var mImageUri: Uri? = null

    private fun setUpImageFile(){


        try {
            // place where to store camera taken picture
            photo = createTemporaryFile("seconds", ".jpg")
            photo!!.delete()
        } catch (e: Exception) {
            Log.i("Main", "Can't create file to take picture!")
            Toast.makeText(this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show()
        }

        mImageUri = Uri.fromFile(photo)
        Log.i("Main", "mImageUri.path : ${mImageUri!!.path}")
    }

    private fun createTemporaryFile(part: String, ext : String) : File {
        var tempDir = Environment.getExternalStorageDirectory();
        tempDir = File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
//
//            //var imgFile = ;
//            if(photo!!.exists()) {
//                var myBitmap = BitmapFactory.decodeFile(photo!!.getAbsolutePath());
//
//                data!!.getData()
//
//                mainImageView.setImageBitmap(myBitmap);
//            }
//            else{
//                Log.i("Main", "Image file doesn't exist.")
//            }
////            grabImage();
//            val extras = data!!.extras
//            imageBitmap = extras!!.get("data") as Bitmap
//            var path = android.os.Environment.DIRECTORY_DCIM
//            Log.i("Main",path)
//
//            Log.i("Main",imageBitmap.height.toString())
//            Log.i("Main",imageBitmap.width.toString())
//            mainImageView.setImageBitmap(imageBitmap)
//            val outfile = "seconds.png"
//            path = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/${outfile}"
//            Log.d("Main", path)
//            val file =
//                File(path)
//
//            try {
//                val fos = applicationContext.openFileOutput(outfile, Context.MODE_PRIVATE)
//
//// Writing the bitmap to the output stream
//                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//                fos.flush()
//                fos.close()
//
//
////                var outStream = FileOutputStream(file)
////                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
////
////                outStream.close()
//                Toast.makeText(this,file.exists().toString(), Toast.LENGTH_LONG ).show()
//                Log.d("Main", file.exists().toString())
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//        if (requestCode == 1 && resultCode === Activity.RESULT_OK) {
//            try {
//                val imageUri = data!!.getData()
//                val imageStream = contentResolver.openInputStream(imageUri)
//                val selectedImage = BitmapFactory.decodeStream(imageStream)
//                mainImageView.setImageBitmap(selectedImage)
//                Log.i("Main",selectedImage.height.toString())
//                Log.i("Main",selectedImage.width.toString())
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//                //Toast.makeText(this@PostImage, "Something went wrong", Toast.LENGTH_LONG).show()
//            }
//
//        } else {
//           // Toast.makeText(this@PostImage, "You haven't picked Image", Toast.LENGTH_LONG).show()
//        }
//    }

    companion object{
        lateinit var imageBitmap : Bitmap
    }
}
