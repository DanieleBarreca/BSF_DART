import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

const bedUrl = "v1/bed";

@Injectable()
export class BedService {

  constructor(private http: HttpClient,private auth: AuthenticationService) { }

  getAll(genome: string){
    let options=this.auth.getOptions();
    options.addParam('genome', genome);

    return <Observable<any>> this.http.get(environment.server + bedUrl, options);
  }


}
