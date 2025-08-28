import { Routes } from '@angular/router';

import { DashboardComponent } from '../../dashboard/dashboard.component';
import { EventsComponent } from '../../events/events.component';
import { ReservationsComponent } from '../../reservations/reservations.component';
import { UsersComponent } from '../../users/users.component';
import { UserProfileComponent } from '../../user-profile/user-profile.component';
import { NotificationsComponent } from '../../notifications/notifications.component';

export const AdminLayoutRoutes: Routes = [
    { path: 'dashboard',      component: DashboardComponent },
    { path: 'events',         component: EventsComponent },
    { path: 'reservations',   component: ReservationsComponent },
    { path: 'users',          component: UsersComponent },
    { path: 'user-profile',   component: UserProfileComponent },
    { path: 'notifications',  component: NotificationsComponent },
    { path: 'settings',       component: UserProfileComponent }, // temporaire
    { path: 'statistics',     component: DashboardComponent }, // temporaire
];
