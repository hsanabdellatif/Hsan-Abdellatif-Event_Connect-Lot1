import { Component, OnInit } from '@angular/core';
import * as Chartist from 'chartist';
import { EventService } from '../services/event.service';
import { UserService } from '../services/user.service';
import { ReservationService } from '../services/reservation.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

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
  loading = true;
  error = '';

  constructor(
    private eventService: EventService,
    private userService: UserService,
    private reservationService: ReservationService
  ) { }
  startAnimationForLineChart(chart){
      let seq: any, delays: any, durations: any;
      seq = 0;
      delays = 80;
      durations = 500;

      chart.on('draw', function(data) {
        if(data.type === 'line' || data.type === 'area') {
          data.element.animate({
            d: {
              begin: 600,
              dur: 700,
              from: data.path.clone().scale(1, 0).translate(0, data.chartRect.height()).stringify(),
              to: data.path.clone().stringify(),
              easing: Chartist.Svg.Easing.easeOutQuint
            }
          });
        } else if(data.type === 'point') {
              seq++;
              data.element.animate({
                opacity: {
                  begin: seq * delays,
                  dur: durations,
                  from: 0,
                  to: 1,
                  easing: 'ease'
                }
              });
          }
      });

      seq = 0;
  };
  startAnimationForBarChart(chart){
      let seq2: any, delays2: any, durations2: any;

      seq2 = 0;
      delays2 = 80;
      durations2 = 500;
      chart.on('draw', function(data) {
        if(data.type === 'bar'){
            seq2++;
            data.element.animate({
              opacity: {
                begin: seq2 * delays2,
                dur: durations2,
                from: 0,
                to: 1,
                easing: 'ease'
              }
            });
        }
      });

      seq2 = 0;
  };
  ngOnInit() {
    // Charger les statistiques
    this.loadStats();
    
    // Charger les événements récents
    this.loadRecentEvents();
    
    // Charger les réservations récentes
    this.loadRecentReservations();

    this.initCharts();
  }

  private loadStats() {
    this.loading = true;
    this.error = '';
    let loadedCount = 0;
    const totalLoads = 3;

    const checkLoading = () => {
      loadedCount++;
      if (loadedCount === totalLoads) {
        this.loading = false;
      }
    };
    
    // Charger les statistiques une par une pour mieux gérer les erreurs
    this.eventService.getEventStats().pipe(
      catchError(err => {
        console.error('Erreur lors du chargement des stats événements:', err);
        return of({ totalEvents: 0, activeEvents: 0 });
      })
    ).subscribe({
      next: (eventStats) => {
        this.dashboardStats.totalEvents = eventStats.totalEvents || 0;
        this.dashboardStats.activeEvents = eventStats.activeEvents || 0;
        checkLoading();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des stats événements:', error);
        this.error = 'Erreur lors du chargement des statistiques des événements';
        checkLoading();
      }
    });

    this.userService.getUserStats().pipe(
      catchError(err => {
        console.error('Erreur lors du chargement des stats utilisateurs:', err);
        return of({ totalUsers: 0 });
      })
    ).subscribe({
      next: (userStats) => {
        this.dashboardStats.totalUsers = userStats.totalUsers || 0;
        checkLoading();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des stats utilisateurs:', error);
        this.error = 'Erreur lors du chargement des statistiques des utilisateurs';
        checkLoading();
      }
    });

    this.reservationService.getReservationStats().pipe(
      catchError(err => {
        console.error('Erreur lors du chargement des stats réservations:', err);
        return of({ totalReservations: 0, pendingReservations: 0, totalRevenue: 0 });
      })
    ).subscribe({
      next: (reservationStats) => {
        this.dashboardStats.totalReservations = reservationStats.totalReservations || 0;
        this.dashboardStats.pendingReservations = reservationStats.pendingReservations || 0;
        this.dashboardStats.revenue = reservationStats.totalRevenue || 0;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des stats réservations:', error);
        this.error = 'Erreur lors du chargement des données. Veuillez réessayer plus tard.';
        this.loading = false;
      }
    });
  }

  private loadRecentEvents() {
    this.eventService.getActiveEvents().pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des événements récents:', error);
        this.error = 'Erreur lors du chargement des événements';
        return of([]);
      })
    ).subscribe(events => {
      this.recentEvents = events.slice(0, 3).map(event => ({
        nom: event.titre || 'Sans titre',
        date: event.dateDebut,
        reservations: event.placesMax - event.placesDisponibles || 0
      }));
    });
  }
  private loadRecentReservations() {
    this.reservationService.getAllReservations().pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des réservations récentes:', error);
        this.error = 'Erreur lors du chargement des réservations';
        return of([]);
      })
    ).subscribe(reservations => {
      this.recentReservations = reservations.slice(0, 3).map(reservation => ({
        utilisateur: reservation.utilisateurNom || 'Utilisateur inconnu',
        evenement: reservation.evenementNom || 'Événement inconnu',
        statut: reservation.statut || 'EN_ATTENTE'
      }));
    });
  }

  private initCharts() {
      /* ----------==========     Daily Sales Chart initialization For Documentation    ==========---------- */

      const dataDailySalesChart: any = {
          labels: ['M', 'T', 'W', 'T', 'F', 'S', 'S'],
          series: [
              [12, 17, 7, 17, 23, 18, 38]
          ]
      };

     const optionsDailySalesChart: any = {
          lineSmooth: Chartist.Interpolation.cardinal({
              tension: 0
          }),
          low: 0,
          high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
          chartPadding: { top: 0, right: 0, bottom: 0, left: 0},
      }

      var dailySalesChart = new Chartist.Line('#dailySalesChart', dataDailySalesChart, optionsDailySalesChart);

      this.startAnimationForLineChart(dailySalesChart);


      /* ----------==========     Completed Tasks Chart initialization    ==========---------- */

      const dataCompletedTasksChart: any = {
          labels: ['12p', '3p', '6p', '9p', '12p', '3a', '6a', '9a'],
          series: [
              [230, 750, 450, 300, 280, 240, 200, 190]
          ]
      };

     const optionsCompletedTasksChart: any = {
          lineSmooth: Chartist.Interpolation.cardinal({
              tension: 0
          }),
          low: 0,
          high: 1000, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
          chartPadding: { top: 0, right: 0, bottom: 0, left: 0}
      }

      var completedTasksChart = new Chartist.Line('#completedTasksChart', dataCompletedTasksChart, optionsCompletedTasksChart);

      // start animation for the Completed Tasks Chart - Line Chart
      this.startAnimationForLineChart(completedTasksChart);



      /* ----------==========     Emails Subscription Chart initialization    ==========---------- */

      var datawebsiteViewsChart = {
        labels: ['J', 'F', 'M', 'A', 'M', 'J', 'J', 'A', 'S', 'O', 'N', 'D'],
        series: [
          [542, 443, 320, 780, 553, 453, 326, 434, 568, 610, 756, 895]

        ]
      };
      var optionswebsiteViewsChart = {
          axisX: {
              showGrid: false
          },
          low: 0,
          high: 1000,
          chartPadding: { top: 0, right: 5, bottom: 0, left: 0}
      };
      var responsiveOptions: any[] = [
        ['screen and (max-width: 640px)', {
          seriesBarDistance: 5,
          axisX: {
            labelInterpolationFnc: function (value) {
              return value[0];
            }
          }
        }]
      ];
      var websiteViewsChart = new Chartist.Bar('#websiteViewsChart', datawebsiteViewsChart, optionswebsiteViewsChart, responsiveOptions);

      //start animation for the Emails Subscription Chart
      this.startAnimationForBarChart(websiteViewsChart);
  }

}
