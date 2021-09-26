package ru.netology.inmedia.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentNewEventBinding
import ru.netology.inmedia.model.EventFeedModelState
import ru.netology.inmedia.model.PhotoModel
import ru.netology.inmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.inmedia.viewmodel.EventsViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.widget.*
import com.bumptech.glide.Glide
import ru.netology.inmedia.ui.NewPostFragment.Companion.photoArg
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localDate





@AndroidEntryPoint
class NewEventFragment : Fragment() {
    private val photoRequestCode = 1
    private val cameraRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_post_menu, menu)
    }

    private var fragmentBinding: FragmentNewEventBinding? = null
    private var photoValue = PhotoModel(null)
    private var itemType = ""
    private var dateTime = ""


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if (fragmentBinding?.edit?.text?.isBlank() == true ||
                    fragmentBinding?.dateTimeET?.text?.isBlank() == true
                ) {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_empty_content,
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
                fragmentBinding?.let {
                    viewModel.changeContent(
                        it.edit.text.toString(),
                        dateTime,
                        itemType
                    )
                    viewModel.save(photoValue)
                    requireView().hideKeyboard()
                    if (viewModel.dataState.value == EventFeedModelState(loading = true)) {
                        findNavController().previousBackStackEntry
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
        private const val TEXT_KEY = "TEXT_KEY"
        private const val DATETIME_KEY = "DATETIME_KEY"
        private const val TYPE_KEY = "TYPE_KEY"
        private const val PHOTO_KEY = "PHOTO_KEY"

        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)

        var Bundle.datetimeArg: String?
            set(value) = putString(DATETIME_KEY, value)
            get() = getString(DATETIME_KEY)

        var Bundle.typeArg: String?
            set(value) = putString(TYPE_KEY, value)
            get() = getString(TYPE_KEY)

        var Bundle.photoArg: String?
            set(value) = putString(PHOTO_KEY, value)
            get() = getString(PHOTO_KEY)

    }

    private val viewModel: EventsViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg?.let(binding.edit::setText)
        arguments?.datetimeArg?.let{
            dateTime = it
            binding.dateTimeET.text =
                DateTimeFormatter.ofPattern("dd.MM.yyyy 'в' HH:mm ")
                .withZone(ZoneId.of("Europe/Moscow"))
                .format(Instant.parse(it))
        }

        arguments?.photoArg?.let { url->
            val uri = Uri.parse(url)
            viewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
        }


        fragmentBinding = binding


        val date = Calendar.getInstance()
        fun showDateTimePicker() {
            val currentDate = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    date.set(year, monthOfYear, dayOfMonth)
                    val myFormat = "dd.MM.yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            date.set(Calendar.MINUTE, minute)
                            val myTimeFormat = "HH:mm"
                            val sdfTime = SimpleDateFormat(myTimeFormat, Locale.ROOT)
                            binding.dateTimeET.text = sdf.format(date.time) +"в" + sdfTime.format(date.time)
                            val date = sdf.format(date.time) + " " + sdfTime.format(date.time)
                            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
                                "dd.MM.yyyy HH:mm",
                                Locale.ROOT
                            )
                            dateTime = LocalDateTime.parse(date, formatter).toInstant(OffsetDateTime.now().offset).toString()
                        }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], true
                    ).show()
                },
                currentDate[Calendar.YEAR],
                currentDate[Calendar.MONTH],
                currentDate[Calendar.DATE]
            ).show()
        }

        binding.dateTimeET.setOnClickListener {
            showDateTimePicker()
        }


        val itemList = arrayOf("ONLINE", "OFFLINE")

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
        binding.eventTypeET.adapter = arrayAdapter


        binding.eventTypeET.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (arguments?.typeArg != null) {
                    arguments?.typeArg?.let { type ->
                        if (type == "ONLINE"){
                            binding.eventTypeET.setSelection(0)
                            itemType = parent?.getItemAtPosition(0) as String
                        } else {
                            binding.eventTypeET.setSelection(1)
                            itemType = parent?.getItemAtPosition(1) as String
                        }
//                        itemType = type
                        arguments?.typeArg = null
                    }
                } else itemType = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
            photoValue = PhotoModel(null)
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)

            Glide.with(binding.photo)
                .load("${it.uri}")
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.photo)
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            viewModel.loadEvents()
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
            viewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            viewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
            return
        }
    }
}

