import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BedTableComponent } from './bed-table.component';

describe('BedTableComponent', () => {
  let component: BedTableComponent;
  let fixture: ComponentFixture<BedTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BedTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BedTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
