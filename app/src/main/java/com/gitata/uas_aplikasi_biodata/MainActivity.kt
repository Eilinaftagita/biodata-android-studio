package com.gitata.uas_aplikasi_biodata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitata.uas_aplikasi_biodata.adapter.NoteAdapter
import com.gitata.uas_aplikasi_biodata.adapter.NoteAdapter.*
import com.gitata.uas_aplikasi_biodata.databinding.ActivityMainBinding
import com.gitata.uas_aplikasi_biodata.db.NoteHelper
import com.gitata.uas_aplikasi_biodata.entity.Note
import com.gitata.uas_aplikasi_biodata.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            // Akan dipanggil jika request codenya ADD
            when (result.resultCode) {
                NoteAddUpdateActivity.RESULT_ADD -> {
                    val note =
                        result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }
                NoteAddUpdateActivity.RESULT_UPDATE -> {
                    val note =
                        result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    val position =
                        result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                    adapter.updateItem(position, note)
                    binding.rvNotes.smoothScrollToPosition(position)
                    showSnackbarMessage("Satu item berhasil diubah")
                }
                NoteAddUpdateActivity.RESULT_DELETE -> {
                    val position =
                        result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                    adapter.removeItem(position)
                    showSnackbarMessage("Satu item berhasil dihapus")
                }
            }
        }
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Biodata"


        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)

        adapter = NoteAdapter(object : OnItemClickCallback {
            override fun onItemClicked(selectedNote: Note?, position: Int?) {
                val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, selectedNote)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            resultLauncher.launch(intent)
        }

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
        return true

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

//        when (item.itemId){
//            R.id.action_about -> {
//                Toast.makeText(this, "testing",Toast.LENGTH_SHORT).show()
//                return true
//            }
//        }
        if (item.itemId == R.id.action_about) {
            Log.d("item", item.toString())
            val intent = Intent(this@MainActivity, AboutActivity::class.java)
            startActivity(intent)
            return true
        }else{
            return super.onOptionsItemSelected(item)
        }

//        return super.onOptionsItemSelected(item)
    }

    private fun loadNotesAsync() {
        lifecycleScope.launch {
            binding.progressbar.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
            noteHelper.close()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    /**
     * Tampilkan snackbar
     *
     * @param message inputan message
     */
    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }
}