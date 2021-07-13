import { Component, OnInit } from '@angular/core';
import {UserService} from '../../user.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  pwdDetails: any = {};
  loading = false;
  dangerBox = false;
  successBox = false;
  submitattempt = false;
  submiterror: any ;
  submitmsg: string;

  constructor(private userService: UserService, private route: ActivatedRoute) { }

  ngOnInit(): void {

  }


  submit(chpwdform) {
    if (chpwdform.form.valid && (this.pwdDetails.newPassword === this.pwdDetails.passwordConfirm)) {

      this.userService.changePassword(this.route.snapshot.paramMap.get('id'), this.pwdDetails)
        .subscribe(
          response => {
            this.loading = false;
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
