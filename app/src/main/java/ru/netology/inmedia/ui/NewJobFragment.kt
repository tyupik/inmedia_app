package ru.netology.inmedia.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentNewJobBinding
import ru.netology.inmedia.model.EventFeedModelState
import ru.netology.inmedia.model.JobFeedModelState
import ru.netology.inmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.inmedia.viewmodel.JobViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.widget.DatePicker

import ru.netology.inmedia.MainActivity
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg


@AndroidEntryPoint
class NewJobFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_post_menu, menu)
    }

    private var fragmentBinding: FragmentNewJobBinding? = null
    private var startDateTime = 0L
    private var finishDateTime = 0L

    private val viewModel: JobViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if (fragmentBinding?.start?.text?.isBlank() == true ||
                    fragmentBinding?.position?.text?.isBlank() == true ||
                    fragmentBinding?.name?.text?.isBlank() == true
                ) {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_empty_job_fields,
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
                fragmentBinding?.let {
                    if (fragmentBinding?.finish?.text?.isBlank() == true) {
                        viewModel.changeContent(
                            startDateTime,
                            it.name.text.toString(),
                            it.position.text.toString(),
                            0L
                        )
                    } else {
                        viewModel.changeContent(
                            startDateTime,
                            it.name.text.toString(),
                            it.position.text.toString(),
                            finishDateTime,
                        )
                    }
                    viewModel.save()
                    requireView().hideKeyboard()
                    if (viewModel.dataState.value == JobFeedModelState(loading = true)) {
                        findNavController().navigateUp()
                    } else {
                        print(viewModel.dataState.value)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val START_KEY = "START_KEY"
        private const val FINISH_KEY = "FINISH_KEY"
        private const val NAME_KEY = "NAME_KEY"
        private const val POSITION_KEY = "POSITION_KEY"

        var Bundle.startArg: Long
            set(value) = putLong(START_KEY, value)
            get() = getLong(START_KEY)

        var Bundle.finishArg: Long
            set(value) = putLong(FINISH_KEY, value)
            get() = getLong(FINISH_KEY)

        var Bundle.nameArg: String?
            set(value) = putString(NAME_KEY, value)
            get() = getString(NAME_KEY)

        var Bundle.positionArg: String?
            set(value) = putString(POSITION_KEY, value)
            get() = getString(POSITION_KEY)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )

        fragmentBinding = binding
        val date = Calendar.getInstance()

        arguments?.nameArg?.let (binding.name::setText)
        arguments?.positionArg?.let (binding.position::setText)
        arguments?.startArg?.let { start ->
            binding.start.text = convertDate(start)
            startDateTime = start
        }
        arguments?.finishArg?.let { finish ->
            if (finish == 0L) {
                binding.finish.text = ""
            } else {
                binding.finish.text = convertDate(finish)
                finishDateTime = finish
            }
        }

        binding.start.setOnClickListener {
            val currentDate = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    date.set(year, monthOfYear, dayOfMonth)
                    val myFormat = "MM.yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.ROOT)
                    binding.start.text = sdf.format(date.time)
                    startDateTime = date.timeInMillis
                },
                currentDate[Calendar.YEAR],
                currentDate[Calendar.MONTH],
                currentDate[Calendar.DATE]
            ).show()
        }



        binding.finish.setOnClickListener {
            val currentDate = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    date.set(year, monthOfYear, dayOfMonth)
                    val myFormat = "MM.yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.ROOT)
                    binding.finish.text = sdf.format(date.time)
                    finishDateTime = date.timeInMillis
                },
                currentDate[Calendar.YEAR],
                currentDate[Calendar.MONTH],
                currentDate[Calendar.DATE]
            ).show()
        }

        binding.cancelStartDate.setOnClickListener {
            binding.start.text = ""
        }
        binding.cancelFinishDate.setOnClickListener {
            binding.finish.text = ""
        }



        return binding.root
    }

    private fun convertDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MM.yyyy")
        return format.format(date)
    }


}