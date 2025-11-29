package com.rex50.tuneflow.data.repository

import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.repository.ServiceStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryServiceStateRepository : ServiceStateRepository {

    private val _isServiceEnabled = MutableStateFlow(false)
    override val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

    private val _dataUpdates = MutableStateFlow(ServiceState(speed = 0f, volume = 0))
    override val dataUpdates: Flow<ServiceState> = _dataUpdates.asStateFlow()

    override suspend fun updateData(state: ServiceState) {
        _dataUpdates.update { state }
    }

    override suspend fun updateServiceEnabled(enabled: Boolean) {
        _isServiceEnabled.update { enabled }
    }
}

