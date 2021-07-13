import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '../../authentication.service';
import {NgForm} from '@angular/forms';
import {log} from 'util';
import {User} from '../../model/user';
import {UserDetails} from '../../model/user-details';

@Component({
    moduleId: module.id.toString(),
    templateUrl: 'login.component.html',
    styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
    user: User;
    model: any = {};
    loading = false;
    returnUrl: string;
    loginerror: any ;
    loginmsg: string;
    dangerBox = false;
    submitattempt = false;


    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService
    ) { }

    ngOnInit() {

      this.authenticationService.getLoggedInUser().subscribe((userDetails) => {
        if (userDetails) {
          this.router.navigate([this.makeRedirectUrl(userDetails)]);
        }
        });
    }

    makeRedirectUrl(userDetails: UserDetails): string{
      let redirectUrl: string = null;
      if(this.hasRole('ROLE_TENANT', userDetails) && this.hasRole('ROLE_HOST', userDetails))
        redirectUrl = '/choosepage';
      else if(this.hasRole('ROLE_HOST', userDetails))
        redirectUrl = '/hostpage';
      else if(this.hasRole('ROLE_ADMIN', userDetails))
        redirectUrl = '/adminpage';
      else
        redirectUrl = '/homepage';

      return redirectUrl;
    }

    hasRole(rolename: string , userDetails: UserDetails): boolean{
      let flag = false;
      if(userDetails) {
        userDetails.roles.forEach((role) => {
          if (role === rolename)
            flag = true;
        });
      }
      return flag;
    }


    login(loginform) {
      if (loginform.form.valid) {
        this.loading = true;
        this.authenticationService.login(this.model.username, this.model.password)
          .subscribe(
            response => {
              const userDetails = new UserDetails();
              this.user = response.body;
              userDetails.id = this.user.id;
              userDetails.token = response.headers.get('Authorization');
              this.user.roles.forEach( (role) => {
                if (role.name === 'ROLE_TENANT')
                  this.returnUrl = '/homepage';
                else if(role.name === 'ROLE_HOST')
                  this.returnUrl = '/hostpage';
                else if(role.name === 'ROLE_ADMIN')
                  this.returnUrl = '/adminpage';
                userDetails.roles.push(role.name);

              } );
              this.authenticationService.setLoggedInUser(userDetails);

              this.route.queryParams.subscribe((params) => {
                if(params && params.returnUrl){
                  this.router.navigate([params.returnUrl]).then(() => { location.reload(); });
                }
                else
                  this.router.navigate([this.makeRedirectUrl(userDetails)]);
              });
            },
            error => {
              this.loading = false;
              this.loginerror = error;
              if (error.error.message === 'Bad credentials') {
                this.loginmsg = ': Invalid username or password';
              }
              this.dangerBox = true;
              this.submitattempt = true;
            }
          );
      }
      else{
        this.submitattempt = true;
        this.dangerBox = false;
      }

    }
}
