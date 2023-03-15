package com.digitalinclined.edugate.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.CreateClassroomIconDialogBinding

class CreateClassroomIconDialog : DialogFragment() {

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
        _binding = CreateClassroomIconDialogBinding.inflate(layoutInflater, container, false)
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
                changeAvatarID(
                    R.drawable.classroom_icon1,
                    "https://firebasestorage.googleapis.com/v0/b/fitme-minor-project.appspot.com/o/images%2F6NMEfmmnwjPEoIA8mYCC1678567312786.jpg?alt=media&token=6feb83b6-db5e-437e-964b-9767650dd305"
                )
            }

            classIcon2.setOnClickListener {
                changeAvatarID(
                    R.drawable.classroom_icon2,
                    "https://firebasestorage.googleapis.com/v0/b/fitme-minor-project.appspot.com/o/images%2F6NMEfmmnwjPEoIA8mYCC1678567390295.jpg?alt=media&token=096a60f4-453f-406d-8355-9b7e2e0b8059"
                )
            }

            classIcon3.setOnClickListener {
                changeAvatarID(
                    R.drawable.classroom_icon3,
                    "https://firebasestorage.googleapis.com/v0/b/fitme-minor-project.appspot.com/o/images%2F6NMEfmmnwjPEoIA8mYCC1678567445505.jpg?alt=media&token=99541eb5-6090-4fff-8e54-54e4950006c0"
                )
            }

            classIcon4.setOnClickListener {
                changeAvatarID(
                    R.drawable.classroom_icon4,
                    "https://firebasestorage.googleapis.com/v0/b/fitme-minor-project.appspot.com/o/images%2F6NMEfmmnwjPEoIA8mYCC1678567262065.jpg?alt=media&token=559f9bf4-0606-4905-8982-8e0e926750f0"
                )
            }

        }
    }

    // change const avatar id
    private fun changeAvatarID(id: Int, link: String) {
        dialog!!.dismiss()
        onItemClickListener?.let {
            it(id, link)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    // On click listener
    private var onItemClickListener: ((Int, link: String) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int, link: String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}