package com.eventconnect.dto;

import java.math.BigDecimal;

/**
 * DTO pour les statistiques des réservations
 *
 * @author EventConnect Team
 * @version 2.0.1
 */
public class ReservationStats {
    private long totalReservations;
    private long reservationsEnAttente;
    private long reservationsConfirmees;
    private long reservationsAnnulees;
    private BigDecimal chiffreAffairesTotal;
    private double tauxConfirmation;

    public ReservationStats() {}

    public long getTotalReservations() { return totalReservations; }
    public void setTotalReservations(long totalReservations) { this.totalReservations = totalReservations; }

    public long getReservationsEnAttente() { return reservationsEnAttente; }
    public void setReservationsEnAttente(long reservationsEnAttente) { this.reservationsEnAttente = reservationsEnAttente; }

    public long getReservationsConfirmees() { return reservationsConfirmees; }
    public void setReservationsConfirmees(long reservationsConfirmees) { this.reservationsConfirmees = reservationsConfirmees; }

    public long getReservationsAnnulees() { return reservationsAnnulees; }
    public void setReservationsAnnulees(long reservationsAnnulees) { this.reservationsAnnulees = reservationsAnnulees; }

    public BigDecimal getChiffreAffairesTotal() { return chiffreAffairesTotal; }
    public void setChiffreAffairesTotal(BigDecimal chiffreAffairesTotal) { this.chiffreAffairesTotal = chiffreAffairesTotal; }

    public double getTauxConfirmation() { return tauxConfirmation; }
    public void setTauxConfirmation(double tauxConfirmation) { this.tauxConfirmation = tauxConfirmation; }
}