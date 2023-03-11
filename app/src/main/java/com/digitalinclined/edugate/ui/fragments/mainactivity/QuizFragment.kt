package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.QuizMainAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentQuizBinding
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.digitalinclined.edugate.models.QuizDataClassRoigh
import com.digitalinclined.edugate.models.quizzesmodel.QuizDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class QuizFragment : Fragment() {

    // TAG
    private val TAG = "QuizFragment"

    // binding
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // Adapters
    lateinit var recyclerAdapter: QuizMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(layoutInflater,container,false)

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

            // set up recycler view
            setupRecyclerView()

            // fetch quizzes
            fetchQuizData()
        }
    }

    // fetch quiz data from Json
    private fun fetchQuizData() {
        binding.apply {
            var gson = Gson()

            try {
                val inputStream: InputStream = requireActivity().assets.open("quizzes.json")
                var json: String? = inputStream.bufferedReader().use { it.readText() }

                var testModel = gson.fromJson(json, QuizDataClass::class.java)

                Log.d(TAG,testModel.totalQuizzes.toString())

                if(testModel.quizzes.isNotEmpty()) {
                    recyclerAdapter.differ.submitList(testModel.quizzes)
                } else {
                    Snackbar.make(binding.root,"No quiz present at a moment!",Snackbar.LENGTH_SHORT).show()
                }

                progressBar.visibility = View.GONE
            }catch (e: IOException) {
                Log.d(TAG,e.message.toString())
                Snackbar.make(binding.root,"Failed to load quiz!",Snackbar.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }

        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = QuizMainAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener {
                val bundle = bundleOf(
                    "quizze" to it,
                    "fromFragment" to "quizFragment"
                )
                findNavController().navigate(R.id.action_quizFragment_to_quizPerformingFragment,bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}