import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupUserManagementComponent } from './group-user-management.component';

describe('GroupUserManagementComponent', () => {
  let component: GroupUserManagementComponent;
  let fixture: ComponentFixture<GroupUserManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupUserManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupUserManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
