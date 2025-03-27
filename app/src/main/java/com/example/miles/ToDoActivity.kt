package com.example.miles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miles.adapter.ToDoAdapter
import com.example.miles.model.ToDoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ToDoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var taskInput: EditText
    private lateinit var addTaskButton: Button
    private lateinit var shareButton: Button
    private lateinit var shareEmailInput: EditText
    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var todoAdapter: ToDoAdapter

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        // Initialize views
        taskInput = findViewById(R.id.taskInput)
        addTaskButton = findViewById(R.id.addTaskButton)
        shareButton = findViewById(R.id.shareButton)
        shareEmailInput = findViewById(R.id.shareEmailInput)
        todoRecyclerView = findViewById(R.id.todoRecyclerView)

        // Setup RecyclerView
        todoRecyclerView.layoutManager = LinearLayoutManager(this)
        todoAdapter = ToDoAdapter(listOf())
        todoRecyclerView.adapter = todoAdapter

        // Check if user is logged in
        auth.currentUser?.let {
            userEmail = it.email
        } ?: run {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Button Listeners
        addTaskButton.setOnClickListener { addTaskToFirestore() }
        shareButton.setOnClickListener { shareToDoList() }

        loadSharedToDoLists()
    }

    private fun addTaskToFirestore() {
        val taskText = taskInput.text.toString().trim()

        if (taskText.isEmpty()) {
            Toast.makeText(this, "Enter a valid task!", Toast.LENGTH_SHORT).show()
            return
        }

        userEmail?.let { email ->
            val taskId = db.collection("ToDoLists").document().id
            val task = hashMapOf(
                "id" to taskId,
                "task" to taskText,
                "sharedWith" to listOf(email),
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("ToDoLists").document(taskId)
                .set(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show()
                    taskInput.text.clear()
                    loadSharedToDoLists()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun shareToDoList() {
        val sharedEmail = shareEmailInput.text.toString().trim()
        if (sharedEmail.isEmpty()) {
            Toast.makeText(this, "Enter an email to share!", Toast.LENGTH_SHORT).show()
            return
        }

        userEmail?.let { email ->
            db.collection("ToDoLists")
                .whereArrayContains("sharedWith", email)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach { document ->
                        val taskId = document.id
                        val existingTask = document.toObject(ToDoItem::class.java)
                        val updatedSharedWith = existingTask.sharedWith + sharedEmail

                        db.collection("ToDoLists").document(taskId)
                            .update("sharedWith", updatedSharedWith)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Shared with $sharedEmail!", Toast.LENGTH_SHORT).show()
                                loadSharedToDoLists()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Sharing failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching tasks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadSharedToDoLists() {
        userEmail?.let { email ->
            db.collection("ToDoLists")
                .whereArrayContains("sharedWith", email)
                .orderBy("timestamp")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Toast.makeText(this, "Failed to load tasks!", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    val todoList = snapshots?.documents?.mapNotNull { it.toObject(ToDoItem::class.java) } ?: listOf()
                    if (::todoAdapter.isInitialized) {
                        todoAdapter.updateList(todoList)
                    }
                }
        }
    }
}
