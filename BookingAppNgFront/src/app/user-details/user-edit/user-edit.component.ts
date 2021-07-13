import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../user.service';
import {User} from '../../model/user';
import {AuthenticationService} from '../../authentication.service';
import {UserDetails} from '../../model/user-details';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {
  user: User = new User();
  loading = false;
  dangerBox = false;
  successBox = false;
  submitattempt = false;
  submiterror: any ;
  submitmsg: string;

  constructor(private userService: UserService, private route: ActivatedRoute , private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    this.userService.getUser(this.route.snapshot.paramMap.get('id')).subscribe((user) => {
        this.user.id = user.id;
        this.user.firstName = user.firstName;
        this.user.lastName = user.lastName;
        this.user.username = user.username;
        this.user.email = user.email;
        this.user.phone = user.phone;
      }
    );
  }


  submit(usereditform){
    if (usereditform.form.valid) {
      this.loading = true;
      this.userService.editUser(this.user)
        .subscribe(
          response => {
            this.loading = false;
            const token = response.headers.get('Authorization');
            if (token) {
              let userDetails: UserDetails = null;
              this.authenticationService.getLoggedInUser().subscribe((uDetails) => {
                  userDetails = uDetails;
              });
              userDetails.token = token;
              this.authenticationService.setLoggedInUser(userDetails);
            }
            this.submitmsg = response.body;
            this.successBox = true;
            this.dangerBox = false;
          },
          error => {
            this.loading = false;
            this.submiterror = error;
            this.dangerBox = true;
            this.successBox = false;
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
