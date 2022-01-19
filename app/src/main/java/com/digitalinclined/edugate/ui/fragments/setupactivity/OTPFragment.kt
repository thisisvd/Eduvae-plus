package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentOtpBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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

    // progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // init args values that have been passed
        recentFragment = args.fragment
        phoneNumber = "+91${args.phone}"

        binding.apply {

            // progress init
            progressDialog = ProgressDialog(requireContext())
            progressDialog.apply {
                setTitle("Please wait")
                setCanceledOnTouchOutside(false)
            }

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
            verifyButton.setOnClickListener{
                val code = otpEdittext.text.toString().trim()
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
        }
    }

    // Setting up the phone code callback
    private fun firebasePhoneCallback() {
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG,"OnVerificationComplete")

                val code = phoneAuthCredential.smsCode.toString()
                binding.otpEdittext.setText(code)

                signInWIthPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.d(TAG,"VerificationFailed : ${e.message}")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG,"onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()
                Log.d(TAG,"OnCodeSent Successfully with ID : $verificationId")
            }
        }
    }

    // start Phone verification and send the code first time
    private fun startPhoneNumberVerification(phone: String) {
        Log.d(TAG,"startPhoneNumberVerification $phone")

        // progress update
        progressDialog.apply {
            setMessage("Verifying Phone Number...")
            show()
        }

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

        // progress update
        progressDialog.apply {
            setMessage("Resending Code...")
            show()
        }

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

        progressDialog.setMessage("Logging IN")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                // login success
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser!!.phoneNumber

                checkForAccount()

            }
            .addOnFailureListener { e ->
                // login failed
                progressDialog.dismiss()
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
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                            isAccountExistsForLogin = true
                        }

                        // CODE TO BE RUN IF RECENT FRAGMENT IS SIGN UP
                        if(recentFragment == "signUP") {
                            // User already have an account in db
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
            "profilephotolink" to ""
        )

        // create db in fireStore
        dbReference.document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_OTPFragment_to_completeProfileFragment)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}