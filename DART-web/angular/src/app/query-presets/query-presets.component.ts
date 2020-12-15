import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { PresetService } from '../preset.service';
import { QueryBuilderConfig } from '../query-builder/query-builder-config';
import { AuthenticationService } from '../authentication.service';
import {QueryFilter} from "../variant-filter-query/FullQuery";

@Component({
  selector: 'app-query-presets',
  templateUrl: './query-presets.component.html',
  styleUrls: ['./query-presets.component.css']
})
export class QueryPresetsComponent implements OnInit {
  @Output() presetSelected = new EventEmitter<any>();

  vcfId: number;

  queryPresets: any[] = [];

  constructor(public bsModalRef: BsModalRef, private presetService: PresetService, private auth: AuthenticationService) { }

  ngOnInit() {
    this.queryPresets = [];

    this.presetService.getQueryPresets(this.vcfId).subscribe(
      (data) => (data as Array<QueryFilter>).forEach(element => {
        this.queryPresets.push({preset: element, qbConfig: new QueryBuilderConfig(element.FIELDS, false, element.RULE, element.REF_ID)});
      })
    );
  }

  deletePreset(id: number){
    this.presetService.deletePreset(id).subscribe(
      (data) => this.ngOnInit(),
      (err) =>console.log(err)
    );
  }

  canDeletePreset(){
    if (this.auth.getPermissions() && this.auth.getPermissions().canSavePreset){
      return true;
    }

    return false;
  }

}
