import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAgGridComponent } from './variant-ag-grid.component';

describe('VariantAgGridComponent', () => {
  let component: VariantAgGridComponent;
  let fixture: ComponentFixture<VariantAgGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantAgGridComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAgGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
