package com.tarot.service.impl;

import com.tarot.entity.Client;
import com.tarot.entity.PointsLog;
import com.tarot.entity.Session;
import com.tarot.exception.InsufficientPointsException;
import com.tarot.repository.ClientRepository;
import com.tarot.repository.PointsLogRepository;
import com.tarot.service.PointsLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointsLedgerServiceImpl implements PointsLedgerService {

    private final ClientRepository clientRepository;
    private final PointsLogRepository pointsLogRepository;

    @Override
    @Transactional
    public void awardPoints(Client client, Session session, int points) {
        client.setTotalPoints(client.getTotalPoints() + points);
        clientRepository.save(client);

        PointsLog log = PointsLog.builder()
                .client(client)
                .session(session)
                .pointsDelta(points)
                .balanceAfter(client.getTotalPoints())
                .reason("Начисление за сеанс: " + session.getService().getName())
                .build();
        pointsLogRepository.save(log);
    }

    @Override
    @Transactional
    public void spendPoints(Client client, Session session, int points) {
        if (client.getTotalPoints() < points) {
            throw new InsufficientPointsException("Недостаточно баллов для списания");
        }
        client.setTotalPoints(client.getTotalPoints() - points);
        clientRepository.save(client);

        PointsLog log = PointsLog.builder()
                .client(client)
                .session(session)
                .pointsDelta(-points)
                .balanceAfter(client.getTotalPoints())
                .reason("Списание баллов — скидка")
                .build();
        pointsLogRepository.save(log);
    }
}
