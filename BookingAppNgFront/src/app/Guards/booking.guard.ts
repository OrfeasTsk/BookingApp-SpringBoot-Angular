import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {UserService} from '../user.service';
import {AuthenticationService} from '../authentication.service';
import {ErrorService} from '../error.service';
import {map} from 'rxjs/operators';
import {UserDetails} from '../model/user-details';

@Injectable({
  providedIn: 'root'
})
export class BookingGuard implements CanActivate {

  userDetails: UserDetails;

  constructor(private authService: AuthenticationService, private router: Router) {
  }


  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    this.authService.getLoggedInUser().subscribe((userDetails) => {
      this.userDetails = userDetails;
    });
    if (!this.userDetails) {
      this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
      return false;
    }
    if (this.hasRole('ROLE_TENANT'))
      return true;
    else if (this.hasRole('ROLE_HOST')) {
      this.router.navigate(['/hostpage']);
      return false;
    } else if (this.hasRole('ROLE_ADMIN')) {
      this.router.navigate(['/adminpage']);
      return false;
    }
  }

  hasRole(rolename: string): boolean{
    let flag = false;
    if (this.userDetails) {
      this.userDetails.roles.forEach((role) => {
        if (role === rolename) {
          flag = true;
        }
      });
    }
    return flag;
  }

}
