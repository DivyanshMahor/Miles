package com.example.miles.model

data class ToDoItem(
    val id: String = "",
    val task: String = "",
    val sharedWith: List<String> = listOf() // अब इसमें शेयर की गई ईमेल स्टोर होंगी
)
