import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import * as sjcl from 'sjcl';
import { UserService } from '../user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-change-password',
  templateUrl: './user-change-password.component.html',
  styleUrls: ['./user-change-password.component.css']
})
export class UserChangePasswordComponent implements OnInit {
  oldPwd: string;
  newPwd: string;
  newPwdConfirm: string;

  message: string;

  constructor(public bsModalRef: BsModalRef, private userService: UserService, private router: Router) { }

  ngOnInit() {
  }

  submitChanges(){
    this.userService.changePassword(
      sjcl.codec.base64.fromBits(sjcl.hash.sha256.hash(this.oldPwd)), 
      sjcl.codec.base64.fromBits(sjcl.hash.sha256.hash(this.newPwd))
    ).subscribe((data) => {
        if (data['status'] == "OK"){
          this.bsModalRef.hide();
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          this.message = "Not authorized"
        }else if (data['status'] == "ERROR"){
          this.message = data['message'];
        }
      }
    )
  }

}
