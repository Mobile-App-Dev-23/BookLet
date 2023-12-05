import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditBookActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK = "extra_book"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_book_activity)

        // Retrieve the book object passed as an extra from the intent
        val book = intent.getSerializableExtra(EXTRA_BOOK) as Book?

        // Initialize the EditText fields
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val authorEditText = findViewById<EditText>(R.id.authorEditText)
        val pageEditText = findViewById<EditText>(R.id.pageEditText)
        val notesEditText = findViewById<EditText>(R.id.notesEditText)

        // Populate fields if the book is not null
        book?.let {
            titleEditText.setText(it.title)
            authorEditText.setText(it.author)
            pageEditText.setText(it.page.toString())
            notesEditText.setText(it.notes)
        }

        // Save button and its click listener
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            // Validate input and then save changes
            val updatedBook = Book(
                title = titleEditText.text.toString(),
                author = authorEditText.text.toString(),
                page = pageEditText.text.toString().toIntOrNull() ?: 0,
                notes = notesEditText.text.toString()
            )
            // TODO: Implement saving logic here

            // Show a confirmation message
            Toast.makeText(this, "Book updated!", Toast.LENGTH_SHORT).show()

            // Return to the previous screen
            finish()
        }
    }
}
