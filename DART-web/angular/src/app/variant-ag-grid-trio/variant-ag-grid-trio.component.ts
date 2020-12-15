import { Component, OnInit } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";

@Component({
  selector: 'app-variant-ag-grid-trio',
  templateUrl: './variant-ag-grid-trio.component.html',
  styleUrls: ['./variant-ag-grid-trio.component.css']
})
export class VariantAgGridTrioComponent implements ICellRendererAngularComp {

  public annotation: any;

  constructor(){}

  agInit(params: any): void {
    this.annotation = params.value;
  }

  refresh(): boolean {
    return false;
  }
}
