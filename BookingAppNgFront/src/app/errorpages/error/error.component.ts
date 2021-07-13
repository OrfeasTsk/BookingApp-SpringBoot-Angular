import { Component, OnInit } from '@angular/core';
import {ErrorService} from '../../error.service';
import {AuthenticationService} from '../../authentication.service';
import {UserDetails} from '../../model/user-details';
import {Router} from '@angular/router';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {

  msg: string;
  userDetails: UserDetails;

  constructor(private errorService: ErrorService, private authService: AuthenticationService, private router: Router) { }

  ngOnInit(): void {
    this.errorService.getErrMsg().subscribe( (errormsg) => {
        this.msg = errormsg;
      }
    );

    this.authService.getLoggedInUser().subscribe((userDetails) => {
      this.userDetails = userDetails;
    });

    if(this.msg === '')
      if(!this.userDetails){
        this.router.navigate(['/homepage']);
      }
      if( this.hasRole('ROLE_HOST')){
        this.router.navigate(['/hostpage']);
      }
      else if( this.hasRole('ROLE_TENANT')) {
        this.router.navigate(['/homepage']);
      }
      else if( this.hasRole('ROLE_ADMIN')) {
        this.router.navigate(['/adminpage']);
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
