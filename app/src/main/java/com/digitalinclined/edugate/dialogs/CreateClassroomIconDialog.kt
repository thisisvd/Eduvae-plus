package com.digitalinclined.edugate.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.CreateClassroomIconDialogBinding

class CreateClassroomIconDialog: DialogFragment() {

    // TAG
    private val TAG = "OpenClassroomFragment"

    // binding
    private var _binding: CreateClassroomIconDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateClassroomIconDialogBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // on back pressed handle
            back.setOnClickListener {
                dialog!!.dismiss()
            }

            classIcon1.setOnClickListener {
                changeAvatarID(R.drawable.classroom_icon1)
            }

            classIcon2.setOnClickListener {
                changeAvatarID(R.drawable.classroom_icon2)
            }

            classIcon3.setOnClickListener {
                changeAvatarID(R.drawable.classroom_icon3)
            }

            classIcon4.setOnClickListener {
                changeAvatarID(R.drawable.classroom_icon4)
            }

        }
    }

    // change const avatar id
    private fun changeAvatarID(id: Int){
        dialog!!.dismiss()
        onItemClickListener?.let {
            it(id)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    // On click listener
    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}