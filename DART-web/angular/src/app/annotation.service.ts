import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AuthenticationService} from "./authentication.service";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";

export const annotationUrl = 'v1/annotation';

@Injectable()
export class AnnotationService {


  constructor(private http: HttpClient, private auth: AuthenticationService) { }


  getConditionDictionaries(): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.get(environment.server+annotationUrl + "/conditionTerms/dictionaries", options);

  }

  getConditionTerms(dictionary: string, query: string, first: number = 0, pageSize: number = 100): Observable<any> {
    let options = this.auth.getOptions();

    options['params'] = new HttpParams().append('query',query).append('first',String(first)).append('pageSize',String(pageSize));


    return this.http.get(environment.server+annotationUrl + "/conditionTerms/" +dictionary, options);

  }

  getAnnotationDictionaries(): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.get(environment.server+annotationUrl + "/annotationTerms/dictionaries", options);

  }

  getAnnotationTerms(dictionary: string): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.get(environment.server+annotationUrl + "/annotationTerms/" +dictionary, options);

  }

  getInheritanceTerms(dictionary: string): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.get(environment.server+annotationUrl + "/inheritanceTerms/" +dictionary, options);

  }

  postAnnotation(annotation:any): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.post(environment.server+annotationUrl, annotation, options);

  }

  deleteAnnotation(annotationId:number): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.delete(environment.server+annotationUrl+"/"+annotationId, options);

  }

  postValidationStatus(variantSample:any): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.post(environment.server+annotationUrl+"/variant-sample", variantSample, options);

  }

  deleteVariantSampleAnnotation(annotationId:number): Observable<any> {
    let options = this.auth.getOptions();


    return this.http.delete(environment.server+annotationUrl+"/variant-sample/"+annotationId, options);

  }

  public addConditionToSample(sampleId: number, condition: number){
    let options = this.auth.getOptions();

    return this.http.post(environment.server+annotationUrl+"/sample/"+sampleId+"/condition",condition, options);
  }

  public removeConditionFromSample(sampleId: number, condition: number){
    let options = this.auth.getOptions();

    return this.http.delete(environment.server+annotationUrl+"/sample/"+sampleId+"/condition/"+condition, options);
  }


}
