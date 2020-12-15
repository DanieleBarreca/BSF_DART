import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpParams, HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';
import { environment } from '../environments/environment';
import {map} from "rxjs/operators";

const aggregationsUrl = "v1/aggregations";

@Injectable()
export class AggregationsService {

  constructor(private http: HttpClient, private auth: AuthenticationService) { }

  getMutations(vcfId: number, transcript: string, sampleName?: string): Observable<any> {
    let options = this.auth.getOptions();
    if (sampleName) options['params'] = new HttpParams().append('sampleName',sampleName);
    return this.http.get(environment.server+aggregationsUrl + "/variants/" +vcfId+"/" + transcript, options).pipe(map((data: any) => {
      if (data) {
          return data.map( (mutation,index) => {
              return {
                id: index,
                x: mutation.id.pos,
                donors: mutation.count,
                impact: mutation.id.impact
              }
          });
        }
        return [];
      }
    ));

  }

}
