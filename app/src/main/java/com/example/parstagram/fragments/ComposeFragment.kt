package com.example.parstagram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parstagram.Post
import com.example.parstagram.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var etDescription: EditText
    lateinit var btnTakePicture: Button
    lateinit var ivPicture: ImageView
    lateinit var btnSubmit: Button
    lateinit var pbLoading: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etDescription = view.findViewById(R.id.etDescription)
        btnTakePicture = view.findViewById(R.id.btnTakePicture)
        ivPicture= view.findViewById(R.id.ivPicture)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)

        // Set onClickListeners and set up logic

        // 1. Setting the description of the post
        // 2. A button to launch the camera to take a picture
        // 3. An ImageView to show the picture the user has taken
        // 4. A button to save and send the post to our Parse server

        btnSubmit.setOnClickListener {
            // send post to server without an image
            // Get the description that they have inputted
            val description = etDescription.text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(TAG, "No photo provided")
                Toast.makeText(requireContext(), "An image must be included in post", Toast.LENGTH_SHORT).show()
            }
        }

        btnTakePicture.setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }
    }

    // Send a Post object to our Parse server
    fun submitPost(description: String, user: ParseUser, file: File) {
        // Create the Post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            if (exception != null) {
                // Something has gone wrong
                Log.e(TAG, "Error while saving post")
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error submitting post", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Successfully saved post")
                Toast.makeText(requireContext(), "Successfully saved post", Toast.LENGTH_SHORT).show()
                etDescription.setText("")
                ivPicture.setImageDrawable(null)
            }
        }
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                pbLoading.visibility = ProgressBar.VISIBLE
                ivPicture.setImageBitmap(takenImage)
                pbLoading.visibility = ProgressBar.INVISIBLE
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "ComposeFragment"
    }
}