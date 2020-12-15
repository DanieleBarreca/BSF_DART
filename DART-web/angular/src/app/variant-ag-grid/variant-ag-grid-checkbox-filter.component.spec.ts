import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {VariantAgGridCheckboxFilterComponent} from "./variant-ag-grid-checkbox-filter.component";

describe('VariantAgGridCheckboxFilterComponent', () => {
  let component: VariantAgGridCheckboxFilterComponent;
  let fixture: ComponentFixture<VariantAgGridCheckboxFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantAgGridCheckboxFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAgGridCheckboxFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
