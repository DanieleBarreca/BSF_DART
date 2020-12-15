import { TestBed, inject } from '@angular/core/testing';

import { GroupAdminService } from './group-admin.service';

describe('GroupAdminService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GroupAdminService]
    });
  });

  it('should be created', inject([GroupAdminService], (service: GroupAdminService) => {
    expect(service).toBeTruthy();
  }));
});
