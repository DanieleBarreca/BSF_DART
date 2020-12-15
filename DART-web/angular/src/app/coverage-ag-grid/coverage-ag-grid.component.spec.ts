import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CoverageAgGridComponent } from './coverage-ag-grid.component';

describe('CoverageAgGridComponent', () => {
  let component: CoverageAgGridComponent;
  let fixture: ComponentFixture<CoverageAgGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CoverageAgGridComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CoverageAgGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
