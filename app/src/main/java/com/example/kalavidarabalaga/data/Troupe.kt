package com.example.kalavidarabalaga.data

data class Troupe(
    val id: String = "",
    val name: String = "",
    val artForm: String = "",
    val leadContact: String = "",
    val phoneNumber: String = "",
    val district: String = "",
    val groupPhotoUrl: String = "",
    val portfolioPhotos: List<String> = emptyList(),
    val videoLinks: List<String> = emptyList(),
    val equipment: List<String> = emptyList(),
    val description: String = ""
)
