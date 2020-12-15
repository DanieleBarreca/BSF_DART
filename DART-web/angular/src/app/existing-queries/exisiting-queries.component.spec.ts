import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ExistingQueriesComponent } from './existing-queries.component';

describe('ExistingQueriesComponent', () => {
  let component: ExistingQueriesComponent;
  let fixture: ComponentFixture<ExistingQueriesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExistingQueriesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExistingQueriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
