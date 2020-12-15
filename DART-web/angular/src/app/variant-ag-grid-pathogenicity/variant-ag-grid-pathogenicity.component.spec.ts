import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAgGridPathogenicityComponent } from './variant-ag-grid-pathogenicity.component';

describe('VariantAgGridPathogenicityComponent', () => {
  let component: VariantAgGridPathogenicityComponent;
  let fixture: ComponentFixture<VariantAgGridPathogenicityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantAgGridPathogenicityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAgGridPathogenicityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
