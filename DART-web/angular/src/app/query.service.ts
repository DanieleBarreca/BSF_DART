import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';
import { Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../environments/environment';


const queryUrl = 'v1/query';

@Injectable()
export class QueryService {

  allQueriesObservable: Subject<any> = new Subject<any>();

  constructor(private http: HttpClient, private authService: AuthenticationService) {
  }

  public getAllQueries() {

    let options = this.authService.getOptions();
    if (this.authService.getQueryToken()){
      options.addParam("userToken",this.authService.getQueryToken())
    }

    this.http.get(environment.server+queryUrl,options).subscribe(
      (data) => this.allQueriesObservable.next(data),
      (err) => this.allQueriesObservable.error(err)
    );
    return this.allQueriesObservable;
  }

  public getQuery(queryId: string){
    return this.http.get(environment.server+queryUrl+"/"+queryId,this.authService.getOptions());
  }

  public deleteQuery(queryId: string){
    return this.deleteQueries(new Array<string>(queryId));
  }

  public deleteQueries(queryIds: Array<string>){
    let options = this.authService.getOptionsForText();
    for (var i = 0; i < queryIds.length; i++) {
      options.appendParam('ids',queryIds[i]);
    }

    let deleteQueryObservable = this.http.delete(environment.server+queryUrl,options).pipe(
      tap(_ => this.getAllQueries())
    );
    return deleteQueryObservable;
  }

}
