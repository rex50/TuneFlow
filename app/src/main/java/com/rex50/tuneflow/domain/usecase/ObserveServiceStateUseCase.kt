package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.ServiceStateRepository

class ObserveServiceStateUseCase(
    private val repository: ServiceStateRepository
) {
    operator fun invoke() = repository.serviceState
}

