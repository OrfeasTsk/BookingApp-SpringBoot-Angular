import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../authentication.service';
import {User} from '../../model/user';
import {Role} from '../../model/role';
import {UserDetails} from '../../model/user-details';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  user: User = new User();
  hostRole: Role = new Role();
  tenantRole: Role = new Role();
  loading = false;
  returnUrl: string;
  signuperror: any ;
  dangerBox = false;
  submitattempt = false;
  profilePhoto: File;


  constructor(
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


  onHostCheckBoxChange(hostCheckbox) {
    if (hostCheckbox.checked) {
      this.hostRole.name = hostCheckbox.value;
    }
    else {
      this.hostRole.name = null;
    }

  }

  onTenantCheckBoxChange(tenantCheckbox) {
    if (tenantCheckbox.checked) {
      this.tenantRole.name = tenantCheckbox.value;
    }
    else {
      this.tenantRole.name = null;
    }
  }

  setProfilePhoto(inputElement){
    this.profilePhoto = inputElement.files[0];
  }


  signup(signupform) {
    if (signupform.form.valid && (this.hostRole.name || this.tenantRole.name) && (this.user.password === this.user.passwordConfirm) && this.profilePhoto && (this.profilePhoto.type === 'image/jpeg' || this.profilePhoto.type === 'image/png')) {
      if (this.hostRole.name) {
        this.user.roles.push(this.hostRole);
      }
      if (this.tenantRole.name) {
        this.user.roles.push(this.tenantRole);
      }
      const formWrapper = new FormData();
      const userBlob = new Blob([JSON.stringify(this.user)], { type: 'application/json'});
      if (this.profilePhoto) {
        formWrapper.append('imageFile' , this.profilePhoto , 'profilePhoto');
      }
      formWrapper.append('object', userBlob );
      this.loading = true;
      this.authenticationService.signup(formWrapper)
        .subscribe(
          response => {
            const userDetails = new UserDetails();
            this.user = response.body;
            userDetails.token = response.headers.get('Authorization');
            userDetails.id = this.user.id;
            this.user.roles.forEach( (role) => {
              userDetails.roles.push(role.name);
            } );
            this.authenticationService.setLoggedInUser(userDetails);
            this.router.navigate([this.makeRedirectUrl(userDetails)]);
          }
          ,
          error => {
            this.loading = false;
            this.signuperror = error;
            this.dangerBox = true;
            this.user.roles = [];
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
