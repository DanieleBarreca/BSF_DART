import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupVcfManagementComponent } from './group-vcf-management.component';

describe('GroupVcfManagementComponent', () => {
  let component: GroupVcfManagementComponent;
  let fixture: ComponentFixture<GroupVcfManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupVcfManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupVcfManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
