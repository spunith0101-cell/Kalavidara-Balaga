package com.example.kalavidarabalaga.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalavidarabalaga.data.Troupe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TroupeViewModel : ViewModel() {
    private val db: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }
    
    // Master list containing all troupes (mock + cloud + local)
    private val _allTroupes = MutableStateFlow<List<Troupe>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedDistrict = MutableStateFlow("All Districts")
    val selectedDistrict: StateFlow<String> = _selectedDistrict

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // The source of truth for the UI - reacts to search, district, and list changes
    val troupes: StateFlow<List<Troupe>> = combine(_allTroupes, _searchQuery, _selectedDistrict) { all, query, district ->
        all.filter { troupe ->
            val matchesDistrict = district == "All Districts" || troupe.district.equals(district, ignoreCase = true)
            val matchesQuery = query.isBlank() || 
                    troupe.name.contains(query, ignoreCase = true) || 
                    troupe.artForm.contains(query, ignoreCase = true) || 
                    troupe.district.contains(query, ignoreCase = true)
            matchesDistrict && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchTroupes()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onDistrictChanged(district: String) {
        _selectedDistrict.value = district
    }

    fun fetchTroupes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val database = db
                if (database != null) {
                    val snapshot = database.collection("troupes").limit(100).get().await()
                    val fetchedTroupes = snapshot.toObjects(Troupe::class.java)
                    
                    // Update master list with cloud data
                    val currentLocalRegistrations = _allTroupes.value.filter { it.id.startsWith("local_") }
                    val merged = (fetchedTroupes + currentLocalRegistrations).distinctBy { it.id }
                    
                    if (merged.isEmpty()) {
                        _allTroupes.value = getMockData()
                    } else {
                        _allTroupes.value = merged
                    }
                } else if (_allTroupes.value.isEmpty()) {
                    _allTroupes.value = getMockData()
                }
            } catch (e: Exception) {
                if (_allTroupes.value.isEmpty()) {
                    _allTroupes.value = getMockData()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerTroupe(troupe: Troupe, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val database = db
                val newTroupe = if (database != null) {
                    val docRef = database.collection("troupes").document()
                    val t = troupe.copy(id = docRef.id)
                    docRef.set(t).await()
                    t
                } else {
                    // Local registration if Firestore is unavailable
                    troupe.copy(id = "local_${System.currentTimeMillis()}")
                }
                
                // Add to master list immediately
                _allTroupes.value = _allTroupes.value + newTroupe
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Registration failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getMockData(): List<Troupe> {
        return listOf(
            Troupe(
                id = "1",
                name = "Sagara Dollu Kunitha",
                artForm = "Dollu Kunitha",
                district = "Shimoga",
                leadContact = "Ramesh Hegde",
                phoneNumber = "9876543210",
                groupPhotoUrl = "https://karnatakatourism.org/wp-content/uploads/2020/06/Dollu-Kunitha-1.jpg",
                equipment = listOf("10 Large Drums", "Cymbals", "Traditional Costumes"),
                description = "Award-winning Dollu Kunitha performers specializing in high-energy rhythmic performances."
            ),
            Troupe(
                id = "2",
                name = "Mandya Pooja Kunitha",
                artForm = "Pooja Kunitha",
                district = "Mandya",
                leadContact = "Suresh Gowda",
                phoneNumber = "9123456789",
                groupPhotoUrl = "https://karnatakatourism.org/wp-content/uploads/2020/06/Pooja-Kunitha.jpg",
                equipment = listOf("Decorated Metal Pots", "Brass Lamps", "Silk Costumes"),
                description = "Graceful Pooja Kunitha troupe carrying the ancient tradition of balancing pots."
            ),
            Troupe(
                id = "3",
                name = "Goravara Kunitha Sangha",
                artForm = "Goravara Kunitha",
                district = "Haveri",
                leadContact = "Mariyappa",
                phoneNumber = "9988776655",
                groupPhotoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRz-hP7kG7Xy4g9r7X7X7X7X7X7X7X7X7X7Xg&s",
                equipment = listOf("Damaru", "Flute", "Bear Skin Headgear"),
                description = "Devotional dancers of Lord Mailara Linga."
            )
        )
    }
}
