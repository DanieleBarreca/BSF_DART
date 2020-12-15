import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { PresetService } from '../preset.service';
import { BsModalRef } from '../../../node_modules/ngx-bootstrap/modal';
import { AuthenticationService } from '../authentication.service';
import {QueryPanel} from "../variant-filter-query/FullQuery";

@Component({
  selector: 'app-gene-panel-presets',
  templateUrl: './gene-panel-presets.component.html',
  styleUrls: ['./gene-panel-presets.component.css']
})
export class GenePanelPresetsComponent implements OnInit {
  @Output() presetSelected: EventEmitter<QueryPanel> = new EventEmitter<QueryPanel>();

  constructor(public bsModalRef: BsModalRef, private presetService: PresetService, private auth: AuthenticationService) { }

  panelPresets: QueryPanel[] = [];

  ngOnInit() {
    this.panelPresets = [];

    this.presetService.getPanelsPresets().subscribe(
      (data) => this.panelPresets=data,
      (error) => console.error(error)
    );
  }

  deletePreset(id: number){
    this.presetService.deletePanelPreset(id).subscribe(
      (data) => this.ngOnInit(),
      (err) =>console.log(err)
    );
  }

  canDeletePreset(){
    if (this.auth.getPermissions() && this.auth.getPermissions().canSavePanel){
      return true;
    }

    return false;
  }

}
