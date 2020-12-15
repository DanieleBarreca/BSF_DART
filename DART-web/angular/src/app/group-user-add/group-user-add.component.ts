import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { GroupAdminService } from '../group-admin.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-group-user-add',
  templateUrl: './group-user-add.component.html',
  styleUrls: ['./group-user-add.component.css']
})
export class GroupUserAddComponent implements OnInit {
  @Output() userSubmitted = new EventEmitter<any>();

  user = {
    userName: "",
    firstName: "",
    lastName: "",
    email: ""
  };
  isNewUser = false;

  constructor(public modalRef: BsModalRef,private adminService: GroupAdminService) { }

  ngOnInit() {

  }

  submitUser(){

    let response: Observable<any>;
    if (this.isNewUser){
      response = this.adminService.addNewUserToGroup(this.user);
    }else{
      response = this.adminService.addExistingUserToGroup(this.user.userName);
    }

    response.subscribe(
      (data) => {
        if (data['status'] == "OK"){
          if (this.isNewUser){
            window.alert("Temporary Password: "+data['payload']);
          }
          this.userSubmitted.emit();
          this.modalRef.hide();
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized to add users" )
        }else if (data['status'] == "ERROR"){
          window.alert("Error while adding user \n"+data['message'])          
        }
      }
    );

  }

  submitSearch(controlElement){
    if (controlElement.valid && !controlElement.pristine){    
      this.adminService.getUserWithMail(controlElement.value).subscribe(
        (data) => {
          if (data['status'] == "OK"){
            let userResponse = data['payload'];
            if (userResponse){
              this.user = userResponse;
              this.isNewUser = false;
            }else{
              this.isNewUser = true;
              this.user.userName = "";
              this.user.firstName = "";
              this.user.lastName = "";
            }
          }else if (data['status'] == "AUTHORIZATION_ERROR") {
            window.alert("Not authorized to search users" )
          }else if (data['status'] == "ERROR"){
            window.alert("Error while searching user \n"+data['message'])          
          }
        }
      )
    }
  }
}
