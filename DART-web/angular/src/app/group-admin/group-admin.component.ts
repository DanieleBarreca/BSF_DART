import { Component, OnInit } from '@angular/core';
import { PresetService } from '../preset.service';
import { AuthenticationService } from '../authentication.service';
import { QueryBuilderConfig } from '../query-builder/query-builder-config';
import {QueryFilter, QueryPanel} from "../variant-filter-query/FullQuery";

@Component({
  selector: 'app-group-admin',
  templateUrl: './group-admin.component.html',
  styleUrls: ['./group-admin.component.css']
})
export class GroupAdminComponent implements OnInit {

  queryPresets: any[] = [];
  panelPresets: QueryPanel[] = [];

  constructor(private presetService: PresetService, private auth: AuthenticationService) { }

  ngOnInit() {
    this.queryPresets = [];

    this.presetService.getQueryPresets().subscribe(
      (data) => (data as Array<QueryFilter>).forEach(element => {
        this.queryPresets.push({REF_ID: element.REF_ID, MNEMONIC: element.MNEMONIC, config: new QueryBuilderConfig(element.FIELDS, false, element.RULE, element.REF_ID)});
      })
    );

    this.panelPresets = [];

    this.presetService.getPanelsPresets().subscribe(
      (data) => this.panelPresets=data,
      (error) => console.error(error)
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


  deletePanel(id: number){
    this.presetService.deletePanelPreset(id).subscribe(
      (data) => this.ngOnInit(),
      (err) =>console.log(err)
    );
  }

  canEditPanel(){
    if (this.auth.getPermissions() && this.auth.getPermissions().canSavePanel){
      return true;
    }

    return false;
  }
}
