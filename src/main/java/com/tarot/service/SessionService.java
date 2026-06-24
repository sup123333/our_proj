package com.tarot.service;

import com.tarot.entity.Session;

import java.util.List;

public interface SessionService {

    // clientId должен быть получен вызывающим кодом из аутентифицированного principal, а не из тела запроса.
    Session createSession(Long clientId, Long serviceId, String question, boolean usePoints, boolean ownQuestion);

    Session markAsPaid(Long sessionId);

    Session markAsDone(Long sessionId);

    List<Session> getClientSessions(Long clientId);

    List<Session> getPendingSessions();
}
