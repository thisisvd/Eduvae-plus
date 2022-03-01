package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentOnBoardingScreenBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OnBoardingScreenFragment : Fragment(R.layout.fragment_on_boarding_screen) {

    // TAG
    private val TAG = "OnBoardingScreenFragment"

    // viewBinding
    private lateinit var binding: FragmentOnBoardingScreenBinding

    // temp onBackPressed count
    private var isBackPressedTwice = false

    // alert progress dialog
    private lateinit var dialog: Dialog

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase db instances
    private val db = Firebase.firestore
    private val dbReference = db.collection("users")

    // google client signIn
    private lateinit var googleSignInClient: GoogleSignInClient

    // constant for google sign-in
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG_GOOGLE_SIGN_IN = "GOOGLE_SING_IN_TAG"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnBoardingScreenBinding.bind(view)

        // initialize firebase
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }

        // init Loading Dialog
        dialog = Dialog(requireContext())
        dialog.apply {
            setContentView(R.layout.custom_dialog)
            setCancelable(false)
            if(dialog.window != null){
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
        }

        // Configure the google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1011062373162-2b0g10j8h2kpf5m7n8a565hagdl3fhg5.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.apply {

            // navigate to signup screen
            getStartedButton.setOnClickListener {
                isBackPressedTwice = false
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_signUpScreenFragment)
            }

            // navigate to login screen
            login.setOnClickListener {
                isBackPressedTwice = false
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_loginFragment)
            }

            // google login
            googleVerification.setOnClickListener {
//                Toast.makeText(requireContext(),"Google Button Clicked!",Toast.LENGTH_SHORT).show()
                dialog.show()
                val intent = googleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }

            // handle onBack Pressed
            onBack()
        }
    }

    // on activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG_GOOGLE_SIGN_IN, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_GOOGLE_SIGN_IN, "Google sign in failed : ${e.statusCode}", e)
                dialog.dismiss()
            }
        }
    }

    // signing with google auth
    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG_GOOGLE_SIGN_IN,"firebaseAuthWithGoogleAccount: begin firebase auth with google account.")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->

                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG_GOOGLE_SIGN_IN, "signInWithCredential:success")
                val user = firebaseAuth.currentUser

                if(authResult.additionalUserInfo!!.isNewUser){
                    Toast.makeText(requireContext(), "News User : ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    createNewAccount(user!!.displayName,user!!.email,user!!.phoneNumber,user!!.photoUrl.toString())
                } else {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                    dialog.dismiss()
                }

            }.addOnFailureListener { exception ->
                Log.w(TAG_GOOGLE_SIGN_IN, "signInWithCredential:failure", exception)
                dialog.dismiss()
            }
    }

    // create a new account in fireStore for users [SIGN-UP CODE]
    private fun createNewAccount(name: String? = null, email: String? = null, phone: String? = null, photoUrlLink: String? = null) {

        // user data
        val user = hashMapOf(
            "name" to (name ?: ""),
            "email" to (email ?: ""),
            "phone" to (phone ?: ""),
            "course" to "",
            "year" to "",
            "city" to "",
            "profilephotolink" to (photoUrlLink ?: ""),
            "following" to arrayListOf<String>()
        )

        // create db in fireStore
        dbReference.document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
//                progressButton.buttonSuccessfullyFinished("Verified!")
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_completeProfileFragment)
            }
            .addOnFailureListener { e ->
//                progressButton.buttonFailed("Verify")
                Log.w(TAG, "Error adding document", e)
            }
        dialog.dismiss()
    }

    // handle onBack pressed
    private fun onBack() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event

            if(isBackPressedTwice) {
                requireActivity().finish()
            }else {
                Toast.makeText(requireContext(),"Press back one more time to quit!",Toast.LENGTH_SHORT).show()
                isBackPressedTwice = true
            }

        }
    }

}


