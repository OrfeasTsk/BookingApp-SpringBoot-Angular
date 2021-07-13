import { Component, OnInit } from '@angular/core';
import { AuthenticationService} from './authentication.service';
import {UserDetails} from './model/user-details';
import {NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs/operators';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'BookingApp';
  userDetails: UserDetails;
  redirectUrl: string;

  constructor(private authService: AuthenticationService, private router: Router) {

  }


  ngOnInit(): void {

    this.authService.getLoggedInUser().subscribe((userDetails) => {
    this.userDetails = userDetails;
    });

  }

  hasRole(rolename: string): boolean{
    let flag = false;
    if(this.userDetails) {
      this.userDetails.roles.forEach((role) => {
        if (role === rolename)
          flag = true;
      });
    }
    return flag;
  }

  makeRedirectUrl():string{
    let redirectUrl: string = null;
    if(this.hasRole('ROLE_TENANT') && this.hasRole('ROLE_HOST'))
      redirectUrl = '/choosepage';
    else if(this.hasRole('ROLE_HOST'))
      redirectUrl = '/hostpage';
    else if(this.hasRole('ROLE_ADMIN'))
      redirectUrl = '/adminpage';
    else
      redirectUrl = '/homepage';


    return redirectUrl;
  }


  logout(){
    this.authService.logout();
  }

  goToProfile(){
    this.router.navigate(['/users/' + this.userDetails.id.toString()]).then(() => {
      location.reload();
    });
  }


}
