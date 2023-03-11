package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.quizSubmissionObserver
import com.digitalinclined.edugate.databinding.FragmentQuizPerformingBinding
import com.digitalinclined.edugate.models.quizzesmodel.QuizSubmissionDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizPerformingFragment : Fragment() {

    // binding
    private var _binding: FragmentQuizPerformingBinding? = null
    private val binding get() = _binding!!

    // args
    private val args: QuizPerformingFragmentArgs by navArgs()

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // global questions counts
    private var selectedOption: String = ""
    private var counter: Int = 0
    private var correctAnswerCounter: Int = 0
    private var correctOption: String = ""

    // class id
    private var classID = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizPerformingBinding.inflate(layoutInflater,container,false)

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Quiz"
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // click listeners
            setUpClickListeners()

            // fetch quiz
            if(args != null) {
                fetchQuizFromServer()
            }
        }
    }

    private fun fetchQuizFromServer() {
        binding.apply {

            // un-selecting all radio button
            for (i in 0 until quizRadioGroup.childCount) {
                (quizRadioGroup.getChildAt(i) as RadioButton).isChecked = false

//                (quizRadioGroup.getChildAt(i) as RadioButton).isSelected = false
            }

            // remove selected option
            selectedOption = ""

            if (counter < args.quizze.quizTotalNumbers) {
                // update question number
                questionNumberTv.text = "Question ${counter + 1}/${args.quizze.quizTotalNumbers}"

                // update question
                questionTv.text = args.quizze.quizQuestions[counter].question

                // update options
                for (item in args.quizze.quizQuestions[0].options!!) {
                    for (i in 0 until quizRadioGroup.childCount) {
                        (quizRadioGroup.getChildAt(i) as RadioButton).text =
                            args.quizze.quizQuestions[counter].options?.get(i) ?: ""
                    }
                }

                // update correct option
                correctOption =
                    args.quizze.quizQuestions[counter].answer?.let {
                        args.quizze.quizQuestions[counter].options?.get(
                            it
                        )
                    } ?: ""
            }
        }
    }

    private fun setUpClickListeners() {
        binding.apply {

            // radio click listener
            quizRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
                binding.root.findViewById<RadioButton>(i).apply {
                    selectedOption = this.text.toString()
                }
            }

            // next listener
            nextButton.setOnClickListener {

                if ((counter+1) == (args.quizze.quizTotalNumbers - 1)) {
                    nextButton.text = "Submit"
                }

                if (counter < (args.quizze.quizTotalNumbers-1)) {
                    if(selectedOption != "") {
                        if (selectedOption == correctOption) {
                            correctAnswerCounter++
                        }
                        counter++
                        fetchQuizFromServer()
                    } else {
                        Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    makeSubmitDialog()
                }
            }
        }
    }

    // make submit dialog
    private fun makeSubmitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Quiz result")
            setMessage("Your score is ${correctAnswerCounter+1} out of ${args.quizze.quizTotalNumbers}.")
            setCancelable(false)
            setPositiveButton("OK"){ dialog, _ ->
                dialog.dismiss()
                if (args.fromFragment == "openClassroom") {
                    quizSubmissionObserver.postValue(QuizSubmissionDataClass(true,correctAnswerCounter+1, args.quizze.quizTotalNumbers))
                    findNavController().popBackStack()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}