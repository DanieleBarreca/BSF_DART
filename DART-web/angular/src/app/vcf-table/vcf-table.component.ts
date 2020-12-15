import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VcfService } from '../vcf.service';
import {RowEvent} from "ag-grid-community";

@Component({
  selector: 'app-vcf-table',
  templateUrl: './vcf-table.component.html',
  styleUrls: ['./vcf-table.component.css']
})
export class VcfTableComponent implements OnInit {

  @Output() vcfSelected = new EventEmitter<any>();

  data: Array<any>;
  columnDefs =  [
    {
      headerName : 'SAMPLE',
      field : 'SAMPLE.SAMPLE_NAME',
      sortable: true,
      pinned: "left",
      filter:true,
      resizable: true
    },
    {
      headerName : 'VCF FILE',
      field : 'VCF.VCF_FILE',
      sortable: true,
      pinned: "left",
      filter:true,
      resizable: true
    },
    {
      headerName : 'DATE',
      //filter: "agDateColumnFilter",
      //floatingFilter: true,
      valueGetter: function(params) {
        if (params.data) {
          let creationDate = new Date(params.data['VCF']['CREATION_DATE']);
          return new Date(creationDate.getFullYear(),creationDate.getMonth() , creationDate.getDate());
        }
        return null;
      },
      valueFormatter: function(params) {
        if (params.value) {
          return (params.value as Date).toDateString();
        }
        return "";
      },
      sortable: true,
      suppressMovable: true,
      resizable: true
    },
    {
      headerName : 'TYPE',
      field : 'VCF.VCF_TYPE',
      suppressMovable: true,
      resizable: true
    },
    {
      headerName : 'GENOME',
      field : 'VCF.REF_GENOME',
      suppressMovable: true,
      resizable: true
    }

  ];

  constructor(private vcfService: VcfService){  }

  ngOnInit() {
    this.vcfService.getVcfs().subscribe(vcfs => {
      this.data = new Array<any>();
      (vcfs as Array<any>).forEach((vcf) => {
        vcf['SAMPLES'].forEach((sample) => {
          this.data.push({
            SAMPLE: sample,
            VCF: vcf
          })
        });
      });
    });
  }


  selectRow(vcf: RowEvent) : void {
    this.vcfSelected.emit(vcf.data);
  }

}
