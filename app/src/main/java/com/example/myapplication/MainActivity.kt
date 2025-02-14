package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Checkbox

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListApp() {
    var taskText by remember { mutableStateOf(TextFieldValue()) }
    var tasks by remember { mutableStateOf(listOf<Task>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-Do List", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Input field for task
                OutlinedTextField(
                    value = taskText,
                    onValueChange = { taskText = it },
                    placeholder = { Text("Enter a new task...") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (taskText.text.isNotBlank()) {
                                tasks = tasks + Task(taskText.text, false) // Add task with unchecked checkbox
                                taskText = TextFieldValue()
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to add task
                Button(
                    onClick = {
                        if (taskText.text.isNotBlank()) {
                            tasks = tasks + Task(taskText.text, false) // Add task with unchecked checkbox
                            taskText = TextFieldValue()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Task")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Task List
                LazyColumn {
                    itemsIndexed(tasks) { index, task ->
                        TaskItem(
                            task = task,
                            onTaskCheckedChange = { isChecked ->
                                tasks = tasks.toMutableList().apply {
                                    this[index] = this[index].copy(isChecked = isChecked)
                                }
                            },
                            onDeleteTask = {
                                tasks = tasks.filterIndexed { idx, _ -> idx != index } // Correctly remove the selected task
                            },
                            onEditTask = { newTaskText ->
                                tasks = tasks.toMutableList().apply {
                                    this[index] = this[index].copy(text = newTaskText)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

data class Task(val text: String, val isChecked: Boolean)

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChange: (Boolean) -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: (String) -> Unit
) {
    var editText by remember { mutableStateOf(TextFieldValue(task.text)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF1F1F1))
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isChecked,
            onCheckedChange = onTaskCheckedChange
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Task edit functionality
        BasicTextField(
            value = editText,
            onValueChange = {
                editText = it
                onEditTask(it.text)
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Delete task button
        IconButton(onClick = { onDeleteTask() }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoListApp()
}
