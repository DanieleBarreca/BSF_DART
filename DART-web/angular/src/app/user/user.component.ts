import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { UserService } from '../user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user = null;
  groups = [];
  message: String;

  constructor(public bsModalRef: BsModalRef, private userService: UserService) { }

  ngOnInit() {    
    if (this.user){
      this.groups = Object.keys(this.user['permissions']);  
    }
  }

  submitChanges(){
    this.userService.updateUser(this.user).subscribe(
      (data) => {
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
