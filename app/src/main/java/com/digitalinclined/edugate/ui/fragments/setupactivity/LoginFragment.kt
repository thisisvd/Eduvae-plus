package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentLoginBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment: Fragment(R.layout.fragment_login) {

    // TAG
    private val TAG = "LoginFragment"

    // viewBinding
    private lateinit var binding: FragmentLoginBinding

    // temp login first time open variable
    private var isOpenFirstTime = true

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
        binding = FragmentLoginBinding.bind(view)

        // initialize firebase
        firebaseAuth = FirebaseAuth.getInstance()

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

            // verify button listener
            verifyWithOTP.setOnClickListener {
                onVerificationClicked()
            }

            // navigate to signup screen
            signUP.setOnClickListener {
                findNavController().navigate(
                    R.id.action_loginFragment_to_signUpScreenFragment
                )
            }

            // google login
            googleVerification.setOnClickListener {
                dialog.show()
                val intent = googleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }

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
                    dialog.dismiss()
                    dialogForNewUser(user)
                } else {
                    dialog.dismiss()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }

            }.addOnFailureListener { exception ->
                Log.d(TAG_GOOGLE_SIGN_IN, "signInWithCredential:failure", exception)
                dialog.dismiss()
            }
    }

    // dialog to check for account
    private fun dialogForNewUser(user: FirebaseUser?){
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Account don't Exists!")
                .setMessage("No Account exists with this email. " +
                        "Do you want to create a new account with this email?")
            setPositiveButton("Create") { _,_ ->
                dialog.show()
                createNewAccount(user!!.displayName,user!!.email,user!!.phoneNumber,user!!.photoUrl.toString())
            }
            setNegativeButton("Go back") { _,_ ->
                // signing out the authenticated user via (LOGIN)
                firebaseAuth.currentUser!!.delete().addOnSuccessListener {
                    Log.d(TAG, "USER NOT FOUND HENCE DELETED!")
                }
                findNavController().navigate(R.id.onBoardingScreenFragment)
            }
            setCancelable(false)
        }.show()
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
            "profilephotolink" to (photoUrlLink ?: "")
        )

        // create db in fireStore
        dbReference.document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_loginFragment_to_completeProfileFragment)
                dialog.dismiss()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                dialog.dismiss()
            }
    }
    
    // on verification button clicked
    private fun onVerificationClicked() {

        binding.apply {

            // check for layout visibility
            if(phoneNumberLayout.isVisible && !isTextEmpty()){

                val bundle = Bundle().apply {
                    putString("fragment", "login")
                    putString("phone",phoneNumber.text.toString())
                }
                findNavController().navigate(R.id.action_loginFragment_to_OTPFragment, bundle)

            } else {
                if(isOpenFirstTime) {
                    // Animate loginLinearLayout
                    ObjectAnimator.ofFloat(loginLinearLayout, "translationY", 222f).apply {
                        duration = 800
                        start()
                    }

                    // Animate phone number Layout
                    val fadeInAnimation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_down)
                    phoneNumberLayout.startAnimation(fadeInAnimation)
                    phoneNumberLayout.visibility = View.VISIBLE

                    isOpenFirstTime = false
                }
            }

        }

    }

    // check for empty edit texts
    private fun isTextEmpty(): Boolean {
        var isTextEmpty = false

        binding.apply {

            // Activate the edittext listener's
            phoneNumber.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    phoneNumberEdittextLayout.error = "*Phone number can't be empty!"
                } else {
                    phoneNumberEdittextLayout.error = null
                }
            }

            // checks for empty or null
            if (phoneNumber.text.isNullOrEmpty()) {
                isTextEmpty = true
                phoneNumberEdittextLayout.error = "*Phone number can't be empty!"
            } else if(phoneNumber.text.toString().length < 10) {
                isTextEmpty = true
                phoneNumberEdittextLayout.error = "*Please enter a valid phone number!"
            }

            return isTextEmpty
        }
    }


}