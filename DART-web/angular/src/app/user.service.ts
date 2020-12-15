import { Injectable } from '@angular/core';
import { userUrl, AuthenticationService } from './authentication.service'
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable()
export class UserService {

  constructor(private http: HttpClient, private auth: AuthenticationService) { }

  updateUser(user) {
    return this.http.post(environment.server+userUrl, user, this.auth.getOptions());
  }

  changePassword(oldPwdEncoded, newPwdEncoded) {
    let options = this.auth.getOptions();
    return this.http.put(environment.server+userUrl+"/password",{newPwd: newPwdEncoded, oldPwd: oldPwdEncoded},options);
  }
}
