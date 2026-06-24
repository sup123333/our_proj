package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import com.tarot.entity.Service;
import com.tarot.entity.Session;
import com.tarot.exception.ResourceNotFoundException;
import com.tarot.repository.ClientRepository;
import com.tarot.repository.ServiceRepository;
import com.tarot.repository.SessionRepository;
import com.tarot.service.ClientStatusService;
import com.tarot.service.PointsLedgerService;
import com.tarot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final ServiceRepository serviceRepository;
    private final ClientRepository clientRepository;
    private final PointsLedgerService pointsLedgerService;
    private final VolumeDiscountPolicy volumeDiscountPolicy;
    private final PointsDiscountPolicy pointsDiscountPolicy;
    private final ClientStatusService clientStatusService;
    private final LoyaltyProperties loyaltyProperties;

    @Override
    @Transactional
    public Session createSession(Long clientId, Long serviceId, String question, boolean usePoints, boolean ownQuestion) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Услуга не найдена"));

        BigDecimal price = service.getPrice();
        BigDecimal volumeDiscount = volumeDiscountPolicy.calculateDiscount(client, price, ownQuestion);
        BigDecimal pointsDiscount = usePoints
                ? pointsDiscountPolicy.calculateDiscount(client, price, ownQuestion)
                : BigDecimal.ZERO;
        BigDecimal totalDiscount = volumeDiscount.add(pointsDiscount).min(price);

        Session session = Session.builder()
                .client(client)
                .service(service)
                .price(price)
                .ownQuestion(ownQuestion)
                .discountApplied(totalDiscount)
                .pointsDiscountApplied(pointsDiscount)
                .finalPrice(price.subtract(totalDiscount))
                .clientQuestion(question)
                .build();

        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public Session markAsPaid(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Сеанс не найден"));

        session.setStatus(Session.SessionStatus.PAID);
        session.setPaidAt(LocalDateTime.now());

        if (session.getPointsDiscountApplied().compareTo(BigDecimal.ZERO) > 0) {
            pointsLedgerService.spendPoints(session.getClient(), session, loyaltyProperties.getPointsForDiscount());
        }

        int earned = session.getService().getPointsReward() > 0
                ? session.getService().getPointsReward()
                : loyaltyProperties.getPointsPerSession();
        session.setPointsEarned(earned);
        pointsLedgerService.awardPoints(session.getClient(), session, earned);

        Session saved = sessionRepository.save(session);

        long completedCount = sessionRepository.countCompletedByClient(session.getClient().getId());
        clientStatusService.updateStatus(session.getClient(), completedCount);

        return saved;
    }

    @Override
    @Transactional
    public Session markAsDone(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Сеанс не найден"));

        session.setStatus(Session.SessionStatus.DONE);
        session.setCompletedAt(LocalDateTime.now());
        session.getClient().setLastSessionAt(LocalDateTime.now());
        clientRepository.save(session.getClient());

        return sessionRepository.save(session);
    }

    @Override
    public List<Session> getClientSessions(Long clientId) {
        return sessionRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }

    @Override
    public List<Session> getPendingSessions() {
        return sessionRepository.findByStatus(Session.SessionStatus.PENDING);
    }
}
