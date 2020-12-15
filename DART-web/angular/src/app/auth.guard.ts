import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthenticationService, private router: Router){}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.auth.getUser()!=null) {
      return true;
    }

    let queryParams = {}
    queryParams['returnUrl'] = next.routeConfig.path;
    for (let param in next.params){
      queryParams[param] = next.params[param];
    }
    
    this.router.navigate(['/login'], { queryParams: queryParams});
    return false;
  }
}
