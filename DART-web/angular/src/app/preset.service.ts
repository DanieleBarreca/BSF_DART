import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthenticationService } from './authentication.service';
import { HttpClient } from '@angular/common/http';
import {FilterRule, QueryFilter, QueryPanel,} from "./variant-filter-query/FullQuery";
import {Observable} from "rxjs";

const presetUrl = 'v1/preset';

@Injectable()
export class PresetService {


  constructor(private http: HttpClient, private auth: AuthenticationService) { }

  checkQueryPreset(queryRule:FilterRule): Observable<QueryFilter>{
      let options = this.auth.getOptions();

      return this.http.post(environment.server +presetUrl+"/check-query", queryRule, options) as Observable<QueryFilter>;
  }

  checkPanelPreset(panel:QueryPanel): Observable<QueryPanel>{
    let options = this.auth.getOptions();

    return this.http.post(environment.server +presetUrl+"/check-panel", panel, options) as Observable<QueryPanel>;
  }

  saveQueryPreset(queryPreset: QueryFilter){
    let options = this.auth.getOptions();

    return this.http.post(environment.server +presetUrl+"/queries", queryPreset, options);
  }

  getQueryPresets(vcfId?: number): Observable<QueryFilter[]>{
    let options = this.auth.getOptions();
    if (vcfId){
      options.addParam('vcfId', vcfId.toString());
    }

    return this.http.get(environment.server +presetUrl+"/queries", options) as Observable<QueryFilter[]>;
  }

  getFieldsPreset(vcfType: string): Observable<number[]>{
    let options = this.auth.getOptions();
    options.addParam('vcfType', vcfType);

    return (this.http.get(environment.server +presetUrl+"/fields", options) as Observable<number[]>);
  }

  saveFieldsPreset(fieldsPreset:number[], vcfType: string){
    let options = this.auth.getOptions();
    options.addParam('vcfType', vcfType);

    return this.http.post(environment.server +presetUrl+"/fields", fieldsPreset, options);
  }

  deletePreset(id: number) {
    return this.http.delete(environment.server +presetUrl+"/queries/"+id.toString(), this.auth.getOptions());
  }

  savePanelPreset(panelPreset: QueryPanel){
    let options = this.auth.getOptions();

    return this.http.post(environment.server +presetUrl+"/panels", panelPreset, options);
  }

  getPanelsPresets(): Observable<QueryPanel[]> {
    let options = this.auth.getOptions();

    return this.http.get(environment.server +presetUrl+"/panels", options) as Observable<QueryPanel[]>;
  }

  deletePanelPreset(id: number) {
    return this.http.delete(environment.server +presetUrl+"/panels/"+id.toString(), this.auth.getOptions());
  }

}
