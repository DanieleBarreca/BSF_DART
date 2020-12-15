import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryPresetsComponent } from './query-presets.component';

describe('QueryPresetsComponent', () => {
  let component: QueryPresetsComponent;
  let fixture: ComponentFixture<QueryPresetsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryPresetsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryPresetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
