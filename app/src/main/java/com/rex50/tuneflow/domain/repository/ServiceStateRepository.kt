package com.rex50.tuneflow.domain.repository

import com.rex50.tuneflow.domain.model.ServiceState
import kotlinx.coroutines.flow.Flow

interface ServiceStateRepository {
    val serviceState: Flow<ServiceState>
    suspend fun updateState(state: ServiceState)
}

