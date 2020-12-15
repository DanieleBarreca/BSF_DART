import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LolliplotComponent } from './lolliplot.component';

describe('LolliplotComponent', () => {
  let component: LolliplotComponent;
  let fixture: ComponentFixture<LolliplotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LolliplotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LolliplotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
