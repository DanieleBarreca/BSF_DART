import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAgGridTrioComponent } from './variant-ag-grid-trio.component';

describe('VariantAgGridTrioComponent', () => {
  let component: VariantAgGridTrioComponent;
  let fixture: ComponentFixture<VariantAgGridTrioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantAgGridTrioComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAgGridTrioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
