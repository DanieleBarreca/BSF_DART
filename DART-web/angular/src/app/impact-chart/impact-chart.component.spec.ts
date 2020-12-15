import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpactChartComponent } from './impact-chart.component';

describe('ImpactChartComponent', () => {
  let component: ImpactChartComponent;
  let fixture: ComponentFixture<ImpactChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImpactChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpactChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
