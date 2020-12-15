import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAgGridValidationComponent } from './variant-ag-grid-validation.component';

describe('VariantAgGridValidationComponent', () => {
  let component: VariantAgGridValidationComponent;
  let fixture: ComponentFixture<VariantAgGridValidationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantAgGridValidationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAgGridValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
