import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAnnotationModalComponent } from './variant-annotation-modal.component';

describe('VariantAnnotationModalComponent', () => {
  let component: VariantAnnotationModalComponent;
  let fixture: ComponentFixture<VariantAnnotationModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantAnnotationModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAnnotationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
