import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { EventService } from '../services/event.service';
import { UserService } from '../services/user.service';
import { ReservationService } from '../services/reservation.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  dashboardStats: any = {
    totalEvents: 0,
    activeEvents: 0,
    totalUsers: 0,
    totalReservations: 0,
    pendingReservations: 0,
    revenue: 0
  };
  recentEvents: any[] = [];
  recentReservations: any[] = [];
  dailyStats: any[] = [];
  monthlyStats: any[] = [];
  loading = true;
  error = '';

  // Reference to chart canvases
  @ViewChild('dailySalesChart') dailySalesChartElement!: ElementRef<HTMLCanvasElement>;
  @ViewChild('completedTasksChart') completedTasksChartElement!: ElementRef<HTMLCanvasElement>;
  @ViewChild('websiteViewsChart') websiteViewsChartElement!: ElementRef<HTMLCanvasElement>;

  constructor(
    private eventService: EventService,
    private userService: UserService,
    private reservationService: ReservationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadStats();
    this.loadRecentEvents();
    this.loadRecentReservations();
  }

  ngAfterViewInit() {
    // Defer chart initialization until data is loaded
  }

  private loadStats() {
    this.loading = true;
    this.error = '';
    let loadedCount = 0;
    const totalLoads = 4;

    const checkLoading = () => {
      loadedCount++;
      if (loadedCount === totalLoads) {
        this.loading = false;
        this.cdr.detectChanges(); // Ensure DOM is updated
        this.initCharts();
      }
    };

    this.eventService.getEventStats().pipe(
      catchError(err => {
        console.error('Erreur lors du chargement des stats événements:', err);
        this.error = 'Erreur lors du chargement des statistiques des événements';
        return of({ total: 0, futurs: 0, disponibles: 0 });
      })
    ).subscribe({
      next: (eventStats) => {
        this.dashboardStats.totalEvents = eventStats.total || 0;
        this.dashboardStats.activeEvents = eventStats.disponibles || 0;
        checkLoading();
      },
      error: () => checkLoading()
    });

    this.userService.getUserStats().pipe(
      catchError(err => {
        console.error('Erreur lors du chargement des stats utilisateurs:', err);
        this.error = 'Erreur lors du chargement des statistiques des utilisateurs';
        return of({ totalUsers: 0 });
      })
    ).subscribe({
      next: (userStats) => {
        this.dashboardStats.totalUsers = userStats.totalUsers || 0;
        checkLoading();
      },
      error: () => checkLoading()
    });

    this.reservationService.getReservationStats().pipe(
      catchError(err => {
        console.error('Erreur lors du chargement des stats réservations:', err);
        this.error = 'Erreur lors du chargement des statistiques des réservations';
        return of({ totalReservations: 0, pendingReservations: 0, totalRevenue: 0 });
      })
    ).subscribe({
      next: (reservationStats) => {
        this.dashboardStats.totalReservations = reservationStats.totalReservations || 0;
        this.dashboardStats.pendingReservations = reservationStats.reservationsEnAttente || 0;
        this.dashboardStats.revenue = reservationStats.chiffreAffairesTotal || 0;
        checkLoading();
      },
      error: () => checkLoading()
    });

    this.loadHistoricalStats(checkLoading); // Pass the callback
  }

  private loadRecentEvents() {
    this.eventService.getActiveEvents().pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des événements récents:', error);
        this.error = 'Erreur lors du chargement des événements récents';
        return of([]);
      })
    ).subscribe(events => {
      if (Array.isArray(events)) {
        this.recentEvents = events.slice(0, 3).map(event => ({
          nom: event.titre || 'Événement sans titre',
          date: event.dateDebut ? new Date(event.dateDebut) : new Date(),
          reservations: (event.capaciteMax || 0) - (event.placesDisponibles || 0)
        }));
      } else {
        console.error('Les données reçues ne sont pas un tableau:', events);
        this.error = 'Erreur de format des données des événements';
      }
    });
  }

  private loadRecentReservations() {
    this.reservationService.getAllReservations().pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des réservations récentes:', error);
        this.error = 'Erreur lors du chargement des réservations récentes';
        return of([]);
      })
    ).subscribe(reservations => {
      if (Array.isArray(reservations)) {
        this.recentReservations = reservations.slice(0, 3).map(reservation => ({
          utilisateur: reservation.utilisateur?.nomComplet || 'Utilisateur inconnu',
          evenement: reservation.evenement?.titre || 'Événement inconnu',
          statut: reservation.statut || 'EN_ATTENTE'
        }));
      } else {
        console.error('Les données reçues ne sont pas un tableau:', reservations);
        this.error = 'Erreur de format des données des réservations';
      }
    });
  }

  private loadHistoricalStats(callback: () => void) {
    const today = new Date();
    const sevenDaysAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
    const startOfYear = new Date(today.getFullYear(), 0, 1);

    let historicalLoads = 0;
    const totalHistoricalLoads = 2;

    const checkHistoricalLoading = () => {
      historicalLoads++;
      if (historicalLoads === totalHistoricalLoads) {
        callback();
      }
    };

    this.reservationService.getHistoricalStats('DAILY',
      sevenDaysAgo.toISOString(),
      today.toISOString()
    ).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des stats historiques quotidiennes:', error);
        this.error = 'Erreur lors du chargement des statistiques historiques';
        return of([]);
      })
    ).subscribe(stats => {
      this.dailyStats = stats;
      checkHistoricalLoading();
    });

    this.reservationService.getHistoricalStats('MONTHLY',
      startOfYear.toISOString(),
      today.toISOString()
    ).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des stats historiques mensuelles:', error);
        this.error = 'Erreur lors du chargement des statistiques historiques';
        return of([]);
      })
    ).subscribe(stats => {
      this.monthlyStats = stats;
      checkHistoricalLoading();
    });
  }

  private initCharts() {
    if (this.error) {
      console.warn('Charts not initialized due to error state');
      return;
    }

    // Ensure canvas elements are available
    if (!this.dailySalesChartElement || !this.completedTasksChartElement || !this.websiteViewsChartElement) {
      console.error('Un ou plusieurs éléments de graphique sont introuvables dans le DOM');
      return;
    }

    // Daily Sales Chart (Line)
    const dailySalesChart = new Chart(this.dailySalesChartElement.nativeElement, {
      type: 'line',
      data: {
        labels: this.dailyStats.length > 0 ? this.dailyStats.map(stat => stat.date.split('T')[0]) : ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'],
        datasets: [{
          label: 'Revenus Quotidiens',
          data: this.dailyStats.length > 0 ? this.dailyStats.map(stat => stat.totalRevenue) : [0, 0, 0, 0, 0, 0, 0],
          borderColor: '#4caf50',
          backgroundColor: 'rgba(76, 175, 80, 0.2)',
          fill: true,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            max: Math.max(...(this.dailyStats.length > 0 ? this.dailyStats.map(stat => stat.totalRevenue) : [100]), 100) * 1.2
          }
        },
        plugins: {
          legend: {
            display: true
          }
        },
        animation: {
          duration: 1000,
          easing: 'easeOutQuint'
        }
      }
    });

    // Completed Tasks Chart (Line)
    const completedTasksChart = new Chart(this.completedTasksChartElement.nativeElement, {
      type: 'line',
      data: {
        labels: this.dailyStats.length > 0 ? this.dailyStats.map(stat => stat.date.split('T')[0]) : ['12h', '15h', '18h', '21h', '00h', '03h', '06h', '09h'],
        datasets: [{
          label: 'Réservations',
          data: this.dailyStats.length > 0 ? this.dailyStats.map(stat => stat.totalReservations) : [0, 0, 0, 0, 0, 0, 0, 0],
          borderColor: '#ff9800',
          backgroundColor: 'rgba(255, 152, 0, 0.2)',
          fill: true,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            max: Math.max(...(this.dailyStats.length > 0 ? this.dailyStats.map(stat => stat.totalReservations) : [50]), 50) * 1.2
          }
        },
        plugins: {
          legend: {
            display: true
          }
        },
        animation: {
          duration: 1000,
          easing: 'easeOutQuint'
        }
      }
    });

    // Website Views Chart (Bar)
    const websiteViewsChart = new Chart(this.websiteViewsChartElement.nativeElement, {
      type: 'bar',
      data: {
        labels: this.monthlyStats.length > 0 ? this.monthlyStats.map(stat => this.getMonthName(stat.month)) : ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'],
        datasets: [{
          label: 'Événements Actifs',
          data: this.monthlyStats.length > 0 ? this.monthlyStats.map(stat => stat.totalReservations) : [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          backgroundColor: '#f44336',
          borderColor: '#f44336',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            max: Math.max(...(this.monthlyStats.length > 0 ? this.monthlyStats.map(stat => stat.totalReservations) : [50]), 50) * 1.2
          },
          x: {
            grid: {
              display: false
            }
          }
        },
        plugins: {
          legend: {
            display: true
          }
        },
        animation: {
          duration: 1000,
          easing: 'easeOutQuint'
        }
      }
    });
  }

  private getMonthName(monthNumber: string): string {
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'];
    return months[parseInt(monthNumber) - 1] || 'Inconnu';
  }

  retryLoading() {
    this.error = '';
    this.loading = true;
    this.dailyStats = [];
    this.monthlyStats = [];
    this.recentEvents = [];
    this.recentReservations = [];
    this.dashboardStats = {
      totalEvents: 0,
      activeEvents: 0,
      totalUsers: 0,
      totalReservations: 0,
      pendingReservations: 0,
      revenue: 0
    };
    this.ngOnInit();
  }
}