import { Component, OnInit, ViewChild, EventEmitter } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { VariantFilterQueryComponent } from '../variant-filter-query/variant-filter-query.component';
import { Router } from '@angular/router';
import {FullQuery} from "../variant-filter-query/FullQuery";

@Component({
  selector: 'app-query-modal',
  templateUrl: './query-modal.component.html',
  styleUrls: ['./query-modal.component.css']
})
export class QueryModalComponent implements OnInit {
  @ViewChild('queryComponent') variantQuery: VariantFilterQueryComponent;

  public querySubmitted = new EventEmitter<string>();

  constructor(public bsModalRef: BsModalRef, private router: Router) {}

  query: FullQuery = null;

  ngOnInit() {
  }

  submit(){

    let submittedQueryObs = this.variantQuery.submitQuery();
    if (submittedQueryObs) {
      submittedQueryObs.subscribe(
        (data) => {
          this.querySubmitted.emit(data);
          this.bsModalRef.hide();
        },
        (err) => {console.log(err)}
      );
    }

  }



}
