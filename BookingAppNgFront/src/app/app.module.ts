import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule} from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppComponent } from './app.component';
import { UserService } from './user.service';
import { LoginComponent } from './Auth/login/login.component';
import { AuthGuard } from './Auth/guards/auth.guard';
import { ErrorInterceptor} from './Auth/helpers/error.interceptor';
import { JwtInterceptor} from './Auth/helpers/jwt.interceptor';
import { SignupComponent } from './Auth/signup/signup.component';
import {AuthenticationService} from './authentication.service';
import { HomeComponent } from './home/home.component';
import { HostHomeComponent } from './host-home/host-home.component';
import { DepartmentRegisterComponent } from './host-home/department-register/department-register.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { DepartmentEditComponent } from './host-home/department-edit/department-edit.component';
import {NgxPaginationModule} from 'ngx-pagination';
import { DepartmentDetailsComponent } from './department-details/department-details.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { ChangePasswordComponent } from './user-details/change-password/change-password.component';
import { UserEditComponent } from './user-details/user-edit/user-edit.component';
import { DepartmentSearchComponent } from './home/department-search/department-search.component';
import { ModalModule } from 'ngx-bootstrap/modal';
import { BookingListComponent } from './home/booking-list/booking-list.component';
import { AdminHomeComponent } from './admin-home/admin-home.component';
import { MessagePageComponent } from './message-page/message-page.component';
import { ChoosePageComponent } from './choose-page/choose-page.component';
import { AppDataExportComponent } from './admin-home/app-data-export/app-data-export.component';
import {HostGuard} from './Guards/host.guard';
import { ErrorComponent } from './errorpages/error/error.component';
import {BookingGuard} from './Guards/booking.guard';
import {ChooseGuard} from './Guards/choose.guard';
import {AdminGuard} from './Guards/admin.guard';
import {UserGuard} from './Guards/user.guard';
import {User} from './model/user';

const appRoutes: Routes = [
  { path: '', redirectTo: '/homepage', pathMatch: 'full'},
  { path: 'users', children: [
      {path: ':id' , children: [
          {path: '', component: UserDetailsComponent},
          {path: 'passwordchange', component: ChangePasswordComponent, canActivate: [UserGuard]},
          {path: 'edit', component: UserEditComponent, canActivate: [UserGuard]},
          { path: 'messages', component: MessagePageComponent, canActivate: [UserGuard]}
          ] }
    ] },
  { path: 'login', component: LoginComponent } ,
  { path: 'signup', component: SignupComponent },
  { path: 'adminpage', children: [
      {path: '', component: AdminHomeComponent},
      {path: 'exportdata', component: AppDataExportComponent }
    ] , canActivate: [AdminGuard]},
  { path: 'choosepage' , component: ChoosePageComponent, canActivate: [ChooseGuard]},
  {path: 'departments/:id' ,  component: DepartmentDetailsComponent},
  { path: 'homepage', children: [
      {path: '' ,  component: HomeComponent, children: [{path: 'search' , component: DepartmentSearchComponent}]},
      {path: 'bookings', component: BookingListComponent, canActivate: [BookingGuard]}
    ] },
  { path: 'hostpage', children: [
      { path: '' , component: HostHomeComponent},
      {path: 'departmentregister' ,  component: DepartmentRegisterComponent},
      {path: 'departments/:id/edit' ,  component: DepartmentEditComponent}
    ], canActivate: [HostGuard]
  },
  { path: 'error', component: ErrorComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    HomeComponent,
    HostHomeComponent,
    DepartmentRegisterComponent,
    DepartmentEditComponent,
    DepartmentDetailsComponent,
    UserDetailsComponent,
    ChangePasswordComponent,
    UserEditComponent,
    DepartmentSearchComponent,
    BookingListComponent,
    AdminHomeComponent,
    MessagePageComponent,
    ChoosePageComponent,
    AppDataExportComponent,
    ErrorComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot(appRoutes),
    HttpClientModule,
    NgbModule,
    NgxPaginationModule,
    ModalModule.forRoot()
  ],
  providers: [
    UserService,
    AuthenticationService,
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
