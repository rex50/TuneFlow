package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.model.PermissionsUiState
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to observe permission status and convert to UI state
 */
class ObservePermissionsUseCase @Inject constructor(
    private val repository: PermissionStatusRepository
) {
    operator fun invoke(): Flow<PermissionsUiState> {
        return repository.observePermissionStatus().map { statuses ->
            val missingRequirements = statuses.filter { !it.isGranted }

            if (missingRequirements.isEmpty()) {
                PermissionsUiState.AllGranted
            } else {
                PermissionsUiState.MissingRequirements(missingRequirements)
            }
        }
    }
}

