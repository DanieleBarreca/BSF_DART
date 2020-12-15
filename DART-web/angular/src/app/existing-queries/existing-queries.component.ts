import {Component, OnDestroy, OnInit} from '@angular/core';
import { QueryService } from '../query.service';
import { Router, NavigationEnd } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { QueryModalComponent } from '../query-modal/query-modal.component';
import {FullQuery} from "../variant-filter-query/FullQuery";

@Component({
  selector: 'app-existing-queries',
  templateUrl: './existing-queries.component.html',
  styleUrls: ['./existing-queries.component.css']
})
export class ExistingQueriesComponent implements OnDestroy {
  navigationSubscription;

  tabs = [];

  constructor(private queryService: QueryService, private router: Router, private modalService: BsModalService) {
    this.init();
  }

  private init(): void {
    this.navigationSubscription = this.router.events.subscribe(
      (event: any) => {
        if (event instanceof NavigationEnd) {
          this.initialise();
        }
      }
    );
  }

  ngOnDestroy() {
    if (this.navigationSubscription){
      this.navigationSubscription.unsubscribe();
    }
  }

  initialise() {
    this.queryService.getAllQueries().subscribe(
      (data) => {

        this.tabs =  [];

        Object.keys(data).forEach( (value, index) => {
          this.tabs.push({title: value, active: index == 0, queries: data[value]});
        });

      },
      (err) => console.log(err)
    )
  }

  showQueryModal(cachedQueryInfo?: any){
    let initialState = {};
    if (cachedQueryInfo){
      initialState['query'] = cachedQueryInfo.theQuery as FullQuery;
    }

    let bsModalRef = this.modalService.show(QueryModalComponent, {initialState: initialState, class: 'variant-modal', backdrop: 'static'});
    let queryComponent: QueryModalComponent = bsModalRef.content;
    queryComponent.querySubmitted.subscribe(
      (data) => {this.initialise()}
    )
  }


  deleteQueries(tabId){
    let toDelete = this.tabs.filter( (value => value.title == tabId))[0].queries;

    this.queryService.deleteQueries(toDelete.map( queryWrapper => queryWrapper.uuid )).subscribe(
      (data) => this.initialise(),
      (err) => console.error(err)
    );
  }

  deleteQuery(){
    this.initialise();
  }

}
