import { Component, OnInit } from '@angular/core';
import {User} from '../model/user';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../user.service';
import {DomSanitizer} from '@angular/platform-browser';
import {Photo} from '../model/photo';
import {AuthenticationService} from '../authentication.service';
import {UserDetails} from '../model/user-details';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.css']
})
export class UserDetailsComponent implements OnInit {

  user: User = new User();
  validprofphoto = true;
  changeButton = false;
  userDetails: UserDetails;

  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService, private domSanitizer: DomSanitizer, private authService: AuthenticationService) { }

  ngOnInit(): void {
    this.authService.getLoggedInUser().subscribe((userDetails) => {
      this.userDetails = userDetails;
    });

    this.userService.getUser(this.route.snapshot.paramMap.get('id')).subscribe((user) => {
        Object.assign(this.user , user);
      },
      error => {
        if(this.userDetails)
          this.router.navigate(['/users', this.userDetails.id.toString()]).then(() =>{
            location.reload();
          });
        else
          this.router.navigate(['/homepage']).then(() => {
            location.reload();
          });
      }
    );
  }

  displayProfilePhoto(): any{
    if(this.user.profilePhoto) {
      if (this.user.profilePhoto.type === 'image/png')
        return this.domSanitizer.bypassSecurityTrustUrl('data:image/png;base64,' + this.user.profilePhoto.photoBytes);
      else if (this.user.profilePhoto.type === 'image/jpeg')
        return this.domSanitizer.bypassSecurityTrustUrl('data:image/jpeg;base64,' + this.user.profilePhoto.photoBytes);
    }
    return null;
  }

  getRoles(){
    let str = '';
    this.user.roles.forEach((role) => {
      if(role.name === 'ROLE_ADMIN')
        str = str + 'Admin,';
      else if(role.name === 'ROLE_TENANT')
        str = str + 'Tenant,';
      else if(role.name === 'ROLE_HOST')
        str = str + 'Host,';
      });

    return str.slice(0, -1);
  }

  changeProfilePhoto(inputElement){
    const file: File = inputElement.files[0];
    if (file.type === 'image/jpeg' || file.type === 'image/png') {
      this.validprofphoto = true;
      const photo = new FormData();
      photo.append('imageFile', file, 'profilePhoto');
      this.userService.changeProfilePhoto(this.user.id, photo).subscribe((response) => {
        location.reload();
      });
    }
    else{
      this.validprofphoto = false;
    }

  }

  newTab(photo: Photo){
    const image = new Image();
    if (photo.type === 'image/png') {
      image.src = 'data:image/png;base64,' + photo.photoBytes;
    }
    else if (photo.type === 'image/jpeg') {
      image.src = 'data:image/jpeg;base64,' + photo.photoBytes;
    }

    const w = window.open(  '_blank');
    w.document.write(image.outerHTML);
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


  userHasRole(rolename: string): boolean{
    let flag = false;
    if (this.user) {
      this.user.roles.forEach((role) => {
        if (role.name === rolename) {
          flag = true;
        }
      });
    }
    return flag;
  }



  accepthost(){
    this.userService.accepthost(this.user.id).subscribe((response) => {
      location.reload();
    });
  }

}

