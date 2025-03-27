package com.example.miles.model

data class ToDoListModel(
    val id: String = "",
    val owner: String = "",
    val sharedWith: List<String> = emptyList()
)
