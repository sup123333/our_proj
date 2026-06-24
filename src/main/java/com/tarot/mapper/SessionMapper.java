package com.tarot.mapper;

import com.tarot.dto.session.SessionResponse;
import com.tarot.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionResponse toResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getClient().getId(),
                session.getClient().getName(),
                session.getService().getId(),
                session.getService().getName(),
                session.getPrice(),
                session.getOwnQuestion(),
                session.getDiscountApplied(),
                session.getFinalPrice(),
                session.getStatus(),
                session.getClientQuestion(),
                session.getCreatedAt(),
                session.getPaidAt(),
                session.getCompletedAt(),
                session.getPointsEarned()
        );
    }
}
