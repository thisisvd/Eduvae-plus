package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.NotificationAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentNotificationBinding
import com.digitalinclined.edugate.models.BranchListDataClass
import com.digitalinclined.edugate.models.NotificationDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class NotificationFragment : Fragment() {

    // TAG
    private val TAG = "NotificationFragment"

    // viewBinding
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: NotificationAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    // dialog vars
    private lateinit var notifyTime: TextView
    private lateinit var notifyTitle: TextView
    private lateinit var notifyContent: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Notifications"

        // toggle setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // recycler init
            setupRecyclerView()

            // fetch-items
            fetchNotifyItemsFromFirebase()

        }
    }

    // change notify dialog details
    private fun showNotifyDialogDetails(dataClass: NotificationDataClass) {
        binding.apply {
            AlertDialog.Builder(requireContext())
                .setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .setTitle(dataClass.title)
                .setMessage(dataClass.content)
                .create().show()
        }
    }

    // fetch notify & items list data from firebase
    private fun fetchNotifyItemsFromFirebase() {
        var notifyList = ArrayList<NotificationDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            if (!Constants.USER_CURRENT_COURSE.isNullOrEmpty()) {
                Firebase.firestore.collection("notificationsAndAlerts").get()
                    .addOnSuccessListener { documentResult ->
                        if (documentResult != null) {
                            Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                            for (document in documentResult) {
                                val dataClass = document.toObject(NotificationDataClass::class.java)!!
                                notifyList.add(dataClass)
                            }
                            Log.d(TAG, "List size : ${notifyList.size}")
                            if (notifyList.isNotEmpty()) {
                                notifyList.reverse()
                                recyclerAdapter.differ.submitList(notifyList)
                            } else {
                                Snackbar.make(
                                    binding.root,
                                    "No Notification & alerts found!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            binding.progressBar.visibility = View.GONE
                        }
                    }.addOnFailureListener { e ->
                        Log.d(TAG, "Error adding document", e)
                        Snackbar.make(binding.root, "Error occurred!", Snackbar.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }
            }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = NotificationAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
            recyclerAdapter.apply {
                setOnItemClickListener { value ->
                    showNotifyDialogDetails(value)
                }
            }
        }
    }

}