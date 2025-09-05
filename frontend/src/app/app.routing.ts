import { NgModule } from '@angular/core';
import { CommonModule, } from '@angular/common';
import { BrowserModule  } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { RegisterComponent } from './auth/register.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AuthGuard } from './services/auth.guard';
import { EventFormComponent} from './events/event-form.component';
import { EventsComponent } from './events/events.component';
import { ReservationsComponent } from './reservations/reservations.component';
const routes: Routes =[
   //{ path: 'events', component: EventsComponent },
  { path: 'events/create', component: EventFormComponent },
  { path: 'events/edit/:id', component: EventFormComponent },
  //{ path: 'events/reservations/:id', component: ReservationsComponent },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [{
      path: '',
      loadChildren: () => import('./layouts/admin-layout/admin-layout.module').then(m => m.AdminLayoutModule)
    }]
  }
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes, {
       useHash: false
    })
  ],
  exports: [
  ],
})
export class AppRoutingModule { }
