package github.karchx.motto.views.notes

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import github.karchx.motto.R
import github.karchx.motto.ads.AdViewer
import github.karchx.motto.databinding.FragmentNotesBinding
import github.karchx.motto.models.date.DateManager
import github.karchx.motto.models.db.user_notes.UserNote
import github.karchx.motto.viewmodels.notes.SavedNotesViewModel
import github.karchx.motto.views.MainActivity
import github.karchx.motto.views.tools.adapters.SavedNotesRecyclerAdapter
import github.karchx.motto.views.tools.listeners.OnClickRecyclerItemListener
import github.karchx.motto.views.tools.managers.Arrow
import github.karchx.motto.views.tools.managers.Toaster

class NotesFragment : Fragment() {

    // Data
    private lateinit var notesViewModel: SavedNotesViewModel
    private var savedNotes: List<UserNote>? = null

    // Views
    private lateinit var mNotesBottomSheet: BottomSheetBehavior<FrameLayout>
    private lateinit var mSavedNotesTextView: TextView
    private lateinit var mSavedNotesRecyclerView: RecyclerView
    private lateinit var mSubmitNoteButton: Button
    private lateinit var mNoteQuoteTextInput: TextInputEditText
    private lateinit var mNoteSourceTextInput: TextInputEditText

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Arrow.hideBackArrow(activity as MainActivity)
        initData()
        initViews()

        observeSavedNotes()

        handleRecyclerScrollAction()
        handleSubmitNoteButton()
        handleNotesRecyclerItemClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveNote(note: UserNote) {
        notesViewModel.insertNote(note)
    }

    private fun observeSavedNotes() {
        notesViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            savedNotes = notes.reversed()
            displaySavedNotesRecycler(savedNotes!!)
        }
    }

    private fun displaySavedNotesRecycler(notes: List<UserNote>) {
        if (notes.isEmpty()) {
            mSavedNotesTextView.text = getString(R.string.no_saved_notes)
        } else {
            mSavedNotesTextView.text = getString(R.string.saved_notes)

            val adapter = SavedNotesRecyclerAdapter(notes)
            mSavedNotesRecyclerView.adapter = adapter
        }
    }

    private fun handleRecyclerScrollAction() {
        mSavedNotesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING)
                    if (dy > 0) {
                        mSavedNotesTextView.animate().translationY(1f)
                        mSavedNotesTextView.visibility = View.GONE
                    } else if (dy < 0) {
                        mSavedNotesTextView.animate().translationY(0f)
                        mSavedNotesTextView.visibility = View.VISIBLE
                    }
            }
        })
    }

    private fun handleSubmitNoteButton() {
        mSubmitNoteButton.setOnClickListener {
            val inputNoteQuote = mNoteQuoteTextInput.text?.trim().toString()
            var inputNoteSource = mNoteSourceTextInput.text?.trim().toString()

            if (inputNoteQuote != "") {
                if (inputNoteSource == "") {
                    inputNoteSource = getString(R.string.unknown_author)
                }
                saveNote(
                    UserNote(
                        id = 0,
                        quote = inputNoteQuote,
                        source = inputNoteSource,
                        dateSaved = DateManager().getCurrentDate()
                    )
                )

                Toaster.displayNoteAddedToast(requireContext(), isAdded = true)
                mNotesBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                hideKeyboard()
                mNoteQuoteTextInput.text?.clear()
                mNoteSourceTextInput.text?.clear()
            } else {
                Toaster.displayNoteAddedToast(requireContext(), isAdded = false)
            }

            displayFullNoteAd()
        }
    }

    private fun handleNotesRecyclerItemClick() {
        mSavedNotesRecyclerView.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), mSavedNotesRecyclerView, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                }

                override fun onItemLongClick(view: View, position: Int) {
                }
            })
        )
    }

    private fun displayFullNoteAd() {
        AdViewer(activity as MainActivity, requireContext()).displayFullNoteAd()
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initSavedNotesRecycler() {
        mSavedNotesRecyclerView = binding.recyclerviewSavedNotes
        val layoutManager = GridLayoutManager(context, 1)
        mSavedNotesRecyclerView.layoutManager = layoutManager
        mSavedNotesRecyclerView.setHasFixedSize(true)
    }

    private fun initBottomSheet() {
        mNotesBottomSheet = BottomSheetBehavior.from(binding.notesBottomSheet).apply {
            peekHeight = 70
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }
    }

    private fun initData() {
        notesViewModel = ViewModelProvider(this).get(SavedNotesViewModel::class.java)
    }

    private fun initViews() {
        initBottomSheet()
        initSavedNotesRecycler()
        mSavedNotesTextView = binding.textviewSavedNotes
        mNoteQuoteTextInput = binding.edittextNoteQuote
        mNoteSourceTextInput = binding.edittextNoteSource
        mSubmitNoteButton = binding.buttonSubmitNewNote
    }
}
