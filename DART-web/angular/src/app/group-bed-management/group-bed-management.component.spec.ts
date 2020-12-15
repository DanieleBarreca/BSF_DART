import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupBedManagementComponent } from './group-bed-management.component';

describe('GroupBedManagementComponent', () => {
  let component: GroupBedManagementComponent;
  let fixture: ComponentFixture<GroupBedManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupBedManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupBedManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
