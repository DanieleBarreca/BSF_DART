import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenePanelPresetsComponent } from './gene-panel-presets.component';

describe('GenePanelPresetsComponent', () => {
  let component: GenePanelPresetsComponent;
  let fixture: ComponentFixture<GenePanelPresetsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GenePanelPresetsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenePanelPresetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
