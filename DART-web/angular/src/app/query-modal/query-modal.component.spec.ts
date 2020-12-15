import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryModalComponent } from './query-modal.component';

describe('QueryModalComponent', () => {
  let component: QueryModalComponent;
  let fixture: ComponentFixture<QueryModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
