import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {User} from '../model/user';
import {UserDetails} from '../model/user-details';
import {Message} from '../model/message';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../user.service';
import {AuthenticationService} from '../authentication.service';
import {MessageService} from '../message.service';
import {Department} from '../model/department';

@Component({
  selector: 'app-message-page',
  templateUrl: './message-page.component.html',
  styleUrls: ['./message-page.component.css']
})
export class MessagePageComponent implements OnInit {

  userDetails: UserDetails = null;
  question: Message;
  page = 1;
  replyModalRef: BsModalRef;
  replyText = '';
  messages: Message[] = new Array<Message>();
  params: any;


  @ViewChild('templateRef1') replyModal: TemplateRef<any>;

  constructor(private router: Router, private userService: UserService , private route: ActivatedRoute, private authService: AuthenticationService, private bsModalService: BsModalService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.authService.getLoggedInUser().subscribe((userDetails) => {
      this.userDetails = userDetails;
      this.route.queryParams.subscribe((params) => {
        this.params = params;
        if(this.params && this.params.role) {
          if (this.hasRole('ROLE_HOST') && this.params.role === 'host') {
            this.messages = [];
            this.messageService.getReceivedMessages(this.userDetails.id, (this.params) ? Number(this.params.depId) : null).subscribe((messages) => {
              this.messages = messages;
              this.messages.sort((a, b) => {
                if(!a.reply && b.reply)
                  return -1;
              });
            });
          }
          else if (this.hasRole('ROLE_TENANT') && this.params.role === 'tenant') {
            this.messages = [];
            this.messageService.getSentMessages(this.userDetails.id, (this.params) ? Number(this.params.depId) : null).subscribe((messages) => {
              this.messages = messages;
              this.messages.sort((a, b) => {
                if(!a.reply && b.reply)
                  return 1;
              });
            });
          }
          else{
            if (this.hasRole('ROLE_TENANT') && !this.hasRole('ROLE_HOST'))
              this.router.navigate(['/users/' + userDetails.id + '/messages'], {queryParams: {depId: this.params.depId, role: 'tenant'}});
            else if (!this.hasRole('ROLE_TENANT') && this.hasRole('ROLE_HOST'))
              this.router.navigate(['/users/' + userDetails.id + '/messages'], {queryParams: {depId: this.params.depId, role: 'host'}});
            else
              this.router.navigate(['/users/' + userDetails.id + '/messages'], {queryParams: {depId: this.params.depId}});
          }
        }
        else{
          if (this.hasRole('ROLE_TENANT') && !this.hasRole('ROLE_HOST'))
            this.router.navigate(['/users/' + userDetails.id + '/messages'], {queryParams: {depId: this.params.depId, role: 'tenant'}});
          else if (!this.hasRole('ROLE_TENANT') && this.hasRole('ROLE_HOST'))
            this.router.navigate(['/users/' + userDetails.id + '/messages'], {queryParams: {depId: this.params.depId, role: 'host'}});
        }
      });
    });

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


  openmodal(question: Message){
    this.replyText = '';
    this.question = question;
    this.replyModalRef = this.bsModalService.show(this.replyModal);

  }

  reply(){
    if (this.replyText) {
      const message = new Message();
      message.text = this.replyText;
      message.isQuestion = false;
      this.messageService.message( this.question.fromUser.id , this.question.aboutDepartment.id , this.question.id, message ).subscribe((response) => {
        this.replyText = '';
        this.replyModalRef.hide();
        location.reload();
      });
    }
  }

  deleteMessage(message: Message){
    if(confirm('Do you want to delete this question?')) {
      this.messageService.deleteMessage(this.userDetails.id, message.id).subscribe((response) => {
        location.reload();
      });
    }
  }


}
