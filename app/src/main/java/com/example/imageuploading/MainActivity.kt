package com.example.imageuploading

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PICK_IMAGE_REQUEST = 1

    private var filePath: Uri? = null
    internal var storage:FirebaseStorage?=null
    internal var storageReference:StorageReference?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        Choose.setOnClickListener(this)
        Upload.setOnClickListener(this)
    }

    override fun onClick(image: View?) {
        if(image==Choose)
            showFileChoser()
        else if (image == Upload)
            uploadFile()
    }

    private fun uploadFile() {
        if(filePath!= null){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading")
            progressDialog.show()

            val imageRef = storageReference!!.child("images/"+UUID.randomUUID().toString())
//            val imageRef = FirebaseStorage.getInstance().reference.child("images/image.jpg")

           // val imageRef = FirebaseStorage.getInstance().reference.child("images/image.jpg")
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"File Upload - SUCCESS", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"File Upload - FAIL", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapShot ->
                    val progress = 100.0 * taskSnapShot.bytesTransferred/taskSnapShot.totalByteCount
                    progressDialog.setMessage("Uploaded"+ " " + progress.toInt()+"%...")

                }

        }
    }

    private fun showFileChoser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data!= null && data.data!=null){
            filePath = data.data
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
                image!!.setImageBitmap(bitmap)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

//    //Auth Error
//    private var auth: FirebaseAuth = Firebase.auth
//// ...
//// Initialize Firebase Auth
//public override fun onStart() {
//    super.onStart()
//    // Check if user is signed in (non-null) and update UI accordingly.
//    val currentUser = auth.currentUser
//    updateUI(currentUser)
//}
//    auth.signInAnonymously()
//        .addOnCompleteListener(this) { task ->
//        if (task.isSuccessful) {
//            // Sign in success, update UI with the signed-in user's information
//            Log.d(TAG, "signInAnonymously:success")
//            val user = auth.currentUser
//            updateUI(user)
//        } else {
//            // If sign in fails, display a message to the user.
//            Log.w(TAG, "signInAnonymously:failure", task.exception)
//            Toast.makeText(baseContext, "Authentication failed.",
//                    Toast.LENGTH_SHORT).show()
//            updateUI(null)
//        }
//
//        // ...
//    }
}


