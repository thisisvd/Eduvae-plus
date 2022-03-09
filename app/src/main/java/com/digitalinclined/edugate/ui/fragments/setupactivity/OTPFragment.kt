package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ProgressButton
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentOtpBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPFragment: Fragment(R.layout.fragment_otp) {

    // TAG
    private val TAG = "OTPFragment"

    // getting arguments as passed
    private val args: OTPFragmentArgs by navArgs()

    // last recent fragment coming from
    private lateinit var recentFragment: String
    private lateinit var phoneNumber: String

    // viewBinding
    private lateinit var binding: FragmentOtpBinding

    // firebase init
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null

    // firebase auth & user
    private var firebaseAuth = FirebaseAuth.getInstance()

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

    // alert progress dialog
    private lateinit var dialog: Dialog

    // progress Button
    private lateinit var progressButton: ProgressButton
    private lateinit var viewProgress: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure the google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1011062373162-2b0g10j8h2kpf5m7n8a565hagdl3fhg5.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // init args values that have been passed
        recentFragment = args.fragment
        phoneNumber = "+91${args.phone}"

        binding.apply {

            // init Loading Dialog
            dialog = Dialog(requireContext())
            dialog.apply {
                setContentView(R.layout.custom_dialog)
                setCancelable(false)
                if(dialog.window != null){
                    dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
                }
            }

            // init progress bar button
            viewProgress = binding.root.findViewById(R.id.progressBarButton)
            progressButton = ProgressButton(requireContext(),viewProgress)
            progressButton.setBtnOriginalName("Verify")

            // Hiding the google/facebook sign in layout
            if(recentFragment == "signUP") {
                linearLayout.visibility = View.GONE
            }

            // callbacks
            firebasePhoneCallback()

            // start verification process (i.e, request for code / send code)
            startPhoneNumberVerification(phoneNumber)

            // navigate to signup screen
            signUP.setOnClickListener {
                findNavController().navigate(R.id.action_OTPFragment_to_signUpScreenFragment)
            }

            // resend code
            resend.setOnClickListener {
                resendVerificationCode(phoneNumber, forceResendingToken!!)
            }

            // code verify with input code
            viewProgress.setOnClickListener{
                val code = otpView.text.toString().trim()
                if(TextUtils.isEmpty(code)) {
                    // Snack bar on empty OTP
                    Snackbar.make(binding.root ,
                        "Please enter verification code!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, code)
                }
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
                firebaseAuth.signOut()
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
            "semester" to "",
            "profilephotolink" to (photoUrlLink ?: ""),
            "following" to arrayListOf<String>()
        )

        // create db in fireStore
        dbReference.document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_OTPFragment_to_completeProfileFragment)
                dialog.dismiss()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                dialog.dismiss()
            }
    }

    // Setting up the phone code callback
    private fun firebasePhoneCallback() {
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG,"OnVerificationComplete")

                val code = phoneAuthCredential.smsCode.toString()
                binding.otpView.setText(code)

                signInWIthPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dialog.dismiss()
                Log.d(TAG,"VerificationFailed : ${e.message}")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG,"onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                dialog.dismiss()
                Log.d(TAG,"OnCodeSent Successfully with ID : $verificationId")
            }
        }
    }

    // start Phone verification and send the code first time
    private fun startPhoneNumberVerification(phone: String) {
        Log.d(TAG,"startPhoneNumberVerification $phone")

        // progress update
        dialog.show()

        // code send
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    // re-sending code verification
    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken) {
        Log.d(TAG,"resendVerificationCode $phone")

        // progress show
        dialog.show()

        // code resend
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBacks!!)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // verify phone number with manually typed code
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String?){
        Log.d(TAG,"verifyPhoneNumberWithCode $verificationId : $code")

        // getting credentials
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code!!)
        signInWIthPhoneAuthCredential(credential)
    }

    // Signing-in with valid credential's
    private fun signInWIthPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG,":::::signInWIthPhoneAuthCredential:::::")

        // progress button activated
        progressButton.buttonActivated("Verifying...")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                // login success
                checkForAccount()
            }
            .addOnFailureListener { e ->
                // login failed
                progressButton.buttonFailed("Verify")
                Toast.makeText(requireContext(),"Wrong OTP Code!", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"${e.message}")
            }
    }

    // check for an account if it exists or not fireStore
    private fun checkForAccount() {
        // temp variable only be used for login checks
        var isAccountExistsForLogin = false
        // temp variable only be used for signup checks
        var isAccountExistsForSignUp = false

        // fetching data and performing operations
        dbReference.get().addOnCompleteListener { snapshot ->
            if(snapshot.isSuccessful){
                for(document in snapshot.result!!) {
                    if(document.id == firebaseAuth.currentUser?.uid) {

                        // CODE TO BE RUN IF RECENT FRAGMENT IS LOGIN
                        if(recentFragment == "login") {
                            // User already have an account in db
                            progressButton.buttonSuccessfullyFinished("Verified!")
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                            isAccountExistsForLogin = true
                        }

                        // CODE TO BE RUN IF RECENT FRAGMENT IS SIGN UP
                        if(recentFragment == "signUP") {
                            // User already have an account in db
                            progressButton.buttonSuccessfullyFinished("Verified!")
                            MaterialAlertDialogBuilder(requireContext()).apply {
                                setTitle("Account Already Exists!")
                                    .setMessage("Account with this number already exists. " +
                                            "Do you want to login with an existing account?")
                                setPositiveButton("Login") { _,_ ->
                                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                                    requireActivity().finish()
                                }
                                setNegativeButton("Go back") { _,_ ->
                                    // signing out the authenticated user via (LOGIN)
                                    firebaseAuth.signOut()
                                    findNavController().popBackStack()
                                }
                                setCancelable(false)
                            }.show()

                            isAccountExistsForSignUp = true
                        }

                    }
                }

                // CODE TO BE RUN IF RECENT FRAGMENT IS LOGIN
                if(recentFragment == "login") {
                    // If the OTP verified account doesn't exist in db
                    if (!isAccountExistsForLogin) {
                        progressButton.buttonFailed("Verify")
                        Snackbar.make(
                            binding.root,
                            "You don't have an account with this phone!",
                            Snackbar.LENGTH_LONG
                        ).show()
                        // signing out the authenticated user via (SIGNUP)
                        firebaseAuth.currentUser!!.delete().addOnSuccessListener {
                            Log.d(TAG, "USER NOT FOUND HENCE DELETED!")
                        }
                    }
                } else if (recentFragment == "signUP") {  // CODE TO BE RUN IF RECENT FRAGMENT IS SIGN UP
                    // User don't have an account in db
                    if (!isAccountExistsForSignUp) {
                        createNewAccount()
                    }
                }
            }
        }
    }

    // create a new account in fireStore for users [SIGN-UP CODE]
    private fun createNewAccount() {
        val user = hashMapOf(
            "name" to Constants.TEMP_CREATE_USER_NAME,
            "email" to Constants.TEMP_CREATE_USER_EMAIL,
            "phone" to firebaseAuth.currentUser?.phoneNumber,
            "course" to "",
            "year" to "",
            "city" to "",
            "semester" to "",
            "profilephotolink" to "",
            "following" to arrayListOf<String>()
        )

        // create db in fireStore
        dbReference.document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                progressButton.buttonSuccessfullyFinished("Verified!")
                findNavController().navigate(R.id.action_OTPFragment_to_completeProfileFragment)
            }
            .addOnFailureListener { e ->
                progressButton.buttonFailed("Verify")
                Log.w(TAG, "Error adding document", e)
            }
    }

}