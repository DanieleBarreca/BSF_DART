import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VcfTableComponent } from './vcf-table.component';

describe('VcfTableComponent', () => {
  let component: VcfTableComponent;
  let fixture: ComponentFixture<VcfTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VcfTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VcfTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
