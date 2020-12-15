import {Component, OnInit, Output, EventEmitter, Input} from '@angular/core';
import { BedService } from '../bed.service';

@Component({
  selector: 'app-bed-table',
  templateUrl: './bed-table.component.html',
  styleUrls: ['./bed-table.component.css']
})
export class BedTableComponent implements OnInit {
  @Output() bedSelected = new EventEmitter<any>();
  @Input() genome: string;

  constructor(private bedService: BedService) { }

  beds: any[];
  searchNameString: string;

  ngOnInit() {
    this.bedService.getAll(this.genome).subscribe(data => this.beds=data);
  }

  selectRow(bed:any) : void {
    this.bedSelected.emit(bed);
  }

}
