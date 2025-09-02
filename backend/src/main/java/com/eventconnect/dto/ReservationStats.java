package com.eventconnect.dto;

import java.math.BigDecimal;

/**
 * DTO pour les statistiques des réservations
 */
public class ReservationStats {
    private long totalReservations;
    private long pendingReservations;
    private long totalRevenue;

    public ReservationStats() {
    }

    // Getters et Setters
    public long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public long getPendingReservations() {
        return pendingReservations;
    }

    public void setPendingReservations(long pendingReservations) {
        this.pendingReservations = pendingReservations;
    }

    public long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}