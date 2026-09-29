package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.Connection;

public record ConnectionDto(Long fromId,
                            Long toId,
                            double distanceKm,
                            double timeMin,
                            String roadType,
                            boolean bidirectional) {

    public static ConnectionDto from(Connection connection) {
        return new ConnectionDto(connection.fromId(), connection.toId(), connection.distanceKm(),
                connection.timeMin(), connection.roadType(), connection.bidirectional());
    }
}
