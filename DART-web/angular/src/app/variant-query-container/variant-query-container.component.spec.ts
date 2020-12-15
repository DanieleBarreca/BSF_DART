import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantQueryContainerComponent } from './variant-query-container.component';

describe('VariantQueryContainerComponent', () => {
  let component: VariantQueryContainerComponent;
  let fixture: ComponentFixture<VariantQueryContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantQueryContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantQueryContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
