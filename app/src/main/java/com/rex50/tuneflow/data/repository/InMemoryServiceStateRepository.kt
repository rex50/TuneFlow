package com.rex50.tuneflow.data.repository

import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.repository.ServiceStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryServiceStateRepository : ServiceStateRepository {

    private val stateFlow = MutableStateFlow(ServiceState(speed = 0f, volume = 0))

    override val serviceState: Flow<ServiceState> = stateFlow.asStateFlow()

    override suspend fun updateState(state: ServiceState) {
        stateFlow.value = state
    }
}

