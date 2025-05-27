package com.example.domain

import com.example.data.model.Media
import com.example.data.repositories.HomeRepository
import com.example.domain.interactors.HomeInteractorImpl
import com.example.domain.interactors.NetworkMonitor
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HomeInteractorImplTest {

    private lateinit var homeRepository: HomeRepository
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var interactor: HomeInteractorImpl

    @BeforeEach
    fun setup() {
        homeRepository = mock()
        networkMonitor = mock()
        interactor = HomeInteractorImpl(homeRepository)
    }


    @org.junit.jupiter.api.Test
    fun `fetchHome returns data when connected`() = runTest {
        // Given
        val expected = listOf(Media(1, "Home", "", 1))
        whenever(networkMonitor.isConnected).thenReturn(flowOf(true))
        whenever(homeRepository.getUpcomingPage(1,1)).thenReturn(expected)

        // When
        val result = interactor.getUpcomingPage(1,1)

        // Then
        Assertions.assertEquals(expected, result)
        verify(homeRepository).getUpcomingPage(1,1)
    }
}