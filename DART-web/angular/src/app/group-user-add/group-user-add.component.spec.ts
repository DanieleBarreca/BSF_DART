import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupUserAddComponent } from './group-user-add.component';

describe('GroupUserAddComponent', () => {
  let component: GroupUserAddComponent;
  let fixture: ComponentFixture<GroupUserAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupUserAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupUserAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
