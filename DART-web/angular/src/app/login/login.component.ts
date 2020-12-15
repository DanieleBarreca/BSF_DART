import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../authentication.service';
import { ActivatedRoute, Router } from '@angular/router';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  model: any = {};
  loading = false;
  returnUrl: string;
  returnParams = {};

  error: string;


  constructor(private route: ActivatedRoute, private auth: AuthenticationService, private router:Router) { }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.returnParams = Object.assign({},this.route.snapshot.queryParams)
    delete this.returnParams['returnUrl']

    if (this.auth.autoLoginPromise) {
      this.loading = true;
      this.error = null;
      this.auth.autoLoginPromise.pipe(first()).subscribe(
        data => this.confirmLogin(data),
        error => this.loginError(error)
      );
    }
  }

  login() {
    this.loading = true;
    this.error = null;
    this.auth.login(this.model.username, this.model.password);
    this.auth.autoLoginPromise.pipe(first()).subscribe(
      data => this.confirmLogin(data),
      error => this.loginError(error)
    );
  }

  confirmLogin(data) {    
    if (data == null) {
      this.router.navigate(["login"]);
    }else{
      if (this.returnUrl!="/" && Object.keys(this.returnParams).length!=0) {
        this.router.navigate([this.returnUrl, this.returnParams]);
      }else{
        this.router.navigate([this.returnUrl])
      }
    }
        
    this.loading = false;
  }

  loginError(err) {
    this.error = err.message;
    this.loading = false;
  }
  

}
