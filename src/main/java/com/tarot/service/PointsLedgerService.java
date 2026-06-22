package com.tarot.service;

import com.tarot.entity.Client;
import com.tarot.entity.Session;

// Начисление и списание баллов клиента + ведение журнала PointsLog.
public interface PointsLedgerService {

    void awardPoints(Client client, Session session, int points);

    void spendPoints(Client client, Session session, int points);
}
