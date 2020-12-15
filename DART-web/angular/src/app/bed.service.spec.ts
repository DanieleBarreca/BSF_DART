import { TestBed, inject } from '@angular/core/testing';

import { BedService } from './bed.service';

describe('BedService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BedService]
    });
  });

  it('should be created', inject([BedService], (service: BedService) => {
    expect(service).toBeTruthy();
  }));
});
