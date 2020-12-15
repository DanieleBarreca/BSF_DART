import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PresetModalComponent } from './preset-modal.component';

describe('PresetModalComponent', () => {
  let component: PresetModalComponent;
  let fixture: ComponentFixture<PresetModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PresetModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PresetModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
