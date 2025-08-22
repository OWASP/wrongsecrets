package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Challenge57ControllerTest {

  @Mock private Challenge57 challenge;

  @InjectMocks private Challenge57Controller controller;

  @Test
  void triggerDatabaseErrorShouldReturnErrorMessage() {
    // Given
    String expectedError = "Database connection failed with connection string: ...";
    org.mockito.Mockito.when(challenge.simulateDatabaseConnectionError()).thenReturn(expectedError);

    // When
    String result = controller.triggerDatabaseError();

    // Then
    assertThat(result).isEqualTo(expectedError);
    org.mockito.Mockito.verify(challenge).simulateDatabaseConnectionError();
  }
}
