import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantFilterQueryComponent } from './variant-filter-query.component';

describe('VariantFilterQueryComponent', () => {
  let component: VariantFilterQueryComponent;
  let fixture: ComponentFixture<VariantFilterQueryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantFilterQueryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantFilterQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
