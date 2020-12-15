import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantTableConfigComponent } from './variant-table-config.component';

describe('VariantTableConfigComponent', () => {
  let component: VariantTableConfigComponent;
  let fixture: ComponentFixture<VariantTableConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantTableConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantTableConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
