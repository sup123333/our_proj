package com.tarot.service.impl;

import com.tarot.entity.Client;
import com.tarot.entity.Session;
import com.tarot.exception.InsufficientPointsException;
import com.tarot.repository.ClientRepository;
import com.tarot.repository.PointsLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointsLedgerServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PointsLogRepository pointsLogRepository;

    private PointsLedgerServiceImpl ledgerService() {
        return new PointsLedgerServiceImpl(clientRepository, pointsLogRepository);
    }

    @Test
    void spendPoints_throwsWhenInsufficientBalance() {
        var ledgerService = ledgerService();
        Client client = Client.builder().totalPoints(10).build();
        Session session = Session.builder().build();

        assertThatThrownBy(() -> ledgerService.spendPoints(client, session, 50))
                .isInstanceOf(InsufficientPointsException.class);

        verify(clientRepository, never()).save(client);
        verify(pointsLogRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void spendPoints_deductsBalanceWhenSufficient() {
        var ledgerService = ledgerService();
        Client client = Client.builder().totalPoints(50).build();
        Session session = Session.builder().build();

        ledgerService.spendPoints(client, session, 50);

        assertThat(client.getTotalPoints()).isZero();
        verify(clientRepository).save(client);
        verify(pointsLogRepository).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void awardPoints_increasesBalance() {
        var ledgerService = ledgerService();
        Client client = Client.builder().totalPoints(5).build();
        Session session = Session.builder()
                .service(com.tarot.entity.Service.builder().name("Расклад").build())
                .build();

        ledgerService.awardPoints(client, session, 10);

        assertThat(client.getTotalPoints()).isEqualTo(15);
    }
}
