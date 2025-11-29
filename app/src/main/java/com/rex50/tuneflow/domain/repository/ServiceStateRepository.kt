package com.rex50.tuneflow.domain.repository

import com.rex50.tuneflow.domain.model.ServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ServiceStateRepository {
    val isServiceEnabled: StateFlow<Boolean>
    val dataUpdates: Flow<ServiceState>
    suspend fun updateData(state: ServiceState)

    suspend fun updateServiceEnabled(enabled: Boolean)
}

