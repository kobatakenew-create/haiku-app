package com.example.haikuapp.service;

import com.example.haikuapp.service.port.RememberMeTokenData;
import com.example.haikuapp.service.port.RememberMeTokenPort;
import java.time.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RememberMeServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-15T00:00:00Z"), ZoneOffset.UTC);
    private final LocalDateTime now = LocalDateTime.now(clock);
    private final RememberMeTokenPort port = mock(RememberMeTokenPort.class);
    private final RememberMeService service = new RememberMeService(port, clock);

    @Test void storesHashWithThirtyDayExpiryInsteadOfRawToken() {
        String token = service.issue(1L);
        var hash = ArgumentCaptor.forClass(String.class);
        verify(port).save(eq(1L), hash.capture(), eq(now.plusDays(30)));
        verify(port).deleteExpiredTokens(now);
        assertTrue(token.matches("[A-Za-z0-9_-]{43}"));
        assertTrue(hash.getValue().matches("[a-f0-9]{64}"));
        assertNotEquals(token, hash.getValue());
        assertNotEquals(token, service.issue(1L));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"short", "<invalid>"})
    void malformedTokensNeverReachDatabase(String token) {
        assertTrue(service.restoreUserId(token).isEmpty());
        service.revoke(token);
        verifyNoInteractions(port);
    }
    @Test void restoresOnlyUnexpiredToken() {
        when(port.findByTokenHash(anyString())).thenReturn(Optional.of(new RememberMeTokenData(2L, now.plusSeconds(1))));
        assertEquals(Optional.of(2L), service.restoreUserId("a".repeat(43)));
        verify(port, never()).deleteByTokenHash(anyString());
    }
    @Test void tokenExpiresExactlyAtExpiryTimeAndIsDeleted() {
        when(port.findByTokenHash(anyString())).thenReturn(Optional.of(new RememberMeTokenData(2L, now)));
        assertTrue(service.restoreUserId("a".repeat(43)).isEmpty());
        verify(port).deleteByTokenHash(anyString());
    }
    @Test void unknownTokenCannotRestoreSession() {
        when(port.findByTokenHash(anyString())).thenReturn(Optional.empty());
        assertTrue(service.restoreUserId("a".repeat(43)).isEmpty());
    }
}
